package mobileapp.ctemplar.com.ctemplarapp.main;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import io.sentry.Sentry;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.contacts.ContactsFragment;
import mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames;
import timber.log.Timber;

public class MainFragment extends Fragment {
    private static final String TAG = MainFragment.class.getSimpleName();
    private final int contentLayoutId = R.id.content_frame;

    private InboxFragment inboxFragment;
    private ContactsFragment contactsFragment;

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
        contactsFragment = new ContactsFragment();

        Toolbar toolbar = view.findViewById(R.id.main_toolbar);
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
                int menuColor = ContextCompat.getColor(activity, R.color.secondaryTextColor);
                Drawable menuDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_drawer_menu);
                if (menuDrawable != null) {
                    DrawableCompat.setTint(menuDrawable, menuColor);
                }
                actionBar.setHomeAsUpIndicator(menuDrawable);
        }
        showFragment(inboxFragment);
    }

    @Nullable
    private FragmentManager getParentFragmentManagerInternal() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return null;
        }
        try {
            return getParentFragmentManager();
        } catch (IllegalStateException e) {
            Sentry.captureException(e, "getParentFragmentManager exception");
            return null;
        }
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManagerInternal();
        if (fragmentManager == null) {
            return;
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(contentLayoutId, fragment);
        ft.commit();
    }

    @Nullable
    private Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getParentFragmentManagerInternal();
        if (fragmentManager == null) {
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
            if (!(currentFragment instanceof ContactsFragment)) {
                showFragment(contactsFragment);
            }
        } else {
            if (!(currentFragment instanceof InboxFragment)) {
                showFragment(inboxFragment);
            }
        }
    }

    boolean isCurrentFragmentIsContact() {
        return getCurrentFragment() instanceof ContactsFragment;
    }

    boolean isCurrentFragmentIsInbox() {
        return getCurrentFragment() instanceof InboxFragment;
    }

    void clearListAdapter() {
        if (inboxFragment == null) {
            return;
        }
        inboxFragment.clearListAdapter();
    }
}
