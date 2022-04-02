package com.application.pm1_proyecto_final.adapters;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.application.pm1_proyecto_final.databinding.ItemContainerReceivedMessageBinding;
import com.application.pm1_proyecto_final.databinding.ItemContainerSentMessageBinding;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.pm1_proyecto_final.models.Chat;
import com.application.pm1_proyecto_final.utils.ResourceUtil;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Chat> chatList;
    private final String senderId;
    private final String nameUser;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(List<Chat> chatList, String senderId, String nameUser) {
        this.chatList = chatList;
        this.senderId = senderId;
        this.nameUser = nameUser;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        } else {
            return new ReceivedMessageViewHolder(
                ItemContainerReceivedMessageBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatList.get(position));
        } else {
            ((ReceivedMessageViewHolder) holder).setData(chatList.get(position), nameUser);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatList.get(position).getSenderId().equals(senderId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setData(Chat chatMessage) {
            binding.textMessage.setText(chatMessage.getMessage());
            binding.textDateTimeMessageSend.setText(chatMessage.getDateTime());
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void setData(Chat chatMessage, String nameUser) {
            binding.textMessage.setText(chatMessage.getMessage());
            binding.textDateTimeMessageReceived.setText(chatMessage.getDateTime());
            String[] infoUser = nameUser.split(" ");
            binding.imageProfileChatReceived.setText(ResourceUtil.letterIcon(infoUser[0], infoUser[1]));
        }
    }

}
