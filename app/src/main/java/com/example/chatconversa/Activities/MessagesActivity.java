package com.example.chatconversa.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private ImageButton profile;
    private ImageButton insertarFoto;
    private ImageButton insertarUbicacion;
    private ImageButton send;
    private EditText mensaje;
    private ServicioWeb servicioWeb;
    private RecyclerView mensajes;
    private CircleImageView fotoPerfil;
    private ArmarMensaje armador;
    private SharedPreferences preferences;
    private String token;
    private String username;
    private String user_id;
    private String pathPhoto;
    private static final String CHANNEL_ID = "PUSHER_MSG";
    private final static int REQUEST_PERMISSION = 1001;
    private final static int REQUEST_CAMERA = 1002;
    private final static String[] PERMISSION_REQUIRED =
            new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        getPreferences();
        createChannel();
        Intent intent = new Intent(this, MessagesActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MessagesActivity.this, 0, intent, 0);
        insertarFoto = findViewById(R.id.insertarFoto);
        insertarUbicacion = findViewById(R.id.insertarUbicacion);
        profile = findViewById(R.id.profileButton);
        profile.setOnClickListener(this);
        mensaje = findViewById(R.id.textMessage);
        send = findViewById(R.id.sendMessage);
        mensajes = findViewById(R.id.messages);
        fotoPerfil = findViewById(R.id.atrasMensaje);
        armador = new ArmarMensaje(this, username);
        LinearLayoutManager linear = new LinearLayoutManager(this);
        mensajes.setLayoutManager(linear);
        mensajes.setAdapter(armador);
        mensajes.setHasFixedSize(true);
        mensajes.setItemViewCacheSize(30);
        mensajes.setDrawingCacheEnabled(true);
        mensajes.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        PusherOptions options = new PusherOptions();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://chat-conversa.unnamed-chile.com/ws/message/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        servicioWeb = retrofit.create(ServicioWeb.class);
        servicio();
        notifications(options, pendingIntent);

        insertarUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initLocalizacion();
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

        if(verifyPermission()){
            startCameraInit();
        }else{
            ActivityCompat.requestPermissions(this, PERMISSION_REQUIRED, REQUEST_PERMISSION);
        }

    }
    private boolean verifyPermission(){
        for(String permission : PERMISSION_REQUIRED){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
    private void startCameraInit(){
        insertarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startCamera();
            }
        });
    }

    private void startCamera(){
        if(false){
            Intent iniciarCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(iniciarCamara.resolveActivity(getPackageManager()) != null){
                startActivityForResult(iniciarCamara, REQUEST_CAMERA);
            }
        }else{
            Intent iniciarCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(iniciarCamara.resolveActivity(getPackageManager()) != null){
                File photoFile = null;
                try{
                    photoFile = createFilePhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(photoFile != null){
                    Uri photoUri = FileProvider.getUriForFile(this,
                            "com.example.chatconversa.fileprovider",
                            photoFile);
                    iniciarCamara.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(iniciarCamara, REQUEST_CAMERA);
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CAMERA && resultCode == RESULT_OK){
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString("vengoDe", "camara");
            edit.putString("pathPhoto", pathPhoto);
            edit.commit();
            initPhotoMensaje();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION){
            if(verifyPermission()){
                startCameraInit();
            }else{
                Toast.makeText(this, "Los permisos deben ser autorizados", Toast.LENGTH_LONG).show();
                finish();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private File createFilePhoto() throws IOException {
        String timestamp =  new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String file_name = "JPEG_" + timestamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photo = File.createTempFile(
                file_name,
                ".jpg",
                storageDir
        );
        pathPhoto = photo.getAbsolutePath();
        return photo;
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
                        Log.d("MENSAJITOS", respuestaWSMessages.toString());
                        Data[] datas = respuestaWSMessages.getData();
                        for (int i=datas.length-1; i>=0;i--){
                                armador.addMensaje(new Mensaje(datas[i].getUser().getUsername(),datas[i].getMessage(), datas[i].getUser().getUser_image(),
                                        datas[i].getDate(), datas[i].getImage(), datas[i].getThumbnail(), datas[i].getLatitude(), datas[i].getLongitude()));
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
        //RequestBody file = RequestBody.create(MediaType.parse("multipart/form-data"), archivoImage);
        //MultipartBody.Part archivo = MultipartBody.Part.create(file);
        MultipartBody.Part file = null;
        RequestBody user = RequestBody.create(MediaType.parse("multipart/form-data"), user_id);
        RequestBody user_name = RequestBody.create(MediaType.parse("multipart/form-data"), username);
        if(!mensaje.getText().toString().equalsIgnoreCase("") || file != null ){
            final Call<RespuestaWSSendMessage> respuestaWSSendMessageCall= servicioWeb.send("Bearer "+token, user, user_name,mensaje.getText().toString(),file, null, null   );
            respuestaWSSendMessageCall.enqueue(new Callback<RespuestaWSSendMessage>() {
                @Override
                public void onResponse(Call<RespuestaWSSendMessage> call, Response<RespuestaWSSendMessage> response) {
                    Log.d("TOKEN", token);
                    if(response != null){
                        Log.d("CODIGO", ""+response.code());
                        if(response.body() != null && response.code()== 200){
                            Log.d("MAPA2", response.body().toString());
                            Log.d("EXITO", "Mensaje enviado con exito");
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

    private void notifications(PusherOptions options, PendingIntent pendingIntent){
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
                if(preferences.getString("token", null) != null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            NotificationCompat.Builder nBuilder = null;
                            try {
                                JSONObject object = new JSONObject(event.toString());
                                Log.d("User", object.getJSONObject("data").getJSONObject("message").getJSONObject("user").getString("username")+ " usuario: "+ username);
                                if(!object.getJSONObject("data").getJSONObject("message").getJSONObject("user").getString("username").equalsIgnoreCase(username)){
                                    nBuilder = new NotificationCompat.Builder(MessagesActivity.this,CHANNEL_ID)
                                            .setContentIntent(pendingIntent)
                                            .setSmallIcon(R.drawable.ic_logov2_removebg)
                                            .setContentTitle("Mensaje de: "+object.getJSONObject("data").getJSONObject("message")
                                                    .getJSONObject("user").getString("username"))
                                            .setContentText(object.getJSONObject("data").getJSONObject("message").getString("message"))
                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                                            .setAutoCancel(true);
                                }
                                    String lat = null;
                                    String lon = null;
                                    if (!object.getJSONObject("data").getJSONObject("message").getString("longitude").equalsIgnoreCase("null")){
                                        Log.d("SOY NULO", "MENTIRA" + object.getJSONObject("data").getJSONObject("message").getString("message"));
                                        lat = object.getJSONObject("data").getJSONObject("message").getString("latitude");
                                        lon = object.getJSONObject("data").getJSONObject("message").getString("longitude");
                                    }
                                armador.addMensaje(new Mensaje(object.getJSONObject("data").getJSONObject("message").getJSONObject("user").getString("username"),
                                        object.getJSONObject("data").getJSONObject("message").getString("message"),
                                        object.getJSONObject("data").getJSONObject("message").getJSONObject("user").getString("user_image"),
                                        object.getJSONObject("data").getJSONObject("message").getString("date"),
                                        object.getJSONObject("data").getJSONObject("message").getString("image"),
                                        object.getJSONObject("data").getJSONObject("message").getString("thumbnail"),
                                        lat,
                                        lon));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (nBuilder != null){
                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MessagesActivity.this);
                                notificationManagerCompat.notify(5, nBuilder.build());
                            }
                        }
                    });
                }

            }
        });
    }


    private void initPhotoMensaje(){
        Intent photoMensaje = new Intent(this, PhotoMensajeActivity.class);
        startActivity(photoMensaje);
        finish();
    }
    private void initLocalizacion(){
        Intent localizacion = new Intent(this, LocalizacionActivity.class);
        startActivity(localizacion);
        finish();
    }
}
