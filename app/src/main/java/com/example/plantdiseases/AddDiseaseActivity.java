// AddDiseaseActivity.java
package com.example.plantdiseases;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddDiseaseActivity extends AppCompatActivity {
    private EditText etName, etDescription, etSymptoms;
    private Button btnSave;
    private FirebaseFirestore db;

    private TextView navUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_disease);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Добавить болезнь");
        }
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navUsername = headerView.findViewById(R.id.nav_header_name);



        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etSymptoms = findViewById(R.id.etSymptoms);
        btnSave = findViewById(R.id.btnSave);
        db = FirebaseFirestore.getInstance();

        btnSave.setOnClickListener(v -> saveDisease());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_account) {
            Toast.makeText(this, "Account clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if(id == R.id.nav_home){
            Intent intent = new Intent(this, CategoryAdapter.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveDisease() {
        String name = etName.getText().toString();
        String desc = etDescription.getText().toString();
        String sym = etSymptoms.getText().toString();

        Map<String, Object> disease = new HashMap<>();
        disease.put("name", name);
        disease.put("description", desc);
        disease.put("symptoms", sym);

        db.collection("diseases").add(disease)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Болезнь добавлена", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        navUsername.setText("Имя: " + name);
    }
}