package com.example.chatconversa.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrarActivity extends AppCompatActivity implements View.OnClickListener{
    private Button registrar, cancelar;
    private TextInputEditText name, lastname, run, username, email, password, cpassword, token;
    private TextInputLayout nameL, lastnameL, runL, usernameL, emailL, passwordL, cpasswordL, tokenL;
    private ServicioWeb servicioWeb;
    public static final Pattern FORMAT_TOKEN = Pattern.compile("^[A-Z\\d]{6,6}$");
    public static final Pattern FORMAT_PASS = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{6,12}$");
    public static final Pattern FORMAT_EMAIL = Pattern.compile("[^@]+@[^@]+\\.[a-zA-Z]{2,}");
    public static final Pattern FORMAT_RUN = Pattern.compile("(\\d{7,8})$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_registrar);
        registrar = findViewById(R.id.registrar);
        cancelar = findViewById(R.id.cancelar);
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
        cpassword = findViewById(R.id.cpassword);
        cpasswordL = findViewById(R.id.cpasswordL);
        token = findViewById(R.id.token);
        tokenL = findViewById(R.id.tokenL);
        validarRun();
        validaUserName();
        validaEmail();
        validaPassword();
        confirmaPass();
        validaToken();
        registrar.setOnClickListener(this);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initInicio();
            }
        });
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
                                .setTitle("Felicitaciones!")
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

    private void validarRun(){
        run.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Matcher matcher = FORMAT_RUN.matcher(run.getText().toString());
                if (!matcher.find()){
                    runL.setError("El run debe tener mínimo 7 caracteres");
                    registrar.setEnabled(false);
                }else{
                    runL.setError(null);
                    registrar.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void validaUserName(){
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (username.getText().toString().length()<4){
                    usernameL.setError("El username debe tener mínimo 4 caracteres");
                    registrar.setEnabled(false);
                }else {
                    usernameL.setError(null);
                    registrar.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void validaPassword(){
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Matcher matcher = FORMAT_PASS.matcher(password.getText().toString());
                if (matcher.find()){
                    passwordL.setError(null);
                    registrar.setEnabled(true);
                }else {
                    registrar.setEnabled(false);
                    passwordL.setError("Formato inválido, debe contener solo letras mayúsculas, minúsculas y números");

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void validaToken(){
        token.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Matcher matcher = FORMAT_TOKEN.matcher(token.getText().toString());
                if (matcher.find()){
                    tokenL.setError(null);
                    registrar.setEnabled(true);
                }else {
                    tokenL.setError("Formato inválido");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void validaEmail(){
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Matcher matcher = FORMAT_EMAIL.matcher(email.getText().toString());
                if (matcher.find()){
                    emailL.setError(null);
                    registrar.setEnabled(true);
                }else {
                    emailL.setError("Ingrese un correo válido");
                    registrar.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void confirmaPass(){
        cpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String pass1 = password.getText().toString();
                String pass2 = cpassword.getText().toString();
                if (pass1.equals(pass2)){
                    registrar.setEnabled(true);
                    cpasswordL.setError(null);
                }else {
                    cpasswordL.setError("Las passwords no coinciden");
                    registrar.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

}
