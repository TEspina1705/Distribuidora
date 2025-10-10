package com.example.distribuidora;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
//clase AuthActivity maneja el login de ususarios
public class AuthActivity extends AppCompatActivity {

    //variables de edittext, boton y base de datos Firebase
    EditText email, password;
    Button btn_login;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);

        //dentro de onCreate vinculamos los elemento grafico con el codigo e inicializamos Firebase Autentication
        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        btn_login = findViewById(R.id.btn_login);
        mAuth = FirebaseAuth.getInstance();

        //boton Acceder activamos el evento con onclick
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailUser = email.getText().toString().trim();//obtiene email ingresado por usuario
                String passUser = password.getText().toString().trim();//obtiene contraseña ingresada por usuario

                // Validar si los campos están vacíos
                if (emailUser.isEmpty()) {
                    email.setError("Ingrese su correo electrónico");
                    email.requestFocus();
                    return;
                }

                // Validar formato de correo electrónico
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailUser).matches()) {
                    email.setError("Correo electrónico inválido");
                    email.requestFocus();
                    return;
                }

                // Validar contraseña vacía
                if (passUser.isEmpty()) {
                    password.setError("Ingrese su contraseña");
                    password.requestFocus();
                    return;
                }

                // Validar longitud mínima de contraseña
                if (passUser.length() < 6) {
                    password.setError("La contraseña debe tener al menos 6 caracteres");
                    password.requestFocus();
                    return;
                }

                // Si pasa todas las validaciones → intentar iniciar sesión
                loginUser(emailUser, passUser);

            }
        });
    }
    //Usamos Firebase Autentication para iniciar sesion
    private void loginUser(String emailUser, String passUser) {
        mAuth.signInWithEmailAndPassword(emailUser, passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){//si el inicio de sesion es exitoso nos dirige a MenuActivity
                    finish();
                    startActivity(new Intent(AuthActivity.this,MenuActivity.class));
                    Toast.makeText(AuthActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AuthActivity.this, "Error", Toast.LENGTH_SHORT).show();//muestra error
                }
            }
        //manejo de errores
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AuthActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}