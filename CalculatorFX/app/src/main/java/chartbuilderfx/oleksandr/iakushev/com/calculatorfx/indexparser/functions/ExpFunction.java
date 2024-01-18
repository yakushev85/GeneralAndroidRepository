package chartbuilderfx.oleksandr.iakushev.com.calculatorfx.indexparser.functions;


public class ExpFunction extends MathFunction {
	public ExpFunction() {
		super.setName("exp");
	}
	
	@Override
	public double eval(double arg) {
		return Math.exp(arg);
	}
}
