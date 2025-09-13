package com.example.plantdiseases;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DiseaseAdapter adapter;
    private List<Disease> diseaseList;
    private Button btnAdd;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.btnAdd);
        db = FirebaseFirestore.getInstance();

        diseaseList = new ArrayList<>();
        adapter = new DiseaseAdapter(diseaseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, AddDiseaseActivity.class)));

        loadDiseases();
    }

    private void loadDiseases() {
        db.collection("diseases").get().addOnSuccessListener(querySnapshot -> {
            diseaseList.clear();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                Disease d = doc.toObject(Disease.class);
                diseaseList.add(d);
            }
            adapter.notifyDataSetChanged();
        });
    }
}
