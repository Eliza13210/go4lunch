package com.oc.liza.go4lunch.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> listFragments;
    private static int fragmentCount = 3;

    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> listFragments) {
        super(fm);
        this.listFragments = listFragments;
    }

    @Override
    public Fragment getItem(int i) {
        return this.listFragments.get(i);
    }


    @Override
    public int getCount() {
        return fragmentCount;
    }
}
