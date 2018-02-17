package com.android.msqhealthpoc1.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.msqhealthpoc1.R;
import com.android.msqhealthpoc1.fragments.ProductFragment;
import com.android.msqhealthpoc1.fragments.profile.create.LocationFragment;
import com.android.msqhealthpoc1.fragments.profile.create.PracticeConfirmation;
import com.android.msqhealthpoc1.fragments.profile.create.ProfileCompleteFragment;
import com.android.msqhealthpoc1.fragments.profile.create.ProfileOnboardingExplainerFragment;
import com.android.msqhealthpoc1.fragments.profile.create.ProfilePictureNameFragment;

/**
 * Created by sihlemabaleka on 7/9/17.
 */

public class WelcomeActivity extends AppCompatActivity {


    private ViewPager viewPager;
    private SectionsPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);


        layouts = new int[]{0, 1, 2, 3, 4};

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);


        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myViewPagerAdapter);

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[5];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }


    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return new ProfileOnboardingExplainerFragment();
                case 1:
                    return new ProfilePictureNameFragment();
                case 2:
                    return new LocationFragment();
                case 3:
                    return new PracticeConfirmation();
                case 4:
                    return new ProfileCompleteFragment();
                default:
                    return new ProductFragment();
            }

        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 5;
        }

    }

    public void moveToNext() {
        int current = getItem(+1);
        if (current < layouts.length) {
            // move to next screen
            viewPager.setCurrentItem(current);
            ((TextView) findViewById(R.id.screen_count)).setText((current + 1) + " of 5");
            addBottomDots(current);
        }
    }


}
