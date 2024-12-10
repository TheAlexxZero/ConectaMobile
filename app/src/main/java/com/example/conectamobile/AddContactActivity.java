package com.example.conectamobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddContactActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcontact_activity);  // Aquí debes tener el diseño XML adecuado

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();

            // Verificar si los campos están vacíos
            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(AddContactActivity.this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear un intent con los datos
            Intent resultIntent = new Intent();
            resultIntent.putExtra("name", name);
            resultIntent.putExtra("email", email);

            // Establecer el resultado de la actividad y regresar
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }
}
