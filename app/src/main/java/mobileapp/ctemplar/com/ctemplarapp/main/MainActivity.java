package mobileapp.ctemplar.com.ctemplarapp.main;

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
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.ActivityInterface;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.folders.ManageFoldersActivity;
import mobileapp.ctemplar.com.ctemplarapp.login.LoginActivity;
import mobileapp.ctemplar.com.ctemplarapp.message.SendMessageActivity;
import mobileapp.ctemplar.com.ctemplarapp.message.SendMessageFragment;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.entity.AttachmentsEntity;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.settings.SettingsActivity;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncryptUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ThemeUtils;
import mobileapp.ctemplar.com.ctemplarapp.view.ResizeAnimation;
import timber.log.Timber;

import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.ALL_MAILS;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.ARCHIVE;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.CONTACT;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.DRAFT;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.INBOX;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.OUTBOX;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.OUTBOX_DEAD_MAN;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.OUTBOX_DELAYED_DELIVERY;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.OUTBOX_SELF_DESTRUCT;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.SENT;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.SPAM;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.STARRED;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.TRASH;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.UNREAD;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int CUSTOM_FOLDER_STEP = 10;

    private FrameLayout contentContainer;
    private NavigationView navigationView;

    private MainActivityViewModel mainModel;
    private List<FoldersResult> customFoldersList;
    private List<FoldersResult> customFoldersListAll = new ArrayList<>();
    private String toggleFolder;
    private int customFoldersShowCount = 3;
    private boolean isTablet;
    private JSONObject unreadFolders;

    private int drawerMaxWidth = 400;
    private int drawerMinWidth = 100;

    private MainFragment mainFragment;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ThemeUtils.setStatusBarTheme(this);

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
        mainModel.getActionsStatus().observe(this, this::handleMainActions);
        mainModel.getCurrentFolder().observe(this, folder -> {
            showFragmentByFolder(folder);
            loadFolders();
        });

        mainModel.getResponseStatus().observe(this, this::handleResponseStatus);
        mainModel.getFoldersResponse().observe(this, foldersResponse -> {
            if (foldersResponse != null) {
                handleFoldersResponse(navigationView, foldersResponse);
            }
        });

        mainModel.getUnreadFoldersBody().observe(this, unreadFoldersBody -> {
            if (unreadFoldersBody != null) {
                mainModel.getFolders(customFoldersShowCount, 0);
                TextView inboxCounter = (TextView) navigationMenu.findItem(R.id.nav_inbox).getActionView();
                TextView draftCounter = (TextView) navigationMenu.findItem(R.id.nav_draft).getActionView();
                TextView outboxCounter = (TextView) navigationMenu.findItem(R.id.nav_outbox).getActionView();
                TextView starredCounter = (TextView) navigationMenu.findItem(R.id.nav_starred).getActionView();
                TextView spamCounter = (TextView) navigationMenu.findItem(R.id.nav_spam).getActionView();

                int inbox = 0, draft = 0, starred = 0, spam = 0, outbox = 0;
                try {
                    String unreadFoldersString = unreadFoldersBody.string();
                    unreadFolders = new JSONObject(unreadFoldersString);
                    inbox = unreadFolders.getInt(INBOX);
                    draft = unreadFolders.getInt(DRAFT);
                    starred = unreadFolders.getInt(STARRED);
                    spam = unreadFolders.getInt(SPAM);
                    int outboxDead = unreadFolders.getInt(OUTBOX_DEAD_MAN);
                    int outboxDelayed = unreadFolders.getInt(OUTBOX_DELAYED_DELIVERY);
                    int outboxDestruct = unreadFolders.getInt(OUTBOX_SELF_DESTRUCT);
                    outbox = outboxDelayed + outboxDead + outboxDestruct;
                } catch (IOException | JSONException e) {
                    Timber.e(e);
                }
                String inboxString = inbox > 0 ? String.valueOf(inbox) : "";
                String draftString = draft > 0 ? String.valueOf(draft) : "";
                String starredString = starred > 0 ? String.valueOf(starred) : "";
                String spamString = spam > 0 ? String.valueOf(spam) : "";
                String outboxString = outbox > 0 ? String.valueOf(outbox) : "";

                inboxCounter.setText(inboxString);
                draftCounter.setText(draftString);
                starredCounter.setText(starredString);
                spamCounter.setText(spamString);
                outboxCounter.setText(outboxString);
            }
        });

        loadFolders();
        loadUserInfo();

        // default folder
        setTitle(R.string.nav_drawer_inbox);
        mainModel.setCurrentFolder(INBOX);
        handleSendToIntent(getIntent());
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!(getCurrentFragment() instanceof MainFragment)) {
            return false;
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
    protected void onResume() {
        super.onResume();
        loadFolders();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (isTablet && isHandledPressBack(getCurrentFragment())) {
            return false;
        }

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        int id = item.getItemId();
        boolean closeDrawer = true;

        if (id == R.id.nav_inbox) {
            setTitle(R.string.nav_drawer_inbox);
            toggleFolder = INBOX;
        } else if (id == R.id.nav_draft) {
            setTitle(R.string.nav_drawer_draft);
            toggleFolder = DRAFT;
        } else if (id == R.id.nav_sent) {
            setTitle(R.string.nav_drawer_sent);
            toggleFolder = SENT;
        } else if (id == R.id.nav_outbox) {
            setTitle(R.string.nav_drawer_outbox);
            toggleFolder = OUTBOX;
        } else if (id == R.id.nav_all_mails) {
            setTitle(R.string.nav_drawer_all_mails);
            toggleFolder = ALL_MAILS;
        } else if (id == R.id.nav_unread) {
            setTitle(R.string.nav_drawer_unread);
            toggleFolder = UNREAD;
        } else if (id == R.id.nav_starred) {
            setTitle(R.string.nav_drawer_starred);
            toggleFolder = STARRED;
        } else if (id == R.id.nav_archive) {
            setTitle(R.string.nav_drawer_archive);
            toggleFolder = ARCHIVE;
        } else if (id == R.id.nav_spam) {
            setTitle(R.string.nav_drawer_spam);
            toggleFolder = SPAM;
        } else if (id == R.id.nav_trash) {
            setTitle(R.string.nav_drawer_trash);
            toggleFolder = TRASH;
        } else if (id == R.id.nav_contact) {
            setTitle(R.string.nav_drawer_contact);
            toggleFolder = CONTACT;
        } else if (id == R.id.nav_settings) {
            Intent settingsScreen = new Intent(this, SettingsActivity.class);
            startActivity(settingsScreen);
        } else if (id == R.id.nav_logout) {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.dialog_log_out))
                    .setMessage(getResources().getString(R.string.dialog_log_out_confirm))
                    .setPositiveButton(getResources().getString(R.string.btn_log_out), (dialog, which) -> mainModel.logout()
                    )
                    .setNeutralButton(R.string.btn_cancel, null)
                    .show();
        } else if (id == R.id.nav_manage_folders) {
            Intent manageFolders = new Intent(this, ManageFoldersActivity.class);
            startActivity(manageFolders);
        } else if (id == R.id.nav_manage_folders_more) {
            customFoldersShowCount += CUSTOM_FOLDER_STEP;
            loadFolders();
            closeDrawer = false;
        } else {
            for (FoldersResult folderItem :
                    customFoldersList) {
                if (id == folderItem.getId()) {
                    setTitle(folderItem.getName());
                    toggleFolder = folderItem.getName();
                }
            }
        }

        final Fragment currentFragment = getCurrentFragment();
        if (closeDrawer && !isTablet) {
            if (currentFragment instanceof MainFragment) {
                ((MainFragment) currentFragment).clearListAdapter();

                handler.post(() -> drawer.closeDrawer(GravityCompat.START));
            }
        } else if (isTablet) {
            if (currentFragment instanceof MainFragment) {
                ((MainFragment) currentFragment).clearListAdapter();
            }
            mainModel.setCurrentFolder(toggleFolder);
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setupTabletDrawer();
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
                sendToEmailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
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
                    new String[] { email },
                    new String[] {},
                    new String[] {},
                    null,
                    new AttachmentsEntity(),
                    null
            );
            showActivityOrFragment(sendToEmailIntent, fragmentSendToEmail);
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

    private void handleFoldersResponse(NavigationView navigationView, FoldersResponse foldersResponse) {
        customFoldersList = foldersResponse.getFoldersList();
        int customFoldersCount = foldersResponse.getTotalCount();
        Menu navigationMenu = navigationView.getMenu();

        List<FoldersResult> foldersListForDeleting = customFoldersListAll;
        for (FoldersResult folderItem : customFoldersListAll) {
            navigationMenu.removeItem(folderItem.getId());
        }
        customFoldersListAll.removeAll(foldersListForDeleting);

        MenuItem manageFolders = navigationMenu.findItem(R.id.nav_manage_folders);
        String manageFoldersTitle = getResources().getString(R.string.nav_drawer_manage_folders_param, customFoldersCount);
        manageFolders.setTitle(manageFoldersTitle);

        for (FoldersResult folderItem : customFoldersList) {
            customFoldersListAll.add(folderItem);
            int folderId = folderItem.getId();
            int order = folderItem.getSortOrder();
            String folderName = folderItem.getName();
            MenuItem menuItem = navigationMenu.add(
                    R.id.activity_main_drawer_folders,
                    folderId,
                    order,
                    folderName
            );
            menuItem.setCheckable(true);

            menuItem.setIcon(R.drawable.ic_manage_folders);
            Drawable itemIcon = menuItem.getIcon();
            itemIcon.mutate();
            int folderColor = Color.parseColor(folderItem.getColor());
            itemIcon.setColorFilter(folderColor, PorterDuff.Mode.SRC_IN);

            menuItem.setActionView(R.layout.menu_message_counter);
            TextView actionView = (TextView) menuItem.getActionView();
            try {
                int unreadMessages = unreadFolders.getInt(folderName);
                String unreadString = unreadMessages > 0 ? String.valueOf(unreadMessages) : null;
                actionView.setText(unreadString);
            } catch (Exception e) {
                Timber.e(e);
            }
        }

        MenuItem moreFolders = navigationMenu.findItem(R.id.nav_manage_folders_more);
        if (customFoldersShowCount < customFoldersCount) {
            moreFolders.setVisible(true);
        } else {
            moreFolders.setVisible(false);
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
        return fragment instanceof ActivityInterface && !((ActivityInterface) fragment).onBackPressed();
    }

    private Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        return fragmentManager.findFragmentById(contentContainer.getId());
    }

    private void loadFolders() {
        mainModel.getUnreadFoldersList();
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
                }
            }
        };
        drawerToggle.setToolbarNavigationClickListener(v ->
                Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show());
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
        mainModel.getMailboxes(20, 0);
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

    private void handleResponseStatus(ResponseStatus status) {
        if (status == null) {
            return;
        }
        if (status == ResponseStatus.RESPONSE_NEXT_MAILBOXES) {
            showMailboxDetailsInNavigationDrawer();
        }
    }

    private void showMailboxDetailsInNavigationDrawer() {
        MailboxEntity defaultMailbox = EncryptUtils.getDefaultMailbox();
        if (defaultMailbox != null) {
            Timber.i("Standard startup");
            View headerView = navigationView.getHeaderView(0);
            navigationView.setCheckedItem(R.id.nav_inbox);

            TextView navUsername = headerView.findViewById(R.id.main_activity_username);
            TextView navEmail = headerView.findViewById(R.id.main_activity_email);
            navUsername.setText(defaultMailbox.displayName);
            navEmail.setText(defaultMailbox.email);
        }
    }

    private void startSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
