package com.example.plantdiseases;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
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
    private TextView accountTell;
    private TextView navUsername;
    private TextView navHeaderEmail;
    private FirebaseFirestore db;

    private LinearLayout EmailEditPopup;
    private Button saveEmailButton, cancelEmailButton;
    private EditText emailEditText;
    private String userId;

    private LinearLayout AboutAccount,LinearLayoutName,LinearLayoutEmail,LinearLayoutTell;

    private LinearLayout tellEditPopup;
    private Button saveTellButton, cancelTellButton;
    private EditText tellEditText;

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
        accountTell = findViewById(R.id.accountTell);

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

        // E-mail элементы
        EmailEditPopup = findViewById(R.id.emailEditPopup);
        saveEmailButton = findViewById(R.id.saveEmailButton);
        cancelEmailButton = findViewById(R.id.cancelEmailButton);
        emailEditText = findViewById(R.id.emailEditText);

        AboutAccount = findViewById(R.id.AboutAccount);
        LinearLayoutName = findViewById(R.id.LinearLayoutName);
        LinearLayoutEmail = findViewById(R.id.LinearLayoutEmail);
        LinearLayoutTell = findViewById(R.id.LinearLayoutTell);

        // Телефон элементы
        tellEditText = findViewById(R.id.tellEditText);
        tellEditPopup = findViewById(R.id.tellEditPopup);
        saveTellButton = findViewById(R.id.saveTellButton);
        cancelTellButton = findViewById(R.id.cancelTellButton);

        // Обработка клика по телефону
        LinearLayoutTell.setOnClickListener(v -> {
            tellEditPopup.setVisibility(VISIBLE);

            // Показываем текущий телефон в поле редактирования
            String currentTell = accountTell.getText().toString();
            if (!currentTell.equals("Телефон не указан")) {
                tellEditText.setText(currentTell);
            }

            AboutAccount.setVisibility(GONE);
            LinearLayoutName.setVisibility(GONE);
            LinearLayoutEmail.setVisibility(GONE);
            LinearLayoutTell.setVisibility(GONE);
        });

        saveTellButton.setOnClickListener(v -> {
            saveTellToDatabase();
        });

        cancelTellButton.setOnClickListener(v -> {
            tellEditPopup.setVisibility(GONE);
            tellEditText.setText("");

            toolbar.setVisibility(VISIBLE);
            AboutAccount.setVisibility(VISIBLE);
            LinearLayoutName.setVisibility(VISIBLE);
            LinearLayoutEmail.setVisibility(VISIBLE);
            LinearLayoutTell.setVisibility(VISIBLE);
        });

        // Обработка клика по email
        LinearLayoutEmail.setOnClickListener(v -> {
            EmailEditPopup.setVisibility(VISIBLE);

            // Показываем текущий email в поле редактирования
            String currentEmail = accountEmail.getText().toString();
            if (!currentEmail.equals("Email не указан")) {
                emailEditText.setText(currentEmail);
            }

            AboutAccount.setVisibility(GONE);
            LinearLayoutName.setVisibility(GONE);
            LinearLayoutEmail.setVisibility(GONE);
            LinearLayoutTell.setVisibility(GONE);
        });

        saveEmailButton.setOnClickListener(v -> {
            saveEmailToDatabase();
        });

        cancelEmailButton.setOnClickListener(v -> {
            EmailEditPopup.setVisibility(GONE);
            emailEditText.setText("");

            toolbar.setVisibility(VISIBLE);
            AboutAccount.setVisibility(VISIBLE);
            LinearLayoutName.setVisibility(VISIBLE);
            LinearLayoutEmail.setVisibility(VISIBLE);
            LinearLayoutTell.setVisibility(VISIBLE);
        });
    }

    private void saveTellToDatabase() {
        String newTell = tellEditText.getText().toString().trim();

        if (newTell.isEmpty()) {
            Toast.makeText(this, "Введите телефон", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.PHONE.matcher(newTell).matches()) {
            Toast.makeText(this, "Введите корректный телефон", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userId == null) {
            Toast.makeText(this, "Ошибка: пользователь не определен", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("tell", newTell);

        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    accountTell.setText(newTell);

                    // Обновляем навигационную панель если нужно
                    if (navHeaderEmail != null) {
                        // Можно добавить отображение телефона в навигационной панели
                        // navHeaderEmail.setText("Телефон: " + newTell);
                        navHeaderEmail.setVisibility(View.VISIBLE);
                    }

                    tellEditPopup.setVisibility(GONE);
                    tellEditText.setText("");
                    Toast.makeText(this, "Телефон успешно сохранён!", Toast.LENGTH_SHORT).show();

                    toolbar.setVisibility(VISIBLE);
                    AboutAccount.setVisibility(VISIBLE);
                    LinearLayoutName.setVisibility(VISIBLE);
                    LinearLayoutEmail.setVisibility(VISIBLE);
                    LinearLayoutTell.setVisibility(VISIBLE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ошибка сохранения телефона: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

                    toolbar.setVisibility(VISIBLE);
                    AboutAccount.setVisibility(VISIBLE);
                    LinearLayoutName.setVisibility(VISIBLE);
                    LinearLayoutEmail.setVisibility(VISIBLE);
                    LinearLayoutTell.setVisibility(VISIBLE);
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
                        String fetchedTell = documentSnapshot.getString("tell");

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

                        // Загрузка телефона
                        if (fetchedTell != null && !fetchedTell.isEmpty()) {
                            accountTell.setText(fetchedTell);
                        } else {
                            accountTell.setText("Телефон не указан");
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
            finish();
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            if (username != null) {
                intent.putExtra("USERNAME", username);
            }
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}