package com.example.chatconversa.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatconversa.R;


public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
        Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo);
        TextView texto = findViewById(R.id.textView);
        TextView texto2 = findViewById(R.id.marcaRegistrada);
        ImageView imagen = findViewById(R.id.imageView);
        imagen.setAnimation(animation2);
        texto.setAnimation(animation1);
        texto2.setAnimation(animation1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = getSharedPreferences(LoginActivity.CREDENTIALS, MODE_PRIVATE);
                String token = preferences.getString("token", "Token no encontrado");
                Log.d("TOKEN", token);
                if(token.equalsIgnoreCase("Token no encontrado")){
                    Intent login = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(login);
                    finish();
                }else {
                    Intent messages = new Intent(SplashScreenActivity.this , MessagesActivity.class);
                    startActivity(messages);
                    finish();
                }
            }
        }, 3000);
    }
}
