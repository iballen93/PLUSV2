package com.game.iball.plus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;

public class GameActivity extends AppCompatActivity {

    Handler customHandler = new Handler();
    String gridSize;
    Integer[] mThumbIds;
    int fullWidthPX;
    float fullWidthDP, singleCellWidthDP, singleCellWidthPX;
    boolean allBlack;
    int level = 1;
    int roundMoves = 0;
    int totalMoves = 0;
    int roundMin, roundSec, roundMillisecond;
    int totalMin, totalSec, totalMillisecond;
    long timeInMillisecondsRound = 0;
    long timeInMillisecondsTotal = 0;
    long updatedRoundTime = 0;
    long updatedTotalTime = 0;
    boolean firstClick = true;
    boolean stopClicked;
    private long startRoundTime = 0;
    private long startTotalTime = 0;
    long timeSwapBuffRound = 0;
    long timeSwapBuffTotal = 0;
    String stringMovesSuffix = "";
    TextView tvLevel;
    TextView tvRoundMoves;
    TextView tvTotalMoves;
    TextView tvRoundTime;
    TextView tvTotalTime;
    GridView gvGameBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        generateUI();
        generateGrid();
        generateLevel();
        gvGameBoard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if(firstClick){
                    startTimers();
                    stopClicked = false;
                    firstClick = false;
                }
                generatePlus(position);
                totalMoves++;
                roundMoves++;
                allBlack = true;
                for(int i=0; i<Integer.parseInt(gridSize)*Integer.parseInt(gridSize); i++){
                    if(mThumbIds[i].intValue() == R.drawable.pixel_red){
                        allBlack = false;
                    }
                }

                //~~~~~~~~WIN~~~~~~~~\\
                if(allBlack){
                    if(roundMoves > 1){
                        stringMovesSuffix = "s";
                    }
                    if(!stopClicked){
                        Toast.makeText(GameActivity.this, "You beat Level " + Integer.toString(level) + "!\n" + Integer.toString(roundMoves) + " move" + stringMovesSuffix + '\n' + (((double) updatedRoundTime) / 1000.0d) + " seconds", Toast.LENGTH_LONG).show();
                        stopClicked = true;
                    }
                    stopTimers();
                    level++;
                    generateLevel();
                    roundMoves = 0;
                    firstClick = true;
                }
                setLevelText();
                setMovesText();
            }
        });

        Button regLvl = findViewById(R.id.buttonRegenerateLevel);
        regLvl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateGrid();
                generateLevel();
            }
        });

        Button goHome =  findViewById(R.id.buttonHome);
        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void generateLevel() {
        int i;
        Integer[] a = new Integer[(Integer.parseInt(gridSize) * Integer.parseInt(gridSize))];
        for (i = 0; i < a.length; i++) {
            a[i] = Integer.valueOf(i);
        }
        Collections.shuffle(Arrays.asList(a));
        for (i = 0; i < level; i++) {
            generatePlus(a[i].intValue());
        }
    }

    public void generateUI() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(Integer.MIN_VALUE);
            window.setStatusBarColor(Color.parseColor("#002E3D"));
        }
        Intent thisIntent = getIntent();
        gridSize = thisIntent.getStringExtra("Pass Grid Size");
        Toast.makeText(GameActivity.this, "Starting game with grid size " + gridSize, Toast.LENGTH_SHORT).show();
        tvLevel = findViewById(R.id.textViewLevel);
        tvLevel.setText("Level " + Integer.toString(level));
        tvRoundTime = findViewById(R.id.textViewRoundTime);
        tvTotalTime = findViewById(R.id.textViewTotalTime);
        setTimeText();
    }

    private void generateGrid() {
        boolean black = true;
        mThumbIds = new Integer[Integer.parseInt(gridSize) * Integer.parseInt(gridSize)];
        for (int i = 0; i < (Integer.parseInt(gridSize) * Integer.parseInt(gridSize)); i++){
            if(Integer.parseInt(gridSize) % 2 == 0){
                if(black){
                    mThumbIds[i] = Integer.valueOf(R.drawable.pixel_black);
                    black = false;
                }else{
                    mThumbIds[i] = Integer.valueOf(R.drawable.pixel_grey);
                    black = true;
                }
            }else if (i % 2 == 0){
                mThumbIds[i] = Integer.valueOf(R.drawable.pixel_black);
            }else{
                mThumbIds[i] = Integer.valueOf(R.drawable.pixel_grey);
            }
            if((i + 1) % Integer.parseInt(gridSize) == 0){
                if(black){
                    black = false;
                }else{
                    black = true;
                }
            }
        }
        gvGameBoard = findViewById(R.id.gridviewGameBoard);
        gvGameBoard.setAdapter(new ImageAdapter(this));
        gvGameBoard.setNumColumns(Integer.parseInt(gridSize));
        gvGameBoard.setHorizontalSpacing(0);
        gvGameBoard.setVerticalSpacing(0);
        gvGameBoard.setPadding(12, 180, 10, 0);
    }

    private void generatePlus(int cell) {
        if (cell % Integer.parseInt(gridSize) != 0) {
            toggleColor(cell - 1);
        }
        if (cell % Integer.parseInt(gridSize) != Integer.parseInt(gridSize) - 1) {
            toggleColor(cell + 1);
        }
        if (cell >= Integer.parseInt(gridSize)) {
            toggleColor(cell - Integer.parseInt(gridSize));
        }
        if (cell < (Integer.parseInt(gridSize) * Integer.parseInt(gridSize)) - Integer.parseInt(gridSize)) {
            toggleColor(Integer.parseInt(gridSize) + cell);
        }
        ((GridView) findViewById(R.id.gridviewGameBoard)).setAdapter(new ImageAdapter(this));
    }

    private void toggleColor(int cell) {
        if (mThumbIds[cell].intValue() != R.drawable.pixel_red) {
            mThumbIds[cell] = Integer.valueOf(R.drawable.pixel_red);
        } else if (Integer.parseInt(gridSize) % 2 == 0) {
            if ((cell / Integer.parseInt(gridSize)) % 2 == 0 && (cell % Integer.parseInt(gridSize)) % 2 == 0) {
                mThumbIds[cell] = Integer.valueOf(R.drawable.pixel_black);
            }
            if ((cell / Integer.parseInt(gridSize)) % 2 == 0 && (cell % Integer.parseInt(gridSize)) % 2 == 1) {
                mThumbIds[cell] = Integer.valueOf(R.drawable.pixel_grey);
            }
            if ((cell / Integer.parseInt(gridSize)) % 2 == 1 && (cell % Integer.parseInt(gridSize)) % 2 == 0) {
                mThumbIds[cell] = Integer.valueOf(R.drawable.pixel_grey);
            }
            if ((cell / Integer.parseInt(gridSize)) % 2 == 1 && (cell % Integer.parseInt(gridSize)) % 2 == 1) {
                mThumbIds[cell] = Integer.valueOf(R.drawable.pixel_black);
            }
        } else if (cell % 2 == 0) {
            mThumbIds[cell] = Integer.valueOf(R.drawable.pixel_black);
        } else {
            mThumbIds[cell] = Integer.valueOf(R.drawable.pixel_grey);
        }
        ((GridView) findViewById(R.id.gridviewGameBoard)).setAdapter(new ImageAdapter(this));
    }

    public int getSingleCellWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        fullWidthPX = metrics.widthPixels;
        fullWidthDP = ((metrics.widthPixels) / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        singleCellWidthPX = fullWidthPX / Float.parseFloat(gridSize);
        singleCellWidthDP = fullWidthDP / Float.parseFloat(gridSize);


        return (int) (singleCellWidthPX);
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(getSingleCellWidth(), getSingleCellWidth()));
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }
    }

    private Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            timeInMillisecondsTotal = SystemClock.uptimeMillis() - startTotalTime;
            timeInMillisecondsRound = SystemClock.uptimeMillis() - startRoundTime;
            updatedTotalTime = timeSwapBuffTotal + timeInMillisecondsTotal;
            updatedRoundTime = timeSwapBuffRound + timeInMillisecondsRound;
            totalSec = (int) (updatedTotalTime / 1000);
            totalMin = totalSec / 60;
            totalSec %= 60;
            totalMillisecond = (int) (updatedTotalTime % 1000);
            roundSec = (int) (updatedRoundTime / 1000);
            roundMin = roundSec / 60;
            roundSec %= 60;
            roundMillisecond = (int) (updatedRoundTime % 1000);
            setTimeText();
            customHandler.postDelayed(this, 0);
        }
    };

    public void setTimeText(){
        if(roundMin>0){
            tvRoundTime.setText("Round Time " + roundMin + ":" + roundSec + ":" + String.format("%03d", new Object[]{Integer.valueOf(roundMillisecond)}));
        }else{
            tvRoundTime.setText("Round Time " + roundSec + ":" + String.format("%03d", new Object[]{Integer.valueOf(roundMillisecond)}));
        }
        if(totalMin>0){
            tvTotalTime.setText("Total Time " + totalMin + ":" + totalSec + ":" + String.format("%03d", new Object[]{Integer.valueOf(totalMillisecond)}));
        }else{
            tvTotalTime.setText("Total Time " + totalSec + ":" + String.format("%03d", new Object[]{Integer.valueOf(totalMillisecond)}));
        }
    }

    public void setMovesText(){
        tvRoundMoves = findViewById(R.id.textViewRoundMoves);
        tvRoundMoves.setText("Round Moves: " + Integer.toString(roundMoves));
        tvTotalMoves = findViewById(R.id.textViewTotalMoves);
        tvTotalMoves.setText("Total Moves: " + Integer.toString(totalMoves));
    }

    public void setLevelText(){
        tvLevel = findViewById(R.id.textViewLevel);
        tvLevel.setText("Level " + Integer.toString(level));
    }

    public void startTimers(){
        startTotalTime = SystemClock.uptimeMillis();
        startRoundTime = SystemClock.uptimeMillis();
        timeSwapBuffRound = 0;
        customHandler.postDelayed(updateTimerThread, 0);
    }

    public void stopTimers(){
        timeSwapBuffTotal += timeInMillisecondsTotal;
        timeSwapBuffRound += timeInMillisecondsRound;
        customHandler.removeCallbacks(updateTimerThread);
    }
}
