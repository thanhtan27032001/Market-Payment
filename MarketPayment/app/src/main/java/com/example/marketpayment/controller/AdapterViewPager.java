package com.example.marketpayment.controller;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.marketpayment.view.fragment.CalculatorFragment;
import com.example.marketpayment.view.fragment.HistoryFragment;
import com.example.marketpayment.view.fragment.HomeFragment;

public class AdapterViewPager extends FragmentStatePagerAdapter {
    private final String[] tabTitle = {"Home", "History", "Calculator", "Setting"};
    private final Fragment[] listFragment;

    public AdapterViewPager(@NonNull FragmentManager fm, Fragment[] listFragment) {
        super(fm);
        this.listFragment = listFragment;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return listFragment[0];
            case 1:
                return listFragment[1];
            case 2:
                return listFragment[2];
            case 3:
                return listFragment[3];
        }
        return listFragment[0];
    }

//    @Override
//    public int getItemPosition(@NonNull Object object) {
//        return POSITION_NONE;
//    }

    @Override
    public int getCount() {
        return listFragment.length;
    }

//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return tabTitle[position];
//    }
}
