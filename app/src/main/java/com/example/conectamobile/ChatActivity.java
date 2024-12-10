package com.example.conectamobile;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;

public class ChatActivity extends AppCompatActivity {

    private MqttClient mqttClient;
    private RecyclerView messagesRecyclerView;
    private MessageAdapter messageAdapter;
    private TextInputLayout messageInputLayout;
    private String topic = "chat/messages"; // Define tu tema de MQTT

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);  // Asegúrate de que el layout se llama chat_activity

        // Inicializar la vista
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messageInputLayout = findViewById(R.id.messageInputLayout);

        // Configurar RecyclerView
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter();  // Crea un adapter para mostrar los mensajes
        messagesRecyclerView.setAdapter(messageAdapter);

        // Configurar el botón de enviar
        findViewById(R.id.sendButton).setOnClickListener(v -> sendMessage());

        // Inicializar cliente MQTT
        String clientId = MqttClient.generateClientId();  // Generar un ID único para el cliente
        try {
            mqttClient = new MqttClient("tcp://broker.hivemq.com:1883", clientId, null);  // Conectar al broker (puedes reemplazarlo por tu broker)

            // Configurar opciones de conexión
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);

            // Conectar al broker
            mqttClient.connect(connectOptions);

            // Configurar el callback para recibir mensajes
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    // Manejar la pérdida de conexión
                    runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Conexión perdida", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // Manejar la llegada de un nuevo mensaje
                    String receivedMessage = new String(message.getPayload());
                    // Suponiendo que el remitente es un nombre estático (puedes cambiarlo con lógica real)
                    String sender = "Usuario";  // Aquí deberías extraer el remitente de alguna manera

                    // Crear un objeto Message con el mensaje recibido y el remitente
                    MessageAdapter.Message newMessage = new MessageAdapter.Message(receivedMessage, sender);

                    runOnUiThread(() -> {
                        // Agregar el mensaje recibido al RecyclerView
                        messageAdapter.addMessage(newMessage);
                    });
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Manejar la confirmación de entrega (si es necesario)
                }
            });

            // Suscribirse al tópico
            mqttClient.subscribe(topic);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al conectar con el broker", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessage() {
        String message = messageInputLayout.getEditText().getText().toString().trim();
        if (!TextUtils.isEmpty(message)) {
            try {
                // Crear un mensaje MQTT
                MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                mqttMessage.setQos(1);  // Establecer QoS si es necesario

                // Publicar el mensaje en el tema
                mqttClient.publish(topic, mqttMessage);
                messageInputLayout.getEditText().setText(""); // Limpiar el campo de texto
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al enviar el mensaje", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Por favor, escribe un mensaje", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desconectar MQTT cuando se cierra la actividad
        if (mqttClient.isConnected()) {
            try {
                mqttClient.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
