package com.example.whatsapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    EditText etMessage;
    Button btnSend;
    RecyclerView recyclerView;
    String currentUid, receiverUid;
    DatabaseReference chatRef;
    List<ChatMessage> chatList = new ArrayList<>();
    ChatAdapter adapter;
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 100;
    private Bitmap capturedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ImageView btnCamera = findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else {
                openCamera();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        recyclerView = findViewById(R.id.recyclerChat);

        currentUid = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        receiverUid = getIntent().getStringExtra("receiverUid");
        String receiverName = getIntent().getStringExtra("receiverName");

        ImageView btnBack = findViewById(R.id.buttonBack);
        btnBack.setOnClickListener(v -> finish());

        if (currentUid == null || receiverUid == null || receiverName == null) {
            Toast.makeText(this, "Data pengguna tidak lengkap", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvNama = findViewById(R.id.tvContactName);
        tvNama.setText(receiverName);

        String chatRoomId = generateChatRoomId(currentUid, receiverUid);
        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatRoomId);

        adapter = new ChatAdapter(chatList, currentUid);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnSend.setOnClickListener(v -> {
            String msg = etMessage.getText().toString().trim();
            if (!msg.isEmpty()) {
                ChatMessage chat = new ChatMessage(currentUid, receiverUid, msg, System.currentTimeMillis());
                chat.setRead(false);
                chatRef.push().setValue(chat);
                etMessage.setText("");
            }
        });

        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatMessage chat = snapshot.getValue(ChatMessage.class);
                if (chat != null) {
                    boolean isMine = chat.getSenderId().equals(currentUid);

                    chatList.add(chat);
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(chatList.size() - 1);
                    if (!isMine && !Boolean.TRUE.equals(chat.isRead())) {
                        snapshot.getRef().child("isRead").setValue(true);
                        showNotification("Pesan baru dari " + receiverName, chat.getMessage());
                    }
                }
            }

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    ChatMessage chat = snap.getValue(ChatMessage.class);
                    if (chat != null && !chat.getSenderId().equals(currentUid) && !Boolean.TRUE.equals(chat.isRead())) {
                        snap.getRef().child("isRead").setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    private String generateChatRoomId(String uid1, String uid2) {
        if (uid1 == null || uid2 == null) return "invalid_room";
        return uid1.compareTo(uid2) < 0 ? uid1 + "_" + uid2 : uid2 + "_" + uid1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            capturedImage = (Bitmap) data.getExtras().get("data");

            if (capturedImage != null) {
                ChatMessage photoMessage = new ChatMessage(currentUid, receiverUid, "[Foto terkirim]", System.currentTimeMillis());
                chatList.add(photoMessage);
                adapter.notifyDataSetChanged();

                Toast.makeText(this, "Foto berhasil diambil (simulasi kirim)", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showNotification(String title, String message) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "chat_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Chat Notifications", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }


}
