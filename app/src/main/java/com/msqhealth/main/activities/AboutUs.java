package com.msqhealth.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.msqhealth.main.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AboutUs extends AppCompatActivity implements View.OnClickListener {

    TextView tvTnC;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        fab = findViewById(R.id.about_fab);
        tvTnC = findViewById(R.id.tv_tnc);
        fab.setOnClickListener(this);
        tvTnC.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.about_fab:
                startActivity(new Intent(this, ContactUsActivity.class));
                break;
            case R.id.tv_tnc:
                startActivity(new Intent(this, TermsAndCondtionsActivity.class));
                break;
        }
    }
}
