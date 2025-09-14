package com.example.plantdiseases;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText nameInput, passwordInput;
    private Button loginBtn, registerBtn;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Reuse activity_main.xml

        nameInput = findViewById(R.id.editTextName);
        passwordInput = findViewById(R.id.editTextPassword);
        loginBtn = findViewById(R.id.buttonLogin);
        registerBtn = findViewById(R.id.buttonRegister);

        // Hide register button
        registerBtn.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();

        loginBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (name.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("users").document(name).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String dbPassword = documentSnapshot.getString("password");
                            if (dbPassword != null && dbPassword.equals(password)) {
                                Toast.makeText(LoginActivity.this, "Вход выполнен!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                intent.putExtra("username", name);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this, "Неверный пароль!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Пользователь не найден!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}
