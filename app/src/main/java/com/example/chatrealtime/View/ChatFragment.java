package com.example.chatrealtime.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatrealtime.Adapter.MensajeAdapter;
import com.example.chatrealtime.R;
import com.example.chatrealtime.Model.Mensaje;
import com.example.chatrealtime.Network.FirebaseManager;
import com.example.chatrealtime.Network.MqttManager;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatFragment extends Fragment {

    private RecyclerView rvMensajes;
    private EditText etMensaje;
    private ImageButton btnEnviar;
    private TextView tvTituloChat;

    private MensajeAdapter adapter;
    private List<Mensaje> listaMensajes;

    private String miUid;
    private String uidDestino;
    private String nombreDestino;
    private String TOPIC_CHAT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            uidDestino = getArguments().getString("uid_destino");
            nombreDestino = getArguments().getString("nombre_destino");
        }

        miUid = FirebaseManager.getInstance().getCurrentUser().getUid();

        if (miUid.compareTo(uidDestino) < 0) {
            TOPIC_CHAT = "chat/" + miUid + "_" + uidDestino;
        } else {
            TOPIC_CHAT = "chat/" + uidDestino + "_" + miUid;
        }

        rvMensajes = view.findViewById(R.id.recycleViewMensajes);
        etMensaje = view.findViewById(R.id.etMensaje);
        btnEnviar = view.findViewById(R.id.btnEnviar);

        listaMensajes = new ArrayList<>();

        adapter = new MensajeAdapter(listaMensajes, miUid);

        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setStackFromEnd(true);
        rvMensajes.setLayoutManager(lm);
        rvMensajes.setAdapter(adapter);

        configurarMQTT();

        btnEnviar.setOnClickListener(v -> enviarMensaje());
    }

    private void configurarMQTT() {
        MqttManager mqtt = MqttManager.getInstance();

        // Aseguramos conexión
        mqtt.conectar(requireContext());

        // Nos suscribimos al canal
        mqtt.suscribirse(TOPIC_CHAT, new MqttManager.MensajeListener() {
            @Override
            public void onMensajeRecibido(String mensajeJson) {
                // ... (Tu código actual) ...
            }

            // AGREGA ESTO:
            @Override
            public void onError(String mensajeError) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), mensajeError, Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void enviarMensaje() {
        String texto = etMensaje.getText().toString().trim();
        if (texto.isEmpty()) return;

        String hora = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        try {
            // Creamos un JSON para enviar todo junto
            JSONObject json = new JSONObject();
            json.put("contenido", texto);
            json.put("senderId", miUid); // Importante para saber quién lo envió
            json.put("hora", hora);

            // PUBLICAMOS EN MQTT
            MqttManager.getInstance().publicar(TOPIC_CHAT, json.toString());

            // Limpiamos la caja de texto
            etMensaje.setText("");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void procesarMensajeRecibido(String jsonString) {
        try {
            // Convertimos el texto recibido de nuevo a objeto
            JSONObject json = new JSONObject(jsonString);

            String contenido = json.getString("contenido");
            String sender = json.getString("senderId");
            String hora = json.getString("hora");

            Mensaje nuevoMensaje = new Mensaje(contenido, sender, hora);

            // Agregamos a la lista y actualizamos
            listaMensajes.add(nuevoMensaje);
            adapter.notifyItemInserted(listaMensajes.size() - 1);
            rvMensajes.smoothScrollToPosition(listaMensajes.size() - 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}