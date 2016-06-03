package ch.heigvd.gen.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ch.heigvd.gen.fragments.ContactFragment;
import ch.heigvd.gen.fragments.GroupFragment;

/**
 * Created by guillaume on 03.06.16.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private int tabsNumber;

    public ViewPagerAdapter(FragmentManager fm, int tabsNumber) {
        super(fm);
        this.tabsNumber = tabsNumber;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new GroupFragment();
            default:
                return new ContactFragment();
        }
    }

    @Override
    public int getCount() {
        return tabsNumber;
    }
}
