package com.msqhealth.main.activities.authentication;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.msqhealth.main.R;
import com.msqhealth.main.helpers.GMailSender;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewPracticeRegistration extends Fragment {

    private EditText ePracticeNumber, eFirstName, eLastName, eAddressLine1, eAddressLine2, eAddressLineSuburb, eCellphone, eTelephone;
    ProgressDialog pDialog;

    Button btnSubmit;

    public NewPracticeRegistration newInstance(String mPracticeNumber) {
        NewPracticeRegistration fragment = new NewPracticeRegistration();
        Bundle args = new Bundle();
        args.putString("practice_number", mPracticeNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public NewPracticeRegistration() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_practice_registration, container, false);

        ePracticeNumber = view.findViewById(R.id.practice_number);
        eFirstName = view.findViewById(R.id.firstname);
        eLastName = view.findViewById(R.id.lastname);
        eAddressLine1 = view.findViewById(R.id.address_field_1);
        eAddressLine2 = view.findViewById(R.id.address_field_2);
        eAddressLineSuburb = view.findViewById(R.id.address_field_suburb);
        eTelephone = view.findViewById(R.id.telephone);
        eCellphone = view.findViewById(R.id.cellphone);

        btnSubmit = view.findViewById(R.id.save);


        ePracticeNumber.addTextChangedListener(mTextWatcher);
        eFirstName.addTextChangedListener(mTextWatcher);
        eLastName.addTextChangedListener(mTextWatcher);
        eAddressLine1.addTextChangedListener(mTextWatcher);
        eAddressLine2.addTextChangedListener(mTextWatcher);
        eAddressLineSuburb.addTextChangedListener(mTextWatcher);
        eCellphone.addTextChangedListener(mTextWatcher);
        eTelephone.addTextChangedListener(mTextWatcher);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SendEmail().execute();
            }
        });


        pDialog = new ProgressDialog(getActivity());
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Saving information...");

        checkFieldsForEmptyValues();

        return view;
    }

    protected void checkFieldsForEmptyValues() {
        // TODO Auto-generated method stub
        String text1 = ePracticeNumber.getText().toString().trim();
        String text2 = eFirstName.getText().toString().trim();
        String text3 = eLastName.getText().toString().trim();
        String text4 = eAddressLine1.getText().toString().trim();
        String text5 = eAddressLine2.getText().toString().trim();
        String text6 = eAddressLineSuburb.getText().toString().trim();
        String text7 = eTelephone.getText().toString().trim();
        String text8 = eCellphone.getText().toString().trim();

        String regex = "\\d";
        Pattern pattern = Pattern.compile(regex);

        if ((TextUtils.isEmpty(text1)) || (TextUtils.isEmpty(text2)) || (TextUtils.isEmpty(text3)) || (TextUtils.isEmpty(text4)) || (TextUtils.isEmpty(text5)) || (TextUtils.isEmpty(text6)) || (TextUtils.isEmpty(text7)) || (TextUtils.isEmpty(text8))) {
            btnSubmit.setEnabled(false);
        } else {
            if (pattern.matcher(text2).matches()) {
                eFirstName.setError("First name contains a number");
                btnSubmit.setEnabled(false);
            } else if (pattern.matcher(text3).matches()) {
                eLastName.setError("Last name contains a number");
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

    public class SendEmail extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {

                GMailSender sender = new GMailSender(getString(R.string.sender_email), getString(R.string.sender));
                sender.sendMail("New Practice Number Registration",
                        "First Name : " + eFirstName.getText().toString() + "\n\n"
                                + "Last Name : " + eLastName.getText().toString() + "\n\n"
                                + "Practice Number : " + ePracticeNumber.getText().toString() + "\n\n"
                                + "Address : " + eAddressLine1.getText().toString() + ", " + eAddressLine2.getText().toString() + ", " + eAddressLineSuburb.getText().toString() + "\n\n"
                                + "Cellphone number : " + eCellphone.getText().toString() + "\n\n"
                                + "Telephone Number : " + eTelephone.getText().toString() + "\n\n"
                        ,
                        getString(R.string.sender_email),
                        "info@buildhealth.co.za","no attachment");
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean emailSent) {
            super.onPostExecute(emailSent);
            pDialog.dismiss();
            if (emailSent) {
                orderCompleted().show();
            }
        }
    }

    public AlertDialog orderCompleted() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Thank you. A Customer Service Agent will call to verify your practice before you can sign up in the app.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        getActivity().finish();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }


}
