package com.example.chatconversa.Objetos;

public class Mensaje {
    private String nombre;
    private String mensaje;
    private String fotoPerfil;
    private String fecha;

    public Mensaje() {

    }

    public Mensaje(String nombre, String mensaje, String fotoPerfil, String fecha) {
        this.nombre = nombre;
        this.mensaje = mensaje;
        this.fotoPerfil = fotoPerfil;
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Mensaje{" +
                "nombre='" + nombre + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", fotoPerfil='" + fotoPerfil + '\'' +
                ", fecha='" + fecha + '\'' +
                '}';
    }
}
