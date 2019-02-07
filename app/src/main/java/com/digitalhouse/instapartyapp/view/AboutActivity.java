package com.digitalhouse.instapartyapp.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.digitalhouse.instapartyapp.R;

public class AboutActivity extends AppCompatActivity {

    private Button btnInicar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        btnInicar = findViewById(R.id.buttonIniciar);

        btnInicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this, HomeActivity.class));
            }
        });

    }
}
