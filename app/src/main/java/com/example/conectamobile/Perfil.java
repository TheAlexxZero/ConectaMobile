package com.example.conectamobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class Perfil extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

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
        saveButton = findViewById(R.id.saveButton);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        // Cargar datos del perfil
        loadProfileData();

        // Cambiar foto de perfil
        profileImageView.setOnClickListener(v -> openFileChooser());

        // Guardar cambios
        saveButton.setOnClickListener(v -> saveProfileData());
    }

    private void loadProfileData() {
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String nickname = snapshot.child("nickname").getValue(String.class);
                String photoUrl = snapshot.child("photoUrl").getValue(String.class);

                nicknameEditText.setText(nickname);

                if (photoUrl != null) {
                    // Cargar foto de perfil usando Glide
                    Glide.with(this).load(photoUrl).into(profileImageView);
                }
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Error al cargar perfil", Toast.LENGTH_SHORT).show());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }

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
            // Subir foto de perfil a Firebase Storage
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

    private void saveToDatabase(DatabaseReference ref, Map<String, Object> userData) {
        ref.setValue(userData).addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show());
    }
}
