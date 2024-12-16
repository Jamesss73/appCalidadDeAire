package com.jms.calidaddeaire;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final List<HistoryItem> historyList = new ArrayList<>();

    public void setHistoryList(List<HistoryItem> newList) {
        historyList.clear();
        historyList.addAll(newList);
        notifyDataSetChanged();
    }

    public void addHistoryItem(HistoryItem item) {
        historyList.add(0, item); // Agrega al inicio
        notifyItemInserted(0);
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem item = historyList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTimestamp, tvTemperature, tvHumidity, tvCo2, tvStatus;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
            tvTemperature = itemView.findViewById(R.id.tv_temperature);
            tvHumidity = itemView.findViewById(R.id.tv_humidity);
            tvCo2 = itemView.findViewById(R.id.tv_co2);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }

        public void bind(HistoryItem item) {
            tvTimestamp.setText(item.timestamp);
            tvTemperature.setText(item.temperature + "°C");
            tvHumidity.setText(item.humidity + "%");
            tvCo2.setText(item.co2 + " ppm");
            tvStatus.setText(item.status);

            // Cambia el color de fondo según el estado
            int backgroundColor;
            switch (item.status.toLowerCase()) {
                case "buena":
                    backgroundColor = Color.WHITE;
                    break;
                case "mala":
                    backgroundColor = Color.parseColor("#FFA500"); // Naranja
                    break;
                case "crítica":
                    backgroundColor = Color.RED;
                    break;
                default:
                    backgroundColor = Color.GRAY; // Caso por defecto
            }
            itemView.setBackgroundColor(backgroundColor);
        }
    }
}
