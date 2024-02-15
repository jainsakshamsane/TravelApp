package com.travelapp.Adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.travelapp.Models.Message;
import com.travelapp.R;

import java.util.List;

public class UserMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messageList;
    private String loggedInUserId;
    private Context context;

    private static final int VIEW_TYPE_TEXT_RECEIVER = 1;
    private static final int VIEW_TYPE_TEXT_SENDER = 2;
    private static final int VIEW_TYPE_IMAGE_RECEIVER = 3;
    private static final int VIEW_TYPE_IMAGE_SENDER = 4;

    public UserMessageAdapter(List<Message> messageList, String loggedInUserId, Context context) {
        this.messageList = messageList;
        this.loggedInUserId = loggedInUserId;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_TEXT_RECEIVER:
                View receiverTextView = inflater.inflate(R.layout.receiver_message_item, parent, false);
                return new TextReceiverViewHolder(receiverTextView);
            case VIEW_TYPE_TEXT_SENDER:
                View senderTextView = inflater.inflate(R.layout.sender_message_item, parent, false);
                return new TextSenderViewHolder(senderTextView);
            case VIEW_TYPE_IMAGE_RECEIVER:
                View receiverImageView = inflater.inflate(R.layout.image_receiver_message_item, parent, false);
                return new ImageReceiverViewHolder(receiverImageView);
            case VIEW_TYPE_IMAGE_SENDER:
                View senderImageView = inflater.inflate(R.layout.image_sender_message_item, parent, false);
                return new ImageSenderViewHolder(senderImageView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_TEXT_RECEIVER:
                ((TextReceiverViewHolder) holder).bind(message);
                break;
            case VIEW_TYPE_TEXT_SENDER:
                ((TextSenderViewHolder) holder).bind(message);
                break;
            case VIEW_TYPE_IMAGE_RECEIVER:
                ((ImageReceiverViewHolder) holder).bind(message);
                break;
            case VIEW_TYPE_IMAGE_SENDER:
                ((ImageSenderViewHolder) holder).bind(message);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
            // Determine if the message is an image message
            return (loggedInUserId.equals(message.getSenderId())) ? VIEW_TYPE_IMAGE_SENDER : VIEW_TYPE_IMAGE_RECEIVER;
        } else {
            // Determine if the message is a text message
            if (message.getSenderId() != null && loggedInUserId.equals(message.getSenderId())) {
                return VIEW_TYPE_TEXT_SENDER;
            } else {
                return VIEW_TYPE_TEXT_RECEIVER;
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class TextReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMessageTextView;
        ImageView receiverImageView;

        TextReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMessageTextView = itemView.findViewById(R.id.receiverMessageTextView);
            receiverImageView = itemView.findViewById(R.id.receiverImageView);
        }

        void bind(Message message) {
            receiverMessageTextView.setText(message.getText());

            // Load image from "places" node based on the "placeId" associated with the message
            DatabaseReference placesRef = FirebaseDatabase.getInstance().getReference().child("places");
            Query query = placesRef.orderByChild("id").equalTo(message.getPlaceid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                            String imageUrl = placeSnapshot.child("image").getValue(String.class);
                            if (imageUrl != null) {
                                // Load image using Picasso
                                Picasso.get().load(imageUrl)
                                        .placeholder(R.drawable.authorrr) // Placeholder image while loading
                                        .error(R.drawable.authorrr) // Image to show if loading fails
                                        .into(receiverImageView);
                            }
                        }
                    } else {
                        // If no place found with the given placeId, you can load a default image or handle it as per your requirement
                        // For example, loading a placeholder image
                        receiverImageView.setImageResource(R.drawable.authorrr);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors
                    Log.e("UserMessageAdapter", "Error loading image: " + databaseError.getMessage());
                }
            });
        }
    }

    static class TextSenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMessageTextView;

        TextSenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessageTextView = itemView.findViewById(R.id.senderMessageTextView);
        }

        void bind(Message message) {
            senderMessageTextView.setText(message.getText());
        }
    }

    static class ImageReceiverViewHolder extends RecyclerView.ViewHolder {
        ImageView receiverImageView;
        ProgressBar receiverLoadingIndicator;

        ImageReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverImageView = itemView.findViewById(R.id.receiverImageView);
            receiverLoadingIndicator = itemView.findViewById(R.id.receiverLoadingIndicator);
        }

        void bind(Message message) {
            if (!message.getImageUrl().isEmpty()) {
                receiverLoadingIndicator.setVisibility(View.VISIBLE);
                receiverImageView.setVisibility(View.VISIBLE);
                Picasso.get().load(message.getImageUrl()).into(receiverImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        receiverLoadingIndicator.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        receiverLoadingIndicator.setVisibility(View.GONE);
                    }
                });
            } else {
                receiverImageView.setVisibility(View.GONE);
                receiverLoadingIndicator.setVisibility(View.GONE);
            }
        }
    }

    static class ImageSenderViewHolder extends RecyclerView.ViewHolder {
        ImageView senderImageView;
        ProgressBar senderLoadingIndicator;

        ImageSenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderImageView = itemView.findViewById(R.id.senderImageView);
            senderLoadingIndicator = itemView.findViewById(R.id.senderLoadingIndicator);
        }

        void bind(Message message) {
            if (!message.getImageUrl().isEmpty()) {
                senderLoadingIndicator.setVisibility(View.VISIBLE);
                senderImageView.setVisibility(View.VISIBLE);
                Picasso.get().load(message.getImageUrl()).into(senderImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        senderLoadingIndicator.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        senderLoadingIndicator.setVisibility(View.GONE);
                    }
                });
            } else {
                senderImageView.setVisibility(View.GONE);
                senderLoadingIndicator.setVisibility(View.GONE);
            }
        }
    }
}
