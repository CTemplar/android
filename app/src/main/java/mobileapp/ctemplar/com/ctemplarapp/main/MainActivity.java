package mobileapp.ctemplar.com.ctemplarapp.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.contact.ContactFragment;
import mobileapp.ctemplar.com.ctemplarapp.folders.ManageFoldersActivity;
import mobileapp.ctemplar.com.ctemplarapp.login.LoginActivity;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.settings.SettingsActivity;
import timber.log.Timber;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.content_frame)
    FrameLayout mContentFrame;

    @BindView(R.id.progress_bar)
    public ProgressBar progress;

    @BindView(R.id.progress_background)
    public View progressBackground;

    private final int CUSTOM_FOLDER_STEP = 10;
    private MainActivityViewModel mainModel;
    private MailboxEntity defaultMailbox;
    private String toggleFolder;
    private List<FoldersResult> customFoldersList;
    private List<FoldersResult> customFoldersListAll = new ArrayList<>();
    private int customFoldersShowCount = 3;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeAsUpIndicator(R.drawable.ic_drawer_menu);
        }

        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.main_activity_username);
        TextView navEmail = headerView.findViewById(R.id.main_activity_email);

        if (savedInstanceState == null) {
            defaultMailbox = CTemplarApp.getAppDatabase().mailboxDao().getDefault();
            if (defaultMailbox != null) {
                Timber.i("Standard startup");
                setCheckedItem(R.id.nav_inbox);
                showFragment(new InboxFragment());
                navUsername.setText(defaultMailbox.displayName);
                navEmail.setText(defaultMailbox.email);
            }
        }

        mainModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        loadCustomFolders();
        mainModel.getActionsStatus().observe(this, new Observer<MainActivityActions>() {
            @Override
            public void onChanged(@Nullable MainActivityActions mainActivityActions) {
                handleMainActions(mainActivityActions);
            }
        });

        mainModel.getCurrentFolder().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String folder) {
                showFragmentByFolder(folder);
            }
        });

        mainModel.getResponseStatus().observe(this, new Observer<ResponseStatus>() {
            @Override
            public void onChanged(@Nullable ResponseStatus status) {
                handleResponseStatus(status);
            }
        });

        mainModel.getFoldersResponse().observe(this, new Observer<FoldersResponse>() {
            @Override
            public void onChanged(@Nullable FoldersResponse foldersResponse) {
                if (foldersResponse != null) {
                    handleFoldersResponse(navigationView, foldersResponse);
                }
            }
        });

        // default folder
        mainModel.setCurrentFolder("inbox");
        setTitle("Inbox");
        loadUserInfo();
    }

    private void handleFoldersResponse(NavigationView navigationView, FoldersResponse foldersResponse) {
        customFoldersList = foldersResponse.getFoldersList();
        int customFoldersCount = foldersResponse.getTotalCount();
        Menu navigationMenu = navigationView.getMenu();

        List<FoldersResult> foldersListForDeleting = customFoldersListAll;
        for (FoldersResult folderItem :
                customFoldersListAll) {
            navigationMenu.removeItem((int) folderItem.getId());
        }
        customFoldersListAll.removeAll(foldersListForDeleting);

        MenuItem manageFolders = navigationMenu.findItem(R.id.nav_manage_folders);
        String manageFoldersTitle = getResources().getString(R.string.nav_drawer_manage_folders_param, customFoldersCount);
        manageFolders.setTitle(manageFoldersTitle);

        for (FoldersResult folderItem :
                customFoldersList) {
            customFoldersListAll.add(folderItem);
            MenuItem menuItem = navigationMenu.add(R.id.activity_main_drawer_folders,
                    (int)folderItem.getId(), (int)folderItem.getId(), folderItem.getName());
            menuItem.setCheckable(true);
            menuItem.setIcon(R.drawable.ic_manage_folders);
            Drawable itemIcon = menuItem.getIcon();
            itemIcon.mutate();
            int folderColor = Color.parseColor(folderItem.getColor());
            itemIcon.setColorFilter(folderColor, PorterDuff.Mode.SRC_IN);
        }

        MenuItem moreFolders = navigationMenu.findItem(R.id.nav_manage_folders_more);
        if (customFoldersShowCount < customFoldersCount) {
            moreFolders.setVisible(true);
        } else {
            moreFolders.setVisible(false);
        }
    }

    private InboxFragment inboxFragment;

    private InboxFragment getInboxFragment() {
        if (inboxFragment == null) {
            inboxFragment = new InboxFragment();
        }
        return inboxFragment;
    }

    private ContactFragment contactFragment;

    private ContactFragment getContactFragment() {
        if (contactFragment == null) {
            contactFragment = new ContactFragment();
        }
        return contactFragment;
    }

    private void showFragmentByFolder(String folder) {
        if (folder == null) {
            return;
        }
        Fragment currentFragment = getCurrentFragment();
        switch (folder) {
            case "contact":
                if (!(currentFragment instanceof ContactFragment)) {
                    showFragment(getContactFragment());
                }
                break;
            default:
                if (!(currentFragment instanceof InboxFragment)) {
                    showFragment(getInboxFragment());
                }
        }
    }

    private void showFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(mContentFrame.getId(), fragment);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
            getSupportActionBar().setTitle(title);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        int id = item.getItemId();
        boolean closeDrawer = true;

        if (id == R.id.nav_inbox) {
            setTitle(R.string.nav_drawer_inbox);
            toggleFolder = "inbox";
        } else if (id == R.id.nav_draft) {
            setTitle(R.string.nav_drawer_draft);
            toggleFolder = "draft";
        } else if (id == R.id.nav_sent) {
            setTitle(R.string.nav_drawer_sent);
            toggleFolder = "sent";
        } else if (id == R.id.nav_outbox) {
            setTitle(R.string.nav_drawer_outbox);
            toggleFolder = "outbox";
        } else if (id == R.id.nav_starred) {
            setTitle(R.string.nav_drawer_starred);
            toggleFolder = "starred";
        } else if (id == R.id.nav_archive) {
            setTitle(R.string.nav_drawer_archive);
            toggleFolder = "archive";
        } else if (id == R.id.nav_spam) {
            setTitle(R.string.nav_drawer_spam);
            toggleFolder = "spam";
        } else if (id == R.id.nav_trash) {
            setTitle(R.string.nav_drawer_trash);
            toggleFolder = "trash";
        } else if (id == R.id.nav_contact) {
            setTitle(R.string.nav_drawer_contact);
            toggleFolder = "contact";
        } else if (id == R.id.nav_settings) {
            Intent settingsScreeen = new Intent(this, SettingsActivity.class);
            startActivity(settingsScreeen);
        } else if (id == R.id.nav_logout) {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.dialog_log_out))
                    .setMessage(getResources().getString(R.string.dialog_log_out_confirm))
                    .setPositiveButton(getResources().getString(R.string.btn_log_out), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mainModel.logout();
                                }
                            }
                    )
                    .setNeutralButton(R.string.btn_cancel, null)
                    .show();
        } else if (id == R.id.nav_manage_folders) {
            Intent manageFolders = new Intent(this, ManageFoldersActivity.class);
            startActivity(manageFolders);
        } else if (id == R.id.nav_manage_folders_more) {
            customFoldersShowCount += CUSTOM_FOLDER_STEP;
            loadCustomFolders();
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

        if (closeDrawer) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(mContentFrame.getId());
    }

    private void setCheckedItem(int itemId) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(itemId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCustomFolders();
    }

    private void loadCustomFolders() {
        mainModel.getFolders(customFoldersShowCount, 0);
    }

    //    public void blockUI() {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//    }
//
//    public void unlockUI() {
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//    }

//    public void blockNavigation() {
//        blockDrawer(true);
//    }
//
//    public void unlockNavigation() {
//        blockDrawer(false);
//    }
//
//    private void blockDrawer(boolean state) {
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        drawer.setDrawerLockMode(state?DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
//    }

//    private void handleDialogState(DialogState state) {
//        if(state != null) {
//            switch (state) {
//                case SHOW_PROGRESS_DIALOG:
//                    progress.setVisibility(View.VISIBLE);
//                    progressBackground.setVisibility(View.VISIBLE);
//                    blockUI();
//                    break;
//                case HIDE_PROGRESS_DIALOG:
//                    progress.setVisibility(View.GONE);
//                    progressBackground.setVisibility(View.GONE);
//                    unlockUI();
//                    break;
//            }
//        }
//    }

    private void loadUserInfo() {
        mainModel.getMailboxes(20, 0);
    }

    private void handleMainActions(MainActivityActions actions) {
        if (actions == null) {
            return;
        }
        switch (actions) {
            case ACTION_LOGOUT:
                startSignInActivity();
                break;
        }
    }

    private void handleResponseStatus(ResponseStatus status) {
        if (status == null) {
            return;
        }
        switch (status) {
            case RESPONSE_NEXT_MAILBOXES:
                if (defaultMailbox == null) {
                    Timber.i("Standard startup");
                    setCheckedItem(R.id.nav_inbox);
                    showFragment(new InboxFragment());
                }
                break;
        }
    }

    private void startSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
