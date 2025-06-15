package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvRegister;
    FirebaseAuth auth;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (!LoginValidator.isValidEmail(email)) {
                Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!LoginValidator.isValidPassword(password)) {
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String uid = auth.getCurrentUser().getUid();

                            dbRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String name = snapshot.child("name").getValue(String.class);
                                        String phone = snapshot.child("phone").getValue(String.class);

                                        if (name != null && phone != null) {
                                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                            i.putExtra("name", name);
                                            i.putExtra("phone", phone);
                                            startActivity(i);
                                            finish();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Data pengguna tidak lengkap", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    Toast.makeText(LoginActivity.this, "Gagal mengambil data pengguna", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(this, "Login gagal: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}
