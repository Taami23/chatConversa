package com.example.chatconversa.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.chatconversa.Interfaces.ServicioWeb;
import com.example.chatconversa.R;
import com.example.chatconversa.Respuestas.RespuestaWSLogin;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity  implements View.OnClickListener {
    private Button iniciar;
    private TextInputEditText username;
    private TextInputLayout usernameL;
    private TextInputEditText password;
    private TextInputLayout passwordL;
    private ServicioWeb servicioWeb;
    private static String uniqueID = null;
    private static String elToken = null;
    private static Integer elId = null;
    private static String userName = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        iniciar = findViewById(R.id.iniciarL);
        username = findViewById(R.id.username);
        usernameL = findViewById(R.id.usernameL);
        password = findViewById(R.id.password);
        passwordL=findViewById(R.id.passwordL);
        iniciar.setOnClickListener(this);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://chat-conversa.unnamed-chile.com/ws/user/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        servicioWeb = retrofit.create(ServicioWeb.class);
    }

    @Override
    public void onClick(View view){
        String idUnico = id(this);
        final Call<RespuestaWSLogin> respuestaWSLoginCall = servicioWeb.login(username.getText().toString(), password.getText().toString(), idUnico);
        respuestaWSLoginCall.enqueue(new Callback<RespuestaWSLogin>() {
            @Override
            public void onResponse(Call<RespuestaWSLogin> call, Response<RespuestaWSLogin> response) {
                if (response != null){
                    if (response.body() != null && response.code()==200){
                        RespuestaWSLogin respuestaWSLogin = response.body();
                        Log.d("Retrofit", "Sesión Iniciada");
                        Log.d("Retrofit", respuestaWSLogin.toString());
                        //Envía la respuesta del login
                        savePreferences(respuestaWSLogin);
                    }
                }
            }
            @Override
            public void onFailure(Call<RespuestaWSLogin> call, Throwable t) {

            }
        });
    }

    //Guarda las preferencias
    public void savePreferences(RespuestaWSLogin respuestaWSLogin){
        SharedPreferences preferences = getSharedPreferences("credenciales",
                Context.MODE_PRIVATE);

        //Extrae las preferencias requeridas desde
        //la respuesta del login
        elToken = respuestaWSLogin.getToken();
        elId = respuestaWSLogin.getData().getId();
        userName = respuestaWSLogin.getData().getUsername();

        //Guarda y comitea las preferencias
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", elToken);
        editor.putString("id", elId.toString());
        editor.putString("username", userName);
        editor.commit();
    }

    public synchronized static String id(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }
        }
        return uniqueID;
    }
}
