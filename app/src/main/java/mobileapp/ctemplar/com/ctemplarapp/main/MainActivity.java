package mobileapp.ctemplar.com.ctemplarapp.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.ActivityInterface;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.folders.ManageFoldersActivity;
import mobileapp.ctemplar.com.ctemplarapp.login.LoginActivity;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.UnreadFoldersListResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.settings.SettingsActivity;
import mobileapp.ctemplar.com.ctemplarapp.view.ResizeAnimation;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int CUSTOM_FOLDER_STEP = 10;
    private static final int DRAWER_MAX_WIDTH = 640;
    private static final int DRAWER_MIN_WIDTH = 110;


    private FrameLayout contentContainer;
    private NavigationView navigationView;

    private MainActivityViewModel mainModel;
    private List<FoldersResult> customFoldersList;
    private List<FoldersResult> customFoldersListAll = new ArrayList<>();
    private String toggleFolder;
    private int customFoldersShowCount = 3;
    private boolean isTablet;

    private MainFragment mainFragment;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contentContainer = findViewById(R.id.content_container);
        navigationView = findViewById(R.id.nav_view);

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


        mainModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

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
                loadFolders();
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

        mainModel.getUnreadFoldersResponse().observe(this, new Observer<UnreadFoldersListResponse>() {
            @Override
            public void onChanged(@Nullable UnreadFoldersListResponse unreadFoldersListResponse) {
                if (unreadFoldersListResponse != null) {
                    Menu navigationMenu = navigationView.getMenu();
                    TextView inboxCounter = (TextView) navigationMenu.findItem(R.id.nav_inbox).getActionView();
                    TextView outboxCounter = (TextView) navigationMenu.findItem(R.id.nav_outbox).getActionView();
                    TextView spamCounter = (TextView) navigationMenu.findItem(R.id.nav_spam).getActionView();

                    int inboxUnread = unreadFoldersListResponse.getInbox();
                    int outboxDelayed = unreadFoldersListResponse.getOutboxDelayedDeliveryCounter();
                    int spam = unreadFoldersListResponse.getSpam();

                    if (inboxUnread > 0) {
                        inboxCounter.setText(String.valueOf(inboxUnread));
                    } else {
                        inboxCounter.setText(null);
                    }
                    if (outboxDelayed > 0) {
                        outboxCounter.setText(String.valueOf(outboxDelayed));
                    } else {
                        outboxCounter.setText(null);
                    }
                    if (spam > 0) {
                        spamCounter.setText(String.valueOf(spam));
                    } else {
                        spamCounter.setText(null);
                    }
                }
            }
        });

        loadFolders();

        // default folder
        setTitle("Inbox");
        mainModel.setCurrentFolder("inbox");

        loadUserInfo();
    }

    public void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(contentContainer.getId(), fragment)
                .addToBackStack(null)
                .commit();
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
                    (int) folderItem.getId(), (int) folderItem.getId(), folderItem.getName());
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!isHandledPressBack(getCurrentFragment())) {
                super.onBackPressed();
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    finish();
                }
            }
        }
    }

    public boolean isHandledPressBack(Fragment fragment) {
        return fragment instanceof ActivityInterface && !((ActivityInterface) fragment).onBackPressed();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!(getCurrentFragment() instanceof MainFragment)) {
            return false;
        }

        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
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

    private Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        return fragmentManager.findFragmentById(contentContainer.getId());
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
            Intent settingsScreen = new Intent(this, SettingsActivity.class);
            startActivity(settingsScreen);
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

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });
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
    protected void onResume() {
        super.onResume();
        loadFolders();
    }

    private void loadFolders() {
        mainModel.getFolders(customFoldersShowCount, 0);
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
        drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void setupTabletDrawer() {
        if (isPortrait()) {
            navigationView.setTag(DRAWER_MAX_WIDTH);
            ViewGroup.LayoutParams layoutParams = navigationView.getLayoutParams();
            layoutParams.width = DRAWER_MIN_WIDTH;
            navigationView.requestLayout();
        }
    }

    private void closeOpenNavigationView() {
        int toWidth = DRAWER_MIN_WIDTH;
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
                showMailboxDetailsInNavigationDrawer();
                break;
        }
    }

    private void showMailboxDetailsInNavigationDrawer() {
        MailboxEntity defaultMailbox = MessageProvider.getDefaultMailbox();
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
        startActivity(intent);
        finish();
    }
}