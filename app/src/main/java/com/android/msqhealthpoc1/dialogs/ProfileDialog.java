package com.android.msqhealthpoc1.dialogs;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.msqhealthpoc1.R;
import com.android.msqhealthpoc1.activities.EditProfileActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileDialog extends DialogFragment {


    CircleImageView mDisplayPicture;
    TextView mUsername, mOccupation, mLocation;

    FloatingActionButton btnFabEdit;

    private DatabaseReference mDatabase;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog view = super.onCreateDialog(savedInstanceState);
        view.requestWindowFeature(Window.FEATURE_NO_TITLE);
        view.setContentView(R.layout.fragment_profile_dialog);

        Window window = view.getWindow();
        WindowManager.LayoutParams dlp = window.getAttributes();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dlp.gravity = Gravity.CENTER;
        window.setAttributes(dlp);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDisplayPicture = (CircleImageView) view.findViewById(R.id.display_picture);

        mUsername = (TextView) view.findViewById(R.id.name);
        mOccupation = (TextView) view.findViewById(R.id.occupation);
        mLocation = (TextView) view.findViewById(R.id.location);

        btnFabEdit = (FloatingActionButton) view.findViewById(R.id.edit_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            mUsername.setText("Sihle Mabaleka");
//            Toast.makeText(getActivity(), user.getPhotoUrl().toString(), Toast.LENGTH_LONG).show();
            Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/msq-health.appspot.com/o/images%2Fcropped652655456.jpg?alt=media&token=8197e4e7-cb2d-4767-a581-5497a6ab1a3c").into(mDisplayPicture);

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();

            mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mOccupation.setText(dataSnapshot.child("occupation").getValue().toString());
                    mLocation.setText(dataSnapshot.child("location").getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            btnFabEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().startActivity(new Intent(getActivity(), EditProfileActivity.class));
                }
            });
        }


        return view;

    }
}
