package com.example.plantdiseases;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView accountName;
    private TextView accountEmail;
    private TextView navUsername;
    private TextView navHeaderEmail;
    private FirebaseFirestore db;

    private LinearLayout LinearLayoutEmail;
    private LinearLayout EmailEditPopup;
    private Button saveEmailButton, cancelEmailButton;
    private EditText emailEditText;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Аккаунт");
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        accountName = findViewById(R.id.accountName);
        accountEmail = findViewById(R.id.accountEmail);

        db = FirebaseFirestore.getInstance();

        userId = getIntent().getStringExtra("USERNAME");
        if (userId != null) {
            View headerView = navigationView.getHeaderView(0);
            navUsername = headerView.findViewById(R.id.nav_header_name);
            navHeaderEmail = headerView.findViewById(R.id.nav_header_email);

            if (navUsername != null) {
                navUsername.setText("Имя: " + userId);
            }
            accountName.setText(userId);
        } else {
            Toast.makeText(this, "Ошибка: имя пользователя не передано", Toast.LENGTH_SHORT).show();
            return;
        }

        loadUserData(userId);

        LinearLayoutEmail = findViewById(R.id.LinearLayoutEmail);
        EmailEditPopup = findViewById(R.id.emailEditPopup);
        saveEmailButton = findViewById(R.id.saveEmailButton);
        cancelEmailButton = findViewById(R.id.cancelEmailButton);
        emailEditText = findViewById(R.id.emailEditText);

        LinearLayoutEmail.setOnClickListener(v -> {
            String currentEmail = accountEmail.getText().toString();
            if (!currentEmail.equals("Email не указан")) {
                emailEditText.setText(currentEmail);
            }
            EmailEditPopup.setVisibility(VISIBLE);
        });

        saveEmailButton.setOnClickListener(v -> {
            saveEmailToDatabase();
        });

        cancelEmailButton.setOnClickListener(v -> {
            emailEditText.setText("");
            EmailEditPopup.setVisibility(GONE);
        });
    }

    private void saveEmailToDatabase() {
        String newEmail = emailEditText.getText().toString().trim();

        if (newEmail.isEmpty()) {
            Toast.makeText(this, "Введите email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            Toast.makeText(this, "Введите корректный email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userId == null) {
            Toast.makeText(this, "Ошибка: пользователь не определен", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("email", newEmail);

        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    accountEmail.setText(newEmail);
                    if (navHeaderEmail != null) {
                        navHeaderEmail.setText("E-mail: "+ newEmail);
                        navHeaderEmail.setVisibility(View.VISIBLE);
                    }
                    EmailEditPopup.setVisibility(GONE);
                    emailEditText.setText("");
                    Toast.makeText(this, "Email успешно сохранен!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ошибка сохранения email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadUserData(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fetchedName = documentSnapshot.getString("name");
                        String fetchedEmail = documentSnapshot.getString("email");

                        if (fetchedName != null) {
                            accountName.setText(fetchedName);
                        } else {
                            Toast.makeText(this, "Имя пользователя не найдено", Toast.LENGTH_SHORT).show();
                        }

                        if (fetchedEmail != null && !fetchedEmail.isEmpty()) {
                            accountEmail.setText(fetchedEmail);
                            if (navHeaderEmail != null) {
                                navHeaderEmail.setText("E-mail: "+ fetchedEmail);
                                navHeaderEmail.setVisibility(View.VISIBLE);
                            }
                        } else {
                            accountEmail.setText("Email не указан");
                            if (navHeaderEmail != null) {
                                navHeaderEmail.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ошибка загрузки данных: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        String username = getIntent().getStringExtra("USERNAME");

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, CategoryListActivity.class);
            if (username != null) {
                intent.putExtra("USERNAME", username);
            }
            startActivity(intent);
            finish(); // Закрываем текущую активность
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}