package com.android.msqhealthpoc1.fragments.profile.create;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.msqhealthpoc1.R;
import com.android.msqhealthpoc1.activities.WelcomeActivity;
import com.android.msqhealthpoc1.helpers.PrefManager;
import com.android.msqhealthpoc1.model.Practice;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PracticeConfirmation extends Fragment {

    //    Spinner hasPracticeSpinner;
    LinearLayout mInsertPractice, mRegisterPractice;

    DatabaseReference mDatabasePractice, mUsersDatabase;

    TextInputEditText ePracticeName, ePracticeNumber, ePractitionerIDNumber, eSearchPractice;

    Button btnSave;

    ProgressDialog progressDialog;

    PrefManager prefManager;


    public PracticeConfirmation() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_practice_confirmation, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

//        hasPracticeSpinner = view.findViewById(R.id.hasPractice);
        mInsertPractice = view.findViewById(R.id.practice_number_insert);
        mRegisterPractice = view.findViewById(R.id.practice_number_registration);

        ePracticeName = view.findViewById(R.id.register_practice_name);
        ePracticeNumber = view.findViewById(R.id.register_practice_number);
        ePractitionerIDNumber = view.findViewById(R.id.register_practitioner_id_number);
        eSearchPractice = view.findViewById(R.id.practice_number);

        prefManager = new PrefManager(getActivity());

        btnSave = view.findViewById(R.id.save);


        System.out.println("");

        // Spinner Drop down elements
        List<String> categories = new ArrayList<>();
        categories.add("-- Pick --");
        categories.add("Yes");
        categories.add("No");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
//        hasPracticeSpinner.setAdapter(dataAdapter);

        mDatabasePractice = FirebaseDatabase.getInstance().getReference().child("doctors_practice_numbers");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!TextUtils.isEmpty(eSearchPractice.getText().toString().trim())) {
                    Query query = mDatabasePractice.orderByChild("PRACTICE_NUMBER").equalTo(eSearchPractice.getText().toString());
                    progressDialog.setMessage("Searching for practice. Please wait...");
                    progressDialog.show();
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String name = (String) snapshot.child("NAME").getValue();
                                    final String practice_number = (String) snapshot.child("PRACTICE_NUMBER").getValue();
                                    String suburb = (String) snapshot.child("SUBURB").getValue();
                                    String telephone = (String) snapshot.child("TELEPHONE").getValue();

                                    System.out.println("Doctor's Details: \nName" + name + " \n " + "Practice Number: " + practice_number + "  \n" +
                                            "\nSuburb: " + suburb + "\nTelephone: " + telephone);

                                    mUsersDatabase.child("practice_name").setValue(name);
                                    mUsersDatabase.child("practice_number").setValue(practice_number);
                                    mUsersDatabase.child("suburb").setValue(suburb);
                                    mUsersDatabase.child("telephone").setValue(telephone).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Practice " + practice_number + " Found", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                                ((WelcomeActivity) getActivity()).moveToNext();
                                            }
                                        }
                                    });
                                }

                            } else {
                                Toast.makeText(getActivity(), "Your Practice Number could not be found. " +
                                        "Please contact MSQ if this might be a mistake.", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    eSearchPractice.setError("Field empty");
                }

//                mDatabase.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        System.out.println("DATA: " + dataSnapshot.getChildrenCount());
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                            if (snapshot.child("PRACTICE_NUMBER").getValue().toString() == eSearchPractice.getText().toString()){
//                                Toast.makeText(getContext(), "Good!!", Toast.LENGTH_SHORT).show();
//                            }else {
//                                Toast.makeText(getContext(), "BAD", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
            }
        });

        eSearchPractice.addTextChangedListener(new TextWatcher() {
            int prevL = 0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                prevL = eSearchPractice.getText().toString().length();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if ((prevL < length) && (length == 3 || length == 7 || length == 11)) {
                    s.append(",");
                }
            }
        });


//        hasPracticeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
//                switch (parent.getSelectedItemPosition()) {
//                    case 0:
//                        mRegisterPractice.setVisibility(View.GONE);
//                        mInsertPractice.setVisibility(View.GONE);
//                        break;
//                    case 1:
//                        progressDialog.setMessage("Searching for practice. Please wait...");
//                        mRegisterPractice.setVisibility(View.GONE);
//                        mInsertPractice.setVisibility(View.VISIBLE);
//                        break;
//                    case 2:
//                        progressDialog.setMessage("Registering your practice. Please wait...");
//                        mRegisterPractice.setVisibility(View.VISIBLE);
//                        mInsertPractice.setVisibility(View.GONE);
//                        break;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                switch (hasPracticeSpinner.getSelectedItemPosition()) {
//                    case 0:
//                        //Get Practice Details
//                        Toast.makeText(getContext(), "Please select \'Pick\' then Yes or No", Toast.LENGTH_SHORT).show();
//
//                        break;
//                    case 1:
//                        //Get Practice Details
//                        progressDialog.show();
//                        mDatabase.child("doctors_practice_numbers").addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.hasChildren()) {
//                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                        if (snapshot.child("PRACTICE NUMBER").getValue().toString() == eSearchPractice.getText().toString()) {
//                                            progressDialog.setMessage("Adding you to practice");
//                                            mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("practice").setValue(snapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if (task.isSuccessful()) {
//                                                        progressDialog.dismiss();
//                                                        ((WelcomeActivity) getActivity()).moveToNext();
//                                                        return;
//                                                    }
//                                                }
//                                            });
//                                        }
//                                    }
//                                    progressDialog.dismiss();
//                                    eSearchPractice.setError("No practice found...");
//
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//
//                        break;
//                    case 2:
//                        //Register new practice
//
//                        if (!TextUtils.isEmpty(ePracticeName.getText().toString().trim())) {
//                            if (!TextUtils.isEmpty(ePracticeNumber.getText().toString().trim())) {
//                                if (!TextUtils.isEmpty(ePractitionerIDNumber.getText().toString().trim())) {
//                                    Practice practice = new Practice();
//                                    practice.setName(ePracticeName.getText().toString());
//                                    practice.setPracticeNumber(ePracticeNumber.getText().toString());
//                                    practice.setPractitionerID(ePractitionerIDNumber.getText().toString());
//
//                                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//                                    Map<String, Object> postValues = practice.toMap();
//
//                                    String key = mDatabase.child("practices").push().getKey();
//
//                                    Map<String, Object> childUpdates = new HashMap<>();
//                                    childUpdates.put("/practices/" + key, postValues);
//                                    childUpdates.put("/users/" + user.getUid() + "/practice/", postValues);
//                                    FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()) {
//                                                progressDialog.dismiss();
//                                                ((WelcomeActivity) getActivity()).moveToNext();
//                                                prefManager.setFirstTimeSignUp(true);
//                                            } else {
//                                                task.getException().printStackTrace();
//                                                Toast.makeText(getActivity(), "Practice Creation Failed", Toast.LENGTH_LONG).show();
//                                            }
//                                        }
//                                    });
//
//                                    break;
//                                } else {
//                                    ePractitionerIDNumber.setError("Field empty");
//                                }
//                            } else {
//                                ePracticeNumber.setError("Field empty");
//                            }
//                        } else {
//                            ePracticeName.setError("Field empty");
//                        }
//                }
//            }
//        });


        return view;
    }

}
