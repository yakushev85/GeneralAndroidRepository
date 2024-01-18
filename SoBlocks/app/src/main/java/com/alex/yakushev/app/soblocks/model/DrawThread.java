package com.alex.yakushev.app.soblocks.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Oleksandr on 29-Jan-17.
 */

public class DrawThread extends Thread {
    private static final String TAG = "soblocks.DrawThread";

    public static final int MAX_TIME_PAUSE = 500;
    public static final int DT_TIME_PAUSE = 10;
    public static final int LINES_PER_LEVEL = 20;
    public static final int CUB_OFFSET = 2;
    public static final int CUB_INNER_OFFSET = 10;
    public static final int INVERSE_COLOR_CUB_TOP = 0xFFEFEFEF;
    public static final int INVERSE_COLOR_CUB_LEFT = 0xFFDFDFDF;
    public static final int INVERSE_COLOR_CUB_RIGHT = 0xFFDFDFDF;
    public static final int INVERSE_COLOR_CUB_BOTTOM = 0xFFAFAFAF;

    private boolean runFlag;
    private final SurfaceHolder surfaceHolder;
    private final GameSpace gameSpace;
    private final Paint paintCub, paintCubNextFigure, paintGameBorder;
    private final Rect rectCub;
    private boolean isNeedToReDraw;
    private int sizeCubX, sizeCubY, sizeCub, offsetX, offsetY;
    private int maxCanvasWidth, maxCanvasHeight;
    private float bottomOffset;
    private File scoreFile;
    private final Path polyPath;
    private final DrawThreadListener listener;

    public DrawThread(SurfaceHolder surfaceHolder, GameSpace gameSpace, DrawThreadListener listener) {
        runFlag = false;
        this.surfaceHolder = surfaceHolder;
        this.gameSpace = gameSpace;
        this.listener = listener;

        polyPath = new Path();

        paintCub = new Paint();
        paintCub.setStrokeWidth(1);
        paintCub.setStyle(Paint.Style.FILL_AND_STROKE);
        paintCub.setAlpha(100);

        paintCubNextFigure = new Paint();
        paintCubNextFigure.setStrokeWidth(1);
        paintCubNextFigure.setStyle(Paint.Style.FILL_AND_STROKE);

        paintGameBorder = new Paint();
        paintGameBorder.setStrokeWidth(2);
        paintGameBorder.setStyle(Paint.Style.FILL);
        paintGameBorder.setColor(Color.BLACK);

        rectCub = new Rect();
        isNeedToReDraw = true;
    }

    public void moveFigure(int typeOfAction) {
        gameSpace.moveFigure(typeOfAction);
        isNeedToReDraw = true;
    }

    public boolean isRunFlag() {
        return runFlag;
    }

    public void setRunFlag(boolean runFlag) {
        this.runFlag = runFlag;
    }

    @Override
    public void run() {
        Log.i(TAG, "Started.");
        Canvas canvas = null;
        long prev_time = System.currentTimeMillis();
        long now = prev_time;

        while (runFlag) {
            int timePause = MAX_TIME_PAUSE-DT_TIME_PAUSE*(gameSpace.getLinesScore() / LINES_PER_LEVEL);

            if (timePause > 0) {
                now = System.currentTimeMillis();

                if (now-prev_time>timePause) {
                    gameSpace.moveFigure(GameSpace.MOVE_FIGURE_DOWN);
                    isNeedToReDraw = true;
                }
            } else {
                isNeedToReDraw = true;
            }

            if (!isNeedToReDraw) continue;

            try {
                prev_time = System.currentTimeMillis();
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    canvas.drawColor(Color.WHITE);
                    maxCanvasWidth = canvas.getWidth();
                    maxCanvasHeight = canvas.getHeight()-Math.round(bottomOffset);

                    sizeCubX =  maxCanvasWidth / GameSpace.SPACE_NX;
                    sizeCubY =  maxCanvasHeight / GameSpace.SPACE_NY;
                    sizeCub = Math.min(sizeCubX, sizeCubY);
                    offsetX = (maxCanvasWidth-GameSpace.SPACE_NX*sizeCub)/2;
                    offsetY = (maxCanvasHeight-GameSpace.SPACE_NY*sizeCub)/2;

                    //draw game space
                    canvas.drawLine(offsetX-1, offsetY,
                            offsetX-1, GameSpace.SPACE_NY*sizeCub+offsetY, paintGameBorder);
                    canvas.drawLine(offsetX-1, GameSpace.SPACE_NY*sizeCub+offsetY,
                            GameSpace.SPACE_NX*sizeCub+offsetX+1, GameSpace.SPACE_NY*sizeCub+offsetY,
                            paintGameBorder);
                    canvas.drawLine(GameSpace.SPACE_NX*sizeCub+offsetX+1, offsetY,
                            GameSpace.SPACE_NX*sizeCub+offsetX+1, GameSpace.SPACE_NY*sizeCub+offsetY,
                            paintGameBorder);
                    for (int ix=0;ix<GameSpace.SPACE_NX;ix++) {
                        for (int iy = 0; iy < GameSpace.SPACE_NY; iy++) {
                            if (gameSpace.getColorXY(ix, iy) < GameFigure.FIGURES_MAXCOLORS && gameSpace.getColorXY(ix, iy) >= 0) {
                                int curColor = decodeColor(gameSpace.getColorXY(ix, iy));
                                int x = ix * sizeCub + offsetX;
                                int y = iy * sizeCub + offsetY;

                                paintCub.setColor(curColor);

                                drawCub(x,y, canvas, paintCub);
                            }
                        }
                    }

                    //draw figure
                    GameFigure fg = gameSpace.getCurrentFigure();
                    int mintYfg = GameSpace.SPACE_NY+1;
                    for (int i=0;i<4;i++) {
                        int internalX = gameSpace.getCurrentFigureX0()+fg.getXAt(i);
                        int internalY = gameSpace.getCurrentFigureY0()+fg.getYAt(i);
                        if (mintYfg > internalY)
                            mintYfg = internalY;

                        int curColor = decodeColor(fg.getCAt(i));
                        int x = internalX*sizeCub + offsetX;
                        int y = internalY*sizeCub + offsetY;

                        paintCub.setColor(curColor);

                        drawCub(x,y, canvas, paintCub);
                    }

                    //draw next figure
                    if (mintYfg > 3) {
                        GameFigure nextFigure = gameSpace.getNextFigure();
                        for (int i = 0; i < 4; i++) {
                            int curColor = decodeColor(nextFigure.getCAt(i));
                            int x = (GameSpace.FIGURE_X0START + nextFigure.getXAt(i)) * sizeCub + offsetX;
                            int y = (nextFigure.getYAt(i)) * sizeCub + offsetY;

                            paintCubNextFigure.setColor(curColor);
                            paintCubNextFigure.setAlpha(60);

                            drawCub(x,y, canvas, paintCubNextFigure);
                        }
                    }
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }

                isNeedToReDraw = false;
            }

            if (gameSpace.isEnd()) {
                runFlag = false;

                Date curDate = new Date(System.currentTimeMillis());
                long score = gameSpace.getLinesScore();
                ScoreStorage scoreStorage = new ScoreStorage(scoreFile);
                try {
                    scoreStorage.storeScore(curDate, score);
                } catch (IOException e) {
                    Log.e(TAG, "Error when storing score in DrawThread ", e);
                }

                listener.onFinish();

                return;
            }
        }

        Log.i(TAG, "Stopped.");
    }

    private void drawCub(int x, int y, Canvas canvas, Paint paint) {
        int color = paint.getColor();

        int[] outX = {x + CUB_OFFSET, x + sizeCub - CUB_OFFSET, x + CUB_OFFSET, x + sizeCub - CUB_OFFSET};
        int[] outY = {y + CUB_OFFSET, y + CUB_OFFSET, y + sizeCub - CUB_OFFSET, y + sizeCub - CUB_OFFSET};

        int[] inX = {outX[0]+CUB_INNER_OFFSET,outX[1]-CUB_INNER_OFFSET,outX[2]+CUB_INNER_OFFSET,outX[3]-CUB_INNER_OFFSET};
        int[] inY = {outY[0]+CUB_INNER_OFFSET,outY[1]+CUB_INNER_OFFSET,outY[2]-CUB_INNER_OFFSET,outY[3]-CUB_INNER_OFFSET};

        // top
        polyPath.reset();

        polyPath.moveTo(outX[0], outY[0]);
        polyPath.lineTo(outX[1], outY[1]);
        polyPath.lineTo(inX[1], inY[1]);
        polyPath.lineTo(inX[0], inY[0]);
        polyPath.lineTo(outX[0], outY[0]);

        paint.setColor(color & INVERSE_COLOR_CUB_TOP);

        canvas.drawPath(polyPath, paint);

        // left
        polyPath.reset();

        polyPath.moveTo(outX[0], outY[0]);
        polyPath.lineTo(inX[0], inY[0]);
        polyPath.lineTo(inX[2], inY[2]);
        polyPath.lineTo(outX[2], outY[2]);
        polyPath.lineTo(outX[0], outY[0]);

        paint.setColor(color & INVERSE_COLOR_CUB_LEFT);

        canvas.drawPath(polyPath, paint);

        // right
        polyPath.reset();

        polyPath.moveTo(inX[1], inY[1]);
        polyPath.lineTo(outX[1], outY[1]);
        polyPath.lineTo(outX[3], outY[3]);
        polyPath.lineTo(inX[3], inY[3]);
        polyPath.lineTo(inX[1], inY[1]);

        paint.setColor(color & INVERSE_COLOR_CUB_RIGHT);

        canvas.drawPath(polyPath, paint);

        // bottom
        polyPath.reset();

        polyPath.moveTo(inX[2], inY[2]);
        polyPath.lineTo(inX[3], inY[3]);
        polyPath.lineTo(outX[3], outY[3]);
        polyPath.lineTo(outX[2], outY[2]);
        polyPath.lineTo(inX[2], inY[2]);

        paint.setColor(color & INVERSE_COLOR_CUB_BOTTOM);

        canvas.drawPath(polyPath, paint);

        // center
        paint.setColor(color);
        rectCub.set(inX[0], inY[0], inX[3], inY[3]);
        canvas.drawRect(rectCub, paint);
    }

    public void pause() {
        synchronized (surfaceHolder) {
            runFlag = false;
        }
    }

    public int getMoveFromXY(float tx, float ty, boolean isMoving) {
        if (gameSpace == null || !runFlag) return -1;

        GameFigure figure = gameSpace.getCurrentFigure();
        int maxX = 0;
        int maxY = 0;
        int minX = GameSpace.SPACE_NX + 1;
        int x0 = gameSpace.getCurrentFigureX0();
        int y0 = gameSpace.getCurrentFigureY0();

        for (int i=0;i<4;i++) {
            if (x0+figure.getXAt(i) > maxX) maxX = x0+figure.getXAt(i);
            if (x0+figure.getXAt(i) < minX) minX = x0+figure.getXAt(i);
            if (y0+figure.getYAt(i) > maxY) maxY = y0+figure.getYAt(i);
        }

        float minTX = minX*sizeCub + offsetX;
        float maxTX = (maxX+1)*sizeCub + offsetX;
        float maxTy = (maxY+1)*sizeCub + offsetY;

        if (!isMoving && minTX <= tx && tx <= maxTX && maxTy < ty) return GameSpace.MOVE_FIGURE_TOPDOWN;
        if (!isMoving && minTX <= tx && tx <= maxTX) return GameSpace.MOVE_FIGURE_ROTATE;
        if (maxTX < tx) return GameSpace.MOVE_FIGURE_RIGHT;
        if (minTX > tx) return GameSpace.MOVE_FIGURE_LEFT;

        return -1;
    }

    private static int decodeColor(int cc) {
        switch (cc)	{
            case 0:return Color.argb(255, 240, 0, 0);
            case 1:return Color.argb(255, 0, 240, 0);
            case 2:return Color.argb(255, 0, 60, 240);
            case 3:return Color.argb(255, 240, 0, 240);
            case 4:return Color.argb(255, 0, 240, 240);
            case 5:return Color.argb(255, 240, 240, 0);
            default:return Color.argb(255, 195, 195, 195);
        }
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

    public interface DrawThreadListener {
        void onFinish();
    }
}
