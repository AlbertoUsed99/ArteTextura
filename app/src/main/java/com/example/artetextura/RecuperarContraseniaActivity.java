package com.example.artetextura;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class RecuperarContraseniaActivity extends AppCompatActivity {

    private EditText emailEditText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasenia);

        emailEditText = findViewById(R.id.emailEditText);
        Button resetPasswordButton = findViewById(R.id.resetPasswordButton);
        mAuth = FirebaseAuth.getInstance();

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restablecerContrasenia();
            }
        });
    }

    private void restablecerContrasenia() {
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Por favor, ingrese su correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Mensaje de éxito
                            Toast.makeText(RecuperarContraseniaActivity.this, "Se ha enviado un correo electrónico para restablecer su contraseña", Toast.LENGTH_SHORT).show();

                            // Redirigir al inicio de sesión
                            Intent intent = new Intent(RecuperarContraseniaActivity.this, InicioSesion.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Manejo de errores
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                Toast.makeText(RecuperarContraseniaActivity.this, "No existe una cuenta con este correo electrónico", Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(RecuperarContraseniaActivity.this, "Formato de correo electrónico inválido", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(RecuperarContraseniaActivity.this, "Error al enviar el correo de restablecimiento", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
