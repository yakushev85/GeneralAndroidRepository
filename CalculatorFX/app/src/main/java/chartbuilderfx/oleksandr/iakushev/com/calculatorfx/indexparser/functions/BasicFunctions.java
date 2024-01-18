package chartbuilderfx.oleksandr.iakushev.com.calculatorfx.indexparser.functions;

import java.util.ArrayList;

public class BasicFunctions {
	private static ArrayList<MathFunction> basicFunctions = null;

	public static ArrayList<MathFunction> getInstance() {
		if (basicFunctions == null) {
			basicFunctions = new ArrayList<MathFunction> ();
			basicFunctions.add(new SinFunction());
			basicFunctions.add(new CosFunction());
			basicFunctions.add(new TanFunction());
			basicFunctions.add(new ASinFunction());
			basicFunctions.add(new ACosFunction());
			basicFunctions.add(new ATanFunction());
			basicFunctions.add(new SqrtFunction());
			basicFunctions.add(new LnFunction());
			basicFunctions.add(new ExpFunction());
		}
		
		return basicFunctions;
	}
}
