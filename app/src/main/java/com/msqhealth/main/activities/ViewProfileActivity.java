package com.msqhealth.main.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.msqhealth.main.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileActivity extends Activity {

    CircleImageView mDisplayPicture;
    EditText mUsername, mOccupation, mEmail;
    Button btnSelectImage;
    FloatingActionButton btnFabEdit;
    private DatabaseReference mDatabase;

    Boolean isEdited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        mDisplayPicture = (CircleImageView) findViewById(R.id.display_picture);

        mUsername = (EditText) findViewById(R.id.name);
        mOccupation = (EditText) findViewById(R.id.occupation);
        mEmail = (EditText) findViewById(R.id.email);
        btnFabEdit = (FloatingActionButton) findViewById(R.id.edit_profile);

        btnSelectImage = (Button) findViewById(R.id.select_image);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            mUsername.setText(user.getDisplayName());
            mEmail.setText(user.getEmail());
            Glide.with(this).load(user.getPhotoUrl()).into(mDisplayPicture);
            // Check if user's email is verified
            if (!user.isEmailVerified()) {
                mEmail.setError("Email not verified");
            }

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();


            mDatabase.child("users").child(uid).child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mOccupation.setText(dataSnapshot.child("occupation").getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            btnFabEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isEdited) {
                        isEdited = true;
                        btnFabEdit.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_check_black_24dp));
                        mUsername.setEnabled(true);
                        mEmail.setEnabled(true);
                        mOccupation.setEnabled(true);
                        btnSelectImage.setVisibility(View.VISIBLE);
                    } else {
                        isEdited = false;
                        btnFabEdit.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_mode_edit_black));
                        mUsername.setEnabled(false);
                        mEmail.setEnabled(false);
                        mOccupation.setEnabled(false);
                        btnSelectImage.setVisibility(View.GONE);
                        //Save edits
                    }
                }
            });

        }

    }
}
