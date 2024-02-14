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

public class BestPlacesAdapter extends RecyclerView.Adapter<BestPlacesAdapter.ViewHolder> {
    private List<PlaceModel> bestPlaces;
    private Context context;

    public BestPlacesAdapter(Context context, List<PlaceModel> bestPlaces) {
        this.context = context;
        this.bestPlaces = bestPlaces;
    }

    public void updateList(List<PlaceModel> filteredList) {
        bestPlaces.clear();
        bestPlaces.addAll(filteredList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bestplaces, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlaceModel place = bestPlaces.get(position);
        holder.nameTextView.setText(place.getName());
        holder.locationTextView.setText(place.getCity()+ ", " + place.getCountry());
        holder.priceTextView.setText(place.getPrice());

        Picasso.get().load(place.getImage()).into(holder.imageView);

        holder.totalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start details activity and pass data using bundle
                Intent intent = new Intent(context, Details_Activity.class);
                intent.putExtra("placeName", place.getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bestPlaces.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView locationTextView;
        TextView priceTextView;
        ImageView imageView;
        RelativeLayout totalLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textView1);
            locationTextView = itemView.findViewById(R.id.textView2);
            priceTextView = itemView.findViewById(R.id.textView3);
            imageView = itemView.findViewById(R.id.backgroundImageView);
            totalLayout = itemView.findViewById(R.id.Totallayout);
        }
    }
}
