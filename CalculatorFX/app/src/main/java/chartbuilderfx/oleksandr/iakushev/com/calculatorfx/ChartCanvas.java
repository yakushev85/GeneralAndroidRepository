package chartbuilderfx.oleksandr.iakushev.com.calculatorfx;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ChartCanvas extends View {
	private final int MAX_SIZE_MEASURE = 1000;
	private final double PER_DIV_X = 0.05;
	private final double DEFAULT_DIV_X = 1;
	private final double PER_DIV_Y = 0.05;
	private final double DEFAULT_DIV_Y = 1;
	private final double EPSILON_VALUE = 0.000001;
	private final int OFFSET_AXSLES = 6;
	
	private Paint paintAxles, paintChart;
	private ArrayList<ChartPointXY> pointsXY;
	private double maxXValue, maxYValue, minXValue, minYValue;
	
	public ChartCanvas(Context context) {
		super(context);
		initCanvas();
	}
	
	public ChartCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
		initCanvas();
	}
	
	public ChartCanvas(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initCanvas();
	}

	private void initCanvas() {
		paintAxles = new Paint();
		paintAxles.setColor(Color.BLUE);
		paintChart = new Paint();
		paintChart.setColor(Color.BLACK);
	}
	
	private int measure(int dimens)
	{
		int specMode = MeasureSpec.getMode(dimens);
		int specSize = MeasureSpec.getSize(dimens);
		
		if (specMode == MeasureSpec.UNSPECIFIED) return MAX_SIZE_MEASURE;
			else return specSize;
	}
	
	@Override
	protected void onMeasure(int widthMeasure,int heightMeasure)
	{
		int wM = measure(widthMeasure);
		int hM = measure(heightMeasure);
		
		setMeasuredDimension(wM,hM);
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		if ((pointsXY == null) || (pointsXY.isEmpty())) return;
		
		maxXValue = Double.MIN_VALUE;
		maxYValue = Double.MIN_VALUE;
		minXValue = Double.MAX_VALUE;
		minYValue = Double.MAX_VALUE;
		
		for (ChartPointXY pointXY : pointsXY) {
			if (pointXY.isPoint()) {
				if (pointXY.getPointX() > maxXValue) maxXValue = pointXY.getPointX();
				if (pointXY.getPointX() < minXValue) minXValue = pointXY.getPointX();
				if (pointXY.getPointY() > maxYValue) maxYValue = pointXY.getPointY();
				if (pointXY.getPointY() < minYValue) minYValue = pointXY.getPointY();
			}
		}
		
		double dX = maxXValue-minXValue;
		double dY = maxYValue-minYValue;
		double perDivX = (dX > EPSILON_VALUE)?PER_DIV_X*dX:DEFAULT_DIV_X;
		double perDivY = (dY > EPSILON_VALUE)?PER_DIV_Y*dY:DEFAULT_DIV_Y;
		
		minXValue -= perDivX;
		minYValue -= perDivY;
		maxXValue += perDivX;
		maxYValue += perDivY;
		
		Log.w(IdExtras.NAMETAG_LOGGER, "yMin:"+minYValue);
		Log.w(IdExtras.NAMETAG_LOGGER, "yMax:"+maxYValue);

		for (ChartPointXY pointXY : pointsXY) {
			if (pointXY.isPoint()) {
				canvas.drawPoint(convX(pointXY.getPointX()), 
						convY(pointXY.getPointY()), paintChart);
				//Log.w(IdExtras.NAMETAG_LOGGER, pointXY.getPointX()+":"+pointXY.getPointY());
			}
		}
		
		for (int i=0;i<pointsXY.size()-1;i++) {
			double xBeg = pointsXY.get(i).getPointX();
			double yBeg = pointsXY.get(i).getPointY();
			double xEnd = pointsXY.get(i+1).getPointX();
			double yEnd = pointsXY.get(i+1).getPointY();
			
			if (pointsXY.get(i).isPoint() && pointsXY.get(i+1).isPoint()) {
				canvas.drawLine(convX(xBeg), convY(yBeg), convX(xEnd), convY(yEnd), paintChart);
			}
		}
		
		if ((minXValue <= 0) && (0 <= maxXValue)) {
			canvas.drawLine(convX(0), convY(minYValue), convX(0), convY(maxYValue), paintAxles);
			canvas.drawLine(convX(0), convY(maxYValue), 
					convX(0)+OFFSET_AXSLES/2, convY(maxYValue)+OFFSET_AXSLES, paintAxles);
			canvas.drawLine(convX(0), convY(maxYValue), 
					convX(0)-OFFSET_AXSLES/2, convY(maxYValue)+OFFSET_AXSLES, paintAxles);
		}
		
		if ((minYValue <= 0) && (0 <= maxYValue)) {
			canvas.drawLine(convX(minXValue), convY(0), convX(maxXValue), convY(0), paintAxles);
			canvas.drawLine(convX(maxXValue), convY(0), 
					convX(maxXValue)-OFFSET_AXSLES, convY(0)-OFFSET_AXSLES/2, paintAxles);
			canvas.drawLine(convX(maxXValue), convY(0), 
					convX(maxXValue)-OFFSET_AXSLES, convY(0)+OFFSET_AXSLES/2, paintAxles);
		}
	}
	
	private float convX(double xx) {
		double newxx = getWidth()*(xx-minXValue)/(maxXValue-minXValue);
		return (float) newxx;
	}
	
	private float convY(double yy) {
		double newyy = getHeight()*(maxYValue-yy)/(maxYValue-minYValue);
		return (float) newyy;
	}
		
	public ArrayList<ChartPointXY> getPointsXY() {
		return pointsXY;
	}

	public void setPointsXY(ArrayList<ChartPointXY> pointsXY) {
		this.pointsXY = pointsXY;
		this.invalidate();
	}
}
