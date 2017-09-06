package com.android.msqhealthpoc1.fragments.profile.create;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.android.msqhealthpoc1.R;
import com.android.msqhealthpoc1.activities.WelcomeActivity;
import com.android.msqhealthpoc1.model.UserInformation;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfilePictureNameFragment extends Fragment {

    CircleImageView mDisplayPictures;
    private Button btnSelectImage, btnSaveImage;
    private EditText mUsername, mOccupation;
    private DatabaseReference mDatabase;

    ProgressDialog progressDialog;

    public ProfilePictureNameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_picture_name, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDisplayPictures = (CircleImageView) view.findViewById(R.id.display_picture);
        btnSelectImage = (Button) view.findViewById(R.id.select_image);
        mUsername = (EditText) view.findViewById(R.id.name);
        mOccupation = (EditText) view.findViewById(R.id.occupation);
        btnSaveImage = (Button) view.findViewById(R.id.save);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setMessage("Creating profile...");
        progressDialog.setCancelable(false);

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        return view;
    }


    public void selectImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setFixAspectRatio(true)
                .setAspectRatio(1, 1)
                .start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final Uri resultUri = result.getUri();
                Glide.with(getActivity()).load(resultUri).into(mDisplayPictures);
                btnSaveImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mUsername.getText().toString().trim() == "") {
                            mUsername.setError("Required");
                            return;
                        }
                        if (mOccupation.getText().toString().trim() == "") {
                            mOccupation.setError("Required");
                            return;
                        }

                        progressDialog.show();

                        FirebaseStorage storage = FirebaseStorage.getInstance();

                        // Create a storage reference from our app
                        StorageReference storageRef = storage.getReference();


                        StorageReference riversRef = storageRef.child("images/" + resultUri.getLastPathSegment());
                        UploadTask uploadTask = riversRef.putFile(resultUri);

                        // Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads

                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(mUsername.getText().toString())
                                        .setPhotoUri(taskSnapshot.getMetadata().getDownloadUrl())
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    UserInformation userInfo = new UserInformation(mOccupation.getText().toString().trim(), mUsername.getText().toString());
                                                    mDatabase.child("users").child(user.getUid()).child("name").setValue(mUsername.getText().toString());
                                                    mDatabase.child("users").child(user.getUid()).setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                progressDialog.dismiss();

                                                                ((WelcomeActivity) getActivity()).moveToNext();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });

                            }
                        });

                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                error.printStackTrace();
            }
        }
    }

}
