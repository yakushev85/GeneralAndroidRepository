package com.oiakushev.silesabot.model.evalparser.functions;


public class SinFunction extends MathFunction {
	public SinFunction() {
		super.setName("sin");
	}

	@Override
	public double eval(double arg) {
		return Math.sin(arg);
	}
}
