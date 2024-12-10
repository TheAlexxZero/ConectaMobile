package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ContactsAdapter adapter;
    private List<Contact> contactList;
    private SearchView searchView;
    private Button addContactButton;

    private DatabaseReference databaseReference;
    private String userId;
    private static final int REQUEST_ADD_CONTACT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactos_activity);

        recyclerView = findViewById(R.id.contactsRecyclerView);
        searchView = findViewById(R.id.searchView);
        addContactButton = findViewById(R.id.addContactButton);

        // Inicializar la lista de contactos
        contactList = new ArrayList<>();
        adapter = new ContactsAdapter(contactList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Obtener referencia a Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Cargar contactos desde Firebase
        loadContactsFromFirebase("");

        // Configurar el botón para agregar un nuevo contacto
        addContactButton.setOnClickListener(v -> {
            Intent intent = new Intent(ContactsActivity.this, AddContactActivity.class);
            startActivityForResult(intent, REQUEST_ADD_CONTACT);
        });

        // Configurar el SearchView para la búsqueda de contactos
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadContactsFromFirebase(query); // Filtrar contactos
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadContactsFromFirebase(newText); // Filtrar contactos en tiempo real
                return true;
            }
        });
    }

    // Modificada para filtrar contactos según la búsqueda
    private void loadContactsFromFirebase(String query) {
        // Si no hay consulta, cargar todos los contactos
        DatabaseReference queryRef = databaseReference.child("users").child(userId).child("contacts");

        if (!query.isEmpty()) {
            queryRef.orderByChild("name").startAt(query).endAt(query + "\uf8ff")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            contactList.clear();
                            for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                                Contact contact = contactSnapshot.getValue(Contact.class);
                                contactList.add(contact);
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ContactsActivity.this, "Error al cargar contactos", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Si no hay consulta, cargar todos los contactos
            queryRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    contactList.clear();
                    for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                        Contact contact = contactSnapshot.getValue(Contact.class);
                        contactList.add(contact);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ContactsActivity.this, "Error al cargar contactos", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Maneja la adición de un nuevo contacto
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_CONTACT && resultCode == RESULT_OK) {
            // Obtener los datos del nuevo contacto
            String name = data.getStringExtra("name");
            String email = data.getStringExtra("email");

            // Crear un nuevo contacto
            Contact newContact = new Contact(name, email);

            // Guardar el contacto en Firebase
            databaseReference.child("users").child(userId).child("contacts").push().setValue(newContact)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Contacto agregado", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al agregar contacto", Toast.LENGTH_SHORT).show());
        }
    }
}
