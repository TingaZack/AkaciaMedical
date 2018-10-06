package com.msqhealth.main.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.msqhealth.main.R;


public class TermsAndCondtionsActivity extends Activity {
    private WebView wv1;

    private String link;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_terms_and_conditions);

        link = getIntent().getStringExtra("link");
        System.out.println("LINK: " + link);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.dismiss();

        wv1= findViewById(R.id.tc_webview);

        WebSettings webSettings = wv1.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv1.goBack();

        wv1.loadUrl(link);
    }

}
