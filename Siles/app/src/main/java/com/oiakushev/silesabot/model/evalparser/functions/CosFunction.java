package com.oiakushev.silesabot.model.evalparser.functions;


public class CosFunction extends MathFunction {
	public CosFunction() {
		super.setName("cos");
	}
	
	@Override
	public double eval(double arg) {
		return Math.cos(arg);
	}
}
