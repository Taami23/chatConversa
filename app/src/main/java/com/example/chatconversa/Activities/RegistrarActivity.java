package com.example.chatconversa.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.chatconversa.Interfaces.ServicioWeb;
import com.example.chatconversa.R;
import com.example.chatconversa.Respuestas.RespuestaWSRegister;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrarActivity extends AppCompatActivity implements View.OnClickListener{
    private Button registrar;
    private TextInputEditText name;
    private TextInputLayout nameL;
    private TextInputEditText lastname;
    private TextInputLayout lastnameL;
    private TextInputEditText run;
    private TextInputLayout runL;
    private TextInputEditText username;
    private TextInputLayout usernameL;
    private TextInputEditText email;
    private TextInputLayout emailL;
    private TextInputEditText password;
    private TextInputLayout passwordL;
    private TextInputEditText token;
    private TextInputLayout tokenL;
    private ServicioWeb servicioWeb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_registrar);
        registrar = findViewById(R.id.registrar);
        name = findViewById(R.id.name);
        nameL = findViewById(R.id.nameL);
        lastname = findViewById(R.id.lastname);
        lastnameL = findViewById(R.id.lastnameL);
        run = findViewById(R.id.run);
        runL = findViewById(R.id.runL);
        username = findViewById(R.id.username);
        usernameL = findViewById(R.id.usernameL);
        email = findViewById(R.id.email);
        emailL = findViewById(R.id.emailL);
        password = findViewById(R.id.password);
        passwordL = findViewById(R.id.passwordL);
        token = findViewById(R.id.token);
        tokenL = findViewById(R.id.tokenL);
        registrar.setOnClickListener(this);
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://chat-conversa.unnamed-chile.com/ws/user/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        servicioWeb = retrofit.create(ServicioWeb.class);
    }

    @Override
    public void onClick(View view){
        final Call<RespuestaWSRegister> registerCall = servicioWeb.registrer(name.getText().toString(), lastname.getText().toString(),
                run.getText().toString(), username.getText().toString(), email.getText().toString(), password.getText().toString(),
                token.getText().toString());
        registerCall.enqueue(new Callback<RespuestaWSRegister>() {
            @Override
            public void onResponse(Call<RespuestaWSRegister> call, Response<RespuestaWSRegister> response) {
                if (response != null){
                    Log.d("Retrofit", "Error: "+response.code());
                    if (response.body() != null && response.code()==201){
                        RespuestaWSRegister respuestaWSRegister = response.body();
                        Log.d("Retrofit", respuestaWSRegister.getMessage());
                        Log.d("Retrofit", respuestaWSRegister.toString());
                        new MaterialAlertDialogBuilder(RegistrarActivity.this)
                                .setTitle("Titulo")
                                .setMessage(respuestaWSRegister.getMessage())
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
                            String mensaje = jObjError.getString("message");
                            //String mensa = response.body().getMessage().toString();
                            JSONArray names = error.names();
                            new MaterialAlertDialogBuilder(RegistrarActivity.this)
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
                                Log.d("Errores Registro", names.getString(i)+':'+ error.getJSONArray(names.getString(i)).getString(0));
                                switch (nombreError){
                                    case "name":
                                        nameL.setError(message);
                                        break;
                                    case "lastname":
                                        lastnameL.setError(message);
                                        break;
                                    case "run":
                                        runL.setError(message);
                                        break;
                                    case "username":
                                        usernameL.setError(message);
                                        break;
                                    case "email":
                                        emailL.setError(message);
                                        break;
                                    case "password":
                                        passwordL.setError(message);
                                        break;
                                    case "token_enterprise":
                                        tokenL.setError(message);
                                        break;
                                }
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                        //Log.d("Error 400", response.toString());
                        //Log.d("Error 400", response.errorBody().toString());
                    }else if(response.code()==401){
                        try{
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            String mensaje = jObjError.getString("message");
                            new MaterialAlertDialogBuilder(RegistrarActivity.this)
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
            public void onFailure(Call<RespuestaWSRegister> call, Throwable t) {

            }
        });
    }
    private void initInicio(){
        Intent login  = new Intent (this, LoginActivity.class);
        startActivity(login);
        finish();
    }
}
