package chartbuilderfx.oleksandr.iakushev.com.calculatorfx;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.util.Log;

import chartbuilderfx.oleksandr.iakushev.com.calculatorfx.indexparser.ParserEvalIndex;
import chartbuilderfx.oleksandr.iakushev.com.calculatorfx.indexparser.exceptions.BadNumberEvaluateException;
import chartbuilderfx.oleksandr.iakushev.com.calculatorfx.indexparser.exceptions.ParserEvaluateException;
import chartbuilderfx.oleksandr.iakushev.com.calculatorfx.indexparser.functions.BasicFunctions;

public class ProviderData {
	private static final String NAME_VARIABLEX = "x";
	private static final String NAME_VARIABLEPI = "pi";
	private static final String NAME_VARIABLEE = "e";
	private static final double EPSILON_SOLVE = 0.0000001;
	private static final int SCALE_VALUEXY = 6;
    private static final int MAX_SOLVE_POINTS = 10;
    private static final int MAX_SOLVE_ITERATIONS = 20;
	
	private String function;
	private double pointXBegin, pointXEnd;
	private long nPoints;
	
	public ProviderData() {
		function = "0.0";
	}
	
	private double roundValueXY(double dd) {
		return new BigDecimal(dd).setScale(SCALE_VALUEXY, RoundingMode.CEILING).doubleValue();
	}
	
	public ArrayList<ChartPointXY> getArrayPointXY() throws ParserEvaluateException {
		ArrayList<ChartPointXY> res = new ArrayList<ChartPointXY> ();
		
		ParserEvalIndex parserFunc = new ParserEvalIndex();
		parserFunc.setEquation(function);
		parserFunc.addFunctions(BasicFunctions.getInstance());
		parserFunc.addVariable(NAME_VARIABLEPI, Math.PI);
		parserFunc.addVariable(NAME_VARIABLEE, Math.E);
		
		double dDiv = (pointXEnd-pointXBegin)/nPoints;
		for (double x = pointXBegin; x <= pointXEnd; x += dDiv) {
			parserFunc.addVariable(NAME_VARIABLEX, roundValueXY(x));
			
			ChartPointXY pointXY = new ChartPointXY();
			pointXY.setPointX(x);
			try {
				double y = parserFunc.evalEquation();
				pointXY.setPointY(y);
				res.add(pointXY);
			} 
			catch (BadNumberEvaluateException e) {
				pointXY.setPoint(false);
			}
		}
		
		return res;
	}
	
	public ChartData getChartData() throws ParserEvaluateException {
		ChartData chartData = new ChartData();
		ArrayList<ChartPointXY> pointsX0 = new ArrayList<ChartPointXY> ();
		chartData.setPointsX0(pointsX0);
		
		ParserEvalIndex parserFunc = new ParserEvalIndex();
		parserFunc.setEquation(function);
		parserFunc.addFunctions(BasicFunctions.getInstance());
		parserFunc.addVariable(NAME_VARIABLEPI, Math.PI);
		parserFunc.addVariable(NAME_VARIABLEE, Math.E);
		
		chartData.setMinXValue(pointXBegin);
		chartData.setMaxXValue(pointXEnd);
		parserFunc.addVariable(NAME_VARIABLEX, pointXBegin);

		chartData.setMinYValue(Double.MAX_VALUE);
		chartData.setMaxYValue(Double.MIN_VALUE);

        int solve_points = 0;
		double dDiv = (pointXEnd-pointXBegin)/nPoints;
		for (double x = pointXBegin; x <= pointXEnd; x += dDiv) {
			parserFunc.addVariable(NAME_VARIABLEX, x);
			try {
				double y = parserFunc.evalEquation();
				
				if (chartData.getMaxYValue() < y) chartData.setMaxYValue(y);
				
				if (chartData.getMinYValue() > y) chartData.setMinYValue(y);
				
				double xNext = x + dDiv;
				parserFunc.addVariable(NAME_VARIABLEX, xNext);
				double yNext = parserFunc.evalEquation();
	
				if (Math.abs(y) <= EPSILON_SOLVE) {
					ChartPointXY pointXY = new ChartPointXY();
					pointXY.setPointX(x);
					pointXY.setPointY(0.0);
					pointsX0.add(pointXY);
				} else if (y*yNext < 0 && solve_points <= MAX_SOLVE_POINTS) {
                    double x0 = x;
                    double y0 = y;
                    double x1 = xNext;
                    double y1 = yNext;
                    double xAvg = (x+xNext)/2;
                    parserFunc.addVariable(NAME_VARIABLEX, xAvg);
                    double yAvg = parserFunc.evalEquation();
                    int solve_iteration = MAX_SOLVE_ITERATIONS;

                    while (Math.abs(yAvg) > EPSILON_SOLVE && solve_iteration > 0) {
                        if (y0*yAvg < 0) {
                            x1 = xAvg;
                            y1 = yAvg;
                            xAvg = (x0+xAvg)/2;
                        } else if (yAvg*y1 < 0) {
                            x0 = xAvg;
                            y0 = yAvg;
                            xAvg = (xAvg+x1)/2;
                        }

                        parserFunc.addVariable(NAME_VARIABLEX, xAvg);
                        yAvg = parserFunc.evalEquation();
                        solve_iteration--;
                    }

                    if (Math.abs(yAvg) <= EPSILON_SOLVE) {
                        ChartPointXY pointXY = new ChartPointXY();
                        pointXY.setPointX(xAvg);
                        pointXY.setPointY(0.0);
                        pointsX0.add(pointXY);
                        solve_points++;
                    }
				}
			}
			catch (BadNumberEvaluateException e) {
				Log.w(IdExtras.NAMETAG_LOGGER, e.toString());
			}
		}
		
		return chartData;
	}
	
	public void checkFunction() throws ParserEvaluateException {
		ParserEvalIndex parserFunc = new ParserEvalIndex();
		parserFunc.setEquation(function);
		parserFunc.addFunctions(BasicFunctions.getInstance());
		parserFunc.addVariable(NAME_VARIABLEPI, Math.PI);
		parserFunc.addVariable(NAME_VARIABLEE, Math.E);
		parserFunc.addVariable(NAME_VARIABLEX, Math.random());
		try {
			parserFunc.evalEquation();
		} catch (BadNumberEvaluateException e) {
			Log.w(IdExtras.NAMETAG_LOGGER, e.toString());
		}
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public double getPointXBegin() {
		return pointXBegin;
	}

	public void setPointXBegin(double pointXBegin) {
		this.pointXBegin = pointXBegin;
	}

	public double getPointXEnd() {
		return pointXEnd;
	}

	public void setPointXEnd(double pointXEnd) {
		this.pointXEnd = pointXEnd;
	}

	public long getnPoints() {
		return nPoints;
	}

	public void setnPoints(long nPoints) {
		this.nPoints = nPoints;
	}
	
	@SuppressLint("DefaultLocale")
	public static boolean isFunction(String expr) {
		return expr.toLowerCase().indexOf('x') >= 0;
	}
	
	public static String getExpressionValue(String expr) throws ParserEvaluateException, BadNumberEvaluateException {
		ParserEvalIndex parserExpr = new ParserEvalIndex();
		parserExpr.setEquation(expr);
		parserExpr.addFunctions(BasicFunctions.getInstance());
		parserExpr.addVariable(NAME_VARIABLEPI, Math.PI);
		parserExpr.addVariable(NAME_VARIABLEE, Math.E);
        try {
            String resDouble = String.valueOf(parserExpr.evalEquation());
            return resDouble.replaceAll("E", "*10^");
        } catch (NumberFormatException e) {
            throw new BadNumberEvaluateException(e.getMessage());
        }
	}
}
