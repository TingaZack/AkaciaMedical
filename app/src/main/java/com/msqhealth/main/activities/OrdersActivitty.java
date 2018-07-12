package com.msqhealth.main.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.msqhealth.main.R;
import com.msqhealth.main.viewerpager.OrdersViewerPager;

public class OrdersActivitty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_activitty);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager_cal);
        viewPager.setAdapter(new OrdersViewerPager(getSupportFragmentManager(), OrdersActivitty.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout_calendar);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(android.R.color.white));

    }
}
