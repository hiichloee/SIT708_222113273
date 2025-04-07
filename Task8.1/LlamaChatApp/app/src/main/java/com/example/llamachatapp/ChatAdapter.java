package com.example.llamachatapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isUser ? 1 : 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == 1)
                ? R.layout.item_message_user
                : R.layout.item_message_bot;

        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.messageTextView.setText(message.text);
        // Set different backgrounds or alignments based on isUser.

        // Set sender initials (uppercase)
        String initial = message.senderName.substring(0, 1).toUpperCase();
        holder.avatarText.setText(initial);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout messageContainer;
        TextView avatarText;
        TextView messageTextView;

        ViewHolder(View view) {
            super(view);
            messageContainer = itemView.findViewById(R.id.messageContainer);
            avatarText = itemView.findViewById(R.id.avatarText);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }

}