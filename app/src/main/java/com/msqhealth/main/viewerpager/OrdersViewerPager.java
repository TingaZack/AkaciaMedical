package com.msqhealth.main.viewerpager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.msqhealth.main.fragments.PendingOrdersFragment;
import com.msqhealth.main.fragments.PreviousOrdersFragment;

/**
 * Created by Admin on 19/03/2018.
 */

public class OrdersViewerPager extends FragmentPagerAdapter {
    private String tabTitles[] = new String[] { "Pending Orders", "Order History" };
    Context context;

    public OrdersViewerPager(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {

        if(position == 0)
            return new PendingOrdersFragment();
        else
            return new PreviousOrdersFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
