package com.example.original;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {
    private ImageView img;
    private static int splashTimeOut=5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        img = (ImageView) findViewById(R.id.intro);
        Animation animation = AnimationUtils.loadAnimation(Splash.this,R.anim.myanim);
        img.startAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Splash.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        },splashTimeOut);
    }
}
