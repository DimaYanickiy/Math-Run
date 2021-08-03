package mara.tata.run;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    ImageButton btn_play, btn_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_play = findViewById(R.id.imageButton);
        btn_exit = findViewById(R.id.imageButton2);
        btn_play.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, GameActivity.class));
        });
        btn_exit.setOnClickListener(v -> {
            System.exit(0);
        });
    }
}