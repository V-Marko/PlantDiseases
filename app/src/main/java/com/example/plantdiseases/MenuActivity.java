package com.example.plantdiseases;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
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
                            textInfo.setText(fungalInfo);
                        } else {
                            textInfo.setText("Данные о болезни отсутствуют или поле пустое");
                            Log.w("FIREBASE", "Поле Fyngak не найдено или пустое");
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
}