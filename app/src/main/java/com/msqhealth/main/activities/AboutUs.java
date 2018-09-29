package com.msqhealth.main.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msqhealth.main.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AboutUs extends AppCompatActivity /*implements View.OnClickListener*/ {

    TextView tvTnC;
    FloatingActionButton fab;
    private Menu menu;

    private DatabaseReference mUsersDatabaseReference, mMessagesDatabaseReference;

    private String user_name, user_occupation, user_address, telephone;

    private Button mSendMessageButton;
    private EditText mMessageEditText;

    private ProgressDialog mProgressDialog;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String termsLink = "";
    private String akaciaAboutLink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us_layout);

        termsLink = "https://msq-health.firebaseapp.com";
        akaciaAboutLink = getApplicationContext().getString(R.string.akacia_website);

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showContactUsDialog();
            }
        });

        Button TsButton = findViewById(R.id.read_more_btn);
        TsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Uri uri = Uri.parse(getString(R.string.akacia_website));
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);

                Intent termsIntent = new Intent(getApplicationContext(), TermsAndCondtionsActivity.class);
                termsIntent.putExtra("link", akaciaAboutLink);
                startActivity(new Intent(termsIntent));
            }
        });

        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    showOption(R.id.action_info);
                } else if (isShow) {
                    isShow = false;
                    hideOption(R.id.action_info);
                }
            }
        });
//        fab = findViewById(R.id.about_fab);
//        tvTnC = findViewById(R.id.tv_tnc);
//        fab.setOnClickListener(this);
//        tvTnC.setOnClickListener(this);
        initialiseMsgDb();
    }

    public void initialiseMsgDb() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            System.out.println("UID: " + mUser.getUid());
            mUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(mUser.getUid());
            mUsersDatabaseReference.keepSynced(true);
            mMessagesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("messages");
            mMessagesDatabaseReference.keepSynced(true);

            mProgressDialog = new ProgressDialog(this);

            mUsersDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user_name = (String) dataSnapshot.child("Name").getValue();
                    user_occupation = (String) dataSnapshot.child("Speciality").getValue();
                    user_address = (String) dataSnapshot.child("Suburb").getValue();
                    telephone = (String) dataSnapshot.child("Telephone").getValue();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    databaseError.getMessage();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        hideOption(R.id.action_info);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_ts) {
            Intent termsIntent = new Intent(getApplicationContext(), TermsAndCondtionsActivity.class);
            termsIntent.putExtra("link", termsLink);
            startActivity(new Intent(termsIntent));
            return true;
        } else if (id == R.id.action_info) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    public void showContactUsDialog() {
        View mView = LayoutInflater.from(AboutUs.this).inflate(R.layout.contact_pop_up, null);

        Button mSendMsgButton;
        TextView mSubTextView, mContentTextView;

        final android.support.v7.app.AlertDialog.Builder aBuilder = new android.support.v7.app.AlertDialog.Builder(AboutUs.this, R.style.CustomDialog);
        aBuilder.setView(mView);

        mSendMsgButton = (Button) mView.findViewById(R.id.send_msg_btn);
        mMessageEditText = mView.findViewById(R.id.et_message);

        mSendMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String message_edittext = mMessageEditText.getText().toString().trim();

                if (!TextUtils.isEmpty(message_edittext)) {

                    mProgressDialog.setMessage("Sending Message ...");
                    mProgressDialog.show();

                    DatabaseReference mReference = mMessagesDatabaseReference.push();
                    mReference.child("user_name").setValue(user_name);
                    mReference.child("user_email").setValue(mUser.getEmail());
                    mReference.child("user_location").setValue(user_address);
                    mReference.child("user_occupation").setValue(user_occupation);
                    mReference.child("telephone").setValue(telephone);
                    mReference.child("timeStamp").setValue(getCurrentTimeStamp());
                    mReference.child("uid").setValue(mUser.getUid());
                    mReference.child("message").setValue(message_edittext).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mProgressDialog.dismiss();

                                final View mView = LayoutInflater.from(AboutUs.this).inflate(R.layout.check_dialog_box, null);

                                Button mOkayButton;
                                mOkayButton = (Button) mView.findViewById(R.id.dialogDone);

                                mOkayButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        finish();
                                    }
                                });
                                android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(AboutUs.this, R.style.CustomDialog);
                                mBuilder.setView(mView);
                                mBuilder.setCancelable(false);

                                android.support.v7.app.AlertDialog alert = mBuilder.create();
                                alert.show();
                            }
                        }
                    });
                } else {
                    mMessageEditText.setError("This field is mandatory");
                }
            }
        });
        android.support.v7.app.AlertDialog alert = aBuilder.create();
        alert.show();
    }

    //Check the availability if the network
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private Long getCurrentTimeStamp() {
        Long timestamp = System.currentTimeMillis() / 1000;
        return timestamp;
    }


}
