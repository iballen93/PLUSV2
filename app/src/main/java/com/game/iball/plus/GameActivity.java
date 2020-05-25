package com.game.iball.plus;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;
import java.util.Random;

import static java.lang.String.format;

public class GameActivity extends AppCompatActivity {
    private static final int BLACK = Color.BLACK;
    private static final int GRAY = 0xFF111111;

    private Button[][] gameBoard;

    private Handler customHandler = new Handler();
    private TextView tvLevel;
    private TextView tvRoundMoves;
    private TextView tvTotalMoves;
    private TextView tvRoundTime;
    private TextView tvTotalTime;
    private TextView tvScore;

    private boolean firstClick = true;
    private int level = 1;
    private int gridSize;
    private double score = 0.0;
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
        buildGameBoard();
        generateLevel();

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                final int finalRow = row;
                final int finalCol = col;
                gameBoard[row][col].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firstClickActions();
                        generatePlus(finalRow, finalCol);
                        totalMoves++;
                        roundMoves++;
                        setMovesText();

                        //~~~~~~~~WIN~~~~~~~~\\
                        if (isWin()) {
                            stopTimers();
                            updateScore();
                            setScoreText();
                            String movesPlural = roundMoves == 1 ? " move" : " moves";
                            Toast.makeText(GameActivity.this, "You beat Level " + level + "!\n" + roundMoves + movesPlural + '\n'
                                    + roundTimeUpdated / 1000.0 + " seconds", Toast.LENGTH_LONG).show();
                            level++;
                            generateLevel();
                        }
                        setLevelText();
                    }
                });
            }
        }

        Button regenerateLevel = findViewById(R.id.buttonRegenerateLevel);
        regenerateLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetGameBoard();
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

    // TODO make the score get bigger as you play not smaller
    private void updateScore() {
        if (level == 1) {
            if (roundMoves == 1 && isWin()) {
                score = 1;
            } else {
                score -= 1;
            }
        } else {
            if (roundTimeUpdated != 0 && roundMoves != 0 && roundMoves != 1) {
                double roundScore = (1.0 / (roundTimeUpdated / 1000.0));
                double movesScore = (1.0 / (roundMoves));
                score += (roundScore * movesScore * 100.0);
            }
        }
    }

    private void buildGameBoard() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels * .96);

        RelativeLayout relativeLayout = findViewById(R.id.gameBoardLayout);
        relativeLayout.setMinimumWidth(width);
        relativeLayout.setMinimumHeight(width);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width / gridSize, width / gridSize);
        layoutParams.setMarginStart((displayMetrics.widthPixels - width) / 2);

        gameBoard = new Button[gridSize][gridSize];
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                gameBoard[row][col] = new Button(this);
                gameBoard[row][col].setX((float) (width / gridSize) * col);
                gameBoard[row][col].setY((float) (width / gridSize) * row);
                gameBoard[row][col].setLayoutParams(layoutParams);
                setButtonColorDefault(gameBoard[row][col], row, col);
                relativeLayout.addView(gameBoard[row][col], layoutParams);
            }
        }
    }

    private void generateLevel() {
        firstClick = true;
        for (int i = 1; i <= level; i++) {
            Random random = new Random();
            generatePlus(random.nextInt(gridSize), random.nextInt(gridSize));
        }
    }

    private void generatePlus(int row, int col) {
        if (col - 1 >= 0) {
            toggleButtonColor(gameBoard[row][col - 1], row, col - 1); //left
        }
        if (col + 1 < gridSize) {
            toggleButtonColor(gameBoard[row][col + 1], row, col + 1); //right
        }
        if (row - 1 >= 0) {
            toggleButtonColor(gameBoard[row - 1][col], row - 1, col); //up
        }
        if (row + 1 < gridSize) {
            toggleButtonColor(gameBoard[row + 1][col], row + 1, col); //down
        }
    }

    private void toggleButtonColor(Button button, int row, int col) {
        int buttonColor = getButtonColor(button);
        if (buttonColor == Color.RED) {
            setButtonColorDefault(button, row, col);
        } else {
            button.setBackgroundColor(Color.RED);
        }
    }

    private int getButtonColor(Button button) {
        ColorDrawable buttonBackground = (ColorDrawable) button.getBackground();
        return buttonBackground.getColor();
    }

    private void setButtonColorDefault(Button button, int row, int col) {
        if (isEven(row)) {
            button.setBackgroundColor(isEven(col) ? BLACK : GRAY);
        } else {
            button.setBackgroundColor(isEven(col) ? GRAY : BLACK);
        }
    }

    private void resetGameBoard() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                setButtonColorDefault(gameBoard[row][col], row, col);
            }
        }
    }

    private void firstClickActions() {
        if (firstClick) {
            firstClick = false;
            roundMoves = 0;
            startTimers();
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

    private void generateUI() {
        initializeTextviews();
        setLevelText();
        setTimeText();
        setMovesText();
        gridSize = Integer.parseInt(getIntent().getStringExtra("Pass Grid Size"));
        Toast.makeText(GameActivity.this, "Starting game with grid size " + gridSize, Toast.LENGTH_SHORT).show();
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

    private void initializeTextviews() {
        tvLevel = findViewById(R.id.textViewLevel);
        tvRoundTime = findViewById(R.id.textViewRoundTime);
        tvTotalTime = findViewById(R.id.textViewTotalTime);
        tvRoundMoves = findViewById(R.id.textViewRoundMoves);
        tvTotalMoves = findViewById(R.id.textViewTotalMoves);
        tvScore = findViewById(R.id.textViewScore);
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

    private void setScoreText() {
        tvScore.setText(format("Score %s", new DecimalFormat("#,###.00").format(score)));
    }

    private boolean isWin() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (getButtonColor(gameBoard[row][col]) == Color.RED) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isEven(int i) {
        return i % 2 == 0;
    }
}