package com.example.plantdiseases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<String> categories;
    private Context context;
    private OnCategoryClickListener clickListener;
    private String currentLanguage = "ru"; // язык по умолчанию

    // Словарь для перевода названий категорий
    private Map<String, Map<String, String>> categoryTranslations;

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }

    public CategoryAdapter(List<String> categories, OnCategoryClickListener clickListener) {
        this.categories = categories;
        this.clickListener = clickListener;
        initializeTranslations();
    }

    // Метод для установки текущего языка
    public void setLanguage(String language) {
        this.currentLanguage = language;
        notifyDataSetChanged(); // Обновляем все элементы при изменении языка
    }

    private void initializeTranslations() {
        categoryTranslations = new HashMap<>();

        // Перевод для "Fungal"
        Map<String, String> fungalTranslations = new HashMap<>();
        fungalTranslations.put("ru", "Грибковые");
        fungalTranslations.put("en", "Fungal");
        fungalTranslations.put("hy", "Սնկային");
        categoryTranslations.put("Fungal", fungalTranslations);

        // Перевод для "Bacterial"
        Map<String, String> bacterialTranslations = new HashMap<>();
        bacterialTranslations.put("ru", "Бактериальные");
        bacterialTranslations.put("en", "Bacterial");
        bacterialTranslations.put("hy", "Բակտերիալ");
        categoryTranslations.put("Bacterial", bacterialTranslations);

        // Перевод для "Viral"
        Map<String, String> viralTranslations = new HashMap<>();
        viralTranslations.put("ru", "Вирусные");
        viralTranslations.put("en", "Viral");
        viralTranslations.put("hy", "Վիրուսային");
        categoryTranslations.put("Viral", viralTranslations);
    }

    // Метод для получения переведенного названия категории
    private String getTranslatedCategory(String category) {
        if (categoryTranslations.containsKey(category)) {
            Map<String, String> translations = categoryTranslations.get(category);
            if (translations.containsKey(currentLanguage)) {
                return translations.get(currentLanguage);
            }
        }
        // Если перевод не найден, возвращаем оригинальное название
        return category;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        String translatedCategory = getTranslatedCategory(category);

        holder.categoryTextView.setText(translatedCategory);

        // Pass click to MenuActivity
        holder.categoryImageButton.setOnClickListener(v -> clickListener.onCategoryClick(category));

        // Также делаем кликабельным весь элемент, если нужно
        holder.itemView.setOnClickListener(v -> clickListener.onCategoryClick(category));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView categoryImageButton;
        TextView categoryTextView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImageButton = itemView.findViewById(R.id.categoryImage);
            categoryTextView = itemView.findViewById(R.id.categoryText);
        }
    }
}