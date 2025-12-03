package com.example.chatrealtime.Network;

import android.content.Context;
import android.util.Log;

// Imports de la librería base de MQTT (Paho)
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

// Import de la librería MODERNA para Android (Servicio)
import info.mqtt.android.service.MqttAndroidClient;

public class MqttManager {

    private static MqttManager instance;
    private MqttAndroidClient client;

    // Servidor público de pruebas
    private static final String SERVER_URI = "tcp://broker.hivemq.com:1883";
    private static final String TAG = "MqttManager";

    // 1. Interfaz mejorada: Ahora soporta recibir mensajes Y errores
    public interface MensajeListener {
        void onMensajeRecibido(String mensaje);
        void onError(String mensajeError);
    }

    private MensajeListener listener;

    // Singleton
    public static synchronized MqttManager getInstance() {
        if (instance == null) {
            instance = new MqttManager();
        }
        return instance;
    }

    // 2. Método CONECTAR
    public void conectar(Context context) {
        // Si ya estamos conectados, no hacemos nada
        if (client != null && client.isConnected()) {
            Log.d(TAG, "Ya estaba conectado.");
            return;
        }

        String clientId = org.eclipse.paho.client.mqttv3.MqttClient.generateClientId();

        client = new MqttAndroidClient(context.getApplicationContext(), SERVER_URI, clientId, null);

        IMqttToken token = client.connect();
        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d(TAG, "¡Conectado a MQTT exitosamente!");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                // Error asíncrono (ej: servidor caído)
                Log.e(TAG, "Fallo conexión MQTT", exception);
            }
        });

        // Configurar qué pasa cuando llega un mensaje
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.w(TAG, "Se perdió la conexión MQTT", cause);
                if (listener != null) listener.onError("Conexión perdida");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String contenido = new String(message.getPayload());
                Log.d(TAG, "Mensaje recibido: " + contenido);

                if (listener != null) {
                    listener.onMensajeRecibido(contenido);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d(TAG, "Mensaje entregado al servidor");
            }
        });
    }

    // 3. Método SUSCRIBIRSE
    public void suscribirse(String topic, MensajeListener listener) {
        this.listener = listener;
        if (client != null && client.isConnected()) {
            client.subscribe(topic, 1); // Calidad de servicio 1
            Log.d(TAG, "Suscrito al tema: " + topic);
        } else {
            Log.w(TAG, "No se pudo suscribir: Cliente desconectado");
            if (listener != null) listener.onError("No conectado al chat");
        }
    }

    // 4. Método PUBLICAR (Enviar mensaje)
    public void publicar(String topic, String mensaje) {
        if (client != null && client.isConnected()) {
            MqttMessage message = new MqttMessage();
            message.setPayload(mensaje.getBytes());
            client.publish(topic, message);
            Log.d(TAG, "Mensaje enviado: " + mensaje);
        } else {
            Log.e(TAG, "Intento de envío fallido: Cliente desconectado");
            if (listener != null) listener.onError("No hay conexión para enviar");
        }
    }
}