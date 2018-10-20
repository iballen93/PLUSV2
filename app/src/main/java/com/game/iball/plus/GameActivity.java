package com.game.iball.plus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

import static java.lang.String.format;

public class GameActivity extends AppCompatActivity {

    private Handler customHandler = new Handler();
    private Integer[] mThumbIds;
    private GridView gvGameBoard ;
    private TextView tvLevel;
    private TextView tvRoundMoves;
    private TextView tvTotalMoves;
    private TextView tvRoundTime;
    private TextView tvTotalTime;

    private boolean firstClick = true;
    private int level = 1;
    private int gridSize;
    private int roundMoves = 0;
    private int totalMoves = 0;
    private int roundMin, roundSec, roundMillisecond;
    private int totalMin, totalSec, totalMillisecond;
    private long roundTimeStart = 0;
    private long totalTimeStart = 0;
    private long roundTimeUpdated = 0;
    private long totalTimeUpdated = 0;
    private long roundTimeSwapBuff = 0;
    private long totalTimeSwapBuff = 0;
    private long roundTimeInMilliseconds = 0;
    private long totalTimeInMilliseconds = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        cleanupView();
        generateUI();
        generateGrid();
        generateLevel();

        gvGameBoard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                firstClickActions();
                generatePlus(position);
                totalMoves++;
                roundMoves++;

                //~~~~~~~~WIN~~~~~~~~\\
                if (isWin()) {
                    stopTimers();
                    Toast.makeText(GameActivity.this, "You beat Level " + level + "!\n" + roundMoves + generateMoveText() + '\n'
                            + roundTimeUpdated / 1000.0 + " seconds", Toast.LENGTH_LONG).show();
                    level++;
                    generateLevel();
                }
                setLevelText();
                setMovesText();
            }
        });

        Button regenerateLevel = findViewById(R.id.buttonRegenerateLevel);
        regenerateLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateGrid();
                generateLevel();
            }
        });

        Button goHome = findViewById(R.id.buttonHome);
        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isWin() {
        for (int i = 0; i < gridSize * gridSize; i++) {
            if (mThumbIds[i] == R.drawable.pixel_red) {
                return false;
            }
        }
        return true;
    }

    private void firstClickActions() {
        if (firstClick) {
            firstClick = false;
            roundMoves = 0;
            startTimers();
        }
    }

    private void generateUI() {
        initializeTextviews();
        setLevelText();
        setTimeText();
        setMovesText();
        gridSize = Integer.parseInt(getIntent().getStringExtra("Pass Grid Size"));
        Toast.makeText(GameActivity.this, "Starting game with grid size " + gridSize, Toast.LENGTH_SHORT).show();
    }

    private void initializeTextviews() {
        tvLevel = findViewById(R.id.textViewLevel);
        tvRoundTime = findViewById(R.id.textViewRoundTime);
        tvTotalTime = findViewById(R.id.textViewTotalTime);
        tvRoundMoves = findViewById(R.id.textViewRoundMoves);
        tvTotalMoves = findViewById(R.id.textViewTotalMoves);
    }

    private void setLevelText() {
        tvLevel.setText(format("Level %s", Integer.toString(level)));
    }

    private void setTimeText() {
        if (roundMin > 0) {
            tvRoundTime.setText(format("Round Time %d:%d:%s", roundMin, roundSec, format("%03d", roundMillisecond)));
        } else {
            tvRoundTime.setText(format("Round Time %d:%s", roundSec, format("%03d", roundMillisecond)));
        }
        if (totalMin > 0) {
            tvTotalTime.setText(format("Total Time %d:%d:%s", totalMin, totalSec, format("%03d", totalMillisecond)));
        } else {
            tvTotalTime.setText(format("Total Time %d:%s", totalSec, format("%03d", totalMillisecond)));
        }
    }

    private void setMovesText() {
        tvRoundMoves.setText(format("Round Moves: %s", Integer.toString(roundMoves)));
        tvTotalMoves.setText(format("Total Moves: %s", Integer.toString(totalMoves)));
    }

    private String generateMoveText() {
        return roundMoves == 1 ? " move" : " moves";
    }

    private void generateGrid() {
        boolean black = true;
        mThumbIds = new Integer[gridSize * gridSize];
        for (int cell = 0; cell < gridSize * gridSize; cell++) {
            if (isEven(gridSize)) {
                if (black) {
                    mThumbIds[cell] = R.drawable.pixel_black;
                    black = false;
                } else {
                    mThumbIds[cell] = R.drawable.pixel_grey;
                    black = true;
                }
            } else if (isEven(cell)) {
                mThumbIds[cell] = R.drawable.pixel_black;
            } else {
                mThumbIds[cell] = R.drawable.pixel_grey;
            }
            if ((cell + 1) % gridSize == 0) {
                black = !black;
            }
        }
        gvGameBoard = findViewById(R.id.gridviewGameBoard);
        gvGameBoard.setAdapter(new ImageAdapter(this));
        gvGameBoard.setNumColumns(gridSize);
        gvGameBoard.setHorizontalSpacing(0);
        gvGameBoard.setVerticalSpacing(0);
        gvGameBoard.setPadding(12, 180, 10, 0);
    }

    private void generateLevel() {
        firstClick = true;
        int cell;
        Integer[] cells = new Integer[gridSize * gridSize];
        for (cell = 0; cell < cells.length; cell++) {
            cells[cell] = cell;
        }
        Collections.shuffle(Arrays.asList(cells));
        for (cell = 0; cell < level; cell++) {
            generatePlus(cells[cell]);
        }
    }

    private void generatePlus(int cell) {
        if (isLeftOpen(cell)) {
            toggleColor(cell - 1);
        }
        if (isRightOpen(cell)) {
            toggleColor(cell + 1);
        }
        if (isUpOpen(cell)) {
            toggleColor(cell - gridSize);
        }
        if (isDownOpen(cell)) {
            toggleColor(gridSize + cell);
        }
        ((GridView) findViewById(R.id.gridviewGameBoard)).setAdapter(new ImageAdapter(this));
    }

    private boolean isLeftOpen(int cell) {
        return cell % gridSize != 0;
    }

    private boolean isRightOpen(int cell) {
        return cell % gridSize != gridSize - 1;
    }

    private boolean isUpOpen(int cell) {
        return cell >= gridSize;
    }

    private boolean isDownOpen(int cell) {
        return cell < (gridSize * gridSize) - gridSize;
    }

    private void toggleColor(int cell) {
        if (mThumbIds[cell] != R.drawable.pixel_red) {
            mThumbIds[cell] = R.drawable.pixel_red;
        } else if (isBlack(cell)) {
            mThumbIds[cell] = R.drawable.pixel_black;
        } else if (isGrey(cell)) {
            mThumbIds[cell] = R.drawable.pixel_grey;
        }
        ((GridView) findViewById(R.id.gridviewGameBoard)).setAdapter(new ImageAdapter(this));
    }

    private boolean isBlack(int cell) {
        return isEven(gridSize) ? (isEven(cell / gridSize) && isEven(cell % gridSize)) || (isOdd(cell / gridSize) && isOdd(cell % gridSize)) : isEven(cell);
    }

    private boolean isGrey(int cell) {
        return isEven(gridSize) ? (isEven(cell / gridSize) && isOdd(cell % gridSize)) || (isOdd(cell / gridSize) && isEven(cell % gridSize)) : !isEven(cell);
    }

    private boolean isEven(int num) {
        return num % 2 == 0;
    }

    private boolean isOdd(int num) {
        return num % 2 == 1;
    }

    private int getSingleCellWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels / gridSize;
    }

    private class ImageAdapter extends BaseAdapter {

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
            totalTimeInMilliseconds = SystemClock.uptimeMillis() - totalTimeStart;
            roundTimeInMilliseconds = SystemClock.uptimeMillis() - roundTimeStart;
            totalTimeUpdated = totalTimeSwapBuff + totalTimeInMilliseconds;
            roundTimeUpdated = roundTimeSwapBuff + roundTimeInMilliseconds;
            totalSec = (int) (totalTimeUpdated / 1000);
            totalMin = totalSec / 60;
            totalSec %= 60;
            totalMillisecond = (int) (totalTimeUpdated % 1000);
            roundSec = (int) (roundTimeUpdated / 1000);
            roundMin = roundSec / 60;
            roundSec %= 60;
            roundMillisecond = (int) (roundTimeUpdated % 1000);
            setTimeText();
            customHandler.postDelayed(this, 0);
        }
    };

    private void startTimers() {
        totalTimeStart = SystemClock.uptimeMillis();
        roundTimeStart = SystemClock.uptimeMillis();
        roundTimeSwapBuff = 0;
        customHandler.postDelayed(updateTimerThread, 0);
    }

    private void stopTimers() {
        totalTimeSwapBuff += totalTimeInMilliseconds;
        roundTimeSwapBuff += roundTimeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);
    }

    private void cleanupView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(Integer.MIN_VALUE);
            window.setStatusBarColor(Color.parseColor("#002E3D"));
        }
    }
}