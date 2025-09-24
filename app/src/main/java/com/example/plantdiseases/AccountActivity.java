package com.example.plantdiseases;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

public class AccountActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView accountName;
    private TextView accountEmail;
    private TextView navUsername;
    private FirebaseFirestore db;

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

        String userId = getIntent().getStringExtra("USERNAME");
        if (userId != null) {
            View headerView = navigationView.getHeaderView(0);
            navUsername = headerView.findViewById(R.id.nav_header_name);
            if (navUsername != null) {
                navUsername.setText("Имя: " + userId);
            }
            accountName.setText(userId);
        } else {
            Toast.makeText(this, "Ошибка: имя пользователя не передано", Toast.LENGTH_SHORT).show();
            return;
        }

        loadUserData(userId);
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
                        if (fetchedEmail != null) {
                            accountEmail.setText(fetchedEmail);
                        } else {
                            accountEmail.setText("Email не указан");
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

        if (id == R.id.nav_account) {
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Настройки выбраны", Toast.LENGTH_SHORT).show();
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