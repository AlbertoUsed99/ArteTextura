package com.example.artetextura;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.net.wifi.p2p.WifiP2pDevice.UNAVAILABLE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class EditarProductoActivity extends AppCompatActivity {

    private EditText editTextNombre, editTextDescripcion, editTextPrecio;
    private Button buttonGuardar, buttonCancelar;
    private Producto producto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_producto);

        // Inicialización de vistas
        editTextNombre = findViewById(R.id.editTextNombreProducto);
        editTextDescripcion = findViewById(R.id.editTextDescripcionProducto);
        editTextPrecio = findViewById(R.id.editTextPrecioProducto);
        buttonGuardar = findViewById(R.id.buttonGuardar);
        buttonCancelar = findViewById(R.id.buttonCancelar);

        // Recuperar el producto pasado a esta actividad
        producto = (Producto) getIntent().getSerializableExtra("producto");

        // Rellenar los campos con la información del producto
        if (producto != null) {
            editTextNombre.setText(producto.getNombre());
            editTextDescripcion.setText(producto.getDescripción());
            editTextPrecio.setText(String.valueOf(producto.getPrecio()));
        }

        // Botón Guardar
        buttonGuardar.setOnClickListener(v -> {
            // Validar y obtener los valores actualizados de los campos de texto
            String nombre = editTextNombre.getText().toString();
            String descripcion = editTextDescripcion.getText().toString();
            int precio;
            try {
                precio = Integer.parseInt(editTextPrecio.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Precio no válido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificar la conexión a la red antes de la operación de Firestore
            if (isNetworkAvailable()) {
                // Preparar los datos para actualizar, sin incluir DocumentId ni ImagenURL
                Map<String, Object> productoMap = new HashMap<>();
                productoMap.put("Nombre", nombre);
                productoMap.put("Descripción", descripcion);
                productoMap.put("Precio", precio);

                // Actualizar el producto en Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Productos").document(producto.getDocumentId())
                        .update(productoMap)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Producto actualizado con éxito", Toast.LENGTH_SHORT).show();
                            finish(); // Volver a la actividad anterior
                        })
                        .addOnFailureListener(e -> {
                            if (e instanceof FirebaseFirestoreException) {
                                FirebaseFirestoreException firestoreException = (FirebaseFirestoreException) e;
                                switch (firestoreException.getCode()) {
                                    case PERMISSION_DENIED:
                                        Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                                        break;
                                    case UNAVAILABLE:
                                        Toast.makeText(this, "Firestore no disponible", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        Toast.makeText(this, "Error al actualizar el producto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            } else {
                                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón Cancelar
        buttonCancelar.setOnClickListener(v -> {
            finish(); // Volver a la actividad anterior sin guardar cambios
        });
    }

    // Método para verificar la disponibilidad de la red
    private boolean isNetworkAvailable() {
        // Obtiene el ConnectivityManager del sistema.
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Verifica si el ConnectivityManager está disponible.
        if (connectivityManager != null) {
            // Obtiene las capacidades de la red activa.
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

            // Verifica si hay una conexión de Internet.
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    // Hay conexión Wi-Fi.
                    return true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    // Hay conexión de datos móviles.
                    return true;
                }
            }
        }
        // No hay conexión de red disponible.
        return false;
    }
}