package chartbuilderfx.oleksandr.iakushev.com.calculatorfx;

public class ChartPointXY {
	private double pointX, pointY;
	private boolean isPoint;
	
	public ChartPointXY() {
		setPoint(true);
	}
	
	public double getPointX() {
		return pointX;
	}
	
	public void setPointX(double pointX) {
		this.pointX = pointX;
	}

	public double getPointY() {
		return pointY;
	}

	public void setPointY(double pointY) {
		this.pointY = pointY;
	}

	public boolean isPoint() {
		return isPoint;
	}

	public void setPoint(boolean isPoint) {
		this.isPoint = isPoint;
	}	
}
