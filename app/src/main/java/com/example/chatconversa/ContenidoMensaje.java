package com.example.chatconversa;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatconversa.Activities.MessagesActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContenidoMensaje extends RecyclerView.ViewHolder implements View.OnClickListener{

    private TextView nombre, mensaje, fecha, usuario, fechaFoto;
    private ImageButton fotoMapa;
    private String latitud, longitud;
    private CircleImageView fotoPerfil;
    private ImageView imagen;
    private ImageButton fotoMensaje;
    private Context context;
    private MessagesActivity mensajes;
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
        fotoMapa = itemView.findViewById(R.id.mapaMensaje);
        mensajes = new MessagesActivity();
        Dialog myDialog = new Dialog(context);
        ImageButton atras = vistaFoto.findViewById(R.id.atrasMensaje);
        myDialog.setContentView(vistaFoto);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(myDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
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
        fotoMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(getLongitud()==null && getLatitud()==null){
                    new MaterialAlertDialogBuilder(context)
                            .setTitle("Upps!")
                            .setMessage("No se pudo obtener su ubicación")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                    return;
                }else {
                    String geo = "geo:0,0?q="+getLatitud()+","+getLongitud();
                    Uri gmmIntentUri = Uri.parse(geo);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    context.startActivity(mapIntent);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

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

    public ImageButton getFotoMapa() {
        return fotoMapa;
    }

    public void setFotoMapa(ImageButton fotoMapa) {
        this.fotoMapa = fotoMapa;
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


}
