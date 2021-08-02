package com.matematics.mathru;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class RestartActivity extends AppCompatActivity {

    ImageButton btn_home, btn_restart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restart);
        btn_restart = findViewById(R.id.imageButton3);
        btn_home = findViewById(R.id.imageButton5);
        btn_restart.setOnClickListener(v -> {
            startActivity(new Intent(RestartActivity.this, GameActivity.class));
            finish();
        });
        btn_home.setOnClickListener(v -> {
            startActivity(new Intent(RestartActivity.this, MainActivity.class));
            finish();
        });
    }
}