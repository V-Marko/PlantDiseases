package com.example.plantdiseases;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
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

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private TextView navUsername;
    private TextView navHeaderEmail;
    private FirebaseFirestore db;
    private Spinner spinner_language;

    public String selectedLanguage = "en";
    private String username;

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

        spinner_language = findViewById(R.id.spinner_language);

        setupLanguageSpinner();

        //Navigation Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        db = FirebaseFirestore.getInstance();

        loadUserData();
    }

    private void setupLanguageSpinner() {
        spinner_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguageDisplay = parent.getItemAtPosition(position).toString();

                if (selectedLanguageDisplay.equals("English")) {
                    selectedLanguage = "en";
                } else if (selectedLanguageDisplay.equals("Русский")) {
                    selectedLanguage = "ru";
                } else if (selectedLanguageDisplay.equals("Հայերեն")) {
                    selectedLanguage = "hy";
                }

                Log.i("Language", "Selected language: " + selectedLanguage + " (" + selectedLanguageDisplay + ")");

                saveLanguageToDatabase(selectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("Language", "No language selected");
            }
        });
    }

    private void saveLanguageToDatabase(String languageCode) {
        if (username == null || username.isEmpty()) {
            Log.e("SETTINGS", "Username not available for saving language");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("language", languageCode);

        db.collection("users").document(username)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.i("SETTINGS", "Language saved to database: " + languageCode);
                })
                .addOnFailureListener(e -> {
                    Log.e("SETTINGS", "Error saving language to database: " + e.getMessage());
                });
    }

    private String getLanguageDisplayName(String languageCode) {
        switch (languageCode) {
            case "en": return "English";
            case "ru": return "Русский";
            case "hy": return "Հայերեն";
            default: return languageCode;
        }
    }

    private void loadUserData() {
        username = getIntent().getStringExtra("USERNAME");

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

        db.collection("users").document(username).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userEmail = documentSnapshot.getString("email");
                        String userTell = documentSnapshot.getString("tell");
                        String userLanguage = documentSnapshot.getString("language");

                        Log.i("Language", "Language from database: " + userLanguage);

                        if (userLanguage != null && !userLanguage.isEmpty()) {
                            setSpinnerLanguage(userLanguage);
                        }

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

    private void setSpinnerLanguage(String languageCode) {
        int position = 0; // По умолчанию English

        switch (languageCode) {
            case "en":
                position = 0; // English
                break;
            case "ru":
                position = 1; // Русский
                break;
            case "hy":
                position = 2; // Հայերեն
                break;
        }

        spinner_language.setSelection(position);
        selectedLanguage = languageCode;
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

    public String getLanguage(){
        return selectedLanguage;
    }
}