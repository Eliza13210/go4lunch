package com.oc.liza.go4lunch.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.oc.liza.go4lunch.controllers.fragments.ListFragment;
import com.oc.liza.go4lunch.controllers.fragments.MapFragment;
import com.oc.liza.go4lunch.controllers.fragments.UsersFragment;

import java.util.Map;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    static int fragmentCount=3;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                Log.e("adapter", "map instance");
                return MapFragment.newInstance();
            case 1:
                Log.e("adapter", "listfragment instance"+i);
                return ListFragment.newInstance();
            case 2:
                Log.e("adapter", "user instance");
                return UsersFragment.newInstance();
            default:
                Log.e("adapter", "default instance");
                return MapFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return fragmentCount;
    }
}
