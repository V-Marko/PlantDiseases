package com.example.plantdiseases;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CategoryListActivity extends AppCompatActivity {

    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<String> categories;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

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
            startActivity(intent);
        });
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Add test categories
        categories.add("Fungal");
        categories.add("Bacterial");
        categories.add("Viral");
        categoryAdapter.notifyDataSetChanged();
    }
}
