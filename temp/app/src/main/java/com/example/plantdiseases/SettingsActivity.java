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

        // Устанавливаем заголовок
        updateToolbarTitle();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        spinner_language = findViewById(R.id.spinner_language);

        setupLanguageSpinner();

        // Navigation Drawer - УБИРАЕМ ActionBarDrawerToggle для этой активности
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> {
            navigateToHome();
        });

        navigationView.setNavigationItemSelectedListener(this);

        db = FirebaseFirestore.getInstance();

        loadUserData();
    }

    private void updateToolbarTitle() {
        if (getSupportActionBar() != null) {
            if ("ru".equals(selectedLanguage)) {
                getSupportActionBar().setTitle("Настройки");
            } else if ("en".equals(selectedLanguage)) {
                getSupportActionBar().setTitle("Settings");
            } else if ("hy".equals(selectedLanguage)) {
                getSupportActionBar().setTitle("Կարգավորումներ");
            } else {
                getSupportActionBar().setTitle("Настройки");
            }
        }
    }

    private void updateNavUsername() {
        if (navUsername != null && username != null) {
            if ("ru".equals(selectedLanguage)) {
                navUsername.setText(username);
            } else if ("en".equals(selectedLanguage)) {
                navUsername.setText(username);
            } else if ("hy".equals(selectedLanguage)) {
                navUsername.setText( username);
            } else {
                navUsername.setText(username);
            }
        }
    }

    private void updateNavHeaderEmail() {
        if (navHeaderEmail != null) {
            // Если нужно обновить текст email на разных языках
            // Пока оставляем как есть, так как email не зависит от языка
        }
    }

    private void navigateToHome() {
        String username = getIntent().getStringExtra("USERNAME");
        Intent intent = new Intent(this, CategoryListActivity.class);
        if (username != null) {
            intent.putExtra("USERNAME", username);
        }
        startActivity(intent);
        finish();
    }

    private void setupLanguageSpinner() {
        spinner_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguageDisplay = parent.getItemAtPosition(position).toString();

                String oldLanguage = selectedLanguage;

                if (selectedLanguageDisplay.equals("English")) {
                    selectedLanguage = "en";
                } else if (selectedLanguageDisplay.equals("Русский")) {
                    selectedLanguage = "ru";
                } else if (selectedLanguageDisplay.equals("Հայերեն")) {
                    selectedLanguage = "hy";
                }

                Log.i("Language", "Selected language: " + selectedLanguage + " (" + selectedLanguageDisplay + ")");

                // Обновляем интерфейс сразу
                updateUIAfterLanguageChange();

                // Сохраняем в базу данных
                saveLanguageToDatabase(selectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("Language", "No language selected");
            }
        });
    }

    private void updateUIAfterLanguageChange() {
        updateToolbarTitle();

        updateNavUsername();
        updateNavHeaderEmail();

        updateOtherUITexts();
    }

    private void updateOtherUITexts() {}

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
                    showToast(getTranslatedMessage("language_saved"));
                })
                .addOnFailureListener(e -> {
                    Log.e("SETTINGS", "Error saving language to database: " + e.getMessage());
                    showToast(getTranslatedMessage("language_save_error") + e.getMessage());
                });
    }

    private String getTranslatedMessage(String key) {
        switch (key) {
            case "language_saved":
                if ("ru".equals(selectedLanguage)) return "Язык сохранен!";
                if ("en".equals(selectedLanguage)) return "Language saved!";
                if ("hy".equals(selectedLanguage)) return("Լեզուն պահպանված է!");
                return "Язык сохранен!";

            case "language_save_error":
                if ("ru".equals(selectedLanguage)) return "Ошибка сохранения языка: ";
                if ("en".equals(selectedLanguage)) return "Error saving language: ";
                if ("hy".equals(selectedLanguage)) return "Լեզուի պահպանման սխալ՝ ";
                return "Ошибка сохранения языка: ";

            case "user_data_error":
                if ("ru".equals(selectedLanguage)) return "Ошибка: данные пользователя не загружены";
                if ("en".equals(selectedLanguage)) return "Error: user data not loaded";
                if ("hy".equals(selectedLanguage)) return "Սխալ՝ օգտատիրոջ տվյալները չեն բեռնվել";
                return "Ошибка: данные пользователя не загружены";

            case "user_load_error":
                if ("ru".equals(selectedLanguage)) return "Ошибка загрузки данных пользователя";
                if ("en".equals(selectedLanguage)) return "Error loading user data";
                if ("hy".equals(selectedLanguage)) return "Օգտատիրոջ տվյալների բեռնման սխալ";
                return "Ошибка загрузки данных пользователя";

            default:
                return key;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void loadUserData() {
        username = getIntent().getStringExtra("USERNAME");

        if (username == null || username.isEmpty()) {
            Log.e("SETTINGS", "Username not provided in intent");
            showToast(getTranslatedMessage("user_data_error"));
            return;
        }

        View headerView = navigationView.getHeaderView(0);
        navUsername = headerView.findViewById(R.id.nav_header_name);
        navHeaderEmail = headerView.findViewById(R.id.nav_header_email);

        updateNavUsername();

        db.collection("users").document(username).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userEmail = documentSnapshot.getString("email");
                        String userTell = documentSnapshot.getString("tell");
                        String userLanguage = documentSnapshot.getString("language");

                        Log.i("Language", "Language from database: " + userLanguage);

                        if (userLanguage != null && !userLanguage.isEmpty()) {
                            setSpinnerLanguage(userLanguage);
                            selectedLanguage = userLanguage;
                            updateUIAfterLanguageChange(); // Обновляем интерфейс после загрузки языка
                        }

                        if (navHeaderEmail != null) {
                            if (userEmail != null && !userEmail.isEmpty()) {
                                if ("ru".equals(selectedLanguage)) {
                                    navHeaderEmail.setText( userEmail);
                                } else if ("en".equals(selectedLanguage)) {
                                    navHeaderEmail.setText(userEmail);
                                } else if ("hy".equals(selectedLanguage)) {
                                    navHeaderEmail.setText(userEmail);
                                } else {
                                    navHeaderEmail.setText(userEmail);
                                }
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
                    showToast(getTranslatedMessage("user_load_error"));
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
            navigateToHome();
            return true;
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
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            navigateToHome();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            navigateToHome();
        }
    }

    public String getLanguage(){
        return selectedLanguage;
    }
}