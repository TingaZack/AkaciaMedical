package com.msqhealth.main.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.chilkatsoft.CkXml;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msqhealth.main.R;
import com.msqhealth.main.model.Cart;
import com.msqhealth.main.model.CartItem;
import com.msqhealth.main.model.Product;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentGatewayWebView extends AppCompatActivity {

    List<Map<String, Object>> items = new ArrayList<>();
    private WebView webView;
    private Intent intent;
    private ProgressDialog pDialog;
    private FirebaseUser user;
    CkXml sendXml;
    CkXml ordersXML;
    CkXml orderXML;
    String m_id;

    double _amount;
    int invoice_number = 0;
    int invoice_number_total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);


        sendXml = new CkXml();
        ordersXML = new CkXml();
        orderXML = new CkXml();

//        createxmlFile();

        boolean autoCreate = true;
        user = FirebaseAuth.getInstance().getCurrentUser();

        pDialog = new ProgressDialog(PaymentGatewayWebView.this);
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Preparing Cart");

        webView = findViewById(R.id.webview);

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new MyBrowser());

        getURLPostData();
        generateInvoiceNumber();

    }

    public void generateInvoiceNumber() {

//      Get Invoice Number
        FirebaseDatabase.getInstance().getReference().child("carts").child("checked-out").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int invoice_total = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    System.out.println("GET COUNT: " + snapshot.getChildrenCount());

                    invoice_total = (int) (invoice_total + snapshot.getChildrenCount());
                    System.out.println("GET COUNT Total: " + invoice_total);
                }

                invoice_number = invoice_total;
                ;
                invoice_number_total = invoice_number + 1;
                System.out.println("TOT INVOICE: " + invoice_number_total);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getURLPostData() {
        pDialog.show();
        FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("carts").child("pending").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                double total = 0;
                int i = 1;
                _amount = 0;
                sendXml.put_Tag("eExact");
                ordersXML.put_Tag("Orders");
                orderXML.put_Tag("Order");
                orderXML.UpdateAttribute("type", "V");
                ordersXML.AddChildTree(orderXML);
                sendXml.AddChildTree(ordersXML);

                sendXml.UpdateAttrAt("Orders|Order", true, "type", "V");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    _amount = _amount + ((Double.parseDouble(String.valueOf(snapshot.child("product").child("price").getValue()))) * (Integer.parseInt(snapshot.child("quantity").getValue().toString())));
                }


                DecimalFormat df = new DecimalFormat("##.##");
                String finalAmount = String.valueOf(df.format(_amount).replace(",", "."));

                String sent_amount = finalAmount.replace(".", "");
                m_id = "MSQ" + new Date().getTime();
                webView.loadUrl("file:///android_asset/gateway.html?merchantid=" + m_id + "&amount=" + sent_amount + "&thekey=" + getString(R.string.the_key) + "&theid=" + getString(R.string.the_id) + "");

                pDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class MyBrowser extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            pDialog.dismiss();
            System.out.println("--------------page loading started " + url);

            if (!isNetworkAvailable()) {
                //showInfoMessageDialog("network not available");
                //load here your custom offline page for handling such errors

                System.out.println("network not available");
                return;
            } else System.out.println("network available");

            if (url.startsWith("https://msq-health.firebaseapp.com/success.html")) {
                System.out.println("Request Intercepted");

                final CkXml xml = new CkXml();


                FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        orderXML.NewChild("Description", dataSnapshot.child("Practice_Number").getValue().toString());
                        orderXML.NewChild("YourRef", user.getUid());
                        orderXML.NewChild("Currency", "ZAR");
                        Date date = Calendar.getInstance().getTime();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                        String today = format.format(date);
                        orderXML.NewChild("OrderedBy|Date", today);


                        orderXML.UpdateAttrAt("OrderedBy|Debtor ", true, "code", user.getUid());
                        orderXML.UpdateAttrAt("Warehouse", true, "code", "MJ20");
                        orderXML.UpdateAttrAt("Selection ", true, "code", "10");

                        ordersXML.AddChildTree(orderXML);
                        sendXml.AddChildTree(ordersXML);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("carts").child("pending").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {

                            double total = 0;
                            int i = 1;
                            double _amount = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                System.out.println("Break Point 2 :  Items in cart : " + dataSnapshot.getChildrenCount());
                                CartItem cartItem = new CartItem();
                                cartItem.setQuantity(Integer.parseInt(snapshot.child("quantity").getValue().toString()));
                                _amount = _amount + ((Double.parseDouble(String.valueOf(snapshot.child("product").child("price").getValue()))) * (Integer.parseInt(snapshot.child("quantity").getValue().toString())));
                                Product product = new Product();
                                product.setCode(snapshot.child("product").child("code").getValue().toString());
                                product.setConsumables(snapshot.child("product").child("consumables").getValue().toString());
                                product.setDescription(snapshot.child("product").child("description").getValue().toString());
                                product.setPrice(Double.parseDouble(snapshot.child("product").child("price").getValue().toString()));
                                product.setTrueImageUrl(snapshot.child("product").child("trueImageUrl").getValue().toString());
                                product.setUnit_of_messuremeant(snapshot.child("product").child("unit_of_messuremeant").getValue().toString());
                                cartItem.setProduct(product);

                                System.out.println(cartItem.toMap().toString());

                                items.add(cartItem.toMap());


                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss");

                                c.add(Calendar.DATE, 5);  // number of days to add
                                String end_date = df.format(c.getTime());


                                xml.put_Tag("OrderLine");
                                xml.UpdateAttribute("LineNo", String.valueOf(i));
                                xml.UpdateAttrAt("Item", true, "code", snapshot.child("product").child("code").getValue().toString());
//                                xml.UpdateAttribute("code", snapshot.child("product").child("code").getValue().toString());
                                xml.NewChild("Quantity", snapshot.child("quantity").getValue().toString());
                                xml.NewChild("Delivery|Date", end_date);
//                                xml.NewChild("Total Rand Price", String.valueOf(_amount));
                                orderXML.AddChildTree(xml);
                                xml.Clear();
                                ordersXML.AddChildTree(orderXML);
                                sendXml.AddChildTree(ordersXML);

                                i++;
                            }

                            System.out.println("Break Point 2 :  Items in cart : " + items.size());


//                            sendXml.NewChild("Total", String.valueOf(total));


                            final double amount = _amount;

                            String key = FirebaseDatabase.getInstance().getReference().child("carts").push().getKey();

                            final Cart cart = new Cart();
                            cart.setItems(items);
                            cart.setSubtotal(amount);
                            cart.setInvoice_number("MSQ-00-" + String.valueOf(invoice_number_total));
                            cart.setUserID(user.getUid());

                            System.out.println("CART PRINT: " + cart.toMap().toString());

                            final Map<String, Object> postValues = cart.toMap();

                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/carts/checked-out/" + user.getUid() + "/" + key, postValues);
                            childUpdates.put("/users/" + user.getUid() + "/carts/completed/" + key, postValues);

                            createxmlFile();
                            FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        new SendEmail(dataSnapshot).execute();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            } else {
            }
        }

        public class SendEmail extends AsyncTask<Void, Void, Boolean> {


            DataSnapshot dataSnapshot;

            public SendEmail(DataSnapshot dataSnapshot) {
                this.dataSnapshot = dataSnapshot;
            }


            @Override
            protected Boolean doInBackground(Void... voids) {


                try {

                    connnectingwithFTP(getString(R.string.order_address), "mlab", getString(R.string.order_pass));
                    return true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return false;
                }

            }

            @Override
            protected void onPostExecute(Boolean emailSent) {
                super.onPostExecute(emailSent);

                if (emailSent) {
                    dataSnapshot.getRef().removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        }
                    });

                }
            }
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            if (url.startsWith("https://msq-health.firebaseapp.com/success.html")) {
                pDialog.dismiss();
                orderCompleted().show();


            } else if (url.startsWith("https://msq-health.firebaseapp.com/decline.html")) {

                pDialog.dismiss();

            }
        }
    }

    static {
        System.loadLibrary("chilkat");

    }


    public AlertDialog orderCompleted() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentGatewayWebView.this);
        builder.setMessage("Your order has been completed. Please expect delivery in 3 to 5 day.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        finish();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }


    /**
     * @param ip
     * @param userName
     * @param pass
     */
    public void connnectingwithFTP(final String ip, final String userName, final String pass) {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                boolean status = false;
                try {
                    FileInputStream inputStream = getApplicationContext().openFileInput(m_id + "invoice.xml");
                    FTPClient mFtpClient = new FTPClient();
                    mFtpClient.setConnectTimeout(10 * 1000);
                    mFtpClient.connect(InetAddress.getByName(ip));
                    status = mFtpClient.login(userName, pass);
                    Log.e("isFTPConnected", String.valueOf(status));
                    if (FTPReply.isPositiveCompletion(mFtpClient.getReplyCode())) {
                        mFtpClient.setFileType(FTP.ASCII_FILE_TYPE);
                        mFtpClient.enterLocalPassiveMode();
                        FTPFile[] mFileArray = mFtpClient.listFiles();
                        Log.e("Size", String.valueOf(mFileArray.length));

                        uploadFile(mFtpClient, inputStream);
                    }
                    System.out.println("FTP REMOTE: " + mFtpClient.isRemoteVerificationEnabled());
                    System.out.println("FTP REMOT: " + mFtpClient.getReplyCode());
                    mFtpClient.logout();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
    }

    /**
     * @param ftpClient    FTPclient object
     * @param downloadFile local file which need to be uploaded.
     */

    public void uploadFile(FTPClient ftpClient, FileInputStream downloadFile) {
//        InputStream inputStream = context.openFileInput("invoice.xml");
        try {
//            FileInputStream inputStream = getApplicationContext().openFileInput("invoice.xml");
//            FileInputStream srcFileStream = new FileInputStream(inputStream);
            boolean status = ftpClient.storeFile("remote ftp path",
                    downloadFile);
            Log.e("Status", String.valueOf(status));
            downloadFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createxmlFile() {

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput(m_id + "invoice.xml", Context.MODE_PRIVATE));
            outputStreamWriter.write(sendXml.getXml());
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

    }

}
