package chartbuilderfx.oleksandr.iakushev.com.calculatorfx;

import android.annotation.SuppressLint;
import java.util.ArrayList;

public class ChartData {
	private double minXValue, maxXValue, minYValue, maxYValue;
	private ArrayList<ChartPointXY> pointsX0;
	
	public double getMinXValue() {
		return minXValue;
	}
	
	public void setMinXValue(double minXValue) {
		this.minXValue = minXValue;
	}
	
	public double getMaxXValue() {
		return maxXValue;
	}
	
	public void setMaxXValue(double maxXValue) {
		this.maxXValue = maxXValue;
	}

	public double getMinYValue() {
		return minYValue;
	}

	public void setMinYValue(double minYValue) {
		this.minYValue = minYValue;
	}

	public double getMaxYValue() {
		return maxYValue;
	}

	public void setMaxYValue(double maxYValue) {
		this.maxYValue = maxYValue;
	}
	
	public ArrayList<ChartPointXY> getPointsX0() {
		return pointsX0;
	}

	public void setPointsX0(ArrayList<ChartPointXY> pointsX0) {
		this.pointsX0 = pointsX0;
	}

	@SuppressLint("DefaultLocale") 
	@Override
	public String toString() {
		String res = String.format("X [%.6f:%.6f]\nY [%.6f:%.6f]\n", 
				minXValue, maxXValue, minYValue, maxYValue);
		
		for (ChartPointXY pointXY : pointsX0) {
			res += String.format("f(%.6f)=%.6f\n", pointXY.getPointX(), pointXY.getPointY());
		}
		
		return res;
	}

	@SuppressLint("DefaultLocale")
	public String toHtmlText() {
		String res = String.format("<p><i>Y<sub>min</sub> = %.6f</i>;\n" +
				"     <i>Y<sub>max</sub> = %.6f</i>;</p>\n", minYValue, maxYValue);

		for (ChartPointXY pointXY : pointsX0) {
			double resX0 = (Math.abs(pointXY.getPointX())<Math.pow(10.0,-6))?0:pointXY.getPointX();
			res += String.format("<p><i>f(%.6f) ~ 0</i></p>", resX0);
		}

		return res;
	}
}
