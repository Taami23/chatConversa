package com.example.chatconversa.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.chatconversa.ArmarMensaje;
import com.example.chatconversa.Interfaces.ServicioWeb;
import com.example.chatconversa.Objetos.Mensaje;
import com.example.chatconversa.R;
import com.example.chatconversa.Respuestas.RespuestaWSMessages;
import com.example.chatconversa.Respuestas.RespuestaWSSendMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MessagesActivity extends AppCompatActivity implements View.OnClickListener {
    private Button profile, send;
    private EditText mensaje;
    private ServicioWeb servicioWeb;
    private RecyclerView mensajes;
    private CircleImageView fotoPerfil;
    private ArmarMensaje armador;
    private SharedPreferences preferences;
    private String token;
    private String username;
    private String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        profile = findViewById(R.id.profileButton);
        profile.setOnClickListener(this);
        mensaje = findViewById(R.id.textMessage);
        send = findViewById(R.id.sendMessage);
        mensajes = findViewById(R.id.messages);
        fotoPerfil = findViewById(R.id.fotoPerfil);
        armador = new ArmarMensaje(this);
        LinearLayoutManager linear = new LinearLayoutManager(this);
        mensajes.setLayoutManager(linear);
        mensajes.setAdapter(armador);
        armador.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollBar();
            }
        });
        getPreferences();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://chat-conversa.unnamed-chile.com/ws/message/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        servicioWeb = retrofit.create(ServicioWeb.class);
        servicio();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                servicioSend();
            }
        });
    }

    @Override
    public void onClick(View v){
        initProfile();
    }

    public void servicio(){
        final Call<RespuestaWSMessages> respuestaWSMessagesCall= servicioWeb.messages("Bearer "+token, user_id, username);
        respuestaWSMessagesCall.enqueue(new Callback<RespuestaWSMessages>() {
            @Override
            public void onResponse(Call<RespuestaWSMessages> call, Response<RespuestaWSMessages> response) {
                Log.d("TOKEN", token);
                if(response != null){
                    Log.d("CODIGO", ""+response.code());
                    if(response.body() != null && response.code()== 200){
                        RespuestaWSMessages respuestaWSMessages = response.body();
                        Log.d("Retrofit Mensaje", respuestaWSMessages.toString());
                    }else if (response.code()==400){
                        try{
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            JSONObject error = jObjError.getJSONObject("errors");
                            JSONArray names = error.names();
                            String mensaje = jObjError.getString("message");

                            for (int i = 0; i < names.length(); i++) {
                                String nombreError = names.getString(i);
                                String message = error.getJSONArray(names.getString(i)).getString(0);
                                Log.d("Errores Registro", names.getString(i) + ':' + error.getJSONArray(names.getString(i)).getString(0));
                                switch (nombreError) {
                                    case "username":

                                        break;
                                    case "user_id":

                                        break;
                                }
                            }
                        }catch (JSONException | IOException e){
                            e.printStackTrace();
                        }
                    }else if(response.code()==401){
                        try{
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            String mensaje = jObjError.getString("message");
                            Log.d("Retroit Error", mensaje);
                            /*new MaterialAlertDialogBuilder(MessagesActivity.this)
                                    .setTitle("Upps! Ha ocurrido un error")
                                    .setMessage(mensaje)
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();*/
                        }catch (JSONException | IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RespuestaWSMessages> call, Throwable t) {
                Log.d("Failure", t.getMessage());
            }
        });
    }

    public void servicioSend(){
        String pathPhoto="";
        File archivoImage = new File(pathPhoto);
        RequestBody file = RequestBody.create(MediaType.parse("multipart/form-data"), archivoImage);
        MultipartBody.Part archivo = MultipartBody.Part.create(file);
        Double latitude= 32.40;
        Double longitude= 33.40;
        final Call<RespuestaWSSendMessage> respuestaWSSendMessageCall= servicioWeb.send("Bearer "+token, user_id, username,mensaje.getText().toString(),latitude, longitude);
        respuestaWSSendMessageCall.enqueue(new Callback<RespuestaWSSendMessage>() {
            @Override
            public void onResponse(Call<RespuestaWSSendMessage> call, Response<RespuestaWSSendMessage> response) {
                Log.d("TOKEN", token);
                if(response != null){
                    Log.d("CODIGO", ""+response.code());
                    if(response.body() != null && response.code()== 200){
                        RespuestaWSSendMessage respuestaWSSendMessage = response.body();
                        armador.addMensaje(new Mensaje(username,mensaje.getText().toString(), respuestaWSSendMessage.getData().getUser().getUser_image(), respuestaWSSendMessage.getData().getDate()));
                    }else if (response.code()==400){
                        try{
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            JSONObject error = jObjError.getJSONObject("errors");
                            JSONArray names = error.names();
                            String mensaje = jObjError.getString("message");

                            for (int i = 0; i < names.length(); i++) {
                                String nombreError = names.getString(i);
                                String message = error.getJSONArray(names.getString(i)).getString(0);
                                Log.d("Errores Registro", names.getString(i) + ':' + error.getJSONArray(names.getString(i)).getString(0));
                                switch (nombreError) {
                                    case "username":

                                        break;
                                    case "user_id":

                                        break;
                                }
                            }
                        }catch (JSONException | IOException e){
                            e.printStackTrace();
                        }
                    }else if(response.code()==401){
                        try{
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            String mensaje = jObjError.getString("message");
                            Log.d("Retroit Error", mensaje);
                            /*new MaterialAlertDialogBuilder(MessagesActivity.this)
                                    .setTitle("Upps! Ha ocurrido un error")
                                    .setMessage(mensaje)
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();*/
                        }catch (JSONException | IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RespuestaWSSendMessage> call, Throwable t) {
                Log.d("Failure", t.getMessage());
            }
        });
    }

    private void getPreferences(){
        preferences = getSharedPreferences(LoginActivity.CREDENTIALS, MODE_PRIVATE);
        token = preferences.getString("token", "Token no encontrado");
        Log.d("TOKEN", token);
        user_id = preferences.getString("id", "Id no encontrado");
        Log.d("TOKEN", user_id);
        username = preferences.getString("username", "Username no encontrado");
        Log.d("TOKEN", username);
    }

    private void initProfile(){
        Intent profile  = new Intent (this, ProfileActivity.class);
        startActivity(profile);
        finish();
    }

    private void setScrollBar(){
        mensajes.scrollToPosition(armador.getItemCount()-1);
    }
}
