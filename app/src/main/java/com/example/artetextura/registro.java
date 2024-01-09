package com.example.artetextura;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class registro extends AppCompatActivity {

    private EditText nuevoUsernameEditText, nuevoPasswordEditText;
    private Button registroButton, volverButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nuevoUsernameEditText = findViewById(R.id.nuevoUsernameEditText);
        nuevoPasswordEditText = findViewById(R.id.nuevoPasswordEditText);
        registroButton = findViewById(R.id.registroButton);
        volverButton = findViewById(R.id.volverButton);

        registroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Deshabilitar el botón para evitar múltiples clics
                registroButton.setEnabled(false);

                registrarNuevoUsuario();
            }
        });

        volverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(registro.this, InicioSesion.class);
                startActivity(intent);
                finish(); // Opcional, según la lógica de tu aplicación
            }
        });
    }

    private void registrarNuevoUsuario() {
        String nuevoUsername = nuevoUsernameEditText.getText().toString().trim();
        String nuevoPassword = nuevoPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(nuevoUsername) || TextUtils.isEmpty(nuevoPassword)) {
            Toast.makeText(registro.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            registroButton.setEnabled(true);
        } else {
            // Crear nuevo usuario en Firebase Authentication
            mAuth.createUserWithEmailAndPassword(nuevoUsername, nuevoPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Registro exitoso
                                Toast.makeText(registro.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                                // Redirigir a la pantalla de inicio de sesión
                                Intent intent = new Intent(registro.this, InicioSesion.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Si el registro falla, habilitar el botón y mostrar un mensaje de error
                                registroButton.setEnabled(true);
                                Toast.makeText(registro.this, "Error en el registro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}