package com.example.plantdiseases;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText nameInput, passwordInput;
    private Button loginBtn;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nameInput = findViewById(R.id.nameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginBtn = findViewById(R.id.loginBtn);

        if (nameInput == null || passwordInput == null || loginBtn == null) {
            Log.e("LOGIN", "One or more views not found in layout");
            Toast.makeText(this, "Ошибка: UI элементы не найдены", Toast.LENGTH_SHORT).show();
            return;
        }

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        db = FirebaseFirestore.getInstance();
        if (db == null) {
            Log.e("LOGIN", "Firestore initialization failed");
            Toast.makeText(this, "Ошибка инициализации Firestore", Toast.LENGTH_SHORT).show();
            return;
        }

        loginBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            if (name.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("LOGIN", "Attempting login for user: " + name);
            db.collection("users").document(name).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String dbPassword = documentSnapshot.getString("password");
                            if (dbPassword != null && dbPassword.equals(password)) {
                                Log.d("LOGIN", "Login successful for user: " + name);
                                Toast.makeText(this, "Вход выполнен!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, CategoryListActivity.class);
                                intent.putExtra("USERNAME", name);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.w("LOGIN", "Incorrect password for user: " + name);
                                Toast.makeText(this, "Неверный пароль!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w("LOGIN", "User not found: " + name);
                            Toast.makeText(this, "Пользователь не найден!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FIREBASE", "Login error: " + e.getMessage(), e);
                        Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}