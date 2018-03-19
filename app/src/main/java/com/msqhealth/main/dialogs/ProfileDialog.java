package com.msqhealth.main.dialogs;


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

import com.msqhealth.main.R;
import com.msqhealth.main.activities.EditProfileActivityUpdated;
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
        btnFabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), EditProfileActivityUpdated.class));
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        mOccupation.setText(dataSnapshot.child("occupation").getValue().toString());
                        mLocation.setText(dataSnapshot.child("location_address").getValue().toString());

                        // Name, email address, and profile photo Url
                        mUsername.setText(dataSnapshot.child("name").getValue().toString());
                        Glide.with(getActivity()).load(dataSnapshot.child("display_picture").getValue().toString()).into(mDisplayPicture);

                        btnFabEdit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getActivity().startActivity(new Intent(getActivity(), EditProfileActivityUpdated.class));
                            }
                        });

                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }


        return view;

    }
}
