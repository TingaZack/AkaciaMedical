package com.msqhealth.main.activities.authentication;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.reinaldoarrosi.maskededittext.MaskedEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msqhealth.main.R;
import com.msqhealth.main.activities.MainActivity;
import com.msqhealth.main.activities.OnBoardingActivity;
import com.msqhealth.main.helpers.GMailSender;
import com.msqhealth.main.helpers.PrefManager;

import java.util.Date;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewPracticeRegistration extends AppCompatActivity {

    private EditText ePracticeNumber, eFirstName, eLastName, eAddressLine1;
    private EditText eCellphone;
    private ProgressDialog pDialog;

    private Button btnSubmit;
    private TextView mLearnMoreTextView;

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String speciality;

    private Spinner spinner;
    ArrayAdapter<String> adapter;
    private int textlength = 0;

    final String[] paths = {"Select Speciality", "Dentist", "General Practitioner", "Medical Officer",
            "Nurse Assistance/Auxillary", "Paramedic", "Veterinary Surgeon"};

//    public NewPracticeRegistration newInstance(String mPracticeNumber) {
//        NewPracticeRegistration fragment = new NewPracticeRegistration();
//        Bundle args = new Bundle();
//        args.putString("practice_number", mPracticeNumber);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    public NewPracticeRegistration() {
//        // Required empty public constructor
//    }

    private PrefManager prefManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_practice_registration);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.keepSynced(true);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        checkIfUserExist();

        mLearnMoreTextView = findViewById(R.id.tv_learn_more);
        ePracticeNumber = findViewById(R.id.practice_number);
        eFirstName = findViewById(R.id.firstname);
//        eLastName = findViewById(R.id.lastname);
        eAddressLine1 = findViewById(R.id.address_field_1);
//        eAddressLine2 = view.findViewById(R.id.address_field_2);
//        eAddressLineSuburb = view.findViewById(R.id.address_field_suburb);
//        eTelephone = view.findViewById(R.id.telephone);
        eCellphone = findViewById(R.id.cellphone);

        btnSubmit = findViewById(R.id.save);


        ePracticeNumber.addTextChangedListener(mTextWatcher);
        eFirstName.addTextChangedListener(mTextWatcher);
//        eLastName.addTextChangedListener(mTextWatcher);
        eAddressLine1.addTextChangedListener(mTextWatcher);
//        eAddressLine2.addTextChangedListener(mTextWatcher);
//        eAddressLineSuburb.addTextChangedListener(mTextWatcher);
        eCellphone.addTextChangedListener(mTextWatcher);
//        eTelephone.addTextChangedListener(mTextWatcher);

        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_item, paths);

//        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    speciality = "Select Speciality";
                } else if (i == 1) {
                    speciality = "Dentist";
                } else if (i == 2) {
                    speciality = "General Practitioner";
                } else if (i == 3) {
                    speciality = "Medical Officer";
                } else if (i == 4) {
                    speciality = "Nurse Assistance/Auxillary";
                } else if (i == 5) {
                    speciality = "Paramedic";
                } else if (i == 6) {
                    speciality = "Veterinary Surgeon";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        eCellphone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                String text = eCellphone.getText().toString();
                textlength = eCellphone.getText().length();

                if (text.endsWith(" "))
                    return;

                if (textlength == 1) {
                    if (!text.contains("(")) {
                        eCellphone.setText(new StringBuilder(text).insert(text.length() - 1, "(").toString());
                        eCellphone.setSelection(eCellphone.getText().length());
                    }

                } else if (textlength == 5) {

                    if (!text.contains(")")) {
                        eCellphone.setText(new StringBuilder(text).insert(text.length() - 1, ")").toString());
                        eCellphone.setSelection(eCellphone.getText().length());
                    }

                } else if (textlength == 6 || textlength == 10) {
                    eCellphone.setText(new StringBuilder(text).insert(text.length() - 1, " ").toString());
                    eCellphone.setSelection(eCellphone.getText().length());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {

//                eCellphone.addTextChangedListener(new PhoneNumberFormattingTextWatcher("+27"));
//                PhoneNumberUtils.formatNumber(eCellphone.getText().toString());

                if (ePracticeNumber.getText().length() >= 5) {
                    if (eFirstName.getText().length() >= 7) {
                        String mString = ePracticeNumber.getText().toString().substring(0, 5);
                        if (mString.equals("14000")) {
                            if (ePracticeNumber.getText().length() == 12) {
                                if (!TextUtils.isEmpty(eCellphone.getText().toString().trim()) &&
                                        eCellphone.getText().length() == 14) {
                                    if (eCellphone.getText().length() >= 1) {
                                        String mStringNumber = eCellphone.getText().toString().substring(0, 2);
                                        if (mStringNumber.equals("(0")) {
                                            if (eCellphone.getText().length() == 14) {
                                                if (eAddressLine1.getText().length() >= 10) {

                                                    if (!speciality.equals("Select Speciality")) {
//                                                        new SendEmail().execute();
//                                                        finish();
                                                        saveUserInfo();
                                                    } else {
                                                        Toasty.error(getApplicationContext(), "Please select your speciality", Toast.LENGTH_LONG).show();
                                                    }
                                                } else {
                                                    eAddressLine1.setError(getString(R.string.address_error));
                                                }
                                            } else {
                                                eCellphone.setError(getString(R.string.invalid_phone));
                                            }
                                        } else {
                                            eCellphone.setError(getString(R.string.invalid_phone));
                                        }
                                    }
                                } else {
                                    eCellphone.setError(getString(R.string.invalid_phone));
                                }
                            } else {
                                ePracticeNumber.setError(getString(R.string.invalid_practice_number));
                            }
                        } else {
                            ePracticeNumber.setError(getString(R.string.invalid_practice_number));
                        }
                    } else {
                        eFirstName.setError(getString(R.string.valid_names));
                    }
                } else {
                    ePracticeNumber.setError(getString(R.string.invalid_practice_number));
                }
            }
        });


        pDialog = new ProgressDialog(this);
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Saving information...");

        checkFieldsForEmptyValues();
        setDefaulttext();
        learMore();
    }

    public void learMore(){
        mLearnMoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View mView = LayoutInflater.from(NewPracticeRegistration.this).inflate(R.layout.learn_more, null);

                final android.support.v7.app.AlertDialog.Builder aBuilder = new android.support.v7.app.AlertDialog.Builder(NewPracticeRegistration.this, R.style.CustomDialog);
                aBuilder.setView(mView);

                final android.support.v7.app.AlertDialog alert = aBuilder.create();

                alert.show();
            }
        });
    }

    private void saveUserInfo() {
        pDialog.setMessage("Registering ...");
        pDialog.show();
        pDialog.setCancelable(false);
        registerNewUser(mUser.getEmail(), eFirstName.getText().toString(),
                ePracticeNumber.getText().toString(), eAddressLine1.getText().toString(),
                eCellphone.getText().toString(), speciality);
    }

    public void checkIfUserExist() {
        if (mUser == null) {
            startActivity(new Intent(getApplicationContext(), OnBoardingActivity.class));
        }
    }

    protected void checkFieldsForEmptyValues() {
        // TODO Auto-generated method stub
        String text1 = ePracticeNumber.getText().toString().trim();
        String text2 = eFirstName.getText().toString().trim();
//        String text3 = eLastName.getText().toString().trim();
        String text4 = eAddressLine1.getText().toString().trim();
//        String text5 = eAddressLine2.getText().toString().trim();
//        String text6 = eAddressLineSuburb.getText().toString().trim();
//        String text7 = eTelephone.getText().toString().trim();
        String text8 = eCellphone.getText().toString().trim();

        String regex = "\\d";
        Pattern pattern = Pattern.compile(regex);

        if ((TextUtils.isEmpty(text1)) || (TextUtils.isEmpty(text2)) ||
                (TextUtils.isEmpty(text4)) /*|| (TextUtils.isEmpty(text5)) || (TextUtils.isEmpty(text6))
                 || (TextUtils.isEmpty(text7))*/ || (TextUtils.isEmpty(text8))) {
            btnSubmit.setEnabled(false);
        } else {
            if (pattern.matcher(text2).matches()) {
                eFirstName.setError("First name contains a number");
                btnSubmit.setEnabled(false);
            } else {
                btnSubmit.setEnabled(true);
            }
        }
    }


    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            checkFieldsForEmptyValues();
        }
    };

    public void setDefaulttext() {
        eFirstName.setText("Dr ");
        Selection.setSelection(eFirstName.getText(), eFirstName.getText().length());


        eFirstName.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("Dr ")) {
                    eFirstName.setText("Dr ");
                    Selection.setSelection(eFirstName.getText(), eFirstName.getText().length());

                }

            }
        });
    }

    public void registerNewUser(String email, final String first_name,
                                String practice_number, String address, String phone, String speciality) {

        DatabaseReference mDatabaseUsers = mDatabaseReference.child("users").child(mUser.getUid()).child("profile");
        mDatabaseUsers.child("Name").setValue(first_name);
        mDatabaseUsers.child("Practice_Number").setValue(practice_number);
        mDatabaseUsers.child("Speciality").setValue(speciality);
        mDatabaseUsers.child("Telephone").setValue(phone);
        mDatabaseUsers.child("Suburb").setValue(address);
        mDatabaseUsers.child("Email").setValue(email);
        mDatabaseUsers.child("verified").setValue(false);
        mDatabaseUsers.child("Timestamp").setValue(new Date().getTime()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pDialog.dismiss();
                prefManager = new PrefManager(getApplicationContext());
                prefManager.setToFirstTimeRegister(false);
                System.out.println("FISRT TIME: " + prefManager.isFirstTimeRegister());
                Intent registerIntent = new Intent(getApplicationContext(), MainActivity.class);
                registerIntent.putExtra("dr_name", first_name);
                startActivity(registerIntent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pDialog.dismiss();
                Toast.makeText(NewPracticeRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
