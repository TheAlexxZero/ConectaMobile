package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            String title = item.getTitle().toString();  // Obtener el título del item

            // Usar el título del item para redirigir a las actividades correspondientes
            switch (title) {
                case "Perfil":
                    startActivity(new Intent(MainActivity.this, Perfil.class));
                    return true;
                case "Chat":
                    startActivity(new Intent(MainActivity.this, ChatActivity.class));
                    return true;
                case "Contactos":
                    startActivity(new Intent(MainActivity.this, ContactsActivity.class));
                    return true;
                default:
                    return false;
            }
        });
    }
}
