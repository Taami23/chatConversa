package com.example.chatconversa.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.chatconversa.R;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class muestraMapaActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener  {

    private GoogleMap mMap;
    private static final int LOCATION_CODE_REQUEST = 222;
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private boolean permissionDenied = false;

    private FusedLocationProviderClient mFusedLocationProvider;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private MarkerOptions marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MUESTRA", "MAPA");
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_foto);
        final MapView mapView = findViewById(R.id.mapa);
        MapsInitializer.initialize(this);

        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                } else {
                    for(Location location : locationResult.getLocations()){

                    }
                }
            }
        };
    }

    @Override
    public void onClick(View v) {

    }

    private void initializeTrackingLocation() {
        Log.d("MUESTRA", "MAPA");
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
        mMap = googleMap;
        enableLocation();
    }
}