package com.travelapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.travelapp.Chat_Activity;
import com.travelapp.Models.Chat;
import com.travelapp.Models.User;
import com.travelapp.R;

import java.util.List;

public class ChatsRecyclerViewAdapter extends RecyclerView.Adapter<ChatsRecyclerViewAdapter.ViewHolder> {
    private List<Chat> chatList;
    private Context context;
    private String loggedInUserId;

    public ChatsRecyclerViewAdapter(Context context, List<Chat> chatList, String loggedInUserId) {
        this.context = context;
        this.chatList = chatList;
        this.loggedInUserId = loggedInUserId;
    }

    public void updateList(List<Chat> filteredList) {
        chatList = filteredList;
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
        Chat chat = chatList.get(position);
        String placeName = chat.getPlaceName() != null ? chat.getPlaceName() : "";

        // Determine sender's name and ID
        String senderName, senderId;
        if (loggedInUserId.equals(chat.getSenderId())) {
            senderName = chat.getRecipientName() != null ? chat.getRecipientName() : "";
            senderId = chat.getRecipientId() != null ? chat.getRecipientId() : "";
        } else {
            senderName = chat.getSenderName() != null ? chat.getSenderName() : "";
            senderId = chat.getSenderId() != null ? chat.getSenderId() : "";
        }

        // Concatenate senderName and placeName
        String displayName = senderName + " - " + placeName;

        // Set the display name
        holder.topTextView.setText(displayName);
        holder.bottomTextView.setText(chat.getText());
        holder.timetextview.setText(chat.getTime());

        // Load sender's image
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = usersRef.orderByChild("userid").equalTo(senderId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String imageUrl = userSnapshot.child("imageurl").getValue(String.class);
                        if (imageUrl != null) {
                            // Load image using Picasso
                            Picasso.get().load(imageUrl)
                                    .placeholder(R.drawable.authorrr) // Placeholder image while loading
                                    .error(R.drawable.authorrr) // Image to show if loading fails
                                    .into(holder.leftImageView);
                        }
                    }
                } else {
                    // If no user found with the given name, you can load a default image or handle it as per your requirement
                    // For example, loading a placeholder image
                    //holder.leftImageView.setImageResource(R.drawable.authorrr);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
                Log.e("ChatsAdapter", "Error loading sender image: " + databaseError.getMessage());
            }
        });

        // Handle click listener
        holder.specifiedperson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Chat_Activity.class);
                intent.putExtra("Name", senderName);
                intent.putExtra("placename", chat.getPlaceName());
                intent.putExtra("senderid", senderId);
                intent.putExtra("placeid", chat.getPlaceid());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView leftImageView;
        TextView topTextView;
        TextView bottomTextView, timetextview;
        RelativeLayout specifiedperson;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            leftImageView = itemView.findViewById(R.id.leftImageView);
            topTextView = itemView.findViewById(R.id.topTextView);
            timetextview = itemView.findViewById(R.id.timetextview);
            bottomTextView = itemView.findViewById(R.id.bottomTextView);
            specifiedperson = itemView.findViewById(R.id.specifiedperson);
        }
    }
}
