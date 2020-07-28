package com.example.chatconversa.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.chatconversa.Interfaces.ServicioWeb;
import com.example.chatconversa.R;
import com.example.chatconversa.Respuestas.RespuestaWSSendMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotoMensajeActivity extends AppCompatActivity {

    private ImageView imagen;
    private EditText mensaje;
    private ImageButton enviar;
    private SharedPreferences preferences;
    private String token;
    private String user_id;
    private String username;
    private String pathPhoto;
    private ServicioWeb servicioWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_mensaje);
        getPreferences();
        imagen = findViewById(R.id.foto);
        mensaje = findViewById(R.id.textMessage);
        enviar = findViewById(R.id.sendMessage);
        Glide.with(this).load(pathPhoto).into(imagen);
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://chat-conversa.unnamed-chile.com/ws/message/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        servicioWeb = retrofit.create(ServicioWeb.class);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                servicioSend();
        }
        });

    }
    public void servicioSend(){
        File archivoImagen = new File(pathPhoto);
        Log.d("CACA", pathPhoto);
        RequestBody imagen = RequestBody.create(MediaType.parse("multipart/form-data"), archivoImagen);
        MultipartBody.Part file = MultipartBody.Part.createFormData("image", archivoImagen.getName(), imagen);
        Double latitude= 32.40;
        Double longitude= 33.40;
        RequestBody user = RequestBody.create(MediaType.parse("multipart/form-data"), user_id);
        RequestBody user_name = RequestBody.create(MediaType.parse("multipart/form-data"), username);
        if(!mensaje.getText().toString().equalsIgnoreCase("") || file != null ){
            final Call<RespuestaWSSendMessage> respuestaWSSendMessageCall= servicioWeb.send("Bearer "+token, user, user_name,mensaje.getText().toString(),file,latitude, longitude);
            respuestaWSSendMessageCall.enqueue(new Callback<RespuestaWSSendMessage>() {
                @Override
                public void onResponse(Call<RespuestaWSSendMessage> call, Response<RespuestaWSSendMessage> response) {
                    Log.d("TOKEN", token);
                    if(response != null){
                        Log.d("CODIGO", ""+response.code());
                        if(response.body() != null && response.code()== 200){
                            Log.d("EXITO", "Mensaje enviado con exito");
                            initMessage();
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
    }

    private void getPreferences(){
        preferences = getSharedPreferences(LoginActivity.CREDENTIALS, MODE_PRIVATE);
        token = preferences.getString("token", "Token no encontrado");
        Log.d("TOKEN", token);
        user_id = preferences.getString("id", "Id no encontrado");
        Log.d("TOKEN", user_id);
        username = preferences.getString("username", "Username no encontrado");
        Log.d("TOKEN", username);
        pathPhoto = preferences.getString("pathPhoto", "Ruta no encontrada");
        Log.d("TOKEN", "memori"+pathPhoto);
    }

    private void initMessage(){
        Intent message = new Intent(this, MessagesActivity.class);
        startActivity(message);
        finish();
    }
}