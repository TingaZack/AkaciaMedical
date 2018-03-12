package com.android.msqhealthpoc1.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.msqhealthpoc1.R;
import com.android.msqhealthpoc1.fragments.FeaturedFragment;

/**
 * Created by sihlemabaleka on 3/12/18.
 */

public class PublicationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_layout);
        getSupportFragmentManager().beginTransaction().add(R.id.container, new FeaturedFragment()).commit();
    }
}
