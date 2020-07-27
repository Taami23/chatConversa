package com.example.chatconversa.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.chatconversa.R;

public class TakePhotoActivity extends AppCompatActivity {
    private Button tomarFoto;
    private VideoView contenedorVideo;

    private final static int REQUEST_PERMISSION = 1001;
    private final static int REQUEST_CAMERA = 1002;
    private final static String[] PERMISSION_REQUIRED =
            new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};

    private String pathPhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        tomarFoto = findViewById(R.id.tomarFoto);
        contenedorVideo = findViewById(R.id.contenedorVideo);

        if (verifyPermission()) {
            startCameraInit();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSION_REQUIRED, REQUEST_PERMISSION);
        }
    }

    private boolean verifyPermission() {
        for (String permission : PERMISSION_REQUIRED) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (verifyPermission()) {
                startCameraInit();
            } else {
                Toast.makeText(this, "Los permisos deben ser autorizados", Toast.LENGTH_LONG).show();
                finish();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startCameraInit() {
        tomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCamera();
            }
        });
    }

    private void startCamera() {
        Intent iniciarCamara = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (iniciarCamara.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(iniciarCamara, REQUEST_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();

            contenedorVideo.setVideoURI(videoUri);
            contenedorVideo.start();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
