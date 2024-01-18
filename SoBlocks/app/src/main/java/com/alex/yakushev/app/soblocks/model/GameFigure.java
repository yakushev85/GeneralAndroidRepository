package com.alex.yakushev.app.soblocks.model;

public class GameFigure {
	static public final int FIGURES_N = 19;
	static public final int FIGURES_MAXCOLORS = 6;
	static public final int[][] FIGURES_X = {
			{0,1,1,2},
			{1,0,1,0},
			
			{1,2,0,1},
			{0,0,1,1},
			
			{1,0,1,2},
			{0,0,1,0},
			{0,1,2,1},
			{1,0,1,1},
			
			{0,1,0,1},
			
			{0,1,2,3},
			{0,0,0,0},
			
			{0,0,1,2},
			{0,1,0,0},
			{0,1,2,2},
			{1,1,1,0},
			
			{2,0,1,2},
			{0,0,0,1},
			{0,1,2,0},
			{0,1,1,1},

			{1,0,2,1},

			{1,1,0,2},
			{0,1,2,0},
			{0,2,1,1},
			{0,1,2,2}
	};
	static public final int[][] FIGURES_Y = {
			{0,0,1,1},
			{0,1,1,2},
			
			{0,0,1,1},
			{0,1,1,2},
			
			{0,1,1,1},
			{0,1,1,2},
			{0,0,0,1},
			{0,1,1,2},
			
			{0,0,1,1},
			
			{0,0,0,0},
			{0,1,2,3},
			
			{0,1,1,1},
			{0,0,1,2},
			{0,0,0,1},
			{0,1,2,2},
			
			{0,1,1,1},
			{0,1,2,2},
			{0,0,0,1},
			{0,0,1,2},

			{0,1,1,2},

			{0,1,2,2},
			{0,1,1,2},
			{0,0,1,2},
			{1,1,0,2}
	};
	
	private int[] figureCubX;
	private int[] figureCubY;
	private int[] figureCubC;
	private int figureType;
	
	GameFigure() {
		figureCubX = new int[4];
		figureCubY = new int[4];
		figureCubC = new int[4];
	}
	
	public void generateFigure(int typeFigure,int[] colorFigure) {
		figureCubX = FIGURES_X[typeFigure];
		figureCubY = FIGURES_Y[typeFigure];
		figureCubC = colorFigure;
		figureType = typeFigure;
	}
	
	public void rotateFigure() {
		switch (figureType)	{
			case 0:figureType = 1;break;
			case 1:figureType = 0;break;
			
			case 2:figureType = 3;break;
			case 3:figureType = 2;break;
			
			case 4:figureType = 5;break;
			case 5:figureType = 6;break;
			case 6:figureType = 7;break;
			case 7:figureType = 4;break;
			
			case 8:figureType = 8;break;
			
			case 9:figureType = 10;break;
			case 10:figureType = 9;break;
			
			case 11:figureType = 12;break;
			case 12:figureType = 13;break;
			case 13:figureType = 14;break;
			case 14:figureType = 11;break;
			
			case 15:figureType = 16;break;
			case 16:figureType = 17;break;
			case 17:figureType = 18;break;
			case 18:figureType = 15;break;

			case 19:figureType = 19;break;

			case 20:figureType = 21;break;
			case 21:figureType = 22;break;
			case 22:figureType = 23;break;
			case 23:figureType = 20;break;
		}
		
		this.generateFigure(figureType, figureCubC);
	}
	
	public int getXAt(int index) {
		return figureCubX[index];
	}
	
	public int getYAt(int index) {
		return figureCubY[index];
	}
	
	public int getCAt(int index) {
		return figureCubC[index];
	}
	
	public int getTypeFigure() {
		return figureType;
	}
	
	public GameFigure copyTo() {
		GameFigure res = new GameFigure();
		res.generateFigure(figureType, figureCubC);
		return res;
	}
}
