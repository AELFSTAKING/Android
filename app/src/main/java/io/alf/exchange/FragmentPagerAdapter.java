package io.alf.exchange;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class FragmentPagerAdapter extends FragmentStatePagerAdapter {

    private List<String> mTitles;
    private List<Fragment> mFragments;

    public FragmentPagerAdapter(@NonNull FragmentManager fm, List<String> titles, List<Fragment> fragments) {
        super(fm);
        mTitles = titles;
        mFragments = fragments;
    }

    @Override
    public int getCount() {
        return (mFragments != null && mFragments.size() > 0) ? mFragments.size() : 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (mTitles != null && mTitles.size() > 0) ? mTitles.get(position) : "";
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return (mFragments != null && mFragments.size() > 0) ? mFragments.get(position) : null;
    }
}
