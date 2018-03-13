package com.android.msqhealthpoc1.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.msqhealthpoc1.R;
import com.android.msqhealthpoc1.helpers.GMailSender;
import com.android.msqhealthpoc1.model.Cart;
import com.android.msqhealthpoc1.model.CartItem;
import com.android.msqhealthpoc1.model.Product;
import com.chilkatsoft.CkXml;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.util.EncodingUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sihlemabaleka on 9/23/17.
 */

public class PaymentGatewayWebView extends AppCompatActivity {

    List<Map<String, Object>> items = new ArrayList<>();
    private WebView webView;
    private Intent intent;
    private ProgressDialog pDialog;
    private FirebaseUser user;
    CkXml sendXml;
    double _amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.webview);
        sendXml = new CkXml();
        sendXml.put_Tag("Invoice");

        user = FirebaseAuth.getInstance().getCurrentUser();

        pDialog = new ProgressDialog(PaymentGatewayWebView.this);
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Preparing Cart");

        webView = findViewById(R.id.webview);

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new MyBrowser());
        sendXml = new CkXml();
        getURLPostData();

    }

    public void getURLPostData() {

        final CkXml xml = new CkXml();


        FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sendXml.NewChild("Particulars|Practice Name", dataSnapshot.child("Name").getValue().toString());
                sendXml.NewChild("Particulars|Date", new Date().toString());
                sendXml.NewChild("Particulars|Order Person", dataSnapshot.child("Name").getValue().toString());
                sendXml.NewChild("Particulars|Practice Phone", dataSnapshot.child("Telephone").getValue().toString());
                sendXml.NewChild("Particulars|Practice Address", dataSnapshot.child("Suburb").getValue().toString());
                sendXml.NewChild("Delivery|Delivery Address", dataSnapshot.child("billing_infomation").child("delivery_address").getValue().toString());
                sendXml.NewChild("Delivery|Contact Person", dataSnapshot.child("billing_infomation").child("full_names").getValue().toString());
                sendXml.NewChild("Delivery|Contact Number", dataSnapshot.child("billing_infomation").child("phone_number").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("cart").child("cart-items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                double total = 0;
                int i = 1;
                sendXml.put_Tag("Product");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    _amount = 0;
                    CartItem cartItem = new CartItem();

                    _amount = _amount + ((Double.parseDouble(String.valueOf(snapshot.child("product").child("price").getValue()))) * (Integer.parseInt(snapshot.child("quantity").getValue().toString())));
                    total = total +_amount;
                    xml.put_Tag("Row");
                    xml.NewChild("No", String.valueOf(i));
                    xml.NewChild("Part Nr", snapshot.child("product").child("code").getValue().toString());
                    xml.NewChild("Product Description", snapshot.child("product").child("description").getValue().toString());
                    xml.NewChild("Qty", snapshot.child("quantity").getValue().toString());
                    xml.NewChild("Unit Rand Price", snapshot.child("product").child("price").getValue().toString());
                    xml.NewChild("Total Rand Price", String.valueOf(_amount));

                    sendXml.AddChildTree(xml);
                    System.out.println( sendXml.getXml()+"\n\n\n\n");
                    xml.Clear();

                    cartItem.setQuantity(Integer.parseInt(snapshot.child("quantity").getValue().toString()));

                    Product product = new Product();
                    product.setCode(snapshot.child("product").child("code").getValue().toString());
                    product.setConsumables(snapshot.child("product").child("consumables").getValue().toString());
                    product.setDescription(snapshot.child("product").child("description").getValue().toString());
                    product.setPrice(Double.parseDouble(snapshot.child("product").child("price").getValue().toString()));
                    product.setTrueImageUrl(snapshot.child("product").child("trueImageUrl").getValue().toString());
                    product.setUnit_of_messuremeant(snapshot.child("product").child("unit_of_messuremeant").getValue().toString());
                    cartItem.setProduct(product);
                    items.add(cartItem.toMap());
                    System.out.println("Amount is " + _amount);
                    i++;
                }
                sendXml.NewChild("Total", String.valueOf(total));


                StringBuilder builder = null;
                try {
                    String postParams = "Mode=0&" +
                            "MerchantID=F5785ECF-1EAE-40A0-9D37-93E2E8A4BAB3&" +
                            "ApplicationID=C572C9CC-F2C8-4DC8-AC5E-48784B83AB35&" +
                            "MerchantReference=" + user.getUid() + "1&" +
                            "Amount=" + total + "&" +
                            "RedirectSuccessfulURL=http://akacia.co.za" + "&" +
                            "RedirectFailedURL=https://virtual.mygateglobal.com/success_failure.php&" +
                            "txtCurrencyCode=ZAR&";
                    builder = new StringBuilder(postParams);
                    builder.deleteCharAt(builder.length() - 1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                pDialog.dismiss();

                if (builder != null) {
                    webView.postUrl("https://virtual.mygateglobal.com/PaymentPage.cfm", EncodingUtils.getBytes(builder.toString(), "BASE64"));
                } else {
                    System.out.println("String builder is null");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class MyBrowser extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (url.startsWith("http://akacia.co.za")) {
                System.out.println("Request Intercepted");

                finish();
                FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("cart").child("cart-items").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {

                            double _amount = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
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
                                items.add(cartItem.toMap());
                                System.out.println("Amount is " + _amount);
                            }

                            final double amount = _amount;

                            String key = FirebaseDatabase.getInstance().getReference().child("carts").push().getKey();

                            final Cart cart = new Cart();
                            cart.setItems(items);
                            cart.setSubtotal(amount);
                            cart.setUserID(user.getUid());

                            final Map<String, Object> postValues = cart.toMap();

                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/carts/checked-out/" + user.getUid() + "/" + key, postValues);
                            childUpdates.put("/users/" + user.getUid() + "/carts/checked-out/" + key, postValues);

                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        GMailSender sender = new GMailSender("mosecoza@gmail.com", "moses@357");
                                        sender.sendMail("Checked Out Invoice",
                                                sendXml.getXml(),
                                                "mosecoza@gmail.com",
                                                "info@buildhealth.co.za");
                                    } catch (Exception e) {
                                        Log.e("++++++error____SendMail", e.getMessage(), e);
                                    }
                                }

                            }).start();

                            FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        dataSnapshot.getRef().removeValue();

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

        @Override
        public void onPageFinished(WebView view, String url) {
            pDialog.dismiss();
            if (url.equals("http://akacia.co.za")) {
                Toast.makeText(PaymentGatewayWebView.this, "Payment Completed Successfully", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


    static {
        System.loadLibrary("chilkat");

        // Note: If the incorrect library name is passed to System.loadLibrary,
        // then you will see the following error message at application startup:
        //"The application <your-application-name> has stopped unexpectedly. Please try again."
    }
}