package chartbuilderfx.oleksandr.iakushev.com.calculatorfx.indexparser.functions;


public class ACosFunction extends MathFunction {
	public ACosFunction() {
		super.setName("acos");
	}
	
	@Override
	public double eval(double arg) {
		return Math.acos(arg);
	}
}
