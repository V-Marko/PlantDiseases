package com.example.plantdiseases;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class MenuActivity extends AppCompatActivity {

    private TextView textWelcome, textInfo;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        textWelcome = findViewById(R.id.textWelcome);
        textInfo = findViewById(R.id.textInfo);

        if (textWelcome == null || textInfo == null) {
            Log.e("MENU_ACTIVITY", "Views not found: textWelcome=" + (textWelcome == null ? "null" : "found") +
                    ", textInfo=" + (textInfo == null ? "null" : "found"));
            Toast.makeText(this, "Ошибка: UI элементы не найдены", Toast.LENGTH_SHORT).show();
            return;
        }

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        db = FirebaseFirestore.getInstance();
        if (db == null) {
            Log.e("MENU_ACTIVITY", "Firestore initialization failed");
            Toast.makeText(this, "Ошибка инициализации Firestore", Toast.LENGTH_SHORT).show();
            return;
        }

        String category = getIntent().getStringExtra("category");
        if (category == null || category.isEmpty()) {
            Log.e("MENU_ACTIVITY", "No category provided in Intent");
            textWelcome.setText("Ошибка");
            textInfo.setText("Категория не выбрана");
            Toast.makeText(this, "Категория не выбрана", Toast.LENGTH_SHORT).show();
            return;
        }

        loadCategoryData(category);
    }

    private void loadCategoryData(String category) {
        Log.d("MENU_ACTIVITY", "Loading data for category: " + category);
        textWelcome.setText(category);

        String fieldName = category.equals("Fungal") ? "Lateblight" : "info"; // Уточните поле для Virus
        Log.d("MENU_ACTIVITY", "Using field name: " + fieldName);

        db.collection("data").document(category).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d("FIREBASE", "Document exists: " + documentSnapshot.exists());
                    if (documentSnapshot.exists()) {
                        String info = documentSnapshot.getString(fieldName);
                        Log.d("FIREBASE", "Text data: " + info);
                        if (info != null && !info.isEmpty()) {
                            String formattedText = StyleTextDP(info);
                            textInfo.setText(HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_LEGACY));
                        } else {
                            textInfo.setText("Данные о " + category + " отсутствуют");
                            Log.w("FIREBASE", "Field " + fieldName + " not found or empty");
                            Toast.makeText(this, "Данные о " + category + " отсутствуют", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        textInfo.setText("Документ " + category + " не найден");
                        Log.e("FIREBASE", "Document " + category + " not found");
                        Toast.makeText(this, "Документ " + category + " не найден", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Error: " + e.getMessage(), e);
                    textInfo.setText("Ошибка: " + e.getMessage());
                    Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String StyleTextDP(String info) {
        Log.d("MENU_ACTIVITY", "Formatting text: " + info);
        String cleanedText = info
                .replaceAll("<b/>", "</b>")
                .replaceAll("\n", "<br>")
                .replaceAll("^[⦁•]\\s*", "&#8226; ");
        Log.d("MENU_ACTIVITY", "Formatted text result: " + cleanedText);
        return cleanedText;
    }
}