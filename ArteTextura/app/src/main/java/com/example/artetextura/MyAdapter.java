package com.example.artetextura;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<MyDataModel> datos;
    private Context context;
    private MyDataModel selectedItem; // Variable para realizar un seguimiento del elemento seleccionado

    public MyAdapter(List<MyDataModel> datos, Context context) {
        this.datos = datos;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyDataModel item = datos.get(position);

        holder.textViewNombre.setText(item.getNombre());
        holder.textViewDescripcion.setText(item.getDescripcion());
        holder.textViewPrecio.setText("$" + item.getPrecio());

        Picasso.get().load(item.getImagenUrl()).into(holder.imageViewProducto);

        // Agregar lógica para resaltar el elemento seleccionado
        if (selectedItem != null && selectedItem.equals(item)) {
            // Modificar la apariencia del elemento seleccionado según sea necesario
            // Por ejemplo, cambiar el color de fondo o el borde
            // holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSelected));
        } else {
            // Restaurar la apariencia predeterminada para otros elementos
            // holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        // Manejar clics en elementos del RecyclerView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Actualizar el elemento seleccionado y notificar cambios
                selectedItem = item;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    // Obtener el elemento seleccionado
    public MyDataModel getSelectedItem() {
        return selectedItem;
    }

    // ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProducto;
        TextView textViewNombre, textViewDescripcion, textViewPrecio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProducto = itemView.findViewById(R.id.imageViewProducto);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewDescripcion = itemView.findViewById(R.id.textViewDescripcion);
            textViewPrecio = itemView.findViewById(R.id.textViewPrecio);
        }
    }
}
