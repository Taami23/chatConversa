package com.example.chatconversa.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.chatconversa.Interfaces.ServicioWeb;
import com.example.chatconversa.R;
import com.example.chatconversa.Respuestas.RespuestaWSRegister;
import com.google.android.material.textfield.TextInputEditText;

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
    private TextInputEditText lastname;
    private TextInputEditText run;
    private TextInputEditText username;
    private TextInputEditText email;
    private TextInputEditText password;
    private TextInputEditText token;
    private ServicioWeb servicioWeb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_registrar);
        registrar = findViewById(R.id.registrar);
        name = findViewById(R.id.name);
        lastname = findViewById(R.id.lastname);
        run = findViewById(R.id.run);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        token = findViewById(R.id.token);
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
                        Log.d("Retrofit", "Registro exitoso");
                        Log.d("Retrofit", respuestaWSRegister.toString());
                    }else if (response.code()==400){

                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            JSONArray jsonArray = jObjError.getJSONObject("errors").getJSONArray("username");
                            Log.d("cacuca", jsonArray.getString(0));

                            Log.d("Error 400", jObjError.getString("errors"));
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                        //Log.d("Error 400", response.toString());
                        //Log.d("Error 400", response.errorBody().toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<RespuestaWSRegister> call, Throwable t) {

            }
        });
    }
}
