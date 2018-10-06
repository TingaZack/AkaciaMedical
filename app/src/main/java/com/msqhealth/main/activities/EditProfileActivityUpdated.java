package com.msqhealth.main.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.msqhealth.main.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivityUpdated extends AppCompatActivity {


    private static final int GALLERY_REQUEST = 1;
    CircleImageView mDisplayPicture;
    EditText mUsername, mOccupation, mEmail, mLocation;
    Button btnSelectImage, btnSelectSave;
    private DatabaseReference mDatabaseUsers;
    private Uri pictureUri, mImageUri;

    private ProgressDialog progressDialog;

    private String name, user_id, nameString, occupationString, locationString, imageString;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_profile);

        System.out.println("PROFILE EDIT ACTIVITY");

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseUsers.keepSynced(true);

        mDisplayPicture = (CircleImageView) findViewById(R.id.display_picture);

        mUsername = (EditText) findViewById(R.id.name);
        mOccupation = (EditText) findViewById(R.id.occupation);
        mLocation = (EditText) findViewById(R.id.et_location);

        progressDialog = new ProgressDialog(this);

        btnSelectImage = (Button) findViewById(R.id.select_image);
        btnSelectSave = (Button) findViewById(R.id.save);

        user = FirebaseAuth.getInstance().getCurrentUser();

        user_id = user.getUid();

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        if (user_id != null) {

            mDatabaseUsers.child(user_id).child("profile").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    System.out.println("Counting: " + dataSnapshot.getChildrenCount());
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map.get("name") != null) {
                            nameString = map.get("name").toString();
                            mUsername.setText(nameString);
                        }
                        if (map.get("occupation") != null) {
                            occupationString = map.get("occupation").toString();
                            mOccupation.setText(occupationString);
                        }
                        if (map.get("display_picture") != null) {
                            imageString = map.get("display_picture").toString();
                            Glide.with(getApplicationContext()).load(imageString).into(mDisplayPicture);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mDatabaseUsers.child(user_id).child("profile").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    System.out.println("CITY: " + dataSnapshot.child("location").child("city").getValue());
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map.get("location_address") != null) {
                            locationString = map.get("location_address").toString();
                            mLocation.setText(locationString);
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        btnSelectSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNewUpdatedInformation();
            }
        });

    }

    private void saveNewUpdatedInformation() {
        final String username = mUsername.getText().toString().trim();
        final String occupation = mOccupation.getText().toString().trim();
        final String location = mLocation.getText().toString().trim();

        if (!TextUtils.isEmpty(username)) {

            if (!TextUtils.isEmpty(occupation)) {

                if (!TextUtils.isEmpty(location)) {

                    StorageReference mStorageImage = FirebaseStorage.getInstance().getReference();

                    progressDialog.setMessage("Creating Profile ...");
                    progressDialog.show();

                    StorageReference filePath = mStorageImage.child("profile_pictures/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
                    if (mImageUri == null) {
                        System.out.println("Image Uri is emtpy");
                        Map userInfo = new HashMap();
                        userInfo.put("occupation", occupation);
                        userInfo.put("name", username);
                        userInfo.put("location_address", locationString);

                        mDatabaseUsers.child(user_id).updateChildren(userInfo);

                        progressDialog.dismiss();
                        finish();
                    } else {
                        filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadedUri = taskSnapshot.getDownloadUrl();

                                Map userInfo = new HashMap();
                                userInfo.put("display_picture", downloadedUri.toString());
                                userInfo.put("occupation", occupation);
                                userInfo.put("name", username);
                                userInfo.put("location_address", locationString);

                                mDatabaseUsers.child(user_id).updateChildren(userInfo);

                                progressDialog.dismiss();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.getMessage();
                            }
                        });
                    }
                } else {
                    mLocation.setError("Field empty");
                }
            } else {
                mOccupation.setError("Field empty");
            }
        } else {
            mUsername.setError("Field empty");
        }

    }

    private void selectImage() {
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
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();

                mDisplayPicture.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
