package com.msqhealth.main.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.msqhealth.main.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.msqhealth.main.activities.authentication.NewPracticeRegistration;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ProfileActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 1;
    private EditText mNameEditText, mSpecialityEditText, mSuburbEditText, mTelephoneEditText;
    private ImageButton mNameButtonEdit, mSpecialityButtonEdit, mSuburbButtonEdit, mTelephoneButtonEdit;
    private TextView mEmailTextView, mPracticeNumberTextView;

    private Uri mImageUri;

    private DatabaseReference mDatabaseUsers;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;

    private int textlength = 0;

//    private Button mOrdersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mNameEditText = findViewById(R.id.profile_name);
//        mNameButtonEdit = findViewById(R.id.name_edit);
        mSpecialityEditText = findViewById(R.id.profile_speciality);
//        mSpecialityButtonEdit = findViewById(R.id.speciality_edit);
        mSuburbEditText = findViewById(R.id.profile_suburb);
        mSuburbButtonEdit = findViewById(R.id.suburb_edit);
        mTelephoneEditText = findViewById(R.id.profile_telephone);
//        mTelephoneButtonEdit = findViewById(R.id.telephone_edit);
//        mOrdersButton = findViewById(R.id.btn_orders);

        mEmailTextView = findViewById(R.id.profile_email);
        mPracticeNumberTextView = findViewById(R.id.profile_practice_number);

        mAuth = FirebaseAuth.getInstance();

//        mNameButtonEdit.setBackgroundResource(R.drawable.ic_mode_edit_black);
        mSuburbButtonEdit.setBackgroundResource(R.drawable.ic_mode_edit_black);
//        mSpecialityButtonEdit.setBackgroundResource(R.drawable.ic_mode_edit_black);
//        mTelephoneButtonEdit.setBackgroundResource(R.drawable.ic_mode_edit_black);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseUsers.keepSynced(true);
        mStorage = FirebaseStorage.getInstance().getReference();

        mNameEditText.setEnabled(false);
        mSuburbEditText.setEnabled(false);
        mSpecialityEditText.setEnabled(false);
        mTelephoneEditText.setEnabled(false);

        if (mAuth.getCurrentUser() != null) {

//            mOrdersButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    startActivity(new Intent(getApplicationContext(), OrdersActivitty.class));
//                }
//            });

            mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mNameEditText.setText((String) dataSnapshot.child("Name").getValue());
                    mSpecialityEditText.setText((String) dataSnapshot.child("Speciality").getValue());
                    mSuburbEditText.setText((String) dataSnapshot.child("Suburb").getValue());
                    mTelephoneEditText.setText((String) dataSnapshot.child("Telephone").getValue());
                    mEmailTextView.setText((String) dataSnapshot.child("Email").getValue());
                    String practice_number = (String) dataSnapshot.child("Practice_Number").getValue();
                    String repl = practice_number.replaceAll("..(?!$)", "$0 ");
                    mPracticeNumberTextView.setText(Arrays.toString(splitToNChar(practice_number, 3)));

                    System.out.println("IMAGEVIEW: " + dataSnapshot.child("Display_Image").getValue());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if (isNetworkAvailable()) {

//                mNameButtonEdit.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (mNameEditText.isEnabled()) {
//                            mNameEditText.setEnabled(false);
//                            mNameButtonEdit.setBackgroundResource(R.drawable.ic_mode_edit_black);
//                            mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).child("Name").setValue(mNameEditText.getText().toString());
//                        } else {
//                            mNameEditText.setEnabled(true);
//                            mNameButtonEdit.setBackgroundResource(R.drawable.ic_save_black_24dp);
//                        }
//                    }
//                });

//                mSpecialityButtonEdit.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (mSpecialityEditText.isEnabled()) {
//                            mSpecialityEditText.setEnabled(false);
//                            mSpecialityButtonEdit.setBackgroundResource(R.drawable.ic_mode_edit_black);
//                            mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).child("Speciality").setValue(mSpecialityEditText.getText().toString());
//                        } else {
//                            mSpecialityEditText.setEnabled(true);
//                            mSpecialityButtonEdit.setBackgroundResource(R.drawable.ic_save_black_24dp);
//
//                        }
//                    }
//                });

//                if (ePracticeNumber.getText().length() >= 5) {
//                    String mString = ePracticeNumber.getText().toString().substring(0, 5);
//
//                    if (mString.equals("14000")) {
//                        if (ePracticeNumber.getText().length() == 12) {
//                            if (!TextUtils.isEmpty(eCellphone.getText().toString().trim()) &&
//                                    eCellphone.getText().length() == 14) {
//                                if (eCellphone.getText().length() >= 1) {
//                                    String mStringNumber = eCellphone.getText().toString().substring(0, 2);
//                                    if (mStringNumber.equals("(0")) {
//                                        if (eCellphone.getText().length() == 14) {
//                                            if (eAddressLine1.getText().length() >= 10) {
//
//                                                if (!TextUtils.isEmpty(speciality) && !speciality.equals("Select Speciality")) {
//                                                    new NewPracticeRegistration.SendEmail().execute();
//                                                    finish();
//                                                } else {
//                                                    Toasty.error(getApplicationContext(), "Please select your speciality", Toast.LENGTH_LONG).show();
//                                                }
//                                            } else {
//                                                eAddressLine1.setError("address incomplete ...");
//                                            }
//                                        } else {
//                                            eCellphone.setError(getString(R.string.invalid_phone));
//                                        }
//                                    } else {
//                                        eCellphone.setError(getString(R.string.invalid_phone));
//                                    }
//                                }
//                            } else {
//                                eCellphone.setError(getString(R.string.invalid_phone));
//                            }
//                        } else {
//                            ePracticeNumber.setError(getString(R.string.invalid_practice_number));
//                        }
//                    } else {
//                        ePracticeNumber.setError(getString(R.string.invalid_practice_number));
//                    }
//                }

                mSuburbButtonEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mSuburbEditText.isEnabled()) {
                            mSuburbEditText.setEnabled(false);
                            mSuburbButtonEdit.setBackgroundResource(R.drawable.ic_mode_edit_black);
                            mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).child("Suburb").setValue(mSuburbEditText.getText().toString());
                        } else {
                            mSuburbEditText.setEnabled(true);
                            mSuburbButtonEdit.setBackgroundResource(R.drawable.ic_save_black_24dp);
                        }
                    }
                });

//                mTelephoneButtonEdit.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (mTelephoneEditText.isEnabled()) {
//                            mTelephoneEditText.setEnabled(false);
//                            mTelephoneButtonEdit.setBackgroundResource(R.drawable.ic_mode_edit_black);
//                            mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).child("Telephone").setValue(mTelephoneEditText.getText().toString());
//
//                        } else {
//                            mTelephoneEditText.setEnabled(true);
//                            mTelephoneButtonEdit.setBackgroundResource(R.drawable.ic_save_black_24dp);
//                        }
//                    }
//                });
            }

            cellNumberFormatting();

        } else if (!isNetworkAvailable()) {

            Snackbar snack = Snackbar.make(findViewById(R.id.relative_layout), getApplicationContext().getString(R.string.no_connection), Snackbar.LENGTH_INDEFINITE).setDuration(10000);
            snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark));
            View view = snack.getView();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            params.gravity = Gravity.TOP;
            view.setLayoutParams(params);
            snack.show();
        }

    }

    private static String[] splitToNChar(String text, int size) {
        List<String> parts = new ArrayList<>();

        int length = text.length();
        for (int i = 0; i < length; i += size) {
            parts.add(text.substring(i, Math.min(length, i + size)));
        }
        return parts.toArray(new String[0]);
    }


    public void cellNumberFormatting(){
        mTelephoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                String text = mTelephoneEditText.getText().toString();
                textlength = mTelephoneEditText.getText().length();

                if (text.endsWith(" "))
                    return;

                if (textlength == 1) {
                    if (!text.contains("(")) {
                        mTelephoneEditText.setText(new StringBuilder(text).insert(text.length() - 1, "(").toString());
                        mTelephoneEditText.setSelection(mTelephoneEditText.getText().length());
                    }

                } else if (textlength == 5) {

                    if (!text.contains(")")) {
                        mTelephoneEditText.setText(new StringBuilder(text).insert(text.length() - 1, ")").toString());
                        mTelephoneEditText.setSelection(mTelephoneEditText.getText().length());
                    }

                } else if (textlength == 6 || textlength == 10) {
                    mTelephoneEditText.setText(new StringBuilder(text).insert(text.length() - 1, " ").toString());
                    mTelephoneEditText.setSelection(mTelephoneEditText.getText().length());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {

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

                StorageReference filePath = mStorage.child("profile_pictures/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
                filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadedUri = taskSnapshot.getDownloadUrl();
                        mDatabaseUsers.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Display_Image").setValue(downloadedUri.toString());
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
