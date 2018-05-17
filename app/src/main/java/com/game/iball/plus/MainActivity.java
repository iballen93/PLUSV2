package com.game.iball.plus;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button[] buttonGrid = new Button[9];
    boolean isClickable = true;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cleanupView();

        buttonGrid[0] = findViewById(R.id.button0);
        buttonGrid[1] = findViewById(R.id.button1);
        buttonGrid[2] = findViewById(R.id.button2);
        buttonGrid[3] = findViewById(R.id.button3);
        buttonGrid[4] = findViewById(R.id.button4);
        buttonGrid[5] = findViewById(R.id.button5);
        buttonGrid[6] = findViewById(R.id.button6);
        buttonGrid[7] = findViewById(R.id.button7);
        buttonGrid[8] = findViewById(R.id.button8);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int size = (int) (((double) metrics.widthPixels) * 0.91d);
        for (int i = 0; i < 9; i++) {
            this.buttonGrid[i].setText(Integer.toString(i + 5));
            this.buttonGrid[i].setTextSize(51.0f);
            this.buttonGrid[i].setWidth(size / 3);
            this.buttonGrid[i].setHeight(size / 3);
        }

        Button buttonHowTo = findViewById(R.id.buttonHowTo);
        buttonHowTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DirectionsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);
                overridePendingTransition(0,0);
            }
        });

        buttonGrid[0].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("Pass Grid Size", buttonGrid[0].getText().toString());
                startActivity(intent);
            }
        });

        buttonGrid[1].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("Pass Grid Size", buttonGrid[1].getText().toString());
                startActivity(intent);
            }
        });

        buttonGrid[2].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("Pass Grid Size", buttonGrid[2].getText().toString());
                startActivity(intent);
            }
        });

        buttonGrid[3].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("Pass Grid Size", buttonGrid[3].getText().toString());
                startActivity(intent);
            }
        });

        buttonGrid[4].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("Pass Grid Size", buttonGrid[4].getText().toString());
                startActivity(intent);
            }
        });

        buttonGrid[5].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("Pass Grid Size", buttonGrid[5].getText().toString());
                startActivity(intent);
            }
        });

        buttonGrid[6].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("Pass Grid Size", buttonGrid[6].getText().toString());
                startActivity(intent);
            }
        });

        buttonGrid[7].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("Pass Grid Size", buttonGrid[7].getText().toString());
                startActivity(intent);
            }
        });

        buttonGrid[8].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("Pass Grid Size", buttonGrid[8].getText().toString());
                startActivity(intent);
            }
        });
    }

    public void cleanupView(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(Integer.MIN_VALUE);
            window.setStatusBarColor(Color.parseColor("#002E3D"));
        }
    }
}
