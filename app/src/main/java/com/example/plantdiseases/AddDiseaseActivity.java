package com.example.plantdiseases;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddDiseaseActivity extends AppCompatActivity {
    private EditText etName, etDescription, etSymptoms;
    private Button btnSave;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_disease);

        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etSymptoms = findViewById(R.id.etSymptoms);
        btnSave = findViewById(R.id.btnSave);
        db = FirebaseFirestore.getInstance();

        btnSave.setOnClickListener(v -> saveDisease());
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
    }
}
