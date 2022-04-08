package com.ctemplar.app.fdroid.main;

import static com.ctemplar.app.fdroid.repository.constant.MainFolderNames.INBOX;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.BaseContextWrapperDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.BlendModeColorFilterCompat;
import androidx.core.graphics.BlendModeCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.ctemplar.app.fdroid.ActivityInterface;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.folders.FoldersManager;
import com.ctemplar.app.fdroid.folders.ManageFoldersActivity;
import com.ctemplar.app.fdroid.login.LoginActivity;
import com.ctemplar.app.fdroid.message.SendMessageActivity;
import com.ctemplar.app.fdroid.message.SendMessageFragment;
import com.ctemplar.app.fdroid.net.entity.AttachmentsEntity;
import com.ctemplar.app.fdroid.repository.dto.DTOResource;
import com.ctemplar.app.fdroid.repository.dto.PageableDTO;
import com.ctemplar.app.fdroid.repository.dto.folders.CustomFolderDTO;
import com.ctemplar.app.fdroid.repository.entity.MailboxEntity;
import com.ctemplar.app.fdroid.services.NotificationService;
import com.ctemplar.app.fdroid.settings.SettingsActivity;
import com.ctemplar.app.fdroid.settings.keys.MailboxKeyViewModel;
import com.ctemplar.app.fdroid.utils.EditTextUtils;
import com.ctemplar.app.fdroid.utils.EncryptUtils;
import com.ctemplar.app.fdroid.utils.LocaleUtils;
import com.ctemplar.app.fdroid.utils.ThemeUtils;
import com.ctemplar.app.fdroid.utils.ToastUtils;
import com.ctemplar.app.fdroid.view.ResizeAnimation;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int CUSTOM_FOLDER_STEP = 10;

    private FrameLayout contentContainer;
    private NavigationView navigationView;
    private DrawerLayout drawer;

    private MainActivityViewModel mainModel;
    private String toggleFolder;
    private boolean isTablet;

    private int drawerMaxWidth = 400;
    private int drawerMinWidth = 100;

    private int customFoldersShowCount = 3;
    private final List<CustomFolderDTO> navigationViewCustomFolders = new ArrayList<>();
    private final List<CustomFolderDTO> customFolders = new ArrayList<>();

    private final Handler handler = new Handler();
    private MainFragment mainFragment;
    private AppCompatDelegate baseContextWrappingDelegate;
    private MailboxKeyViewModel mailboxKeyViewModel;

    @NonNull
    @Override
    public AppCompatDelegate getDelegate() {
        return baseContextWrappingDelegate != null ?
                baseContextWrappingDelegate :
                (baseContextWrappingDelegate = new BaseContextWrapperDelegate(super.getDelegate()));
    }

    @Override
    public Context createConfigurationContext(Configuration overrideConfiguration) {
        Context context = super.createConfigurationContext(overrideConfiguration);
        return LocaleUtils.getContextWrapper(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);
        setContentView(R.layout.activity_main);
        NotificationService.updateState(this);

        drawer = findViewById(R.id.drawer_layout);
        contentContainer = findViewById(R.id.content_container);
        navigationView = findViewById(R.id.nav_view);
        Menu navigationMenu = navigationView.getMenu();
        Drawable navInboxIcon = navigationMenu.findItem(R.id.nav_inbox).getIcon();
        drawerMinWidth = navInboxIcon.getMinimumWidth() * 3;
        navigationView.setNavigationItemSelectedListener(this);

        isTablet = getResources().getBoolean(R.bool.isTabletLand);
        if (isTablet) {
            setupTabletDrawer();
        } else {
            setNavigationDrawer();
        }
        showMailboxDetailsInNavigationDrawer();

        mainFragment = new MainFragment();
        showFragment(mainFragment);

        mainModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        mailboxKeyViewModel = new ViewModelProvider(this).get(MailboxKeyViewModel.class);
        mainModel.getActionsStatus().observe(this, this::handleMainActions);
        mainModel.getCurrentFolder().observe(this, folder -> {
            showFragmentByFolder(folder);
            mainModel.getUnreadFolders();
        });

        mailboxKeyViewModel.getMailboxesResponseStatus().observe(this,
                responseStatus -> showMailboxDetailsInNavigationDrawer());
        mainModel.getCustomFoldersLiveData().observe(this, this::handleCustomFolders);
        mainModel.getUnreadFoldersLiveData().observe(this, this::handleUnreadFolders);
        mainModel.getUnreadCountSocketLiveData().observe(this, this::handleUnreadCount);

        getCustomFolders();
        loadUserInfo();
        handleSendToIntent(getIntent());
        // default folder
        setFolder(INBOX);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCustomFolders();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!isHandledPressBack(getCurrentFragment())) {
                try {
                    super.onBackPressed();
                } catch (Throwable e) {
                    Timber.wtf(e, "super.onBackPressed: %s", e.getMessage());
                }
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    finish();
                }
            }
        }
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(titleId);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Fragment currentFragment = getCurrentFragment();
        if (!(currentFragment instanceof MainFragment)) {
            return false;
        }
        if (currentFragment.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (isTablet) {
                closeOpenNavigationView();
            } else {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        if (isTablet && isHandledPressBack(getCurrentFragment())) {
//            return false;
//        }
        int id = item.getItemId();
        String folderName = FoldersManager.getNameByResourceId(id, customFolders);
        if (folderName != null) {
            setFolder(folderName);
            return true;
        }
        if (id == R.id.nav_settings) {
            Intent settingsScreen = new Intent(this, SettingsActivity.class);
            startActivity(settingsScreen);
            return true;
        }
        if (id == R.id.nav_logout) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_log_out)
                    .setMessage(R.string.dialog_log_out_confirm)
                    .setPositiveButton(R.string.btn_log_out, (dialog, which) -> mainModel.logout())
                    .setNeutralButton(R.string.btn_cancel, null)
                    .show();
            Button neutralButton = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
            neutralButton.setTextColor(getResources().getColor(R.color.colorBlue));
            return true;
        }
        if (id == R.id.nav_manage_folders) {
            Intent manageFolders = new Intent(this, ManageFoldersActivity.class);
            startActivity(manageFolders);
            return true;
        }
        if (id == R.id.nav_manage_folders_more) {
            customFoldersShowCount += CUSTOM_FOLDER_STEP;
            getCustomFolders();
            return true;
        }
        return true;
    }

    private void setFolder(String folder, String title) {
        setTitle(title);
        final Fragment currentFragment = getCurrentFragment();
        if (isTablet) {
            if (currentFragment instanceof MainFragment) {
                ((MainFragment) currentFragment).clearListAdapter();
            }
            mainModel.setCurrentFolder(folder);
        } else {
            toggleFolder = folder;
            if (currentFragment instanceof MainFragment) {
                ((MainFragment) currentFragment).clearListAdapter();
            }
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                handler.post(() -> drawer.closeDrawer(GravityCompat.START));
            } else {
                mainModel.setCurrentFolder(folder);
            }
        }
        int id = getNavigationViewMenuItemId(folder);
        if (id != -1) {
            navigationView.setCheckedItem(id);
        }
    }

    private String getFolderTitle(String name) {
        int resourceId = FoldersManager.getTitleResourceIdByName(name);
        if (resourceId == -1) {
            return name;
        }
        return getString(resourceId);
    }

    private void setFolder(String folder) {
        setFolder(folder, getFolderTitle(folder));
    }

    private int getNavigationViewMenuItemId(String folder) {
        if (INBOX.equals(folder)) {
            return R.id.nav_inbox;
        }
        return -1;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isTablet) {
            setupTabletDrawer();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleSendToIntent(intent);
    }

    private void handleSendToIntent(Intent intent) {
        String intentAction = intent.getAction();
        if (Intent.ACTION_SENDTO.equals(intentAction) || Intent.ACTION_SEND.equals(intentAction)) {
            String email = "";
            String subject = "";
            String compose = "";
            Intent sendToEmailIntent = new Intent(this, SendMessageActivity.class);
            sendToEmailIntent.putExtras(intent);
            String dataString = Uri.decode(intent.getDataString());
            if (EditTextUtils.isNotEmpty(dataString)) {
                email = dataString.substring(7);
                sendToEmailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            }
            if (EditTextUtils.isNotEmpty(intent.getStringExtra(Intent.EXTRA_SUBJECT))) {
                subject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
            }
            if (EditTextUtils.isNotEmpty(intent.getStringExtra(Intent.EXTRA_TEXT))) {
                compose = intent.getStringExtra(Intent.EXTRA_TEXT);
            }
            Fragment fragmentSendToEmail = SendMessageFragment.newInstance(
                    subject,
                    compose,
                    new String[]{email},
                    new String[]{},
                    new String[]{},
                    null,
                    new AttachmentsEntity(),
                    null,
                    -1
            );
            showActivityOrFragment(sendToEmailIntent, fragmentSendToEmail);
        }
    }

    private void handleCustomFolders(DTOResource<PageableDTO<CustomFolderDTO>> resource) {
        if (!resource.isSuccess()) {
            ToastUtils.showToast(this, resource.getError());
            return;
        }
        PageableDTO<CustomFolderDTO> pageableDTO = resource.getDto();
        List<CustomFolderDTO> customFolders = pageableDTO.getResults();
        this.customFolders.addAll(customFolders);
        int totalCount = pageableDTO.getTotalCount();

        Menu navigationMenu = navigationView.getMenu();
        navigationMenu.findItem(R.id.nav_manage_folders).setTitle
                (getString(R.string.nav_drawer_manage_folders_param, totalCount));
        MenuItem moreFolders = navigationMenu.findItem(R.id.nav_manage_folders_more);
        moreFolders.setVisible(customFoldersShowCount < totalCount);
        for (Iterator<CustomFolderDTO> it = navigationViewCustomFolders.iterator(); it.hasNext(); ) {
            navigationMenu.removeItem(it.next().getId());
            it.remove();
        }
        for (CustomFolderDTO folder : customFolders) {
            String name = folder.getName();
            MenuItem menuItem = navigationMenu.add(
                    R.id.activity_main_drawer_folders,
                    folder.getId(), folder.getSortOrder(), name
            );
            menuItem.setCheckable(true);
            menuItem.setIcon(R.drawable.ic_manage_folders);
            menuItem.setActionView(R.layout.menu_message_counter);
            Drawable iconDrawable = menuItem.getIcon().mutate();
            iconDrawable.setColorFilter(BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    Color.parseColor(folder.getColor()), BlendModeCompat.SRC_IN));
            navigationViewCustomFolders.add(folder);
        }
        mainModel.getUnreadFolders();
    }

    private void handleUnreadFolders(DTOResource<Map<String, Integer>> resource) {
        if (!resource.isSuccess()) {
            ToastUtils.showToast(this, resource.getError());
            return;
        }
        handleUnreadCount(resource.getDto());
    }

    private void handleUnreadCount(Map<String, Integer> unreadCount) {
        Menu navigationMenu = navigationView.getMenu();
        for (Map.Entry<String, Integer> nameToCount : unreadCount.entrySet()) {
            String key = nameToCount.getKey();
            Integer value = nameToCount.getValue();
            int folderId = FoldersManager.getResourceIdByName(key, customFolders);
            if (folderId == -1) {
                continue;
            }
            MenuItem menuItem = navigationMenu.findItem(folderId);
            if (menuItem == null) {
                continue;
            }
            ((TextView) menuItem.getActionView()).setText(value == null ? ""
                    : EditTextUtils.intToStringPositive(value));
            if (key.equals(INBOX)) {
                ((TextView) navigationMenu.findItem(R.id.nav_unread).getActionView())
                        .setText(value == null ? "" : EditTextUtils.intToStringPositive(value));
            }
        }
    }

    public void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(contentContainer.getId(), fragment)
                .addToBackStack(null)
                .commit();
        try {
            getSupportFragmentManager().executePendingTransactions();
        } catch (Throwable e) {
            Timber.w("executePendingTransaction error: %s", e.getMessage());
        }
    }

    private void showFragmentByFolder(String folder) {
        if (!(getCurrentFragment() instanceof MainFragment)) {
            showFragment(mainFragment);
        }
        mainFragment.showFragmentByFolder(folder);
    }

    public void showActivityOrFragment(Intent activityIntent, Fragment fragment) {
        if (isTablet) {
            showFragment(fragment);
        } else {
            startActivity(activityIntent);
        }
    }

    public boolean isHandledPressBack(Fragment fragment) {
        if (fragment instanceof ActivityInterface) {
            if (((ActivityInterface) fragment).onBackPressed()) {
                return true;
            } else {
                return false;
            }
        }
        if (!(getCurrentFragment() instanceof MainFragment)) {
            showFragment(mainFragment);
            return true;
        }
        if (mainFragment.isCurrentFragmentIsContact()) {
            setFolder(INBOX);
            return true;
        }
        if (!mainFragment.isCurrentFragmentIsInbox()) {
            return false;
        }
        if (!INBOX.equals(mainModel.getCurrentFolder().getValue())) {
            setFolder(INBOX);
            return true;
        }
        return false;
    }

    private Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        return fragmentManager.findFragmentById(contentContainer.getId());
    }

    private boolean isPortrait() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    private void setNavigationDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (toggleFolder != null) {
                    mainModel.setCurrentFolder(toggleFolder);
                    toggleFolder = null;
                }
            }
        };
//        drawerToggle.setToolbarNavigationClickListener(v ->
//                Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show());
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void setupTabletDrawer() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        drawerMaxWidth = (int) (displayMetrics.widthPixels * 0.3);

        ViewGroup.LayoutParams layoutParams = navigationView.getLayoutParams();
        if (isPortrait()) {
            navigationView.setTag(drawerMaxWidth);
            layoutParams.width = drawerMinWidth;
        } else {
            navigationView.setTag(drawerMinWidth);
            layoutParams.width = drawerMaxWidth;
        }
        navigationView.requestLayout();
    }

    private void closeOpenNavigationView() {
        if (!isPortrait()) {
            return;
        }
        int toWidth = drawerMinWidth;
        Integer viewTag = (Integer) navigationView.getTag();
        if (viewTag != null) {
            toWidth = viewTag;
        }
        navigationView.setTag(navigationView.getWidth());
        ResizeAnimation resizeAnimation = new ResizeAnimation(
                navigationView,
                navigationView.getWidth(),
                navigationView.getHeight(),
                toWidth,
                navigationView.getHeight()
        );
        navigationView.startAnimation(resizeAnimation);
    }

    private void loadUserInfo() {
        mailboxKeyViewModel.getMailboxes(20, 0);
        mailboxKeyViewModel.getMailboxKeys(20, 0);
        mainModel.getUserMyselfInfo();
    }

    private void getCustomFolders() {
        mainModel.getCustomFolders(customFoldersShowCount, 0);
    }

    private void handleMainActions(MainActivityActions actions) {
        if (actions == null) {
            return;
        }
        if (actions == MainActivityActions.ACTION_LOGOUT) {
            finish();
            startSignInActivity();
        }
    }

    private void showMailboxDetailsInNavigationDrawer() {
        MailboxEntity defaultMailbox = EncryptUtils.getDefaultMailbox();
        if (defaultMailbox == null) {
            Timber.e("defaultMailbox is null");
            return;
        }
        View headerView = navigationView.getHeaderView(0);
        navigationView.setCheckedItem(R.id.nav_inbox);

        TextView navUsername = headerView.findViewById(R.id.main_activity_username);
        TextView navEmail = headerView.findViewById(R.id.main_activity_email);
        navUsername.setText(defaultMailbox.getDisplayName());
        navEmail.setText(defaultMailbox.getEmail());
    }

    private void startSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finishAffinity();
    }
}
