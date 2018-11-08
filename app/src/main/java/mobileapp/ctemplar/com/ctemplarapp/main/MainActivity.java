package mobileapp.ctemplar.com.ctemplarapp.main;

import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.BindView;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.DialogState;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.login.LoginActivity;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import timber.log.Timber;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.content_frame)
    FrameLayout mContentFrame;

    @BindView(R.id.progress_bar)
    public ProgressBar progress;

    @BindView(R.id.progress_background)
    public View progressBackground;

    private int mLastSelectedId;

    private MainActivityViewModel mainModel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        ActionBar bar = getSupportActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeAsUpIndicator(R.drawable.ic_drawer_menu);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            Fragment startFragment;

            Timber.i("Standard startup");
            startFragment = new InboxFragment();
            setCheckedItem(R.id.nav_inbox);

            ft.add(mContentFrame.getId(), startFragment);
            ft.commit();
        }

        mainModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        mainModel.getActionsStatus().observe(this, new Observer<MainActivityActions>() {
            @Override
            public void onChanged(@Nullable MainActivityActions mainActivityActions) {
                handleMainActions(mainActivityActions);
            }
        });

        mainModel.getResponseStatus().observe(this, new Observer<ResponseStatus>() {
            @Override
            public void onChanged(@Nullable ResponseStatus status) {
                handleResponseStatus(status);
            }
        });

        // default folder
        mainModel.setCurrentFolder("inbox");
        loadUserInfo();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
//        MenuItem searchItem = menu.getItem(R.id.action_search);
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = null;
//        if(searchItem != null) {
//            searchView = (SearchView) searchItem.getActionView();
//        }
//        if(searchView != null) {
//            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {
            return true;
        }

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int id = item.getItemId();

        if (id == R.id.nav_inbox) {
            setTitle(R.string.nav_drawer_inbox);
            mainModel.setCurrentFolder("inbox");
        } else if (id == R.id.nav_draft) {
            setTitle(R.string.nav_drawer_draft);
            mainModel.setCurrentFolder("draft");
        } else if (id == R.id.nav_sent) {
            setTitle(R.string.nav_drawer_sent);
            mainModel.setCurrentFolder("sent");
        } else if (id == R.id.nav_starred) {
            setTitle(R.string.nav_drawer_starred);
            mainModel.setCurrentFolder("starred");
        } else if (id == R.id.nav_logout) {
            mainModel.logout();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        // getCurrentFragment().popChildrenWithFade();
        FragmentTransaction ft = manager.beginTransaction();
        // ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.replace(mContentFrame.getId(), fragment);
        ft.commit();
    }

    private Fragment getCurrentFragment() {
        return (Fragment) getSupportFragmentManager().findFragmentById(mContentFrame.getId());
    }

    private void setCheckedItem(int itemId) {
        mLastSelectedId = itemId;
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(itemId);
    }

    private void selectNavigationItem(int itemId) {
        if (mLastSelectedId != itemId) {
            mLastSelectedId = itemId;
        }
    }

    public void blockUI() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void unlockUI() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void blockNavigation() {
        blockDrawer(true);
    }

    public void unlockNavigation() {
        blockDrawer(false);
    }

    private void blockDrawer(boolean state) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(state?DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private void handleDialogState(DialogState state) {
        if(state != null) {
            switch (state) {
                case SHOW_PROGRESS_DIALOG:
                    progress.setVisibility(View.VISIBLE);
                    progressBackground.setVisibility(View.VISIBLE);
                    blockUI();
                    break;
                case HIDE_PROGRESS_DIALOG:
                    progress.setVisibility(View.GONE);
                    progressBackground.setVisibility(View.GONE);
                    unlockUI();
                    break;
            }
        }
    }

    private void loadUserInfo() {
        mainModel.getMailboxes(20, 0);
    }

    private void handleMainActions(MainActivityActions actions) {
        switch (actions) {
            case ACTION_LOGOUT:
                startSignInActivity();
                break;
        }
    }

    private void handleResponseStatus(ResponseStatus status) {
        switch (status) {
            case RESPONSE_NEXT_MAILBOXES:
                // mainModel.getMessages(20, 0, "inbox");
                break;
        }
    }

    private void startSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
