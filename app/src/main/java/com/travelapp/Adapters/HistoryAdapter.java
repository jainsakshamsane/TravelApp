package com.travelapp.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.travelapp.Chat_Activity;
import com.travelapp.Details_Activity;
import com.travelapp.Models.PlacesModel;
import com.travelapp.R;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{
    private List<PlacesModel> placeModelList;
    private Context context;

    public HistoryAdapter(Context context, List<PlacesModel> placeModelList) {
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

        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Chat_Activity.class);
                intent.putExtra("Name", holder.name);
                intent.putExtra("placename", placeModel.getName());
                intent.putExtra("senderid", holder.loggedInUserId);
                intent.putExtra("placeid", placeModel.getId());
                Log.e("Allhistory", "Error all " +holder.name+"**"+ holder.loggedInUserId+"**"+ placeModel.getName()+"**"+placeModel.getId());

                context.startActivity(intent);
            }
        });
        holder.Totallayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the name of the place
                String placeName = placeModel.getName();

                // Start details activity and pass the name of the place
                Intent intent = new Intent(context, Details_Activity.class);
                intent.putExtra("placeName", placeName);
                context.startActivity(intent);
            }
        });
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
        String loggedInUserId,name;

        RelativeLayout Totallayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            locationTextView = itemView.findViewById(R.id.location);
            priceTextView = itemView.findViewById(R.id.price);
            imageView = itemView.findViewById(R.id.image);
            Totallayout = itemView.findViewById(R.id.Totallayout);
            chat = itemView.findViewById(R.id.chat);
            SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences("userdetails", MODE_PRIVATE);
            loggedInUserId = sharedPreferences.getString("userid", "");
            name = sharedPreferences.getString("fullname", "");
        }
    }
}
