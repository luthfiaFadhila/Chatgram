package com.example.whatsapp;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private List<ChatItem> chatList;
    private Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ChatItem item);
    }

    public ChatListAdapter(List<ChatItem> chatList, Context context, OnItemClickListener listener) {
        this.chatList = chatList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatItem item = chatList.get(position);

        holder.tvName.setText(item.getName());
        holder.tvLastMessage.setText(item.getLastMessage());
        holder.tvTime.setText(item.getTime());

        if (item.getUnreadCount() > 0) {
            holder.tvUnreadCount.setVisibility(View.VISIBLE);
            holder.tvUnreadCount.setText(String.valueOf(item.getUnreadCount()));
        } else {
            holder.tvUnreadCount.setVisibility(View.GONE);
        }

        if (item.getProfileUrl() != null && !item.getProfileUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getProfileUrl())
                    .placeholder(R.drawable.ic_profile) // default sementara loading
                    .error(R.drawable.ic_profile) // jika error
                    .into(holder.imgProfile);
        } else {
            holder.imgProfile.setImageResource(R.drawable.ic_profile);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfile;
        TextView tvName, tvLastMessage, tvTime, tvUnreadCount;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            tvName = itemView.findViewById(R.id.tvName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvUnreadCount = itemView.findViewById(R.id.tvUnreadCount);
        }
    }
}
