package com.android.msqhealthpoc1.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.msqhealthpoc1.R;
import com.android.msqhealthpoc1.model.Cart;
import com.android.msqhealthpoc1.model.CartItem;
import com.android.msqhealthpoc1.model.Product;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sihlemabaleka on 9/23/17.
 */

public class PaymentGatewayWebView extends AppCompatActivity {

    private WebView webView;

    private Intent intent;
    private ProgressDialog pDialog;


    private FirebaseUser user;
    List<Map<String, Object>> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.webview);

        user = FirebaseAuth.getInstance().getCurrentUser();

        pDialog = new ProgressDialog(PaymentGatewayWebView.this);
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Preparing Cart");
        pDialog.show();

        webView = (WebView) findViewById(R.id.webview);

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new MyBrowser());
        getURLPostData();

    }

    public void getURLPostData() {

        FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("cart").child("cart-items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
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

                Map<String, Object> postValues = cart.toMap();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/carts/checked-out/" + user.getUid() + "/" + key, postValues);
                childUpdates.put("/users/" + user.getUid() + "/carts/checked-out/" + key, postValues);

                FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            StringBuilder builder = null;
                            String postParams = "Mode=0&" +
                                    "MerchantID=F5785ECF-1EAE-40A0-9D37-93E2E8A4BAB3&" +
                                    "ApplicationID=C572C9CC-F2C8-4DC8-AC5E-48784B83AB35&" +
                                    "MerchantReference=" + user.getUid() + "1&" +
                                    "Amount=" + amount + "&" +
                                    "RedirectSuccessfulURL=http://akacia.co.za" + "&" +
                                    "RedirectFailedURL=https://virtual.mygateglobal.com/success_failure.php&" +
                                    "txtCurrencyCode=ZAR&";
                            builder = new StringBuilder(postParams);
                            builder.deleteCharAt(builder.length() - 1);

                            if (builder != null) {
                                webView.postUrl("https://virtual.mygateglobal.com/PaymentPage.cfm", EncodingUtils.getBytes(builder.toString(), "BASE64"));
                            } else {
                                System.out.println("String builder is null");
                            }


                        } else {
                            task.getException().printStackTrace();
                        }
                    }
                });
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
                            dataSnapshot.getRef().removeValue();
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


}
