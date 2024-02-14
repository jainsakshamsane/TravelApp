package com.travelapp.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.travelapp.Models.CardModel;
import com.travelapp.R;

import java.util.ConcurrentModificationException;
import java.util.List;

public class DebitCardsAdapter extends RecyclerView.Adapter<DebitCardsAdapter.ViewHolder> {

    private List<CardModel> cardList;             //actual change kuch nhi hua hai
    private Context context;

    public DebitCardsAdapter(List<CardModel> cardList, Context context) {
        this.cardList = cardList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_cards, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final CardModel users = cardList.get(position);
        holder.cardnumber.setText(users.getCard_number());
        holder.price.setText(users.getTotal_price());
        holder.expirydate.setText(users.getExpiry_date());
        holder.price.setText("Rs." + users.getTotal_price());

        SharedPreferences sharedPreferencesss = context.getSharedPreferences("carddetails", MODE_PRIVATE);
        String fullName = sharedPreferencesss.getString("name", "");

        holder.cardholdername.setText(fullName);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    // Define the ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView cardnumber, price, cardholdername, expirydate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardnumber = itemView.findViewById(R.id.cardnumber);
            price = itemView.findViewById(R.id.price);
            cardholdername = itemView.findViewById(R.id.cardholdername);
            expirydate = itemView.findViewById(R.id.expirydate);
        }
    }
}
