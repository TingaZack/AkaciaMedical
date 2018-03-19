package com.msqhealth.main.app;

import android.app.Application;

import com.msqhealth.main.model.Cart;
import com.msqhealth.main.model.Product;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by sihlemabaleka on 7/8/17.
 */

public class AppController extends Application {


    private ArrayList<Product> myproducts = new ArrayList<Product>();
    private Cart myCart = new Cart();

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public Product getProducts(int pPosition) {
        return myproducts.get(pPosition);
    }

    public void setProducts(Product products) {
        myproducts.add(products);
    }

    public Cart getCart() {
        return myCart;
    }

    public int getProductArraylistsize() {
        return myproducts.size();
    }
}
