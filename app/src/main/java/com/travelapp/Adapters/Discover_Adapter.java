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
import com.travelapp.Details_Activity;
import com.travelapp.Models.PlaceModel;
import com.travelapp.R;

import java.util.List;

public class Discover_Adapter extends RecyclerView.Adapter<Discover_Adapter.ViewHolder> {
    private List<PlaceModel> places;
    private Context context;

    public Discover_Adapter(Context context, List<PlaceModel> places) {
        this.context = context;
        this.places = places;
    }

    // Inside Discover_Adapter

    public void updateList(List<PlaceModel> filteredList) {
        places = filteredList;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public Discover_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_allplaces, parent, false);
        return new Discover_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Discover_Adapter.ViewHolder holder, int position) {
        PlaceModel place = places.get(position);
        // Bind data to views
        holder.nameTextView.setText(place.getName());
        holder.locationTextView.setText(place.getCity());
//        holder.locationTextView.setText(place.getCity() + ", " + place.getCountry());
        holder.noOfDaysTextView.setText(place.getNo_of_days() + " days");
        holder.seasonTextView.setText(place.getSeason());
        holder.priceTextView.setText(place.getPrice());

        // Load image using Picasso
        Picasso.get().load(place.getImage()).into(holder.imageView);
        holder.Totallayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the name of the place
                String placeName = place.getName();

                // Start details activity and pass the name of the place
                Intent intent = new Intent(context, Details_Activity.class);
                intent.putExtra("placeName", placeName);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView locationTextView;
        TextView noOfDaysTextView;
        TextView seasonTextView;
        TextView priceTextView;
        ImageView imageView;
        RelativeLayout Totallayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.NameTextview);
            locationTextView = itemView.findViewById(R.id.LocationTextview);
            noOfDaysTextView = itemView.findViewById(R.id.NoofdaysTextview);
            seasonTextView = itemView.findViewById(R.id.SeasonTextview);
            priceTextView = itemView.findViewById(R.id.PriceTextview);
            imageView = itemView.findViewById(R.id.imageView);
            Totallayout = itemView.findViewById(R.id.Totallayout);
        }
    }
}
