package chartbuilderfx.oleksandr.iakushev.com.calculatorfx.indexparser.functions;


public class ATanFunction extends MathFunction {
	public ATanFunction() {
		super.setName("atan");
	}
	
	@Override
	public double eval(double arg) {
		return Math.atan(arg);
	}
}
