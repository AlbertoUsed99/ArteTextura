package com.example.artetextura;
import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.Map;

public class MyDataModel {

    private String nombre;
    private String descripcion;
    private double precio;
    private String imagenUrl;
    private String ownerId; // Asumiendo que almacenas el ID del propietario del producto

    // Constructor vacío necesario para Firestore
    public MyDataModel() {
    }

    public MyDataModel(String nombre, String descripcion, double precio, String imagenUrl, String ownerId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
        this.ownerId = ownerId;
    }

    // Métodos getter y setter (necesarios para Firestore)
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    // Método para convertir el objeto a un mapa antes de guardar en Firestore
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nombre", nombre);
        result.put("descripcion", descripcion);
        result.put("precio", precio);
        result.put("imagenUrl", imagenUrl);
        result.put("ownerId", ownerId);
        return result;
    }

    // Método para crear un objeto desde un mapa después de recuperar de Firestore
    public static MyDataModel fromMap(Map<String, Object> map) {
        MyDataModel product = new MyDataModel();
        product.setNombre((String) map.get("nombre"));
        product.setDescripcion((String) map.get("descripcion"));
        if (map.get("precio") instanceof Double) {
            product.setPrecio((Double) map.get("precio"));
        } else if (map.get("precio") instanceof Long) {
            product.setPrecio(((Long) map.get("precio")).doubleValue());
        }
        product.setImagenUrl((String) map.get("imagenUrl"));
        product.setOwnerId((String) map.get("ownerId"));
        return product;
    }
}
