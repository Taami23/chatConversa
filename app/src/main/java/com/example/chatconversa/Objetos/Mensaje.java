package com.example.chatconversa.Objetos;

public class Mensaje {
    private String nombre;
    private String mensaje;
    private String fotoPerfil;
    private String fecha;
    private String fotoMensaje;
    private String thumbnail;
    private String latitud;
    private String longitud;

    public Mensaje() {

    }

    public Mensaje(String nombre, String mensaje, String fotoPerfil, String fecha, String fotoMensaje, String thumbnail, String latitud, String longitud) {
        this.nombre = nombre;
        this.mensaje = mensaje;
        this.fotoPerfil = fotoPerfil;
        this.fecha = fecha;
        this.fotoMensaje = fotoMensaje;
        this.thumbnail = thumbnail;
        this.latitud = latitud;
        this.longitud = longitud;

    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getFotoMensaje() {
        return fotoMensaje;
    }

    public void setFotoMensaje(String fotoMensaje) {
        this.fotoMensaje = fotoMensaje;
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

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    @Override
    public String toString() {
        return "Mensaje{" +
                "nombre='" + nombre + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", fotoPerfil='" + fotoPerfil + '\'' +
                ", fecha='" + fecha + '\'' +
                ", fotoMensaje='" + fotoMensaje + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                '}';
    }
}
