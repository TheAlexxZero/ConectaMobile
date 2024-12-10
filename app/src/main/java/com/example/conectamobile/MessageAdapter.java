package com.example.conectamobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;  // Lista de objetos Message

    public MessageAdapter() {
        messages = new ArrayList<>();
    }

    // Este método crea una nueva vista para cada elemento del RecyclerView
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflar el layout item_message.xml
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    // Este método es llamado para mostrar el contenido en cada elemento
    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageTextView.setText(message.getMessage());  // Asignar el mensaje al TextView
        holder.senderTextView.setText(message.getSender());    // Asignar el remitente al TextView
    }

    // Este método retorna el tamaño de la lista de mensajes
    @Override
    public int getItemCount() {
        return messages.size();
    }

    // Método para añadir un mensaje a la lista
    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);  // Notificar que se insertó un nuevo mensaje
    }

    // ViewHolder que mantiene la referencia del CardView y sus TextViews
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView messageTextView;
        TextView senderTextView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            senderTextView = itemView.findViewById(R.id.senderTextView);
        }
    }

    // Clase Message para almacenar el mensaje y el remitente
    public static class Message {
        private String message;
        private String sender;

        public Message(String message, String sender) {
            this.message = message;
            this.sender = sender;
        }

        public String getMessage() {
            return message;
        }

        public String getSender() {
            return sender;
        }
    }
}
