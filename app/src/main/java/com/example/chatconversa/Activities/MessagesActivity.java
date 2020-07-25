package com.example.chatconversa.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatconversa.Interfaces.ServicioWeb;
import com.example.chatconversa.R;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MessagesActivity extends AppCompatActivity implements View.OnClickListener {

    private Button profile;
    private ServicioWeb servicioWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState){
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
        //Aquí iria un codigo, si tan solo tuviera uno
    }

    public void servicio() {
        SharedPreferences preferences = getSharedPreferences(LoginActivity.CREDENTIALS, MODE_PRIVATE);
        String token = preferences.getString("token", "Token no encontrado");
        Log.d("TOKEN", token);
        String user_id = preferences.getString("id", "Id no encontrado");
        Log.d("TOKEN", user_id);
        String username = preferences.getString("username", "Username no encontrado");
        Log.d("TOKEN", username);
    }
}
