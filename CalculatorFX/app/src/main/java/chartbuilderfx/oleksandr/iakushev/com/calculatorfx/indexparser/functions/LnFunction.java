package chartbuilderfx.oleksandr.iakushev.com.calculatorfx.indexparser.functions;


public class LnFunction extends MathFunction {
	public LnFunction() {
		super.setName("ln");
	}
	
	@Override
	public double eval(double arg) {
		return Math.log(arg);
	}
}
