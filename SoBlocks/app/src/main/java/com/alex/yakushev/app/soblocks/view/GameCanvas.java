package com.alex.yakushev.app.soblocks.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.alex.yakushev.app.soblocks.model.DrawThread;
import com.alex.yakushev.app.soblocks.model.GameSpace;

import java.io.File;

/**
 * Created by Oleksandr on 22-Jan-17.
 */

public class GameCanvas extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "soblocks.GameCanvas";

    private GameSpace gameSpace;
    private DrawThread drawThread;
    private TextView textScore;
    private TextView textLevel;
    private int displayedScore, displayedLevel;
    private float bottomOffset;
    private File scoreFile;
    private DrawThread.DrawThreadListener drawThreadListener;
    private int previousAction;

    public GameCanvas(Context context) {
        super(context);
        initialization();
    }

    public GameCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialization();
    }

    public GameCanvas(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialization();
    }

    private void initialization() {
        this.getHolder().addCallback(this);
        gameSpace = new GameSpace();
        displayedScore = 0;
        displayedLevel = 1;
        previousAction = -1;
    }

    public GameSpace getGameSpace() {
        return gameSpace;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        drawThread = new DrawThread(surfaceHolder, gameSpace, drawThreadListener);
        drawThread.setScoreFile(scoreFile);
        drawThread.setRunFlag(true);
        drawThread.setBottomOffset(bottomOffset);
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        stopThread();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float tx = event.getX();
        float ty = event.getY();

        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_MOVE) {
            boolean isMoving = (action == MotionEvent.ACTION_MOVE) ||
                    (action == MotionEvent.ACTION_UP && previousAction == MotionEvent.ACTION_MOVE);
            int gameAction = drawThread.getMoveFromXY(tx, ty, isMoving);
            if (gameAction != -1) {
                drawThread.moveFigure(gameAction);
            }

            if (displayedScore != gameSpace.getLinesScore() && drawThread.isRunFlag()) {
                displayedScore = gameSpace.getLinesScore();
                displayedLevel = (displayedScore / DrawThread.LINES_PER_LEVEL) + 1;

                setDisplayedValues();
            }
        }

        previousAction = action;

        return true;
    }

    public void toggleThread() {
        drawThread.setRunFlag(!drawThread.isRunFlag());
    }

    public void stopThread() {
        boolean retry = true;
        drawThread.setRunFlag(false);
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
                Log.e(TAG, "Error while stopping thread ", e);
            }
        }
    }

    public DrawThread getDrawThread() {
        return drawThread;
    }

    public TextView getTextScore() {
        return textScore;
    }

    public void setTextScore(TextView textScore) {
        this.textScore = textScore;
    }

    public TextView getTextLevel() {
        return textLevel;
    }

    public void setTextLevel(TextView textLevel) {
        this.textLevel = textLevel;
    }

    private void setDisplayedValues() {
        String scoreFormat = Integer.toString(displayedScore);
        while (scoreFormat.length() < 4) scoreFormat = "0"+scoreFormat;
        String levelFormat = Integer.toString(displayedLevel);
        if (levelFormat.length() < 2) levelFormat = "0"+levelFormat;

        textLevel.setText("Level: "+levelFormat);
        textScore.setText("Score: "+scoreFormat);
    }


    public float getBottomOffset() {
        return bottomOffset;
    }

    public void setBottomOffset(float bottomOffset) {
        this.bottomOffset = bottomOffset;
    }


    public File getScoreFile() {
        return scoreFile;
    }

    public void setScoreFile(File scoreFile) {
        this.scoreFile = scoreFile;
    }

    public DrawThread.DrawThreadListener getDrawThreadListener() {
        return drawThreadListener;
    }

    public void setDrawThreadListener(DrawThread.DrawThreadListener drawThreadListener) {
        this.drawThreadListener = drawThreadListener;
    }
}
