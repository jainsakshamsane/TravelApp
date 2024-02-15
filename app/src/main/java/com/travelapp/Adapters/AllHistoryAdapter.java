package com.travelapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.travelapp.Models.PlaceModel;
import com.travelapp.Models.PlacesModel;
import com.travelapp.R;

import java.util.List;

public class AllHistoryAdapter extends RecyclerView.Adapter<AllHistoryAdapter.ViewHolder> {
    private List<PlacesModel> placeModelList;
    private Context context;

    public AllHistoryAdapter(Context context, List<PlacesModel> placeModelList) {
        this.context = context;
        this.placeModelList = placeModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_allhistory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlacesModel placeModel = placeModelList.get(position);

        // Bind data to views
        holder.nameTextView.setText(placeModel.getName());
        holder.locationTextView.setText(placeModel.getCity() + ", " + placeModel.getCountry());
        holder.priceTextView.setText("Rs." + placeModel.getPrice());

        Picasso.get().load(placeModel.getImage()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return placeModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView locationTextView;
        TextView priceTextView;
        ImageView imageView, chat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            locationTextView = itemView.findViewById(R.id.location);
            priceTextView = itemView.findViewById(R.id.price);
            imageView = itemView.findViewById(R.id.image);
            chat = itemView.findViewById(R.id.chat);
        }
    }
}
