package com.android.msqhealthpoc1.fragments.profile.create;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.msqhealthpoc1.R;
import com.android.msqhealthpoc1.activities.WelcomeActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private static final int GALLERY_REQUEST = 1;
    CircleImageView mDisplayPictures;
    private Button btnSelectImage, btnSaveImage;
    private EditText mUsername, mOccupation;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    String user_uid;

    private Uri mImageUri;

    ProgressDialog progressDialog;

    public ProfilePictureNameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_picture_name, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth = FirebaseAuth.getInstance();

        user_uid = mAuth.getCurrentUser().getUid();
        System.out.println("USER ID: " + user_uid);

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

        btnSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserDetails();
            }
        });

        return view;
    }


    public void selectImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setFixAspectRatio(true)
                    .setAspectRatio(1, 1)
                    .start(getContext(), this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();

                mDisplayPictures.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    public void saveUserDetails() {

        final String username = mUsername.getText().toString().trim();
        final String occupation = mOccupation.getText().toString().trim();

        if (!TextUtils.isEmpty(username)) {

            if (!TextUtils.isEmpty(occupation)) {

                if (mImageUri == null) {
                    mImageUri = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.drawable.placeholder_img);
                }

                StorageReference mStorageImage = FirebaseStorage.getInstance().getReference();

                progressDialog.setMessage("Creating Profile ...");
                progressDialog.show();

                StorageReference filePath = mStorageImage.child("profile_pictures/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
                filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadedUri = taskSnapshot.getDownloadUrl();
                        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").setValue(username);
                        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("occupation").setValue(occupation);
                        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("display_picture").setValue(downloadedUri.toString());

                        progressDialog.dismiss();
                        ((WelcomeActivity) getActivity()).moveToNext();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.getMessage();
                    }
                });
            } else {
                mOccupation.setError("Field empty");
            }
        } else {
            mUsername.setError("Field empty");
        }
    }
}
