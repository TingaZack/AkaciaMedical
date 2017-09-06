package com.android.msqhealthpoc1.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.android.msqhealthpoc1.R;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {


    CircleImageView mDisplayPicture;
    EditText mUsername, mOccupation, mEmail;
    Button btnSelectImage;
    private DatabaseReference mDatabase;
    private Uri pictureUri;


    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_profile);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDisplayPicture = (CircleImageView) findViewById(R.id.display_picture);

        mUsername = (EditText) findViewById(R.id.name);
        mOccupation = (EditText) findViewById(R.id.occupation);
        mEmail = (EditText) findViewById(R.id.email);

        btnSelectImage = (Button) findViewById(R.id.select_image);

        user = FirebaseAuth.getInstance().getCurrentUser();


        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            String uid = user.getUid();


            mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mOccupation.setText(dataSnapshot.child("occupation").getValue().toString());
                    mEmail.setText(dataSnapshot.child("location").getValue().toString());
                    // Name, email address, and profile photo Url
                    mUsername.setText(user.getDisplayName());
                    //Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/msq-health.appspot.com/o/images%2Fcropped652655456.jpg?alt=media&token=8197e4e7-cb2d-4767-a581-5497a6ab1a3c").into(mDisplayPicture);
                    // Check if user's email is verified
                    if (!user.isEmailVerified()) {
                        mEmail.setError("Email not verified");
                    }

                    // The user's ID, unique to the Firebase project. Do NOT use this value to
                    // authenticate with your backend server, if you have one. Use
                    // FirebaseUser.getToken() instead.
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        if (pictureUri != null) {

                            FirebaseStorage storage = FirebaseStorage.getInstance();

                            // Create a storage reference from our app
                            StorageReference storageRef = storage.getReference();


                            StorageReference riversRef = storageRef.child("images/" + pictureUri.getLastPathSegment());
                            UploadTask uploadTask = riversRef.putFile(pictureUri);

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
                                    mDatabase.child("users").child(user.getUid()).child("display_picture").setValue(taskSnapshot.getMetadata().getDownloadUrl().toString());

                                }
                            });
                        }

                        mDatabase.child("users").child(user.getUid()).child("occupation").setValue(mOccupation.getText().toString().trim());
                        mDatabase.child("users").child(user.getUid()).child("location").setValue(mEmail.getText().toString().trim());
                        finish();
                    }
                }
            });


        }

    }


    public void selectImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setFixAspectRatio(true)
                .setAspectRatio(1, 1)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final Uri resultUri = result.getUri();
                pictureUri = resultUri;
                Glide.with(this).load(resultUri).into(mDisplayPicture);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
