package com.example.chatconversa.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.chatconversa.Interfaces.ServicioWeb;
import com.example.chatconversa.R;
import com.example.chatconversa.Respuestas.RespuestaWSLogin;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity  implements View.OnClickListener {
    private Button iniciar;
    private Button registrarse;
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
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public static final Pattern FORMAT_PASS = Pattern.compile("^(?=.[a-z])(?=.[A-Z])(?=.*\\d)[a-zA-Z\\d]{6,12}$");
    public static final String CREDENTIALS = LoginActivity.class.getPackage().getName();

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
        registrarse=findViewById(R.id.registrarL);
        //validaPassword();
        iniciar.setOnClickListener(this);
        preferences = getSharedPreferences(CREDENTIALS, MODE_PRIVATE);
        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initRegistro();
            }
        });



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
                        new MaterialAlertDialogBuilder(LoginActivity.this)
                                .setTitle("Titulo")
                                .setMessage(respuestaWSLogin.getMessage())
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        initLogout();
                                    }
                                })
                                .show();
                    }else if (response.code()==401){
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Log.d("Error", ""+jObjError.length());
                            if (jObjError.length() == 3) {
                                String mensaje = jObjError.getString("message");
                                new MaterialAlertDialogBuilder(LoginActivity.this)
                                        .setTitle("Upps! Ha ocurrido un error")
                                        .setMessage(mensaje)
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .show();

                                JSONObject error = jObjError.getJSONObject("errors");
                                JSONArray names = error.names();

                                for (int i = 0; i < names.length(); i++) {
                                    String nombreError = names.getString(i);
                                    String message = error.getJSONArray(names.getString(i)).getString(0);
                                    Log.d("Errores Registro", names.getString(i) + ':' + error.getJSONArray(names.getString(i)).getString(0));
                                    switch (nombreError) {
                                        case "username":
                                            usernameL.setError(message);
                                            break;
                                        case "password":
                                            passwordL.setError(message);
                                            break;
                                    }
                                }
                            }else {
                                String mensaje = jObjError.getString("message");
                                new MaterialAlertDialogBuilder(LoginActivity.this)
                                        .setTitle("Upps! Ha ocurrido un error")
                                        .setMessage(mensaje)
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .show();
                            }

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }else if(response.code() == 400){
                        try{
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            String mensaje = jObjError.getString("message");
                            new MaterialAlertDialogBuilder(LoginActivity.this)
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
            public void onFailure(Call<RespuestaWSLogin> call, Throwable t) {

            }
        });
    }

    //Guarda las preferencias
    public void savePreferences(RespuestaWSLogin respuestaWSLogin){
        SharedPreferences preferences = getSharedPreferences(CREDENTIALS,
                MODE_PRIVATE);

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

    private void initRegistro(){
        Intent registro = new Intent(this, RegistrarActivity.class);
        startActivity(registro);
        finish();
    }

    private void initLogout(){
        Intent logout = new Intent(this, LogoutActivity.class);
        startActivity(logout);
        finish();
    }
    /*private void validaPassword(){
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Matcher matcher = FORMAT_PASS.matcher(password.getText().toString());
                if (matcher.find()){
                    passwordL.setError(null);
                }else {
                    passwordL.setError("Formato inválido, debe contener solo letras mayúsculas, minúsculas y números");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }*/
}
