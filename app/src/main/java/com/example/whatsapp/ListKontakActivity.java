package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.widget.Button;
import com.google.firebase.database.*;
import com.google.firebase.auth.FirebaseAuth;

public class ListKontakActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private Button tambahKontak;

    private DatabaseReference userRef, kontakRef;
    private String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_kontak);

        recyclerView = findViewById(R.id.recyclerViewKontak);
        tambahKontak = findViewById(R.id.btnKontakBaru);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(userList, user -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("receiverUid", user.getUid());
            intent.putExtra("receiverName", user.getName());
            intent.putExtra("otherUid", user.getUid());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        kontakRef = FirebaseDatabase.getInstance().getReference("kontak").child(currentUid);

        loadUserContacts();

        tambahKontak.setOnClickListener(v -> {
            Intent intent = new Intent(this, TambahKontakActivity.class);
            startActivity(intent);
        });
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadUserContacts() {
        userList.clear();
        String currentUid = FirebaseAuth.getInstance().getUid();

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    User user = snap.getValue(User.class);
                    String uid = snap.getKey();
                    if (user != null && uid != null) {
                        user.setUid(uid);

                        if (!uid.equals(currentUid)) {
                            FirebaseDatabase.getInstance().getReference("kontak")
                                    .child(currentUid)
                                    .child(uid)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot kontakSnapshot) {
                                            if (kontakSnapshot.exists()) {
                                                userList.add(user);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                        }
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadUserContacts(); // refresh ketika balik dari TambahKontakActivity
    }
}
