package com.example.chatconversa;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContenidoMensaje extends RecyclerView.ViewHolder {

    private TextView nombre, mensaje, fecha;
    private CircleImageView fotoPerfil;
    private ImageButton fotoMensaje;
    public ContenidoMensaje(@NonNull View itemView) {
        super(itemView);

        nombre = itemView.findViewById(R.id.nombre);
        mensaje = itemView.findViewById(R.id.mensaje);
        fecha = itemView.findViewById(R.id.fecha);
        fotoPerfil = itemView.findViewById(R.id.fotoPerfil);
        fotoMensaje = itemView.findViewById(R.id.fotoMensaje);
    }

    public ImageButton getFotoMensaje() {
        return fotoMensaje;
    }

    public void setFotoMensaje(ImageButton fotoMensaje) {
        this.fotoMensaje = fotoMensaje;
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

    public CircleImageView getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(CircleImageView fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }
}
