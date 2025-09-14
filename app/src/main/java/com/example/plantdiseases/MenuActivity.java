package com.example.plantdiseases;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class MenuActivity extends AppCompatActivity {

    private TextView textWelcome, textInfo;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        textWelcome = findViewById(R.id.textWelcome);
        textInfo = findViewById(R.id.textInfo);

        if (textWelcome == null || textInfo == null) {
            Log.e("MENU_ACTIVITY", "One or both TextViews not found in layout");
            return;
        }

        textWelcome.setText("Здесь будет информация о растениях и болезнях");

        // Initialize Firebase explicitly to avoid SecurityException
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        db = FirebaseFirestore.getInstance();
        if (db == null) {
            Log.e("MENU_ACTIVITY", "Firestore initialization failed");
            textInfo.setText("Ошибка инициализации Firestore");
            return;
        }

        db.collection("data").document("Data").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d("FIREBASE", "Документ найден: " + documentSnapshot.getData());
                        String fungalInfo = documentSnapshot.getString("Fungal");
                        if (fungalInfo != null && !fungalInfo.isEmpty()) {
                            String formattedText = StyleTextDP(fungalInfo);
                            textInfo.setText(HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_COMPACT));
                        } else {
                            textInfo.setText("Данные о болезни отсутствуют или поле пустое");
                            Log.w("FIREBASE", "Поле Fungal не найдено или пустое");
                        }
                    } else {
                        Log.e("FIREBASE", "Документ НЕ найден");
                        textInfo.setText("Документ не найден");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Ошибка загрузки Firestore: ", e);
                    textInfo.setText("Ошибка загрузки данных: " + e.getMessage());
                });
    }

    private String StyleTextDP(String fungalInfo) {
        String cleanedText = fungalInfo.replaceAll("<b/>", "</b>").replaceAll("\n", "<br>");

        cleanedText = cleanedText.replaceAll("^[⦁•]\\s*", "&#8226; ");

        return cleanedText;
    }
}