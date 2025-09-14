package com.example.plantdiseases;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnGoToLogin, btnGoToRegister;
    private EditText nameInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGoToLogin = findViewById(R.id.buttonLogin);
        btnGoToRegister = findViewById(R.id.buttonRegister);
        nameInput = findViewById(R.id.editTextName);
        passwordInput = findViewById(R.id.editTextPassword);

        // Hide input fields since they are not needed in MainActivity
        nameInput.setVisibility(View.GONE);
        passwordInput.setVisibility(View.GONE);

        btnGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}