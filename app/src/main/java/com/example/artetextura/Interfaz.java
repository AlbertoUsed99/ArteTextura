package com.example.artetextura;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Interfaz extends AppCompatActivity {
    Toast Toast;
    boolean seEstaUsandoFiltro = true ;
    private EditText searchField;
    private RecyclerView
            productosRecyclerView;
    private ProductosAdapter adapter;
    private ArrayList<Producto> listaProductos;
    private ArrayList<Producto> listaFiltrada;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interfaz);

        searchField = findViewById(R.id.searchEditText);
        productosRecyclerView = findViewById(R.id.recyclerView);
        listaProductos = new ArrayList<>();
        listaFiltrada = new ArrayList<>();
        adapter = new ProductosAdapter(listaFiltrada);

        // Configura el RecyclerView para mostrar los productos.
        productosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productosRecyclerView.setAdapter(adapter);

        // Inicializa la instancia de la base de datos de Firebase.
        db = FirebaseFirestore.getInstance();
        // Carga los productos de la base de datos.
        cargarProductos();
        // Agrega un TextWatcher al campo de búsqueda para filtrar productos mientras se escribe.
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtra los productos basándose en el texto introducido.
                filtrarProductos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        // Establece un escucha para eventos de clic en los elementos del adaptador del RecyclerView.
        adapter.setOnItemClickListener(new ProductosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // Maneja el evento de clic en un producto.
                verDetallesProducto(position);
            }
        });
        // Establece un escucha para eventos de clic largo en los elementos del adaptador.
        adapter.setOnItemLongClickListener(new ProductosAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                // Maneja el evento de clic largo en un producto.
                mostrarOpcionesProducto(position);
            }
        });
    }
// Método para cargar productos de la colección "Productos" en Firebase Firestore.
    private void cargarProductos() {
        // Consulta a la base de datos.
        db.collection("Productos").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listaProductos.clear();
                // Itera sobre los documentos obtenidos.
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Convierte cada documento a un objeto Producto.
                    Producto producto = document.toObject(Producto.class);
                    producto.setDocumentId(document.getId()); // Asignar el ID del documento
                    listaProductos.add(producto);
                    Log.d("CargarProductos", "Producto cargado: " + producto.getNombre() + ", ID: " + producto.getDocumentId()); // Depuración
                }
                // Actualiza la lista filtrada de productos.
                filtrarProductos(searchField.getText().toString());
            } else {
                // Maneja errores en la carga de productos.
                Log.e("CargarProductos", "Error al cargar productos: ", task.getException()); // Manejo de errores
                Toast.makeText(this, "Error al cargar productos", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void filtrarProductos(String texto) {
        ArrayList<Producto> listaTemp = new ArrayList<>(listaProductos); // Lista temporal para aplicar filtros
        // Ahora aplica el filtro de búsqueda sobre la listaTemp
        if (!texto.isEmpty()) {
            listaTemp = listaTemp.stream()
                    .filter(producto -> producto.getNombre().toLowerCase().contains(texto.toLowerCase()))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        // Actualiza listaFiltrada y notifica al adaptador
        listaFiltrada.clear();
        listaFiltrada.addAll(listaTemp);
        adapter.actualizarLista(listaFiltrada);
    }

    private void verDetallesProducto(int position) {

        Producto productoSeleccionado = (seEstaUsandoFiltro ? listaFiltrada : listaProductos).get(position);
        Intent intent = new Intent(Interfaz.this, DetalleProductoActivity.class);
        intent.putExtra("producto", productoSeleccionado); // Asegúrate de que Producto implementa Serializable o Parcelable
        startActivity(intent);
    }

    private void mostrarOpcionesProducto(int position) {
        Producto productoSeleccionado = (seEstaUsandoFiltro ? listaFiltrada : listaProductos).get(position);
        CharSequence opciones[] = new CharSequence[]{"Editar", "Borrar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(Interfaz.this);
        builder.setTitle("Selecciona una opción");
        builder.setItems(opciones, (dialog, which) -> {
            if (which == 0) {
                editarProducto(productoSeleccionado);
            } else if (which == 1) {
                borrarProducto(productoSeleccionado);
            }
        });
        builder.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_interfaz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuCerrarSesion) {
            cerrarSesion();
            return true;
        } else if (id == R.id.menuActualizar) {
            cargarProductos();  // Es el método para actualizar los datos
            return true;
        } else if (id == R.id.menuInfo) {
            mostrarInfoEmpresa();
            return true;
        }
        // Lógica para el menú de filtrado
        else if (id == R.id.menuFiltrarMenorMayor) {
            filtrarPorPrecio("menor_a_mayor");
            return true;
        } else if (id == R.id.menuFiltrarMayorMenor) {
            filtrarPorPrecio("mayor_a_menor");
            return true;
        } else if (id == R.id.menuFiltrarAZ) {
            filtrarAlfabeticamente("A_Z");
            return true;
        } else if (id == R.id.menuFiltrarZA) {
            filtrarAlfabeticamente("Z_A");
            return true;
        } else if (id == R.id.menuQuitarFiltros) {
            quitarFiltros();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
    private void quitarFiltros() {
        // Restablecer la listaFiltrada a la lista completa original
        listaFiltrada.clear();
        listaFiltrada.addAll(new ArrayList<>(listaProductos));

        // Actualizar el adaptador con la lista completa
        adapter.actualizarLista(listaFiltrada);

        // Actualizar el estado de los filtros
        seEstaUsandoFiltro = false;
    }

    // Métodos para filtrar
    private void filtrarPorPrecio(String tipo) {
        ArrayList<Producto> productosFiltrados = new ArrayList<>(listaProductos);
        if (tipo.equals("menor_a_mayor")) {
            Collections.sort(productosFiltrados, (p1, p2) -> Double.compare(p1.getPrecio(), p2.getPrecio()));
        } else if (tipo.equals("mayor_a_menor")) {
            Collections.sort(productosFiltrados, (p1, p2) -> Double.compare(p2.getPrecio(), p1.getPrecio()));
        }

        adapter.actualizarLista(productosFiltrados);
        listaFiltrada = new ArrayList<>(productosFiltrados);
    }

    private void filtrarAlfabeticamente(String orden) {
        ArrayList<Producto> productosFiltrados = new ArrayList<>(listaProductos);
        if (orden.equals("A_Z")) {
            Collections.sort(productosFiltrados, (p1, p2) -> p1.getNombre().compareTo(p2.getNombre()));
        } else if (orden.equals("Z_A")) {
            Collections.sort(productosFiltrados, (p1, p2) -> p2.getNombre().compareTo(p1.getNombre()));
        }

        adapter.actualizarLista(productosFiltrados);
        listaFiltrada = new ArrayList<>(productosFiltrados);
    }
    private void mostrarInfoEmpresa() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Información de la Empresa");
        builder.setMessage("Explora nuestro catálogo único de decoración, artesanía y regalos exclusivos, cada uno con un toque especial de nuestro pintoresco pueblo. Descubre piezas auténticas y llenas de encanto local y moderno.  \nDirección: C/Extrevedes 55 Used 50374 \nTeléfono: 608465321");
        builder.setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut(); // Cierra la sesión en Firebase
        Intent intent = new Intent(Interfaz.this, InicioSesion.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia el back stack
        startActivity(intent);
        finish();
    }

    private void editarProducto(Producto producto) {
        // Crear un Intent para iniciar la actividad EditarProductoActivity
        Intent intent = new Intent(this, EditarProductoActivity.class);


        intent.putExtra("producto", producto); // Aquí asumimos que "producto" es un objeto Parcelable o Serializable

        // Iniciar la actividad EditarProductoActivity
        startActivity(intent);
    }

    private void borrarProducto(Producto producto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar Eliminación");
        builder.setMessage("¿Estás seguro de que deseas eliminar este producto?");
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String documentoId = producto.getDocumentId();

                if (documentoId != null && !documentoId.isEmpty()) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Productos").document(documentoId)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Interfaz.this, "Producto eliminado con éxito", Toast.LENGTH_SHORT).show();

                                    // Recargar la lista de productos desde Firebase
                                    cargarProductos();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    manejarErrorFirebaseBo(e);
                                }
                            });
                } else {
                    Toast.makeText(Interfaz.this, "Error: ID del documento no disponible.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // El usuario canceló la eliminación, no hacer nada
            }
        });

        builder.show();
    }
    private void manejarErrorFirebaseBo(Exception e) {
        if (e instanceof FirebaseFirestoreException) {
            FirebaseFirestoreException firestoreException = (FirebaseFirestoreException) e;
            switch (firestoreException.getCode()) {
                case PERMISSION_DENIED:
                    Toast.makeText(Interfaz.this, "No tienes permiso para al borrar este producto.", Toast.LENGTH_LONG).show();
                    break;
                case UNAVAILABLE:
                    Toast.makeText(Interfaz.this, "Servicio no disponible, intenta de nuevo más tarde.", Toast.LENGTH_LONG).show();
                    break;
                // Agrega más casos según sea necesario
                default:
                    Toast.makeText(Interfaz.this, "Error al borrar el producto: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            // Un error no específico de Firebase
            Toast.makeText(Interfaz.this, "Error desconocido: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}


