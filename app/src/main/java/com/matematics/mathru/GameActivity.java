package com.matematics.mathru;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    ImageButton btn_answer_1, btn_answer_2, btn_answer_3, btn_answer_4;
    TextView answer_1, answer_2, answer_3, answer_4, quation, score;
    ImageView fail_1, fail_2, fail_3;

    public Random random;
    public String quationString;
    public String sign = "+";
    public int firstN, secondN;
    public int trueAnswer;
    private int scoreCounter = 0;
    public int answers[] = {0, 0, 0, 0};
    public int button_click = 0;
    public int fails = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        btn_answer_1 = findViewById(R.id.btn_answer_1);
        btn_answer_2 = findViewById(R.id.btn_answer_2);
        btn_answer_3 = findViewById(R.id.btn_answer_3);
        btn_answer_4 = findViewById(R.id.btn_answer_4);
        answer_1 = findViewById(R.id.answer_1);
        answer_2 = findViewById(R.id.answer_2);
        answer_3 = findViewById(R.id.answer_3);
        answer_4 = findViewById(R.id.answer_4);
        quation = findViewById(R.id.quation);
        score = findViewById(R.id.score);
        fail_1 = findViewById(R.id.imageView);
        fail_2 = findViewById(R.id.imageView2);
        fail_3 = findViewById(R.id.imageView3);
        random = new Random();
        createQuation();
        fillButtons();
        checkFails();

        btn_answer_1.setOnClickListener(v -> {
            button_click = 1;
            checkAnswer();
        });

        btn_answer_2.setOnClickListener(v -> {
            button_click = 2;
            checkAnswer();
        });

        btn_answer_3.setOnClickListener(v -> {
            button_click = 3;
            checkAnswer();
        });

        btn_answer_4.setOnClickListener(v -> {
            button_click = 4;
            checkAnswer();
        });
    }

    public void fillButtons(){
        for(int i = 0; i < answers.length; i++){
            answers[i] = Math.abs(random.nextInt()%200);
        }
        int n = Math.abs(random.nextInt()%4);
        answers[n] = trueAnswer;
        answer_1.setText(Integer.toString(answers[0]));
        answer_2.setText(Integer.toString(answers[1]));
        answer_3.setText(Integer.toString(answers[2]));
        answer_4.setText(Integer.toString(answers[3]));
    }

    public void createQuation(){
        firstN = Math.abs(random.nextInt()%100);
        secondN = Math.abs(random.nextInt()%100);
        if(scoreCounter == 0){
            sign = "+";
        }
        if(scoreCounter == 20){
            sign = "-";
        }
        if(scoreCounter == 40){
            sign = "*";
        }
        if(scoreCounter == 60){
            sign = "/";
        }
        quationString = firstN + sign + secondN;
        if(sign.equals("+")){
            trueAnswer = firstN + secondN;
        } else if(sign.equals("-")){
            trueAnswer = firstN - secondN;
        } else if(sign.equals("*")){
            trueAnswer = firstN * secondN;
        } else if(sign.equals("/")){
            trueAnswer = firstN / secondN;
        }
        quation.setText(quationString);
    }

    public void checkFails(){
        if (fails == 0) {
            fail_1.setVisibility(View.INVISIBLE);
            fail_2.setVisibility(View.INVISIBLE);
            fail_3.setVisibility(View.INVISIBLE);
        } else if(fails == 1){
            fail_1.setVisibility(View.VISIBLE);
        } else if(fails == 2){
            fail_2.setVisibility(View.VISIBLE);
        } else if(fails == 3){
            fail_3.setVisibility(View.VISIBLE);
            startActivity(new Intent(GameActivity.this, RestartActivity.class));
            finish();
        }
    }

    public void checkAnswer(){
        switch (button_click){
            case 1:
                if(Integer.parseInt((String)answer_1.getText()) == trueAnswer) {
                    scoreCounter++;
                    score.setText("Score: " + scoreCounter);
                } else{
                    fails++;
                    checkFails();
                }
                createQuation();
                fillButtons();
                break;
            case 2:
                if(Integer.parseInt((String)answer_2.getText()) == trueAnswer){
                    scoreCounter++;
                    score.setText("Score: " + scoreCounter);
                } else{
                    fails++;
                    checkFails();
                }
                createQuation();
                fillButtons();
                break;
            case 3:
                if(Integer.parseInt((String)answer_3.getText()) == trueAnswer){
                    scoreCounter++;
                    score.setText("Score: " + scoreCounter);
                } else{
                    fails++;
                    checkFails();
                }
                createQuation();
                fillButtons();
                break;
            case 4:
                if(Integer.parseInt((String)answer_4.getText()) == trueAnswer){
                    scoreCounter++;
                    score.setText("Score: " + scoreCounter);
                } else{
                    fails++;
                    checkFails();
                }
                createQuation();
                fillButtons();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(GameActivity.this, MainActivity.class));
    }
}