package com.example.whatsapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class KontakAdapter extends RecyclerView.Adapter<KontakAdapter.KontakViewHolder> {

    private List<User> kontakList;

    public interface OnItemClickListener {
        void onItemClick(User user);
    }
    private OnItemClickListener listener;

    @NonNull
    @Override
    public KontakViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kontak, parent, false);
        return new KontakViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KontakViewHolder holder, int position) {
        User user = kontakList.get(position);
        holder.tvName.setText(user.getName());
        holder.tvPhone.setText(user.getPhone());
        holder.imgProfile.setImageResource(R.drawable.ic_profile); // icon default

        holder.itemView.setOnClickListener(v -> {
            if (user.getUid() != null && user.getName() != null && user.getPhone() != null) {
                listener.onItemClick(user);
            } else {
                Toast.makeText(v.getContext(), "Data pengguna tidak lengkap!!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return kontakList.size();
    }

    static class KontakViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone;
        ImageView imgProfile;

        public KontakViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            imgProfile = itemView.findViewById(R.id.imgProfile);
        }
    }
}
