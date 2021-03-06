package com.example.chatconversa;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatconversa.Objetos.Mensaje;

import java.util.ArrayList;
import java.util.List;


public class ArmarMensaje extends RecyclerView.Adapter<ContenidoMensaje>{

    public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;
    private List<Mensaje> mensajeList = new ArrayList<>();
    private Context context;
    private String user;


    public ArmarMensaje(Context context, String user) {
        this.context = context;
        this.user = user;
    }

    public void addMensaje(Mensaje m){
        mensajeList.add(m);
        notifyItemInserted(mensajeList.size());
    }


    @NonNull
    @Override
    public ContenidoMensaje onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == MSG_LEFT){
            v = LayoutInflater.from(context).inflate(R.layout.vista_mensaje,parent,false);
            return new ContenidoMensaje(v, context);
        }else{
            v = LayoutInflater.from(context).inflate(R.layout.vista_mensaje_derecha,parent,false);
            return new ContenidoMensaje(v, context);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ContenidoMensaje holder, int position) {
        holder.getNombre().setText(mensajeList.get(position).getNombre());
        if(mensajeList.get(position).getMensaje() == null ){
            holder.getMensaje().setVisibility(View.GONE);
        }else if (mensajeList.get(position).getMensaje().equalsIgnoreCase("") || mensajeList.get(position).getMensaje().equalsIgnoreCase("null")){
            holder.getMensaje().setVisibility(View.GONE);
        }else{
            holder.getMensaje().setText(mensajeList.get(position).getMensaje());
        }
        holder.getUsuario().setText(mensajeList.get(position).getNombre());
        holder.getFechaFoto().setText(mensajeList.get(position).getFecha());
        holder.getFecha().setText(mensajeList.get(position).getFecha());
        if(mensajeList.get(position).getFotoPerfil().equalsIgnoreCase("")){
            holder.getFotoPerfil().setImageResource(R.drawable.user);
        }else{
            Glide.with(context).load(mensajeList.get(position).getFotoPerfil()).into(holder.getFotoPerfil());
        }
        if ((mensajeList.get(position).getFotoMensaje().equalsIgnoreCase(""))){
            holder.getFotoMensaje().setVisibility(View.GONE);
        }else{
            Glide.with(context).load(mensajeList.get(position).getThumbnail()).into(holder.getFotoMensaje());
            Glide.with(context).load(mensajeList.get(position).getFotoMensaje()).into(holder.getImagen());
        }
        if (mensajeList.get(position).getLatitud()!=null && mensajeList.get(position).getLongitud()!=null){
            holder.setLatitud(mensajeList.get(position).getLatitud());
            holder.setLongitud(mensajeList.get(position).getLongitud());
            Glide.with(context).load(R.drawable.iconmap).into(holder.getFotoMapa());
        }else{
            holder.getFotoMapa().setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mensajeList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mensajeList.get(position).getNombre().equalsIgnoreCase(user)){
            return MSG_RIGHT;
        }else {
            return MSG_LEFT;
        }
    }
}
