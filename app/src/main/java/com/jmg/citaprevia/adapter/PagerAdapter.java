package com.jmg.citaprevia.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.jmg.citaprevia.fragment.CalendarDayFragment;


public class PagerAdapter extends FragmentStateAdapter {

    int tabNumber;

    public PagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment res = null;
        res = new CalendarDayFragment();
        return res;
    }


    @Override
    public int getItemCount() {
        return 0;
    }
}
