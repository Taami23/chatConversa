package com.example.chatconversa.Activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.example.chatconversa.R;
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


public class LocalizacionActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, View.OnClickListener {

    private GoogleMap mMap;
    private static final int LOCATION_CODE_REQUEST = 222;
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private boolean permissionDenied = false;

    //Botones
    private Button actual;
    private Button marcador;

    private FusedLocationProviderClient mFusedLocationProvider;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private MarkerOptions marker;

    //datosViewModel
    private MutableLiveData<LatLng> ubicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localizacion);

        final MapView mapView = findViewById(R.id.mapa);
        MapsInitializer.initialize(this);

        //Botones
        actual = findViewById(R.id.actual);
        marcador = findViewById(R.id.marcador);

        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);

        actual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
//                    Location location = locationResult.getLastLocation();
//                    CameraUpdate pos = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
//                    mMap.moveCamera(pos);
                }
            }
        };
    }

    private void enviarActual() {

    }

    private void enviarMarcador() {
        if (marker != null) {
            ubicacion.setValue(marker.getPosition());
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
}