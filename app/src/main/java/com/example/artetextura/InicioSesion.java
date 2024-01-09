package com.example.artetextura;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class InicioSesion extends AppCompatActivity {
    private static final String KEY = "UnaClaveDe16Byte";
    private static final String INIT_VECTOR = "RandomInitVector";
    private EditText usernameEditText, passwordEditText;
    private Button loginButton, registerButton;
    private CheckBox rememberMeCheckBox;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        mAuth = FirebaseAuth.getInstance();
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("Credenciales", MODE_PRIVATE);
        cargarCredencialesGuardadas();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarSesion();
            }
        });
        TextView forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InicioSesion.this, RecuperarContraseniaActivity.class);
                startActivity(intent);
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InicioSesion.this, registro.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inicio_sesion, menu);
        return true;
    }

    private void cargarCredencialesGuardadas() {
        String emailEncriptado = sharedPreferences.getString("Email", "");
        String passwordEncriptado = sharedPreferences.getString("Password", "");

        if (!emailEncriptado.isEmpty() && !passwordEncriptado.isEmpty()) {
            String email = desencriptar(emailEncriptado);
            String password = desencriptar(passwordEncriptado);

            usernameEditText.setText(email);
            passwordEditText.setText(password);
            rememberMeCheckBox.setChecked(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuInfo) {
            mostrarInfoEmpresa();
            return true;
        } else if (id == R.id.menuCerrarApp) {
            cerrarAplicacion();
            return true;
        }else if (id == R.id.borrar) {
            // Limpiar los campos de texto
            usernameEditText.setText("");
            passwordEditText.setText("");
            rememberMeCheckBox.setChecked(false);

            // Borrar las credenciales guardadas en SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("Email");
            editor.remove("Password");
            editor.apply();  // Asegúrate de llamar a apply() para guardar los cambios

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void mostrarInfoEmpresa() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Información de la Empresa");
        builder.setMessage("Explora nuestro catálogo único de decoración, artesanía y regalos exclusivos, cada uno con un toque especial de nuestro pintoresco pueblo. Descubre piezas auténticas y llenas de encanto local y moderno.  \nDirección: C/Extrevedes 55 Used 50374 \nTeléfono: 608465321");
        builder.setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void cerrarAplicacion() {
        finishAffinity(); // Cierra la actividad actual y todas las actividades padres
    }
    private String encriptar(String value) {
        try {
            // Crear un nuevo IvParameterSpec con el vector de inicialización.
            // Necesario para el modo CBC (Cipher Block Chaining).
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));

            // Crear un nuevo SecretKeySpec para la encriptación AES con la clave proporcionada.
            SecretKeySpec skeySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");

            // Obtener una instancia de Cipher para el algoritmo AES/CBC/PKCS5PADDING.
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

            // Inicializar el Cipher en modo de encriptación con la clave y el IV.
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            // Realizar la encriptación del valor proporcionado.
            byte[] encrypted = cipher.doFinal(value.getBytes());

            // Convertir los bytes encriptados a una cadena Base64 y devolverla.
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception ex) {
            // Imprimir la pila de excepciones en caso de error.
            ex.printStackTrace();
        }
        return null;
    }

    private String desencriptar(String encrypted) {
        try {
            // Crear un nuevo IvParameterSpec con el vector de inicialización.
            // Necesario para el modo CBC.
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));

            // Crear un nuevo SecretKeySpec para la desencriptación AES con la clave proporcionada.
            SecretKeySpec skeySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");

            // Obtener una instancia de Cipher para el algoritmo AES/CBC/PKCS5PADDING.
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

            // Inicializar el Cipher en modo de desencriptación con la clave y el IV.
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            // Decodificar la cadena Base64 encriptada a bytes y desencriptar.
            byte[] original = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT));

            // Convertir los bytes desencriptados a una cadena y devolverla.
            return new String(original);
        } catch (Exception ex) {
            // Imprimir la pila de excepciones en caso de error.
            ex.printStackTrace();
        }
        return null;
    }

    private void iniciarSesion() {
        String email = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validar que los campos no estén vacíos
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor, ingrese correo electrónico y contraseña", Toast.LENGTH_SHORT).show();
            return;  // Salir del método si los campos están vacíos
        }
        // Si el CheckBox está marcado, guardar las credenciales
        if (rememberMeCheckBox.isChecked()) {
            String emailEncriptado = encriptar(email);
            String passwordEncriptado = encriptar(password);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Email", emailEncriptado);
            editor.putString("Password", passwordEncriptado);
            editor.apply();
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Inicio de sesión exitoso
                            Toast.makeText(InicioSesion.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                            // Redirigir a otra actividad
                            Intent intent = new Intent(InicioSesion.this, Interfaz.class);
                            startActivity(intent);



                        } else {
                            // Manejar errores durante el inicio de sesión
                            Toast.makeText(InicioSesion.this, "Error durante el inicio de sesión", Toast.LENGTH_SHORT).show();

                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(InicioSesion.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                            } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(InicioSesion.this, "Credenciales inválidas", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
