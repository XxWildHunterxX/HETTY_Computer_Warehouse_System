package com.junhao.hetty_computer_warehouse_system.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.junhao.hetty_computer_warehouse_system.ui.main.TrackingFirstFragment;
import com.junhao.hetty_computer_warehouse_system.ui.main.TrackingFourthFragment;
import com.junhao.hetty_computer_warehouse_system.ui.main.TrackingSecondFragment;
import com.junhao.hetty_computer_warehouse_system.ui.main.TrackingThirdFragment;

public class TrackingTabPageAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public TrackingTabPageAdapter(@NonNull Context context, FragmentManager fm, int totalTabs) {
        super(fm, totalTabs);
        myContext = context;
        this.totalTabs = totalTabs;

    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                TrackingFirstFragment firstFragment = new TrackingFirstFragment();
                return firstFragment;
            case 1:
                TrackingSecondFragment secondFragment = new TrackingSecondFragment();
                return secondFragment;
            case 2:
                TrackingThirdFragment thirdFragment = new TrackingThirdFragment();
                return thirdFragment;
            case 3:
                TrackingFourthFragment fourthFragment = new TrackingFourthFragment();
                return fourthFragment;
            default:
                return null;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}
