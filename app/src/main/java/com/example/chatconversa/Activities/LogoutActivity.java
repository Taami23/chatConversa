package com.example.chatconversa.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.chatconversa.Interfaces.ServicioWeb;
import com.example.chatconversa.R;
import com.example.chatconversa.Respuestas.RespuestaWSLoguot;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LogoutActivity extends AppCompatActivity implements View.OnClickListener {
    private Button cerrarSesion;
    private ServicioWeb servicioWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        cerrarSesion = findViewById(R.id.cerrarSesion);
        cerrarSesion.setOnClickListener(this);
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://chat-conversa.unnamed-chile.com/ws/user/")
            .addConverterFactory(GsonConverterFactory.create()).build();
        servicioWeb = retrofit.create(ServicioWeb.class);

    }
    @Override
    public void onClick(View view){
        new MaterialAlertDialogBuilder(LogoutActivity.this)
                .setTitle("Sesión")
                .setMessage("Estás seguro que deseas cerrar sesión?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences preferences = getSharedPreferences(LoginActivity.CREDENTIALS, MODE_PRIVATE);
                        String token = preferences.getString("token", "Token no encontrado");
                        String user_id = preferences.getString("id", "Id no encontrado");;
                        String username = preferences.getString("username", "Username no encontrado");;
                        Log.d("Preferences", token.concat(user_id).concat(username));
                        final Call<RespuestaWSLoguot> respuestaWSLoguotCall = servicioWeb.logout("Bearer "+token, user_id, username);
                        respuestaWSLoguotCall.enqueue(new Callback<RespuestaWSLoguot>() {
                            @Override
                            public void onResponse(Call<RespuestaWSLoguot> call, Response<RespuestaWSLoguot> response) {
                                if(response != null){
                                    if(response.body() != null && response.code()==200){
                                        RespuestaWSLoguot respuestaWSLoguot = response.body();
                                        Log.d("Retrofit", respuestaWSLoguot.getMessage());
                                        Log.d("Retrofit", respuestaWSLoguot.toString());
                                        new MaterialAlertDialogBuilder(LogoutActivity.this)
                                                .setTitle("Adiós "+username)
                                                .setMessage(respuestaWSLoguot.getMessage())
                                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        initInicio();
                                                    }
                                                })
                                                .show();
                                    }else if (response.code()==400){
                                        try {
                                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                                            JSONObject error = jObjError.getJSONObject("errors");
                                            JSONArray names = error.names();
                                            String mensaje = jObjError.getString("message");
                                            new MaterialAlertDialogBuilder(LogoutActivity.this)
                                                    .setTitle("Upps! Ha ocurrido un error")
                                                    .setMessage(mensaje)
                                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    })
                                                    .show();
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
                                    }else if(response.code()== 401 ){
                                        try{
                                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                                            String mensaje = jObjError.getString("message");
                                            new MaterialAlertDialogBuilder(LogoutActivity.this)
                                                    .setTitle("Upps! Ha ocurrido un error")
                                                    .setMessage(mensaje)
                                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    })
                                                    .show();
                                        }catch (JSONException | IOException e){
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<RespuestaWSLoguot> call, Throwable t) {

                            }
                        });
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        })
                .show();

    }
    private void initInicio(){
        Intent login  = new Intent (this, LoginActivity.class);
        startActivity(login);
        finish();
    }

}
