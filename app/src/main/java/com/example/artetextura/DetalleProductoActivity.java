package com.example.artetextura;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class DetalleProductoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);

        // Recibir el objeto Producto pasado como extra en el Intent
        Producto producto = (Producto) getIntent().getSerializableExtra("producto");

        if (producto != null) {
            // Encontrar los elementos de la UI en el layout
            TextView nombreTextView = findViewById(R.id.textViewNombreProducto);
            TextView descripcionTextView = findViewById(R.id.textViewDescripcionProducto);
            TextView precioTextView = findViewById(R.id.textViewPrecioProducto);
            ImageView imagenImageView = findViewById(R.id.imageViewProducto);

            // Poblar los elementos de la UI con los detalles del producto
            nombreTextView.setText(producto.getNombre());
            descripcionTextView.setText(producto.getDescripción());
            precioTextView.setText("Precio: " + producto.getPrecio() + "€");

            // Cargar la imagen usando Picasso
            Picasso.get().load(producto.getImagenURL()).into(imagenImageView);

        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalle_producto, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuVolver) {
            finish(); // Finaliza la actividad actual, volviendo a la anterior
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
