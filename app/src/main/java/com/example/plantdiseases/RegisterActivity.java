package com.example.plantdiseases;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameInput, passwordInput;
    private Button registerBtn, loginBtn;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Reuse activity_main.xml

        nameInput = findViewById(R.id.editTextName);
        passwordInput = findViewById(R.id.editTextPassword);
        registerBtn = findViewById(R.id.buttonRegister);
        loginBtn = findViewById(R.id.buttonLogin);

        // Hide login button
        loginBtn.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();

        registerBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (name.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("users").document(name).set(new User(name, password))
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(RegisterActivity.this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}