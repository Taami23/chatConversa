package com.example.chatconversa.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatconversa.Interfaces.ServicioWeb;
import com.example.chatconversa.R;
import com.example.chatconversa.Respuestas.RespuestaWSLoguot;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences preferences;
    private Button cerrarSesion;
    private ServicioWeb servicioWeb;
    private ImageButton atras;
    private ImageButton agregarImagen;
    private TextView username;
    private TextView name;
    private TextView lastname;
    private TextView run;
    private TextView email;
    private CircleImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        cerrarSesion = findViewById(R.id.cerrarSesion);
        username = findViewById(R.id.username);
        name = findViewById(R.id.name);
        lastname = findViewById(R.id.lastname);
        run = findViewById(R.id.run);
        email = findViewById(R.id.email);
        image = findViewById(R.id.image);
        atras = findViewById(R.id.atras);
        agregarImagen = findViewById(R.id.agregarImagen);

        preferences = getSharedPreferences(LoginActivity.CREDENTIALS, MODE_PRIVATE);
        Log.d("Imagen","poto" + preferences.getString("image", "no image"));
        username.setText(preferences.getString("username", "username no encontrado").toUpperCase());
        name.setText("NOMBRE: " + preferences.getString("name", "name no encontrado").toUpperCase());
        lastname.setText("APELLIDO: " + preferences.getString("lastname", "lastname no encontrado").toUpperCase());
        run.setText("RUN: " + preferences.getString("run", "run no encontrado").toUpperCase());
        email.setText("CORREO: " + preferences.getString("email", "email no encontrado").toUpperCase());
        if(preferences.getString("image", "no image").equalsIgnoreCase("no image")){
            image.setImageResource(R.drawable.user);
        }else{
            Glide.with(this).load(preferences.getString("image", "no image")).into(image);
        }
        agregarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPhoto();
            }
        });
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMessages();
            }
        });
        cerrarSesion.setOnClickListener(this);
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://chat-conversa.unnamed-chile.com/ws/user/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        servicioWeb = retrofit.create(ServicioWeb.class);
    }

    @Override
    public void onClick(View view){
        new MaterialAlertDialogBuilder(ProfileActivity.this)
                .setTitle("Sesión")
                .setMessage("Estás seguro que deseas cerrar sesión?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String token = preferences.getString("token", "Token no encontrado");
                        String user_id = preferences.getString("id", "Id no encontrado");
                        String username = preferences.getString("username", "Username no encontrado");
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
                                        new MaterialAlertDialogBuilder(ProfileActivity.this)
                                                .setTitle("Adiós "+username)
                                                .setMessage(respuestaWSLoguot.getMessage())
                                                .setCancelable(false)
                                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        SharedPreferences.Editor editor = preferences.edit();
                                                        editor.clear().apply();
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
                                            new MaterialAlertDialogBuilder(ProfileActivity.this)
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
                                            new MaterialAlertDialogBuilder(ProfileActivity.this)
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

    private  void initMessages(){
        Intent messages = new Intent(this, MessagesActivity.class);
        startActivity(messages);
        finish();
    }
    private  void initPhoto(){
        Intent photo = new Intent(this, PhotoActivity.class);
        startActivity(photo);
        finish();
    }

}
