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
import com.travelapp.Models.TravelDestination;
import com.travelapp.R;

import java.util.List;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ExploreViewHolder> {
    private List<TravelDestination> exploreDestinations;
    private Context context;

    public ExploreAdapter(Context context, List<TravelDestination> exploreDestinations) {
        this.context = context;
        this.exploreDestinations = exploreDestinations;
    }

    public void updateList(List<TravelDestination> filteredList) {
        exploreDestinations.clear();
        exploreDestinations.addAll(filteredList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExploreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_explore, parent, false);
        return new ExploreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreViewHolder holder, int position) {
        TravelDestination destination = exploreDestinations.get(position);
        holder.nameTextView.setText(destination.getName());
        holder.priceTextView.setText("Price: " + destination.getPrice());
        holder.informationTextView.setText(destination.getInformation());
        holder.locationTextView.setText(destination.getCity() + ", " + destination.getCountry());

        Picasso.get().load(destination.getImage()).into(holder.imageView);
        holder.totalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start details activity and pass data using bundle
                Intent intent = new Intent(context, Details_Activity.class);
                intent.putExtra("placeName", destination.getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exploreDestinations.size();
    }

    public static class ExploreViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView priceTextView;
        TextView informationTextView;
        TextView locationTextView;
        ImageView imageView;
        RelativeLayout totalLayout;

        public ExploreViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nametextView1);
            priceTextView = itemView.findViewById(R.id.textView3);
            informationTextView = itemView.findViewById(R.id.desceriptiontextView12);
            locationTextView = itemView.findViewById(R.id.textView2);
            imageView = itemView.findViewById(R.id.backgroundImageView);
            totalLayout = itemView.findViewById(R.id.Totallayout);

        }
    }
}
