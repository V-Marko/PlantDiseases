package com.example.plantdiseases;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

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

        db = FirebaseFirestore.getInstance();

        // register
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (name.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
                    return;
                }

                db.collection("users").document(name).set(new User(name, password))
                        .addOnSuccessListener(unused -> Toast.makeText(MainActivity.this, "Регистрация успешна!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        // login
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (name.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
                    return;
                }

                db.collection("users").document(name).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String dbPassword = documentSnapshot.getString("password");
                                if (dbPassword != null && dbPassword.equals(password)) {
                                    Toast.makeText(MainActivity.this, "Вход выполнен!", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                                    intent.putExtra("username", name);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(MainActivity.this, "Неверный пароль!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Пользователь не найден!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    public static class User {
        public String name;
        public String password;

        public User() {}

        public User(String name, String password) {
            this.name = name;
            this.password = password;
        }
    }
}
