package com.example.conectamobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messageList = new ArrayList<>();

    public void addMessage(Message message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.nicknameTextView.setText(message.getNickname());
        holder.messageTextView.setText(message.getMessage());

        // Cargar la foto de perfil usando Glide
        if (message.getPhotoUrl() != null && !message.getPhotoUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(message.getPhotoUrl()).into(holder.profileImageView);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class Message {
        private String message;
        private String nickname;
        private String photoUrl;

        public Message(String message, String nickname, String photoUrl) {
            this.message = message;
            this.nickname = nickname;
            this.photoUrl = photoUrl;
        }

        public String getMessage() {
            return message;
        }

        public String getNickname() {
            return nickname;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView nicknameTextView;
        TextView messageTextView;
        ImageView profileImageView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            nicknameTextView = itemView.findViewById(R.id.nicknameTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
        }
    }
}
