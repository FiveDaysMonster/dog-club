package com.example.lcd3;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class fa extends FragmentStatePagerAdapter {

    //2.宣告變數為mFragments
    private  Fragment[] mFragments;

    //3.初始化
    public fa(FragmentManager fm, Fragment[] fragments) {
        super(fm);
        mFragments = fragments;
    }

    //4.分頁內容
    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    //5.分頁數量
    @Override
    public int getCount() {
        return mFragments.length;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return mFragments[position];
    }
}
