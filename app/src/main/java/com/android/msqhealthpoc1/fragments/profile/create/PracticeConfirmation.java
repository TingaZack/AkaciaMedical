package com.android.msqhealthpoc1.fragments.profile.create;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
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
import com.android.msqhealthpoc1.model.Practice;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PracticeConfirmation extends Fragment {

    Spinner hasPracticeSpinner;
    LinearLayout mInsertPractice, mRegisterPractice;

    DatabaseReference mDatabase;

    TextInputEditText ePracticeName, ePracticeNumber, ePractitionerIDNumber, eSearchPractice;

    Button btnSave;

    ProgressDialog progressDialog;


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

        hasPracticeSpinner = (Spinner) view.findViewById(R.id.hasPractice);
        mInsertPractice = (LinearLayout) view.findViewById(R.id.practice_number_insert);
        mRegisterPractice = (LinearLayout) view.findViewById(R.id.practice_number_registration);

        ePracticeName = (TextInputEditText) view.findViewById(R.id.register_practice_name);
        ePracticeNumber = (TextInputEditText) view.findViewById(R.id.register_practice_number);
        ePractitionerIDNumber = (TextInputEditText) view.findViewById(R.id.register_practitioner_id_number);
        eSearchPractice = (TextInputEditText) view.findViewById(R.id.practice_number);

        btnSave = (Button) view.findViewById(R.id.save);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<>();
        categories.add("Yes");
        categories.add("No");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        hasPracticeSpinner.setAdapter(dataAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        hasPracticeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                switch (parent.getSelectedItemPosition()) {
                    case 0:
                        progressDialog.setMessage("Searching for practice. Please wait...");
                        mRegisterPractice.setVisibility(View.GONE);
                        mInsertPractice.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        progressDialog.setMessage("Registering your practice. Please wait...");
                        mRegisterPractice.setVisibility(View.VISIBLE);
                        mInsertPractice.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (hasPracticeSpinner.getSelectedItemPosition()) {
                    case 0:
                        //Get Practice Details
                        progressDialog.show();
                        mDatabase.child("practices").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChildren()) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        if (snapshot.child("practice_number").getValue().toString() == eSearchPractice.getText().toString()) {
                                            progressDialog.setMessage("Adding you to practice");
                                            mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("practice").setValue(snapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        progressDialog.dismiss();
                                                        ((WelcomeActivity) getActivity()).moveToNext();
                                                        return;
                                                    }
                                                }
                                            });
                                        }
                                    }
                                    progressDialog.dismiss();
                                    eSearchPractice.setError("No practice found...");

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        break;
                    case 1:
                        //Register new practice
                        Practice practice = new Practice();
                        practice.setName(ePracticeName.getText().toString());
                        practice.setPracticeNumber(ePracticeNumber.getText().toString());
                        practice.setPractitionerID(ePractitionerIDNumber.getText().toString());


                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        Map<String, Object> postValues = practice.toMap();

                        String key = mDatabase.child("practices").push().getKey();

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/practices/" + key, postValues);
                        childUpdates.put("/users/" + user.getUid() + "/practice/", postValues);
                        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    ((WelcomeActivity) getActivity()).moveToNext();
                                } else {
                                    task.getException().printStackTrace();
                                    Toast.makeText(getActivity(), "Practice Creation Failed", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        mDatabase.child("users").child(user.getUid()).child("practice").setValue(practice.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    ((WelcomeActivity) getActivity()).moveToNext();
                                }
                            }
                        });

                        break;
                }
            }
        });


        return view;
    }

}
