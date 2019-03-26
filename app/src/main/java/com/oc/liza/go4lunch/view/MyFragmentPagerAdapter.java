package com.oc.liza.go4lunch.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.oc.liza.go4lunch.controllers.fragments.ListFragment;
import com.oc.liza.go4lunch.controllers.fragments.MapFragment;
import com.oc.liza.go4lunch.controllers.fragments.UsersFragment;

import java.util.List;
import java.util.Map;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private Fragment mCurrentFragment;
    private List<Fragment> listFragments;
    static int fragmentCount = 3;

    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> listFragments) {
        super(fm);
        this.listFragments = listFragments;
    }

    @Override
    public Fragment getItem(int i) {
        return this.listFragments.get(i);
    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return fragmentCount;
    }
}
