package mobileapp.ctemplar.com.ctemplarapp.login.step;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SignUpFragmentsAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> list;
    public SignUpFragmentsAdapter(FragmentManager fm, List<Fragment> listFragments) {
        super(fm);
        this.list = listFragments;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }
}
