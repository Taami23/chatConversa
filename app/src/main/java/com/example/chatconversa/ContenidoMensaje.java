package com.example.chatconversa;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContenidoMensaje extends RecyclerView.ViewHolder {

    private TextView nombre, mensaje, fecha;
    private CircleImageView fotoMensaje;
    public ContenidoMensaje(@NonNull View itemView) {
        super(itemView);

        nombre = itemView.findViewById(R.id.nombre);
        mensaje = itemView.findViewById(R.id.mensaje);
        fecha = itemView.findViewById(R.id.fecha);
        fotoMensaje = itemView.findViewById(R.id.fotoMensaje);
    }

    public TextView getNombre() {
        return nombre;
    }

    public void setNombre(TextView nombre) {
        this.nombre = nombre;
    }

    public TextView getMensaje() {
        return mensaje;
    }

    public void setMensaje(TextView mensaje) {
        this.mensaje = mensaje;
    }

    public TextView getFecha() {
        return fecha;
    }

    public void setFecha(TextView fecha) {
        this.fecha = fecha;
    }

    public CircleImageView getFotoMensaje() {
        return fotoMensaje;
    }

    public void setFotoMensaje(CircleImageView fotoMensaje) {
        this.fotoMensaje = fotoMensaje;
    }
}
