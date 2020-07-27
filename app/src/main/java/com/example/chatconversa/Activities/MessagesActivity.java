package com.example.chatconversa.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.chatconversa.ArmarMensaje;
import com.example.chatconversa.Interfaces.ServicioWeb;
import com.example.chatconversa.Objetos.Data;
import com.example.chatconversa.Objetos.Mensaje;
import com.example.chatconversa.R;
import com.example.chatconversa.Respuestas.RespuestaWSMessages;
import com.example.chatconversa.Respuestas.RespuestaWSSendMessage;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MessagesActivity extends AppCompatActivity implements View.OnClickListener {
    private Button profile, send;
    private EditText mensaje;
    private ServicioWeb servicioWeb;
    private RecyclerView mensajes;
    private CircleImageView fotoPerfil;
    private ArmarMensaje armador;
    private SharedPreferences preferences;
    private String token;
    private String username;
    private String user_id;
    private static final String CHANNEL_ID = "PUSHER_MSG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        createChannel();
        Intent intent = new Intent(this, MessagesActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MessagesActivity.this, 0, intent, 0);
        profile = findViewById(R.id.profileButton);
        profile.setOnClickListener(this);
        mensaje = findViewById(R.id.textMessage);
        send = findViewById(R.id.sendMessage);
        mensajes = findViewById(R.id.messages);
        fotoPerfil = findViewById(R.id.fotoPerfil);
        armador = new ArmarMensaje(this);
        LinearLayoutManager linear = new LinearLayoutManager(this);
        mensajes.setLayoutManager(linear);
        mensajes.setAdapter(armador);
        PusherOptions options = new PusherOptions();

        getPreferences();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://chat-conversa.unnamed-chile.com/ws/message/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        servicioWeb = retrofit.create(ServicioWeb.class);
        servicio();
        options.setCluster("us2");
        Pusher pusher = new Pusher("46e8ded9439a0fef8cbc", options);
        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Log.d("PUSHER", "Estado actual: "+change.getCurrentState().name()
                        +"Estado previo: "
                        +change.getPreviousState().name());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                Log.d("PUHSER", "ERROR PUSHER\n"
                        +"Mensaje: " + message + "\n"
                        +"Código: "+ code + "\n"
                        +"e: "+ e + "\n");
            }
        }, ConnectionState.ALL);

        Channel channel = pusher.subscribe("my-channel");
        channel.bind("my-event", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                        Log.d("PUSHER", "Nuevo mensaje: "+event.toString());
                        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(MessagesActivity.this,CHANNEL_ID)
                                .setContentIntent(pendingIntent)
                                .setSmallIcon(R.drawable.notification_icon)
                                .setContentTitle("Notification")
                                .setContentText("Mensaje")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setAutoCancel(true);
                        try {
                            JSONObject object = new JSONObject(event.toString());
                            nBuilder = new NotificationCompat.Builder(MessagesActivity.this,CHANNEL_ID)
                                    .setContentIntent(pendingIntent)
                                    .setSmallIcon(R.drawable.notification_icon)
                                    .setContentTitle("Mensaje de: "+object.getJSONObject("data").getJSONObject("message")
                                            .getJSONObject("user").getString("username"))
                                    .setContentText(object.getJSONObject("data").getJSONObject("message").getString("message"))
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setAutoCancel(true);



                        } catch (JSONException e) {
                            e.printStackTrace();
                }

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MessagesActivity.this);
                notificationManagerCompat.notify(5, nBuilder.build());
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                servicioSend();
                mensaje.setText("");
            }
        });


        armador.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollBar();
            }
        });
    }

    @Override
    public void onClick(View v){
        initProfile();
    }

    public void servicio(){
        final Call<RespuestaWSMessages> respuestaWSMessagesCall= servicioWeb.messages("Bearer "+token, user_id, username);
        respuestaWSMessagesCall.enqueue(new Callback<RespuestaWSMessages>() {
            @Override
            public void onResponse(Call<RespuestaWSMessages> call, Response<RespuestaWSMessages> response) {
                Log.d("TOKEN", token);
                if(response != null){
                    Log.d("CODIGO", ""+response.code());
                    if(response.body() != null && response.code()== 200){
                        RespuestaWSMessages respuestaWSMessages = response.body();
                        Data[] datas = respuestaWSMessages.getData();
                        for (int i=datas.length-1; i>=0;i--){
                            armador.addMensaje(new Mensaje(datas[i].getUser().getUsername(),datas[i].getMessage(), datas[i].getUser().getUser_image(), datas[i].getDate()));
                        }
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

    public void servicioSend(){
        String pathPhoto="";
        File archivoImage = new File(pathPhoto);
        RequestBody file = RequestBody.create(MediaType.parse("multipart/form-data"), archivoImage);
        MultipartBody.Part archivo = MultipartBody.Part.create(file);
        Double latitude= 32.40;
        Double longitude= 33.40;
        final Call<RespuestaWSSendMessage> respuestaWSSendMessageCall= servicioWeb.send("Bearer "+token, user_id, username,mensaje.getText().toString(),latitude, longitude);
        respuestaWSSendMessageCall.enqueue(new Callback<RespuestaWSSendMessage>() {
            @Override
            public void onResponse(Call<RespuestaWSSendMessage> call, Response<RespuestaWSSendMessage> response) {
                Log.d("TOKEN", token);
                if(response != null){
                    Log.d("CODIGO", ""+response.code());
                    if(response.body() != null && response.code()== 200){
                        RespuestaWSSendMessage respuestaWSSendMessage = response.body();
                        armador.addMensaje(new Mensaje(username,mensaje.getText().toString(), respuestaWSSendMessage.getData().getUser().getUser_image(), respuestaWSSendMessage.getData().getDate()));
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

    private void getPreferences(){
        preferences = getSharedPreferences(LoginActivity.CREDENTIALS, MODE_PRIVATE);
        token = preferences.getString("token", "Token no encontrado");
        Log.d("TOKEN", token);
        user_id = preferences.getString("id", "Id no encontrado");
        Log.d("TOKEN", user_id);
        username = preferences.getString("username", "Username no encontrado");
        Log.d("TOKEN", username);
    }

    private void initProfile(){
        Intent profile  = new Intent (this, ProfileActivity.class);
        startActivity(profile);
        finish();
    }

    private void setScrollBar(){
        mensajes.scrollToPosition(armador.getItemCount()-1);
    }

    private void createChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "PUSHER", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
