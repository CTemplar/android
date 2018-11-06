package mobileapp.ctemplar.com.ctemplarapp.login.step;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

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

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }
}
