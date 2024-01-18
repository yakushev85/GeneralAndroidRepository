package chartbuilderfx.oleksandr.iakushev.com.calculatorfx.indexparser.functions;


public class SqrtFunction extends MathFunction {
	public SqrtFunction() {
		super.setName("sqrt");
	}
	
	@Override 
	public double eval(double arg) {
		return Math.pow(arg, 0.5);
	}
}
