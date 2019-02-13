package mobileapp.ctemplar.com.ctemplarapp.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.contacts.ContactFragment;
import timber.log.Timber;

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    private int contentLayoutId = R.id.content_frame;

    private InboxFragment inboxFragment;
    private ContactFragment contactFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity == null) {
            Timber.tag(TAG).e("Activity is null");
            return;
        }

        inboxFragment = new InboxFragment();
        contactFragment = new ContactFragment();

        Toolbar toolbar = view.findViewById(R.id.main_toolbar);
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer_menu);
        }

        showFragment(inboxFragment);
    }

    public void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager == null) {
            Timber.tag(TAG).e("FragmentManager is null");
            return;
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(contentLayoutId, fragment);
        ft.commit();
    }

    private Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager == null) {
            Timber.tag(TAG).e("FragmentManager is null");
            return null;
        }
        return fragmentManager.findFragmentById(contentLayoutId);
    }

    public void showFragmentByFolder(String folder) {
        if (folder == null) {
            return;
        }
        Fragment currentFragment = getCurrentFragment();
        switch (folder) {
            case "contact":
                if (!(currentFragment instanceof ContactFragment)) {
                    showFragment(contactFragment);
                }
                break;
            default:
                if (!(currentFragment instanceof InboxFragment)) {
                    showFragment(inboxFragment);
                }
                break;
        }
    }

    public void clearListAdapter() {
        inboxFragment.clearListAdapter();
    }
}
