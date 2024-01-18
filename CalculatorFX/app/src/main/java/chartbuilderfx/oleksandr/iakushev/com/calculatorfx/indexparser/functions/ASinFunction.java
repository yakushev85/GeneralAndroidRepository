package chartbuilderfx.oleksandr.iakushev.com.calculatorfx.indexparser.functions;


public class ASinFunction extends MathFunction {
	public ASinFunction() {
		super.setName("asin");
	}

	@Override
	public double eval(double arg) {
		return Math.asin(arg);
	}
}
