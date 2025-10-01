package com.example.plantdiseases;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CategoryListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<String> categories;
    private FirebaseFirestore db;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView navUsername;

    private String currentLanguage = "ru";
    private TextView navHeaderEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        String username = getIntent().getStringExtra("USERNAME");
        if (username != null) {
            View headerView = navigationView.getHeaderView(0);
            navUsername = headerView.findViewById(R.id.nav_header_name);
            navHeaderEmail = headerView.findViewById(R.id.nav_header_email);

            if (navUsername != null) {
                navUsername.setText("Имя: " + username);
            } else {
                Log.e("CATEGORY_LIST", "nav_header_name not found in header");
            }

            loadUserData(username); // Загружаем email и язык
        } else {
            // Если username нет, устанавливаем язык по умолчанию
            setTitleBasedOnLanguage();
        }

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        if (categoryRecyclerView == null) {
            Log.e("CATEGORY_LIST", "RecyclerView not found in layout");
            Toast.makeText(this, "Ошибка: RecyclerView не найден", Toast.LENGTH_SHORT).show();
            return;
        }

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        db = FirebaseFirestore.getInstance();
        if (db == null) {
            Log.e("CATEGORY_LIST", "Firestore initialization failed");
            Toast.makeText(this, "Ошибка инициализации Firestore", Toast.LENGTH_SHORT).show();
            return;
        }

        categories = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categories, category -> {
            Log.d("CATEGORY_LIST", "Category clicked: " + category);
            Intent intent = new Intent(this, MenuActivity.class);
            intent.putExtra("category", category);
            if (username != null) {
                intent.putExtra("USERNAME", username);
            }
            startActivity(intent);
        });
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryRecyclerView.setAdapter(categoryAdapter);

        categories.add("Fungal");
        categories.add("Bacterial");
        categories.add("Viral");
        categoryAdapter.notifyDataSetChanged();
    }

    private void loadUserData(String username) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(username).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userEmail = documentSnapshot.getString("email");
                        String userLanguage = documentSnapshot.getString("language");

                        // Обновляем язык
                        if (userLanguage != null) {
                            currentLanguage = userLanguage;
                        }

                        // Обновляем заголовок
                        setTitleBasedOnLanguage();

                        // Обновляем язык в адаптере
                        if (categoryAdapter != null) {
                            categoryAdapter.setLanguage(currentLanguage);
                        }

                        // Обновляем email
                        if (userEmail != null && !userEmail.isEmpty() && navHeaderEmail != null) {
                            navHeaderEmail.setText("E-mail: "+ userEmail);
                            navHeaderEmail.setVisibility(View.VISIBLE);
                        } else if (navHeaderEmail != null) {
                            navHeaderEmail.setVisibility(View.GONE);
                        }
                    } else {
                        setTitleBasedOnLanguage();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("CATEGORY_LIST", "Error loading user data: " + e.getMessage());
                    setTitleBasedOnLanguage();
                    if (navHeaderEmail != null) {
                        navHeaderEmail.setVisibility(View.GONE);
                    }
                });
    }

    private void setTitleBasedOnLanguage() {
        if (getSupportActionBar() != null) {
            if(currentLanguage.equals("ru")){
                getSupportActionBar().setTitle("Категории болезней");
            }
            else if(currentLanguage.equals("en")){
                getSupportActionBar().setTitle("Disease categories");
            }
            else if(currentLanguage.equals("hy")){
                getSupportActionBar().setTitle("Հիվանդության կատեգորիաներ");
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        String username = getIntent().getStringExtra("USERNAME");

        if (id == R.id.nav_account) {
            Intent intent = new Intent(this, AccountActivity.class);
            if (username != null) {
                intent.putExtra("USERNAME", username);
            }
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            if (username != null) {
                intent.putExtra("USERNAME", username);
            }
            startActivity(intent);
        } else if (id == R.id.nav_home) {}

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