package com.example.whatsapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class TambahKontakActivity extends AppCompatActivity {

    private EditText etName, editPhone;
    private Button btnSave;
    private Switch switchSync;
    private DatabaseReference userRef, kontakRef;
    private String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_kontak);
        etName = findViewById(R.id.etName);
        editPhone = findViewById(R.id.editPhone);
        btnSave = findViewById(R.id.btnSave);
        switchSync = findViewById(R.id.switchSync);
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        kontakRef = FirebaseDatabase.getInstance().getReference("kontak").child(currentUid);
        btnSave.setOnClickListener(v -> tambahKontak());
    }

    private void tambahKontak() {
        String nama = etName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Nomor telepon harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        userRef.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                String otherUid = snap.getKey();

                                if (otherUid.equals(currentUid)) {
                                    Toast.makeText(TambahKontakActivity.this, "Tidak bisa menambahkan diri sendiri", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                kontakRef.child(otherUid).setValue(true).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(TambahKontakActivity.this, "Kontak ditambahkan", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(TambahKontakActivity.this, "Gagal menyimpan kontak", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            }
                        } else {
                            Toast.makeText(TambahKontakActivity.this, "Nomor tidak ditemukan di database", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(TambahKontakActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
