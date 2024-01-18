package com.oiakushev.silesabot.model.evalparser.functions;


public class ASinFunction extends MathFunction {
	public ASinFunction() {
		super.setName("asin");
	}

	@Override
	public double eval(double arg) {
		return Math.asin(arg);
	}
}
