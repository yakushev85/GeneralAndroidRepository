package com.oiakushev.silesabot.model.evalparser.functions;


public class ExpFunction extends MathFunction {
	public ExpFunction() {
		super.setName("exp");
	}
	
	@Override
	public double eval(double arg) {
		return Math.exp(arg);
	}
}
