package com.example.artetextura;

import java.io.Serializable;
public class Producto  implements Serializable  {
    private String documentId;
    private String Descripción;
    private String ImagenURL; // Asegúrate de que el nombre coincida exactamente con el de Firestore
    private String Nombre;

    private int Precio;

    // Constructor por defecto necesario para Firestore
    public Producto() {}

    // Constructor con parámetros
    public Producto(String Descripción, String ImagenURL, String Nombre, int Precio) {
        this.Descripción = Descripción;
        this.ImagenURL = ImagenURL;
        this.Nombre = Nombre;
        this.Precio = Precio;
    }

    // Getters
    public String getDocumentId() { return documentId; }

    public String getDescripción() {
        return Descripción;
    }

    public String getImagenURL() {
        return ImagenURL;
    }

    public String getNombre() {
        return Nombre;
    }

    public int getPrecio() {
        return Precio;
    }

    // Setters
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public void setDescripción(String Descripción) {
        this.Descripción = Descripción;
    }

    public void setImagenURL(String ImagenURL) {
        this.ImagenURL = ImagenURL;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public void setPrecio(int Precio) {
        this.Precio = Precio;
    }

    // Representación en cadena de la clase (opcional pero útil para depuración)
    @Override
    public String toString() {
        return "Producto{" +
                "Descripción='" + Descripción + '\'' +
                ", ImagenURL='" + ImagenURL + '\'' +
                ", Nombre='" + Nombre + '\'' +
                ", Precio=" + Precio +
                '}';
    }
}