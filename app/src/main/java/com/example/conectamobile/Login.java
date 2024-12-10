package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerTextView;

    // Firebase Auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Vincular las vistas con las variables
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);

        // Establecer el comportamiento del botón de inicio de sesión
        loginButton.setOnClickListener(view -> loginUser());

        // Si el usuario ya está logueado, redirigir a la pantalla principal
        if (mAuth.getCurrentUser() != null) {
            // El usuario ya está autenticado
            navigateToMainActivity();
        }

        // Comportamiento del texto para registrarse (Redirigir a la actividad de registro)
        registerTextView.setOnClickListener(v -> {
            // Redirigir a la actividad de registro
            Intent intent = new Intent(Login.this, Registro.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        // Obtener los valores de los campos de texto
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validar los campos
        if (email.isEmpty()) {
            emailEditText.setError("Por favor ingresa tu correo electrónico");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Por favor ingresa tu contraseña");
            passwordEditText.requestFocus();
            return;
        }

        // Autenticación con Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Inicio de sesión exitoso
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(Login.this, "Bienvenido " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    } else {
                        // Error en el inicio de sesión
                        Toast.makeText(Login.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Método para redirigir a la pantalla principal (si el login es exitoso)
    private void navigateToMainActivity() {
        // Redirigir a la actividad principal después de un inicio de sesión exitoso
        Intent intent = new Intent(Login.this, MainActivity.class); // Cambia MainActivity por la actividad principal que deseas
        startActivity(intent);
        finish(); // Finaliza la actividad de login
    }
}
