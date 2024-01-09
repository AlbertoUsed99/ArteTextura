package com.example.artetextura;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Establece el diseño de la interfaz de usuario de esta Actividad.
        setContentView(R.layout.activity_splash);

        // Crea un nuevo Handler y programa un Runnable para ejecutarse tras un retraso.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Crea un Intent para iniciar una nueva actividad.
                Intent i = new Intent(SplashActivity.this, InicioSesion.class);
                startActivity(i); // Inicia la actividad InicioSesion.
                finish(); // Cierra esta actividad, para que no se pueda regresar a ella.
            }
        }, 3000); // 3000 ms = 3 segundos de retraso antes de ejecutar el Runnable.
    }
}