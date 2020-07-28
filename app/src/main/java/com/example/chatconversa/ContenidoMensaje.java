package com.example.chatconversa;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContenidoMensaje extends RecyclerView.ViewHolder {

    private TextView nombre, mensaje, fecha, usuario, fechaFoto;
    private CircleImageView fotoPerfil;
    private ImageView imagen;
    private ImageButton fotoMensaje;
    private Context context;
    public ContenidoMensaje(@NonNull View itemView, Context context) {
        super(itemView);
        this.context = context;
        final View vistaFoto = LayoutInflater.from(context).inflate(R.layout.vista_foto, null);
        nombre = itemView.findViewById(R.id.nombre);
        mensaje = itemView.findViewById(R.id.mensaje);
        fecha = itemView.findViewById(R.id.fecha);
        fotoPerfil = itemView.findViewById(R.id.atrasMensaje);
        fotoMensaje = itemView.findViewById(R.id.fotoMensaje);

        usuario = vistaFoto.findViewById(R.id.nombreUsuario);
        fechaFoto = vistaFoto.findViewById(R.id.fecha);
        imagen = vistaFoto.findViewById(R.id.Imangencita);
        Dialog myDialog = new Dialog(context);
        ImageButton atras = vistaFoto.findViewById(R.id.atrasMensaje);
        Log.d("Click", "Holi");
        myDialog.setContentView(vistaFoto);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(myDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        fotoMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.show();
                myDialog.getWindow().setAttributes(lp);
                atras.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });

            }
        });
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

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public TextView getUsuario() {
        return usuario;
    }

    public void setUsuario(TextView usuario) {
        this.usuario = usuario;
    }

    public TextView getFechaFoto() {
        return fechaFoto;
    }

    public void setFechaFoto(TextView fechaFoto) {
        this.fechaFoto = fechaFoto;
    }

    public ImageView getImagen() {
        return imagen;
    }

    public void setImagen(ImageView imagen) {
        this.imagen = imagen;
    }
}
