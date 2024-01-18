package com.alex.yakushev.app.soblocks.model;

import com.alex.yakushev.app.soblocks.model.GameFigure;

import java.util.Random;

public class GameSpace {
	public static final int MOVE_FIGURE_LEFT = 0;
	public static final int MOVE_FIGURE_RIGHT = 1;
	public static final int MOVE_FIGURE_DOWN = 2;
	public static final int MOVE_FIGURE_TOPDOWN = 3;
	public static final int MOVE_FIGURE_ROTATE = 4;
	
	public static final int RANDOM_CONSTA = 45;
	public static final int RANDOM_CONSTB = 48;
	
	public static final int FIGURE_X0START = 6;
	public static final int FIGURE_Y0START = 0;
	
	public static final int SPACE_NX = 15;
	public static final int SPACE_NY = 25;
	
	private final int[][] spaceCub;
	private int curFigX0,curFigY0;
	private int linesScore;
	private final Random rnd;
	
	private GameFigure curFig, nextFig;
	
	public GameSpace() {
		rnd = new Random();
		spaceCub = new int[SPACE_NX][SPACE_NY];
		clearSpace();
		
		curFig = new GameFigure();
        int figureColor = MRandom(GameFigure.FIGURES_MAXCOLORS);
		int[] colorsFig = {figureColor,figureColor,figureColor,figureColor};
		curFig.generateFigure(MRandom(GameFigure.FIGURES_N), colorsFig);
		
		nextFig = new GameFigure();
        int figureNextColor = MRandom(GameFigure.FIGURES_MAXCOLORS);
		int[] colorsFigN = {figureNextColor,figureNextColor,figureNextColor,figureNextColor};
		nextFig.generateFigure(MRandom(GameFigure.FIGURES_N), colorsFigN);

		clearLinesScore();
		setCurrentFigureCoordinates();
	}
	
	private int MRandom(int rMax) {
		return (int) (rMax*rnd.nextDouble());
	}

	private void setCurrentFigureCoordinates() {
		curFigX0 = FIGURE_X0START;
		curFigY0 = FIGURE_Y0START;
	}
	
	public void genFigure()	{
		setCurrentFigureCoordinates();
		curFig = nextFig.copyTo();

		int figureColor = MRandom(GameFigure.FIGURES_MAXCOLORS);
		int[] colorsFig = {figureColor,figureColor,figureColor,figureColor};
		
		nextFig.generateFigure(MRandom(GameFigure.FIGURES_N),colorsFig);
	}
	
	private boolean isFigureRange() {
		for (int i=0;i<4;i++)
			if ((curFigX0+curFig.getXAt(i)<0)||(curFigY0+curFig.getYAt(i)<0)
					||(curFigX0+curFig.getXAt(i)>=SPACE_NX)||(curFigY0+curFig.getYAt(i)>=SPACE_NY)
					||(spaceCub[curFigX0+curFig.getXAt(i)][curFigY0+curFig.getYAt(i)]>=0)) return false;
		
		return true;
	}
	
	private void checkLines() {
		boolean priz = true;
		int nRow;
		
		while (priz) {
			nRow = -1;
			for (int iy=0;iy<SPACE_NY;iy++)	{
				priz = true;
				for (int ix=0;ix<SPACE_NX;ix++)
					if (spaceCub[ix][iy] < 0) priz = false;
				
				if (priz) nRow = iy;
			}
			
			if (nRow >= 0) {
				linesScore++;
				for (int iy=nRow-1;iy>=0;iy--)
					for (int ix=0;ix<SPACE_NX;ix++)
						spaceCub[ix][iy+1] = spaceCub[ix][iy];
				priz = true;
			}
		}
	}
	
	private void pasteFigure() {
		for (int i=0;i<4;i++)
			spaceCub[curFigX0+curFig.getXAt(i)][curFigY0+curFig.getYAt(i)] = curFig.getCAt(i);
				
		checkLines();
		genFigure();
	}
	
	public boolean isEnd() {
		int hTop = SPACE_NY;
		for (int ix=0;ix<SPACE_NX;ix++)
			for (int iy=0;iy<SPACE_NY;iy++)
				if ((hTop>iy)&&(spaceCub[ix][iy]>=0)) hTop = iy;
		
		return hTop<=5;
	}
	
	public void moveFigure(int typeMove) {
		switch (typeMove) {
		
		case MOVE_FIGURE_LEFT:
			curFigX0--;
			if (!isFigureRange()) curFigX0++;
			break;
			
		case MOVE_FIGURE_RIGHT:
			curFigX0++;
			if (!isFigureRange()) curFigX0--;
			break;
			
		case MOVE_FIGURE_DOWN:
			curFigY0++;
			if (!isFigureRange()) {
				curFigY0--;
				pasteFigure();
			}
			break;
			
		case MOVE_FIGURE_TOPDOWN:
			while (isFigureRange()) curFigY0++;
			curFigY0--;
			pasteFigure();
			break;
			
		case MOVE_FIGURE_ROTATE:
			curFig.rotateFigure();
			while (!isFigureRange()) curFig.rotateFigure();
			break;
		}
	}
	
	public void clearSpace() {
		for (int i=0;i<SPACE_NX;i++)
			for (int j=0;j<SPACE_NY;j++)
				spaceCub[i][j] = -1;
	}
	
	public void clearLinesScore() {
		linesScore = 0;
	}
	
	public int getLinesScore() {
		return linesScore;
	}
	
	public int getColorXY(int xx,int yy) {
		return spaceCub[xx][yy];
	}
	
	public GameFigure getCurrentFigure() {
		return curFig;
	}
	
	public int getCurrentFigureX0() {
		return curFigX0;
	}
	
	public int getCurrentFigureY0() {
		return curFigY0;
	}
	
	public GameFigure getNextFigure() {
		return nextFig;
	}
}

