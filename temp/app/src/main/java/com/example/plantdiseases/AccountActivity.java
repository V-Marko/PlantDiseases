package com.example.plantdiseases;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.util.Log;
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

    private String setLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        updateTitle(); // Обновляем заголовок в зависимости от языка

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
                updateNavUsername(); // Обновляем текст в зависимости от языка
            }
            accountName.setText(userId);
        } else {
            showToast("Ошибка: имя пользователя не передано");
            return;
        }

        loadUserData(userId);

        // E-mail
        EmailEditPopup = findViewById(R.id.emailEditPopup);
        saveEmailButton = findViewById(R.id.saveEmailButton);
        cancelEmailButton = findViewById(R.id.cancelEmailButton);
        emailEditText = findViewById(R.id.emailEditText);

        AboutAccount = findViewById(R.id.AboutAccount);
        LinearLayoutName = findViewById(R.id.LinearLayoutName);
        LinearLayoutEmail = findViewById(R.id.LinearLayoutEmail);
        LinearLayoutTell = findViewById(R.id.LinearLayoutTell);

        // tell
        tellEditText = findViewById(R.id.tellEditText);
        tellEditPopup = findViewById(R.id.tellEditPopup);
        saveTellButton = findViewById(R.id.saveTellButton);
        cancelTellButton = findViewById(R.id.cancelTellButton);

        // update tell
        LinearLayoutTell.setOnClickListener(v -> {
            tellEditPopup.setVisibility(VISIBLE);

            String currentTell = accountTell.getText().toString();
            if (!currentTell.equals(getString(R.string.phone_not_specified))) {
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

        LinearLayoutEmail.setOnClickListener(v -> {
            EmailEditPopup.setVisibility(VISIBLE);

            String currentEmail = accountEmail.getText().toString();
            if (!currentEmail.equals(getString(R.string.email_not_specified))) {
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

    private void updateTitle() {
        if (getSupportActionBar() != null) {
            if ("ru".equals(setLanguage)) {
                getSupportActionBar().setTitle("Аккаунт");
            } else if ("en".equals(setLanguage)) {
                getSupportActionBar().setTitle("Account");
            } else if ("hy".equals(setLanguage)) {
                getSupportActionBar().setTitle("Հաշիվ");
            } else {
                getSupportActionBar().setTitle("Аккаунт");
            }
        }
    }

    private void updateNavUsername() {
        if (navUsername != null && userId != null) {
            if ("ru".equals(setLanguage)) {
                navUsername.setText(userId);
            } else if ("en".equals(setLanguage)) {
                navUsername.setText(userId);
            } else if ("hy".equals(setLanguage)) {
                navUsername.setText(userId);
            } else {
                navUsername.setText(userId);
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String getTranslatedMessage(String key) {
        switch (key) {
            case "enter_phone":
                if ("ru".equals(setLanguage)) return "Введите телефон";
                if ("en".equals(setLanguage)) return "Enter phone number";
                if ("hy".equals(setLanguage)) return "Մուտքագրեք հեռախոսահամարը";
                return "Введите телефон";

            case "enter_valid_phone":
                if ("ru".equals(setLanguage)) return "Введите корректный телефон";
                if ("en".equals(setLanguage)) return "Enter valid phone number";
                if ("hy".equals(setLanguage)) return "Մուտքագրեք վավեր հեռախոսահամար";
                return "Введите корректный телефон";

            case "user_not_defined":
                if ("ru".equals(setLanguage)) return "Ошибка: пользователь не определен";
                if ("en".equals(setLanguage)) return "Error: user not defined";
                if ("hy".equals(setLanguage)) return "Սխալ՝ օգտատերը չի սահմանված";
                return "Ошибка: пользователь не определен";

            case "phone_saved":
                if ("ru".equals(setLanguage)) return "Телефон успешно сохранён!";
                if ("en".equals(setLanguage)) return "Phone number saved successfully!";
                if ("hy".equals(setLanguage)) return "Հեռախոսահամարը հաջողությամբ պահպանված է";
                return "Телефон успешно сохранён!";

            case "phone_save_error":
                if ("ru".equals(setLanguage)) return "Ошибка сохранения телефона: ";
                if ("en".equals(setLanguage)) return "Error saving phone: ";
                if ("hy".equals(setLanguage)) return "Հեռախոսի պահպանման սխալ՝ ";
                return "Ошибка сохранения телефона: ";

            case "enter_email":
                if ("ru".equals(setLanguage)) return "Введите email";
                if ("en".equals(setLanguage)) return "Enter email";
                if ("hy".equals(setLanguage)) return "Մուտքագրեք email";
                return "Введите email";

            case "enter_valid_email":
                if ("ru".equals(setLanguage)) return "Введите корректный email";
                if ("en".equals(setLanguage)) return "Enter valid email";
                if ("hy".equals(setLanguage)) return "Մուտքագրեք վավեր email";
                return "Введите корректный email";

            case "email_saved":
                if ("ru".equals(setLanguage)) return "Email успешно сохранен!";
                if ("en".equals(setLanguage)) return "Email saved successfully!";
                if ("hy".equals(setLanguage)) return "Email-ը հաջողությամբ պահպանված է";
                return "Email успешно сохранен!";

            case "email_save_error":
                if ("ru".equals(setLanguage)) return "Ошибка сохранения email: ";
                if ("en".equals(setLanguage)) return "Error saving email: ";
                if ("hy".equals(setLanguage)) return "Email-ի պահպանման սխալ՝ ";
                return "Ошибка сохранения email: ";

            case "user_not_found":
                if ("ru".equals(setLanguage)) return "Пользователь не найден";
                if ("en".equals(setLanguage)) return "User not found";
                if ("hy".equals(setLanguage)) return "Օգտատերը չի գտնվել";
                return "Пользователь не найден";

            case "load_data_error":
                if ("ru".equals(setLanguage)) return "Ошибка загрузки данных: ";
                if ("en".equals(setLanguage)) return "Error loading data: ";
                if ("hy".equals(setLanguage)) return "Տվյալների բեռնման սխալ՝ ";
                return "Ошибка загрузки данных: ";

            default:
                return key;
        }
    }

    private void saveTellToDatabase() {
        String newTell = tellEditText.getText().toString().trim();

        if (newTell.isEmpty()) {
            showToast(getTranslatedMessage("enter_phone"));
            return;
        }

        if (!android.util.Patterns.PHONE.matcher(newTell).matches()) {
            showToast(getTranslatedMessage("enter_valid_phone"));
            return;
        }

        if (userId == null) {
            showToast(getTranslatedMessage("user_not_defined"));
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("tell", newTell);

        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    accountTell.setText(newTell);

                    tellEditPopup.setVisibility(GONE);
                    tellEditText.setText("");
                    showToast(getTranslatedMessage("phone_saved"));

                    toolbar.setVisibility(VISIBLE);
                    AboutAccount.setVisibility(VISIBLE);
                    LinearLayoutName.setVisibility(VISIBLE);
                    LinearLayoutEmail.setVisibility(VISIBLE);
                    LinearLayoutTell.setVisibility(VISIBLE);
                })
                .addOnFailureListener(e -> {
                    showToast(getTranslatedMessage("phone_save_error") + e.getMessage());
                });
    }

    private void saveEmailToDatabase() {
        String newEmail = emailEditText.getText().toString().trim();

        if (newEmail.isEmpty()) {
            showToast(getTranslatedMessage("enter_email"));
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            showToast(getTranslatedMessage("enter_valid_email"));
            return;
        }

        if (userId == null) {
            showToast(getTranslatedMessage("user_not_defined"));
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("email", newEmail);

        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    accountEmail.setText(newEmail);
                    if (navHeaderEmail != null) {
                        if ("ru".equals(setLanguage)) {
                            navHeaderEmail.setText(newEmail);
                        } else if ("en".equals(setLanguage)) {
                            navHeaderEmail.setText(newEmail);
                        } else if ("hy".equals(setLanguage)) {
                            navHeaderEmail.setText(newEmail);
                        } else {
                            navHeaderEmail.setText(newEmail);
                        }
                        navHeaderEmail.setVisibility(View.VISIBLE);
                    }
                    EmailEditPopup.setVisibility(GONE);
                    emailEditText.setText("");
                    showToast(getTranslatedMessage("email_saved"));

                    toolbar.setVisibility(VISIBLE);
                    AboutAccount.setVisibility(VISIBLE);
                    LinearLayoutName.setVisibility(VISIBLE);
                    LinearLayoutEmail.setVisibility(VISIBLE);
                    LinearLayoutTell.setVisibility(VISIBLE);
                })
                .addOnFailureListener(e -> {
                    showToast(getTranslatedMessage("email_save_error") + e.getMessage());
                });
    }

    private void loadUserData(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fetchedName = documentSnapshot.getString("name");
                        String fetchedEmail = documentSnapshot.getString("email");
                        String fetchedTell = documentSnapshot.getString("tell");
                        String userLanguage = documentSnapshot.getString("language");
                        setLanguage = userLanguage;
                        Log.i("Language", "Language from database: " + userLanguage);

                        // Обновляем интерфейс после загрузки языка
                        updateTitle();
                        updateNavUsername();

                        if (fetchedName != null) {
                            accountName.setText(fetchedName);
                        } else {
                            showToast("Имя пользователя не найдено");
                        }

                        if (fetchedEmail != null && !fetchedEmail.isEmpty()) {
                            accountEmail.setText(fetchedEmail);
                            if (navHeaderEmail != null) {
                                if ("ru".equals(setLanguage)) {
                                    navHeaderEmail.setText(fetchedEmail);
                                } else if ("en".equals(setLanguage)) {
                                    navHeaderEmail.setText(fetchedEmail);
                                } else if ("hy".equals(setLanguage)) {
                                    navHeaderEmail.setText( fetchedEmail);
                                } else {
                                    navHeaderEmail.setText(fetchedEmail);
                                }
                                navHeaderEmail.setVisibility(View.VISIBLE);
                            }
                        } else {
                            accountEmail.setText(getString(R.string.email_not_specified));
                            if (navHeaderEmail != null) {
                                navHeaderEmail.setVisibility(View.GONE);
                            }
                        }

                        // Загрузка телефона
                        if (fetchedTell != null && !fetchedTell.isEmpty()) {
                            accountTell.setText(fetchedTell);
                        } else {
                            accountTell.setText(getString(R.string.phone_not_specified));
                        }
                    } else {
                        showToast(getTranslatedMessage("user_not_found"));
                    }
                })
                .addOnFailureListener(e -> {
                    showToast(getTranslatedMessage("load_data_error") + e.getMessage());
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