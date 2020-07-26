package com.example.chatconversa.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chatconversa.Interfaces.ServicioWeb;
import com.example.chatconversa.R;
import com.example.chatconversa.Respuestas.RespuestaWSMessages;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MessagesActivity extends AppCompatActivity implements View.OnClickListener {
    private Button profile;
    private ServicioWeb servicioWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        profile = findViewById(R.id.profileButton);
        profile.setOnClickListener(this);
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://chat-conversa.unnamed-chile.com/ws/message/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        servicioWeb = retrofit.create(ServicioWeb.class);
        servicio();
    }

    @Override
    public void onClick(View v){
        initProfile();
    }

    public void servicio(){
        SharedPreferences preferences = getSharedPreferences(LoginActivity.CREDENTIALS, MODE_PRIVATE);
        TextView texto = findViewById(R.id.texto1);
        String token = preferences.getString("token", "Token no encontrado");
        Log.d("TOKEN", token);
        texto.setText(token);
        String user_id = preferences.getString("id", "Id no encontrado");
        Log.d("TOKEN", user_id);
        String username = preferences.getString("username", "Username no encontrado");
        Log.d("TOKEN", username);
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

    private void initProfile(){
        Intent profile  = new Intent (this, ProfileActivity.class);
        startActivity(profile);
        finish();
    }
}
