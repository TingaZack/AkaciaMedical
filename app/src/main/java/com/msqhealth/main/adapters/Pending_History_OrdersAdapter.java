package com.msqhealth.main.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msqhealth.main.R;
import com.msqhealth.main.activities.FeaturedItemDetails;
import com.msqhealth.main.activities.ViewOrdersActivity;
import com.msqhealth.main.activities.orders.ActivityOrdersView;
import com.msqhealth.main.model.Cart;
import com.msqhealth.main.model.CartItem;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Admin on 26/03/2018.
 */

public class Pending_History_OrdersAdapter extends RecyclerView.Adapter<Pending_History_OrdersAdapter.ViewHolderOrders> {

    private final List<Cart> mValues;
    private Activity activity;

    private DataSnapshot dataSnapshot;

    public Pending_History_OrdersAdapter(List<Cart> items, Activity activity) {
        mValues = items;
        this.activity = activity;
    }

    @Override
    public ViewHolderOrders onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_orders_fragment, parent, false);
        return new ViewHolderOrders(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolderOrders holder, final int position) {

        long timeStamp = mValues.get(position).getTimeStamp();
        System.out.println("TIME: " + mValues.get(position).getTimeStamp());
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(timeStamp);
        String date = DateFormat.format("dd-MM-yyyy HH:mm:ss", cal).toString();

        holder.mInvoiceDate.setText(String.valueOf(date));
        holder.mInvoiceNumber.setText(mValues.get(position).getInvoice_number());
        DecimalFormat df = new DecimalFormat("##.##");
        holder.mInvoiceTotalPrice.setText("R" + String.valueOf(df.format(mValues.get(position).getSubtotal())).replace(",", "."));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ActivityOrdersView.class);
                intent.putExtra("invoice_number", mValues.get(position).getInvoice_number());
                activity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolderOrders extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mInvoiceNumber;
        public final TextView mInvoiceDate;
        public final TextView mInvoiceTotalPrice;

        public ViewHolderOrders(View view) {
            super(view);
            mView = view;

            mInvoiceNumber = mView.findViewById(R.id.invoice_number);
            mInvoiceDate = mView.findViewById(R.id.invoice_date);
            mInvoiceTotalPrice = mView.findViewById(R.id.invoice_total_price);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mInvoiceNumber.getText() + "'";
        }
    }

}
