package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddContactActivity extends AppCompatActivity {

    private EditText contactName, contactEmail;
    private Button saveContactButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcontact_activity);

        // Inicializar las vistas
        contactName = findViewById(R.id.contactName);
        contactEmail = findViewById(R.id.contactEmail);
        saveContactButton = findViewById(R.id.saveContactButton);

        // Configurar el botón para guardar el contacto
        saveContactButton.setOnClickListener(v -> saveContact());
    }

    private void saveContact() {
        String name = contactName.getText().toString().trim();
        String email = contactEmail.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty()) {
            // Mostrar un mensaje si faltan datos
            Toast.makeText(this, "Por favor, ingresa todos los datos.", Toast.LENGTH_SHORT).show();
        } else {
            // Aquí agregarías el contacto a la base de datos (Firebase, por ejemplo)
            // Por ahora, solo mostramos un mensaje
            Toast.makeText(this, "Contacto agregado: " + name, Toast.LENGTH_SHORT).show();

            // Regresar a la actividad anterior con el nuevo contacto (o actualizar la lista)
            Intent resultIntent = new Intent();
            resultIntent.putExtra("name", name);
            resultIntent.putExtra("email", email);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}
