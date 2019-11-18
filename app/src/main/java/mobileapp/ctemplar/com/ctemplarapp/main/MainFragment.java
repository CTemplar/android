package mobileapp.ctemplar.com.ctemplarapp.main;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.contacts.ContactFragment;
import mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames;
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

    private void showFragment(Fragment fragment) {
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

    void showFragmentByFolder(String folder) {
        if (folder == null) {
            return;
        }
        Fragment currentFragment = getCurrentFragment();
        if (MainFolderNames.CONTACT.equals(folder)) {
            if (!(currentFragment instanceof ContactFragment)) {
                showFragment(contactFragment);
            }
        } else {
            if (!(currentFragment instanceof InboxFragment)) {
                showFragment(inboxFragment);
            }
        }
    }

    void clearListAdapter() {
        inboxFragment.clearListAdapter();
    }
}
