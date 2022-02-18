package mobileapp.ctemplar.com.ctemplarapp.main;

import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.ALL_MAILS;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.ARCHIVE;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.CONTACT;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.DRAFT;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.INBOX;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.OUTBOX;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.SENT;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.SPAM;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.STARRED;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.TRASH;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.UNREAD;
import static mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils.GENERAL_GSON;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mobileapp.ctemplar.com.ctemplarapp.ActivityInterface;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.folders.ManageFoldersActivity;
import mobileapp.ctemplar.com.ctemplarapp.login.LoginActivity;
import mobileapp.ctemplar.com.ctemplarapp.message.SendMessageActivity;
import mobileapp.ctemplar.com.ctemplarapp.message.SendMessageFragment;
import mobileapp.ctemplar.com.ctemplarapp.net.entity.AttachmentsEntity;
import mobileapp.ctemplar.com.ctemplarapp.net.response.emails.UnreadFoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.DTOResource;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.PageableDTO;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.emails.UnreadFoldersDTO;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.folders.CustomFolderDTO;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.mapper.UnreadFoldersMapper;
import mobileapp.ctemplar.com.ctemplarapp.settings.SettingsActivity;
import mobileapp.ctemplar.com.ctemplarapp.settings.keys.MailboxKeyViewModel;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncryptUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.LocaleUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ThemeUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ToastUtils;
import mobileapp.ctemplar.com.ctemplarapp.view.ResizeAnimation;
import okhttp3.ResponseBody;
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
    private final Map<String, Integer> folderToUnread = new HashMap<>();

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
        mainModel.getUnreadFoldersLiveData().observe(this, this::handleUnreadFolders);
        mainModel.getCustomFoldersLiveData().observe(this, this::handleCustomFolders);

        mainModel.getUnreadFolders();
        loadUserInfo();
        handleSendToIntent(getIntent());
        // default folder
        setFolder(INBOX);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainModel.getUnreadFolders();
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
        if (id == R.id.nav_inbox) {
            setFolder(INBOX);
        } else if (id == R.id.nav_draft) {
            setFolder(DRAFT);
        } else if (id == R.id.nav_sent) {
            setFolder(SENT);
        } else if (id == R.id.nav_outbox) {
            setFolder(OUTBOX);
        } else if (id == R.id.nav_all_mails) {
            setFolder(ALL_MAILS);
        } else if (id == R.id.nav_unread) {
            setFolder(UNREAD);
        } else if (id == R.id.nav_starred) {
            setFolder(STARRED);
        } else if (id == R.id.nav_archive) {
            setFolder(ARCHIVE);
        } else if (id == R.id.nav_spam) {
            setFolder(SPAM);
        } else if (id == R.id.nav_trash) {
            setFolder(TRASH);
        } else if (id == R.id.nav_contact) {
            setFolder(CONTACT);
        } else if (id == R.id.nav_settings) {
            Intent settingsScreen = new Intent(this, SettingsActivity.class);
            startActivity(settingsScreen);
            return true;
        } else if (id == R.id.nav_logout) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_log_out)
                    .setMessage(R.string.dialog_log_out_confirm)
                    .setPositiveButton(R.string.btn_log_out, (dialog, which) -> mainModel.logout())
                    .setNeutralButton(R.string.btn_cancel, null)
                    .show();
            Button neutralButton = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
            neutralButton.setTextColor(getResources().getColor(R.color.colorBlue));
            return true;
        } else if (id == R.id.nav_manage_folders) {
            Intent manageFolders = new Intent(this, ManageFoldersActivity.class);
            startActivity(manageFolders);
            return true;
        } else if (id == R.id.nav_manage_folders_more) {
            customFoldersShowCount += CUSTOM_FOLDER_STEP;
            mainModel.getUnreadFolders();
            return true;
        } else {
            for (CustomFolderDTO folder : customFolders) {
                if (id == folder.getId()) {
                    setFolder(folder.getName(), folder.getName());
                }
            }
        }
        return true;
    }

    private int getFolderTitleId(String folder) {
        switch (folder) {
            case INBOX:
                return R.string.nav_drawer_inbox;
            case DRAFT:
                return R.string.nav_drawer_draft;
            case SENT:
                return R.string.nav_drawer_sent;
            case OUTBOX:
                return R.string.nav_drawer_outbox;
            case ALL_MAILS:
                return R.string.nav_drawer_all_mails;
            case UNREAD:
                return R.string.nav_drawer_unread;
            case STARRED:
                return R.string.nav_drawer_starred;
            case ARCHIVE:
                return R.string.nav_drawer_archive;
            case SPAM:
                return R.string.nav_drawer_spam;
            case CONTACT:
                return R.string.nav_drawer_contact;
            default:
                return R.string.nav_drawer_trash;
        }
    }

    private String getFolderTitle(String folder) {
        return getString(getFolderTitleId(folder));
    }

    private void setFolder(String folder) {
        setFolder(folder, getFolderTitle(folder));
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

    private void handleUnreadFolders(DTOResource<ResponseBody> resource) {
        if (!resource.isSuccess()) {
            ToastUtils.showToast(this, resource.getError());
            return;
        }
        ResponseBody response = resource.getDto();
        UnreadFoldersDTO unreadFoldersDTO;
        try {
            String responseString = response.string();
            unreadFoldersDTO = UnreadFoldersMapper.map(GENERAL_GSON.fromJson(responseString,
                    UnreadFoldersResponse.class));
            Map<String, Integer> folderToUnreadCount = GENERAL_GSON.fromJson(responseString,
                    new TypeToken<Map<String, Integer>>() {}.getType());
            if (unreadFoldersDTO == null) {
                unreadFoldersDTO = new UnreadFoldersDTO();
            }
            if (folderToUnreadCount != null) {
                folderToUnread.putAll(folderToUnreadCount);
            }
        } catch (IOException e) {
            Timber.e(e, "unreadFoldersResponse parse error");
            return;
        }
        Menu navigationMenu = navigationView.getMenu();
        ((TextView) (navigationMenu.findItem(R.id.nav_inbox).getActionView()))
                .setText(unreadFoldersDTO.getInboxString());
        ((TextView) (navigationMenu.findItem(R.id.nav_inbox).getActionView()))
                .setText(unreadFoldersDTO.getInboxString());
        ((TextView) (navigationMenu.findItem(R.id.nav_draft).getActionView()))
                .setText(unreadFoldersDTO.getDraftString());
        ((TextView) (navigationMenu.findItem(R.id.nav_outbox).getActionView()))
                .setText(unreadFoldersDTO.getOutboxString());
        ((TextView) (navigationMenu.findItem(R.id.nav_starred).getActionView()))
                .setText(unreadFoldersDTO.getStarredString());
        ((TextView) (navigationMenu.findItem(R.id.nav_spam).getActionView()))
                .setText(unreadFoldersDTO.getSpamString());
        mainModel.getCustomFolders(customFoldersShowCount, 0);
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
            CustomFolderDTO folder = it.next();
            navigationMenu.removeItem(folder.getId());
            it.remove();
        }
        for (CustomFolderDTO folder : customFolders) {
            String name = folder.getName();
            MenuItem menuItem = navigationMenu.add(
                    R.id.activity_main_drawer_folders,
                    folder.getId(),
                    folder.getSortOrder(),
                    name
            );
            menuItem.setCheckable(true);
            menuItem.setIcon(R.drawable.ic_manage_folders);
            menuItem.setActionView(R.layout.menu_message_counter);
            Integer folderCount = folderToUnread.get(name);
            ((TextView) menuItem.getActionView()).setText(folderCount == null ? ""
                    : EditTextUtils.intToStringPositive(folderCount));
            Drawable icon = menuItem.getIcon();
            icon.mutate();
            try {
                icon.setColorFilter(Color.parseColor(folder.getColor()), PorterDuff.Mode.SRC_IN);
            } catch (IllegalArgumentException e) {
                Timber.e(e, "Can't set folder color");
            }
            navigationViewCustomFolders.add(folder);
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
