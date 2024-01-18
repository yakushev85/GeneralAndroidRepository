package com.oiakushev.silesabot.model.evalparser.functions;


public class LnFunction extends MathFunction {
	public LnFunction() {
		super.setName("ln");
	}
	
	@Override
	public double eval(double arg) {
		return Math.log(arg);
	}
}
