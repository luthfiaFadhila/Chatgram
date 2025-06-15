package com.example.whatsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    List<ChatMessage> list;
    String currentUid;

    public ChatAdapter(List<ChatMessage> list, String currentUid) {
        this.list = list;
        this.currentUid = currentUid;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage, txtStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtStatus = itemView.findViewById(R.id.txtStatus); // untuk status pesan
        }
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).senderId.equals(currentUid) ? 1 : 0;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == 1) ? R.layout.item_sent : R.layout.item_received;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = list.get(position);
        holder.txtMessage.setText(message.message);
        if (getItemViewType(position) == 1 && holder.txtStatus != null) {
            holder.txtStatus.setVisibility(View.VISIBLE);
            holder.txtStatus.setText(message.isRead ? "Terlhat" : "Terkirim");
        }
    }

    public int getItemCount() {
        return list.size();
    }
}
