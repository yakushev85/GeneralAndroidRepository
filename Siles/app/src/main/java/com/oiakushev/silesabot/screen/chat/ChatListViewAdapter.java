package com.oiakushev.silesabot.screen.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oiakushev.silesabot.R;
import com.oiakushev.silesabot.tool.SettingsStorage;
import com.oiakushev.silesabot.model.datamodel.Message;

import java.util.ArrayList;

public class ChatListViewAdapter extends RecyclerView.Adapter<ChatListViewAdapter.ChatListViewHolder> {
    private ArrayList<Message> messages;
    private Context context;

    public ChatListViewAdapter(Context context, ArrayList<Message> messages) {
        this.messages = messages;
        this.context = context;
    }

    class ChatListViewHolder extends RecyclerView.ViewHolder {
        TextView chatItemView;

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);

            chatItemView = itemView.findViewById(R.id.chatItemView);
        }
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.chat_item_view, parent, false);
        return new ChatListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {
        Message message = messages.get(position);

        if (message.getAuthor() == Message.SILES) {
            String messageText =
                    (SettingsStorage.Instance.isNameInMessage())?
                            "[Силес] " + message.getMessage():message.getMessage();
            holder.chatItemView.setText(messageText);
            holder.chatItemView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        } else {
            String messageText =
                    (SettingsStorage.Instance.isNameInMessage())?
                            message.getMessage() + " [Вы]":message.getMessage();
            holder.chatItemView.setText(messageText);
            holder.chatItemView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
