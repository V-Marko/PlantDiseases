package com.example.plantdiseases;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameInput, passwordInput;
    private Button registerBtn, loginBtn;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameInput = findViewById(R.id.editTextName);
        passwordInput = findViewById(R.id.editTextPassword);
        registerBtn = findViewById(R.id.buttonRegister);
        loginBtn = findViewById(R.id.buttonLogin);

        loginBtn.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();

        registerBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (name.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            user.put("password", password);
            user.put("email", "");

            db.collection("users").document(name).set(user)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(RegisterActivity.this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}