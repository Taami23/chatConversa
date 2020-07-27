package com.example.chatconversa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatconversa.Objetos.Mensaje;

import java.util.ArrayList;
import java.util.List;

public class ArmarMensaje extends RecyclerView.Adapter<ContenidoMensaje>{

    private List<Mensaje> mensajeList = new ArrayList<>();
    private Context context;

    public ArmarMensaje(Context context) {

        this.context = context;
    }

    public void addMensaje(Mensaje m){
        mensajeList.add(m);
        notifyItemInserted(mensajeList.size());
    }


    @NonNull
    @Override
    public ContenidoMensaje onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.vista_mensaje,parent,false);
        return new ContenidoMensaje(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ContenidoMensaje holder, int position) {
        holder.getNombre().setText(mensajeList.get(position).getNombre());
        holder.getMensaje().setText(mensajeList.get(position).getMensaje());
        holder.getFecha().setText(mensajeList.get(position).getFecha());
    }

    @Override
    public int getItemCount() {
        return mensajeList.size();
    }
}
