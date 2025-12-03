package com.example.chatrealtime.Network;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;

public class MqttManager {

    private static MqttManager instance;
    private MqttAndroidClient client;
    private static final String SERVER_URI = "tcp://broker.hivemq.com:1883";
    private static final String TAG = "MqttManager";

    public interface MensajeListener {
        void onMensajeRecibido(String mensaje);
        void onError(String mensajeError);
    }

    private MensajeListener listener;

    public static synchronized MqttManager getInstance() {
        if (instance == null) {
            instance = new MqttManager();
        }
        return instance;
    }

    // Sobrecarga para mantener compatibilidad si se usa en otro lado
    public void conectar(Context context) {
        conectar(context, null);
    }

    // NUEVO MÉTODO CON CALLBACK: Avisa cuando ya está conectado
    public void conectar(Context context, Runnable onConnected) {
        // 1. Si ya estamos conectados, ejecutamos el callback inmediatamente
        if (client != null && client.isConnected()) {
            if (onConnected != null) onConnected.run();
            return;
        }

        String clientId = org.eclipse.paho.client.mqttv3.MqttClient.generateClientId();
        client = new MqttAndroidClient(context.getApplicationContext(), SERVER_URI, clientId, Ack.AUTO_ACK);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "¡Conectado a MQTT exitosamente!");
                    // 2. Avisamos que ya se conectó para poder suscribirnos
                    if (onConnected != null) onConnected.run();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG, "Fallo conexión MQTT", exception);
                }
            });

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
                    Log.d(TAG, "Mensaje entregado");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void suscribirse(String topic, MensajeListener listener) {
        this.listener = listener;
        try {
            if (client != null && client.isConnected()) {
                client.subscribe(topic, 1);
                Log.d(TAG, "Suscrito al tema: " + topic);
            } else {
                Log.e(TAG, "Error: Intentando suscribirse sin estar conectado");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al suscribirse", e);
            if (listener != null) listener.onError("Error de suscripción");
        }
    }

    public void publicar(String topic, String mensaje) {
        try {
            if (client != null && client.isConnected()) {
                MqttMessage message = new MqttMessage();
                message.setPayload(mensaje.getBytes());
                client.publish(topic, message);
                Log.d(TAG, "Mensaje enviado: " + mensaje);
            } else {
                Log.e(TAG, "Cliente desconectado, no se envió el mensaje");
                if (listener != null) listener.onError("No hay conexión");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al publicar", e);
            if (listener != null) listener.onError("Error al enviar");
        }
    }
}