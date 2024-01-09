package com.example.artetextura;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Interfaz extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interfaz);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtén los datos de Firebase y configúralos en el adaptador
        cargarDatosDesdeFirebase();

        // Configura los botones para editar y eliminar
        configurarBotones();
    }

    // Método para cargar datos desde Firebase y configurar el RecyclerView
    private void cargarDatosDesdeFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("productos")
                .whereEqualTo("ownerId", userId)  // Filtra por el ID del usuario actual
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<MyDataModel> productos = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                // Convierte cada documento en un objeto MyDataModel
                                MyDataModel producto = document.toObject(MyDataModel.class);
                                productos.add(producto);
                            }

                            // Configura el adaptador con la lista de productos
                            adapter = new MyAdapter(productos, Interfaz.this);
                            recyclerView.setAdapter(adapter);
                        } else {
                            // Manejar errores de Firebase aquí
                            Toast.makeText(Interfaz.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Método para configurar los botones de editar y eliminar
    private void configurarBotones() {
        Button editButton = findViewById(R.id.editButton);
        Button deleteButton = findViewById(R.id.deleteButton);

        // Configura un clic en el RecyclerView para abrir una actividad de detalle
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // Abre una actividad de detalle con el producto seleccionado
                        abrirActividadDetalle(adapter.getItem(position));
                    }
                }));

        // Configura clics en los botones de editar y eliminar
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Maneja la lógica para editar el producto seleccionado
                if (adapter.getSelectedItem() != null) {
                    abrirActividadEditar(adapter.getSelectedItem());
                } else {
                    Toast.makeText(Interfaz.this, "Seleccione un producto para editar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Maneja la lógica para eliminar el producto seleccionado
                if (adapter.getSelectedItem() != null) {
                    eliminarProducto(adapter.getSelectedItem());
                } else {
                    Toast.makeText(Interfaz.this, "Seleccione un producto para eliminar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Método para abrir la actividad de detalle
    private void abrirActividadDetalle(MyDataModel producto) {
        // Implementa la lógica para abrir la actividad de detalle aquí
        // Puedes pasar información adicional a la actividad de detalle utilizando Intents
    }

    // Método para abrir la actividad de edición
    private void abrirActividadEditar(MyDataModel producto) {
        // Implementa la lógica para abrir la actividad de edición aquí
        // Puedes pasar información adicional a la actividad de edición utilizando Intents
    }

    // Método para eliminar un producto
    private void eliminarProducto(MyDataModel producto) {
        // Implementa la lógica para eliminar el producto de Firebase aquí
    }
}
