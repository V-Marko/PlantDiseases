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
    private String currentLanguage = "ru";
    private Map<String, Map<String, String>> categoryTranslations;
    private Map<String, Integer> categoryImages; // Store image resource IDs

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }

    public CategoryAdapter(List<String> categories, OnCategoryClickListener clickListener) {
        this.categories = categories;
        this.clickListener = clickListener;
        initializeTranslations();
        initializeImages();
    }

    public void setLanguage(String language) {
        this.currentLanguage = language;
        notifyDataSetChanged();
    }

    private void initializeTranslations() {
        categoryTranslations = new HashMap<>();

        // Fungal
        Map<String, String> fungalTranslations = new HashMap<>();
        fungalTranslations.put("ru", "Грибковые");
        fungalTranslations.put("en", "Fungal");
        fungalTranslations.put("hy", "Սնկային");
        categoryTranslations.put("Fungal", fungalTranslations);

        // Bacterial
        Map<String, String> bacterialTranslations = new HashMap<>();
        bacterialTranslations.put("ru", "Бактериальные");
        bacterialTranslations.put("en", "Bacterial");
        bacterialTranslations.put("hy", "Բակտերիալ");
        categoryTranslations.put("Bacterial", bacterialTranslations);

        // Viral
        Map<String, String> viralTranslations = new HashMap<>();
        viralTranslations.put("ru", "Вирусные");
        viralTranslations.put("en", "Viral");
        viralTranslations.put("hy", "Վիրուսային");
        categoryTranslations.put("Viral", viralTranslations);
    }

    private void initializeImages() {
        categoryImages = new HashMap<>();
        categoryImages.put("Fungal", R.drawable.fungal_late_blight);
        categoryImages.put("Bacterial", R.drawable.bacterial_picture9);
        categoryImages.put("Viral", R.drawable.fungal_late_blight);
    }

    private String getTranslatedCategory(String category) {
        if (categoryTranslations.containsKey(category)) {
            Map<String, String> translations = categoryTranslations.get(category);
            if (translations.containsKey(currentLanguage)) {
                return translations.get(currentLanguage);
            }
        }
        return category;
    }

    private int getCategoryImage(String category) {
        return categoryImages.getOrDefault(category, R.drawable.fungal_late_blight);
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
        int imageResId = getCategoryImage(category);

        holder.categoryTextView.setText(translatedCategory);
        holder.categoryImageButton.setImageResource(imageResId);

        // Set click listener on itemView only to avoid redundancy
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