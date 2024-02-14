package com.travelapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.travelapp.Details_Activity;
import com.travelapp.Models.TravelDestination;
import com.travelapp.R;

import java.util.List;

public class ProfilePost1Adapter extends RecyclerView.Adapter<ProfilePost1Adapter.ViewHolder> {
    private List<TravelDestination> destinationsList;
    private Context context;

    public ProfilePost1Adapter(Context context, List<TravelDestination> destinationsList) {
        this.context = context;
        this.destinationsList = destinationsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_images, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TravelDestination destination = destinationsList.get(position);
        Picasso.get().load(destination.getImage()).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log the name before starting the Details_Activity
                Log.d("ImageAdapter", "Clicked on image with place name: " + destination.getName());

                // Start details activity and pass data using bundle
                Intent intent = new Intent(context, Details_Activity.class);
                intent.putExtra("placeName", destination.getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return destinationsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
