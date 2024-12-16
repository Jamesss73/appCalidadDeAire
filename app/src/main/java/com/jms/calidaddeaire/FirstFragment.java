package com.jms.calidaddeaire;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jms.calidaddeaire.databinding.FragmentFirstBinding;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private static final String CHANNEL_ID = "air_quality_alerts";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Referencias a los TextView
        TextView tvTemperature = binding.tvTemperature;
        TextView tvHumidity = binding.tvHumidity;
        TextView tvCO2 = binding.tvCo2;
        TextView tvStatus = binding.tvStatus;
        TextView tvTimestamp = binding.tvTimestamp;

        // Referencia al contenedor principal (para cambiar el color)
        View lastDataContainer = binding.lastDataContainer;

        // Configuración del botón para navegar al segundo fragmento
        binding.buttonFirst.setOnClickListener(v ->
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
        );
        super.onViewCreated(view, savedInstanceState);



        // Crear el canal de notificaciones
        createNotificationChannel();

        // Conexión a Firebase Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("lecturas/ultima");

        // Listener para obtener datos en tiempo real
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Obtiene los datos del nodo
                    Object tempValue = snapshot.child("temperature").getValue();
                    String temperature = tempValue != null ? tempValue.toString() : "N/A";

                    Object humValue = snapshot.child("humidity").getValue();
                    String humidity = humValue != null ? humValue.toString() : "N/A";

                    Object co2Value = snapshot.child("co2").getValue();
                    String co2 = co2Value != null ? co2Value.toString() : "N/A";

                    Object statusValue = snapshot.child("status").getValue();
                    String status = statusValue != null ? statusValue.toString() : "N/A";

                    Object timestampValue = snapshot.child("timestamp").getValue();
                    String timestamp = timestampValue != null ? timestampValue.toString() : "N/A";

                    // Actualiza los TextView con los valores obtenidos
                    tvTemperature.setText(temperature + "°C");
                    tvHumidity.setText(humidity + "%");
                    tvCO2.setText(co2 + " ppm");
                    tvStatus.setText(status);
                    tvTimestamp.setText(timestamp);

                    // Cambia el color del contenedor dependiendo del estado
                    int backgroundColor;
                    switch (status.toLowerCase()) {
                        case "buena":
                            backgroundColor = Color.WHITE;
                            break;
                        case "mala":
                            backgroundColor = Color.parseColor("#FFA500");
                            sendNotification("Mala", "La calidad del aire es MALA. ¡Toma precauciones!");
                            break;
                        case "crítica":
                            backgroundColor = Color.parseColor("#FF0000");
                            sendNotification("Crítica", "¡La calidad del aire es CRÍTICA! Evita actividades al aire libre.");
                            break;
                        default:
                            backgroundColor = Color.GRAY;
                    }

                    lastDataContainer.setBackgroundColor(backgroundColor);
                } else {
                    Log.w("Firebase", "No existen datos en el nodo 'lecturas/ultima'");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejo de errores
                Log.e("Firebase", "Error al leer datos", error.toException());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Solo para Android 8.0 o superior
            CharSequence name = "Air Quality Alerts";
            String description = "Notificaciones sobre la calidad del aire";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);

            // Registrar el canal con el sistema
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Método para mostrar una notificación
    @SuppressLint("MissingPermission")
    private void sendNotification(String status, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Asegúrate de tener un ícono en res/drawable
                .setContentTitle("Alerta de Calidad del Aire")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
