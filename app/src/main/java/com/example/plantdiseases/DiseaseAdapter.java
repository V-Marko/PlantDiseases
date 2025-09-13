package com.example.plantdiseases;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DiseaseAdapter extends RecyclerView.Adapter<DiseaseAdapter.ViewHolder> {
    private List<Disease> diseases;

    public DiseaseAdapter(List<Disease> diseases) {
        this.diseases = diseases;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_disease, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Disease d = diseases.get(position);
        holder.tvName.setText(d.getName());
        holder.tvDesc.setText(d.getDescription());
        holder.tvSymptoms.setText(d.getSymptoms());
    }

    @Override
    public int getItemCount() {
        return diseases.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvSymptoms;
        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvSymptoms = itemView.findViewById(R.id.tvSymptoms);
        }
    }
}
