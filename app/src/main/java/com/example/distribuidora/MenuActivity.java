package com.example.distribuidora;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {

    Button btn_cerrar, despacho;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);


        mAuth = FirebaseAuth.getInstance();
        despacho = findViewById(R.id.btn_despacho);
        btn_cerrar = findViewById(R.id.btn_cerrar);


        despacho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intento= new Intent(MenuActivity.this,DistanciaActivity2.class);
                startActivity(intento);
            }
        });

        btn_cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(MenuActivity.this, AuthActivity.class));
            }
        });




    }
}