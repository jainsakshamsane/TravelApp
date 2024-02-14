package com.travelapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.travelapp.Chat_Activity;

import com.travelapp.Models.User;
import com.travelapp.R;

import java.util.List;

public class ChatsRecyclerViewAdapter extends RecyclerView.Adapter<ChatsRecyclerViewAdapter.ViewHolder> {
    private List<User> userList;
    private Context context;

    public ChatsRecyclerViewAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }
    public void updateList(List<User> filteredList) {
        userList = filteredList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        holder.userNameTextView.setText(user.getName());

        Picasso.get().load(user.getImage())
                .placeholder(R.drawable.authorrr)
                .error(R.drawable.authorrr)
                .into(holder.userImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        // Image loaded successfully
                    }

                    @Override
                    public void onError(Exception e) {
                        // Handle error
                        Log.e("Picasso", "Error loading image: " + e.getMessage());
                    }
                });

        holder.specifiedperson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Chat_Activity.class);
                intent.putExtra("Name", user.getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImageView;
        TextView userNameTextView;
        RelativeLayout specifiedperson;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.leftImageView);
            userNameTextView = itemView.findViewById(R.id.topTextView);
            specifiedperson = itemView.findViewById(R.id.specifiedperson);
        }
    }
}
