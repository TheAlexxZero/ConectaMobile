package com.example.conectamobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class Registro extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private TextView loginTextView;

    // Firebase Auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_layout);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Vincular las vistas con las variables
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        loginTextView = findViewById(R.id.loginTextView);

        // Establecer el comportamiento del botón de registro
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        // Redirigir al inicio de sesión si ya tienes cuenta
        loginTextView.setOnClickListener(v -> {
            // Aquí puedes poner un intent para redirigir a la pantalla de login
            // Ejemplo: startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });
    }

    private void registerUser() {
        // Obtener los valores de los campos de texto
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validar los campos
        if (email.isEmpty()) {
            emailEditText.setError("Por favor ingresa tu correo electrónico");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Por favor ingresa una contraseña");
            passwordEditText.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.setError("Por favor confirma tu contraseña");
            confirmPasswordEditText.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Las contraseñas no coinciden");
            confirmPasswordEditText.requestFocus();
            return;
        }

        // Crear un nuevo usuario con Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registro exitoso
                        Toast.makeText(Registro.this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                        // Aquí puedes redirigir al usuario a la pantalla principal o de inicio de sesión
                        navigateToLoginActivity();
                    } else {
                        // Error en el registro
                        Toast.makeText(Registro.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Método para redirigir a la pantalla de inicio de sesión
    private void navigateToLoginActivity() {
        // Aquí puedes hacer un Intent para navegar a la actividad de login
        // Ejemplo: startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish(); // Finaliza la actividad de registro
    }
}
