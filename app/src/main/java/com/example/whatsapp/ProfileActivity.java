package com.example.whatsapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    ImageView imgProfile;
    TextView tvName, tvPhone, tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgProfile = findViewById(R.id.imgProfile);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvPhone = findViewById(R.id.tvPhone);
        TextView tvEmail = findViewById(R.id.tvEmail);
        ImageView btnBack = findViewById(R.id.buttonBack);

        btnBack.setOnClickListener(v -> finish());
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);

                    tvName.setText(name != null ? name : "Nama tidak tersedia");
                    tvPhone.setText(phone != null ? phone : "Nomor tidak tersedia");
                    tvEmail.setText(email != null ? email : "Email tidak tersedia");
                } else {
                    Toast.makeText(ProfileActivity.this, "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Gagal memuat data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}