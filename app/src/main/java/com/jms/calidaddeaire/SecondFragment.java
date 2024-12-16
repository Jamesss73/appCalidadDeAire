package com.jms.calidaddeaire;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jms.calidaddeaire.databinding.FragmentSecondBinding;

import java.util.ArrayList;
import java.util.List;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private HistoryAdapter adapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configuración del RecyclerView
        adapter = new HistoryAdapter();
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvHistory.setAdapter(adapter);

        // Botón para volver al primer fragmento
        binding.buttonSecond.setOnClickListener(v ->
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment)
        );


        // Cargar datos desde Firebase
        loadHistoryData();
    }

    private void loadHistoryData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("lecturas/historial");
        Query query = databaseReference.orderByKey().limitToLast(100);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<HistoryItem> historyList = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Object tempValue = child.child("timestamp").getValue();
                    String timestamp = tempValue != null ? tempValue.toString() : "N/A";

                    Object temperatureValue = child.child("temperature").getValue();
                    String temperature = temperatureValue != null ? temperatureValue.toString() : "N/A";

                    Object humidityValue = child.child("humidity").getValue();
                    String humidity = humidityValue != null ? humidityValue.toString() : "N/A";

                    Object co2Value = child.child("co2").getValue();
                    String co2 = co2Value != null ? co2Value.toString() : "N/A";

                    Object statusValue = child.child("status").getValue();
                    String status = statusValue != null ? statusValue.toString() : "N/A";

                    // Agregar al inicio de la lista para mostrar los más recientes primero
                    historyList.add(0, new HistoryItem(timestamp, temperature, humidity, co2, status));
                }
                adapter.setHistoryList(historyList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error al cargar historial", error.toException());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
