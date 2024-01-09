package com.example.artetextura;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Callback;
import java.util.List;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ViewHolder> {

    private ArrayList<Producto> productos;
    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener itemLongClickListener;
    public void actualizarLista(ArrayList<Producto> nuevaLista) {
        this.productos = nuevaLista;
        notifyDataSetChanged();
    }
    public ProductosAdapter(ArrayList<Producto> productos) {
        this.productos = productos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.producto_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto producto = productos.get(position);
        holder.nombreTextView.setText(producto.getNombre());
        Picasso.get().load(producto.getImagenURL()).into(holder.imagenImageView);
        holder.descripcionTextView.setText(producto.getDescripción());
        double precio = producto.getPrecio();
        String precioFormateado = String.format("%.2f", precio); // Formatea a dos decimales
        holder.precioTextView.setText(precioFormateado +"€"); // Agrega el símbolo de moneda
        // Código de Picasso para cargar la imagen
        Picasso.get()
                .load(producto.getImagenURL())
                .error(R.drawable.image_load_error) // Imagen de error en caso de fallo
                .into(holder.imagenImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        // Imagen cargada con éxito
                    }

                    @Override
                    public void onError(Exception e) {
                        // Error al cargar la imagen
                        Log.e("PicassoError", "Error al cargar imagen: ", e);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView;
        TextView descripcionTextView;
        TextView precioTextView;
        ImageView imagenImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nombreTextView);
            descripcionTextView = itemView.findViewById(R.id.descripcionTextView);
            precioTextView = itemView.findViewById(R.id.precioTextView);
            imagenImageView = itemView.findViewById(R.id.imagenImageView);

            itemView.setOnClickListener(v -> {
                if (itemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(v, getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (itemLongClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    itemLongClickListener.onItemLongClick(v, getAdapterPosition());
                    return true;
                }
                return false;
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }
}