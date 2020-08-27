package com.example.chatconversa.Activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.example.chatconversa.Interfaces.ServicioWeb;
import com.example.chatconversa.R;
import com.example.chatconversa.Respuestas.RespuestaWSSendMessage;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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


public class LocalizacionActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, View.OnClickListener {

    private GoogleMap mMap;
    private static final int LOCATION_CODE_REQUEST = 222;
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private boolean permissionDenied = false;

    //Botones
    private Button actual;
    private Button marcador;
    //Variables Globales
    private double lat;
    private double lon;
    //preferencias
    private String token;
    private String user_id;
    private String username;

    private FusedLocationProviderClient mFusedLocationProvider;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private MarkerOptions marker;

    private ServicioWeb servicioWeb;
    private SharedPreferences preferences;

    //datosViewModel
    private MutableLiveData<LatLng> ubicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localizacion);

        final MapView mapView = findViewById(R.id.mapa);
        MapsInitializer.initialize(this);

        //Servicio
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://chat-conversa.unnamed-chile.com/ws/message/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        servicioWeb = retrofit.create(ServicioWeb.class);
        //Botones
        actual = findViewById(R.id.actual);
        marcador = findViewById(R.id.marcador);
        //preferencias
        getPreferences();

        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);

        actual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ACTUAL", "0");
                enviarActual();
            }
        });
        marcador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarMarcador();
            }
        });

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                } else {
                    for(Location location : locationResult.getLocations()){
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                    }
                }
            }
        };
    }

    private void enviarActual() {
        Log.d("ACTUAL", "1");
        if(lat == 0 && lon == 0){
            new MaterialAlertDialogBuilder(LocalizacionActivity.this)
                    .setTitle("Upps!")
                    .setMessage("No se pudo obtener su ubicación")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
            return;
        } else {
            Log.d("ACTUAL", "4");
            servicioSend(lat, lon);
        }
    }

    private void enviarMarcador() {
        if (marker != null) {
            double lat = marker.getPosition().latitude;
            double lon = marker.getPosition().longitude;
            this.servicioSend(lat, lon);
        } else {
            new MaterialAlertDialogBuilder(LocalizacionActivity.this)
                    .setTitle("Upps! Marcador vacio")
                    .setMessage("Presione en el mapa para agregar un marcador")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
    }

    private void initializeTrackingLocation() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationProvider.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    private void enableLocation() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
//                CameraUpdate pos = CameraUpdateFactory.newLatLng(new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude()));
//                mMap.moveCamera(pos);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);
                mMap.moveCamera(zoom);
                initializeTrackingLocation();
            }
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_CODE_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_CODE_REQUEST) {
            return;
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableLocation();
        } else {
            permissionDenied = true;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.setOnMapLongClickListener(this);
        mMap = googleMap;
        enableLocation();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d("UBICACION", "Estoy llegando al longclick: "+latLng);
        marker = new MarkerOptions()
                .position(latLng)
                .title("Ubicación para enviar");
        mMap.clear();
        mMap.addMarker(marker);
    }

    @Override
    public void onClick(View v) {

    }
    public void servicioSend(double lat,double lon){
        Double latitude= lat;
        Double longitude= lon;
        RequestBody user = RequestBody.create(MediaType.parse("multipart/form-data"), user_id);
        RequestBody user_name = RequestBody.create(MediaType.parse("multipart/form-data"), username);
        if(latitude != null && longitude != null){
            final Call<RespuestaWSSendMessage> respuestaWSSendMessageCall= servicioWeb.send("Bearer "+token, user, user_name,null,null,latitude, longitude);
            respuestaWSSendMessageCall.enqueue(new Callback<RespuestaWSSendMessage>() {
                @Override
                public void onResponse(Call<RespuestaWSSendMessage> call, Response<RespuestaWSSendMessage> response) {
                    Log.d("LOCALIZACION", token);
                    if(response != null){
                        Log.d("LOCALIZACION1", ""+response.code());
                        if(response.body() != null && response.code()== 200){
                            Log.d("LOCALIZACION2", "Mensaje enviado con exito");
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
                        }else if (response.code()==503){
                            JSONObject jObjError = null;
                            try {
                                jObjError = new JSONObject(response.errorBody().string());
                                String mensaje = jObjError.getString("message");
                                Log.d("Retroit Error", mensaje);
                                new MaterialAlertDialogBuilder(LocalizacionActivity.this)
                                        .setTitle("Error")
                                        .setMessage("Servicio no disponible")
                                        .setCancelable(false)
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                initMessage();
                                            }
                                        }).show();
                            } catch (JSONException  | IOException e) {
                                e.printStackTrace();
                            }


                        }
                    }
                }

                @Override
                public void onFailure(Call<RespuestaWSSendMessage> call, Throwable t) {
                    new MaterialAlertDialogBuilder(LocalizacionActivity.this)
                            .setTitle("Error")
                            .setMessage("Tiempo de espera excedido")
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    initMessage();
                                }
                            }).show();


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

    private void initMessage(){
        Intent message = new Intent(this, MessagesActivity.class);
        startActivity(message);
        finish();
    }
}