package com.example.conectamobile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class Perfil extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1; // Código para elegir imagen
    private static final int PERMISSION_REQUEST_CODE = 2; // Código para permisos

    private ImageView profileImageView;
    private EditText nicknameEditText;
    private Button saveButton;

    private FirebaseAuth auth;
    private FirebaseStorage storage;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        profileImageView = findViewById(R.id.profileImageView);
        nicknameEditText = findViewById(R.id.nicknameEditText);
        saveButton = findViewById(R.id.btnguardar);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        // Verificar permisos de almacenamiento
        checkStoragePermission();

        // Cargar datos del perfil
        loadProfileData();

        // Cambiar foto de perfil
        profileImageView.setOnClickListener(v -> openFileChooser());

        // Guardar cambios
        saveButton.setOnClickListener(v -> saveProfileData());
    }

    // Verificar permisos de almacenamiento
    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    // Abrir galería de imágenes
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Manejar el resultado de la selección de imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            // Establecer la imagen seleccionada utilizando Picasso
            Picasso.get()
                    .load(imageUri)
                    .resize(500, 500) // Redimensiona la imagen para evitar sobrecargar la memoria
                    .centerCrop()  // Ajusta la imagen para que se recorte de forma adecuada
                    .into(profileImageView);  // Carga la imagen en el ImageView
        }
    }

    // Guardar los datos del perfil
    private void saveProfileData() {
        String userId = auth.getCurrentUser().getUid();
        String nickname = nicknameEditText.getText().toString().trim();

        if (nickname.isEmpty()) {
            Toast.makeText(this, "El nickname no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Crear el mapa de datos a guardar
        Map<String, Object> userData = new HashMap<>();
        userData.put("nickname", nickname);

        if (imageUri != null) {
            // Subir la foto de perfil a Firebase Storage
            StorageReference storageRef = storage.getReference("profile_images/" + userId + ".jpg");
            storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        userData.put("photoUrl", uri.toString());
                        saveToDatabase(userRef, userData);
                    })).addOnFailureListener(e ->
                    Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show());
        } else {
            saveToDatabase(userRef, userData);
        }
    }

    // Guardar los datos en Firebase Database
    private void saveToDatabase(DatabaseReference ref, Map<String, Object> userData) {
        ref.setValue(userData).addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show());
    }

    // Cargar los datos del perfil
    private void loadProfileData() {
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String nickname = snapshot.child("nickname").getValue(String.class);
                String photoUrl = snapshot.child("photoUrl").getValue(String.class);

                nicknameEditText.setText(nickname);

                if (photoUrl != null) {
                    // Cargar la imagen con Picasso
                    Picasso.get()
                            .load(photoUrl)
                            .resize(500, 500)
                            .centerCrop()
                            .into(profileImageView);
                }
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Error al cargar perfil", Toast.LENGTH_SHORT).show());
    }
}
