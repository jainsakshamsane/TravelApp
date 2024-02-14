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
import com.travelapp.Models.Location;
import com.travelapp.R;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
    private List<Location> locationList;
    private Context context;

    public LocationAdapter(Context context, List<Location> locationList) {
        this.context = context;
        this.locationList = locationList;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location location = locationList.get(position);
        holder.nameTextView.setText(location.getName());
        holder.cityTextView.setText(location.getCity());
        holder.informationTextView.setText(location.getInformation());

        // Load image using Picasso
        Picasso.get().load(location.getImage())
                .placeholder(R.drawable.authorrr) // Placeholder image while loading
                .error(R.drawable.authorrr) // Image to show if loading fails
                .into(holder.imageView);

        holder.totalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start details activity and pass data using bundle
                Intent intent = new Intent(context, Details_Activity.class);
                intent.putExtra("placeName", location.getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView cityTextView;
        TextView informationTextView;
        ImageView imageView;
        RelativeLayout totalLayout;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.NameTextview);
            cityTextView = itemView.findViewById(R.id.LocationTextview);
            informationTextView = itemView.findViewById(R.id.SeasonTextview);
            imageView = itemView.findViewById(R.id.imageView);
            totalLayout = itemView.findViewById(R.id.Totallayout);
        }
    }
}
