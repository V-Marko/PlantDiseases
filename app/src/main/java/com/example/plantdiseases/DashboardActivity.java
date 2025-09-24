// DashboardActivity.java
package com.example.plantdiseases;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private DiseaseAdapter adapter;
    private List<Disease> diseaseList;
    private Button btnAdd;
    private FirebaseFirestore db;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView navUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Инициализация Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Панель управления");
        }

        // Инициализация Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Получение имени пользователя и установка в навигационный заголовок
        String username = getIntent().getStringExtra("USERNAME");
        if (username != null) {
            View headerView = navigationView.getHeaderView(0);
            navUsername = headerView.findViewById(R.id.nav_header_name);
            if (navUsername != null) {
                navUsername.setText("Имя: " + username);
            }
        }

        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.btnAdd);
        db = FirebaseFirestore.getInstance();

        diseaseList = new ArrayList<>();
        adapter = new DiseaseAdapter(diseaseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddDiseaseActivity.class);
            if (username != null) {
                intent.putExtra("USERNAME", username);
            }
            startActivity(intent);
        });

        loadDiseases();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
            // startActivity(new Intent(this, AccountActivity.class));
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, SettingsActivity.class));
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

    private void loadDiseases() {
        db.collection("diseases").get().addOnSuccessListener(querySnapshot -> {
            diseaseList.clear();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                Disease d = doc.toObject(Disease.class);
                diseaseList.add(d);
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Ошибка загрузки данных: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}