package com.example.plantdiseases;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private TextView navUsername;
    private TextView navHeaderEmail;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Настройки");
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Настройка переключателя Navigation Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Инициализация Firestore
        db = FirebaseFirestore.getInstance();

        // Загрузка данных пользователя
        loadUserData();
    }

    private void loadUserData() {
        String username = getIntent().getStringExtra("USERNAME");

        if (username == null || username.isEmpty()) {
            Log.e("SETTINGS", "Username not provided in intent");
            Toast.makeText(this, "Ошибка: данные пользователя не загружены", Toast.LENGTH_SHORT).show();
            return;
        }

        View headerView = navigationView.getHeaderView(0);
        navUsername = headerView.findViewById(R.id.nav_header_name);
        navHeaderEmail = headerView.findViewById(R.id.nav_header_email);

        if (navUsername != null) {
            navUsername.setText("Имя: " + username);
        }

        // Загрузка email из Firestore
        db.collection("users").document(username).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userEmail = documentSnapshot.getString("email");
                        String userTell = documentSnapshot.getString("tell");

                        if (navHeaderEmail != null) {
                            if (userEmail != null && !userEmail.isEmpty()) {
                                navHeaderEmail.setText("E-mail: " + userEmail);
                                navHeaderEmail.setVisibility(View.VISIBLE);
                            } else {
                                navHeaderEmail.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        Log.e("SETTINGS", "User document not found in Firestore");
                        if (navHeaderEmail != null) {
                            navHeaderEmail.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("SETTINGS", "Error loading user data: " + e.getMessage());
                    if (navHeaderEmail != null) {
                        navHeaderEmail.setVisibility(View.GONE);
                    }
                    Toast.makeText(this, "Ошибка загрузки данных пользователя", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        String username = getIntent().getStringExtra("USERNAME");

        Intent intent = null;

        if (id == R.id.nav_home) {
            intent = new Intent(this, CategoryListActivity.class);
        } else if (id == R.id.nav_account) {
            intent = new Intent(this, AccountActivity.class);
        } else if (id == R.id.nav_settings) {
            // Уже в настройках, просто закрываем drawer
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        if (intent != null) {
            if (username != null) {
                intent.putExtra("USERNAME", username);
            }
            startActivity(intent);
            if (id == R.id.nav_home) {
                finish();
            }
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