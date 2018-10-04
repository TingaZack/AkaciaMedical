package com.msqhealth.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.msqhealth.main.helpers.GMailSender;

public class EmailTestActivity extends AppCompatActivity {

    Session session = null;
    ProgressDialog pdialog = null;
    Context context = null;
    EditText reciep, sub, msg;
    String rec, subject, textMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_test);
        context = this;

        Button login = (Button) findViewById(R.id.btn_submit);
        reciep = (EditText) findViewById(R.id.et_to);
        sub = (EditText) findViewById(R.id.et_sub);
        msg = (EditText) findViewById(R.id.et_text);

        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                new SendEmail().execute();
            }
        });
    }

    public class SendEmail extends AsyncTask<Void, Void, Boolean> {


        DataSnapshot dataSnapshot;

        public SendEmail(DataSnapshot dataSnapshot) {
            this.dataSnapshot = dataSnapshot;
        }

        @Override
        protected void onPreExecute() {
//            pDialog.setMessage("Completing your order");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {

                GMailSender sender = new GMailSender("brndkt@gmail.com", "ntando140114");
                sender.sendMail("Checked Out Invoice",
                        "First Name : " + "\n\n"
                                + "Practice Number : "  + "\n\n"
                                + "Address : " +  ", " + "\n\n"
                                + "Cellphone number :  ",
//                                + "Telephone Number : " + eTelephone.getText().toString() + "\n\n",
                        "brndkt@gmail.com",
                        "info@buildhealth.co.za", null);
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean emailSent) {
            super.onPostExecute(emailSent);
//            pDialog.dismiss();
            if (emailSent) {
//                dataSnapshot.getRef().removeValue(new DatabaseReference.CompletionListener() {
//                    @Override
//                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                        orderCompleted().show();
//                    }
//                });
                finish();
            }
        }
    }

}
