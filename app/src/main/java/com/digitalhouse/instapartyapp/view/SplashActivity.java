package com.digitalhouse.instapartyapp.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.digitalhouse.instapartyapp.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {
    private Timer timer = new Timer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                jump();
            }
        }, 3000);
    }

    private void jump() {
        timer.cancel();
        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
        finish();
    }
}
