package com.oiakushev.silesabot.model.evalparser;

import com.oiakushev.silesabot.model.evalparser.exceptions.BadNumberEvaluateException;
import com.oiakushev.silesabot.model.evalparser.exceptions.ParserEvaluateException;
import com.oiakushev.silesabot.model.evalparser.functions.MathFunction;

import java.util.ArrayList;
import java.util.HashMap;



public abstract class ParserEval {
	private HashMap<String,Double> paramsEq;
	private ArrayList<MathFunction> functions;
	private String equation;
	
	public ParserEval() {
		paramsEq = new HashMap<String,Double>();
		functions = new ArrayList<MathFunction>();
	}
	
	protected HashMap<String,Double> getParamsEq() {
		return paramsEq;
	}
	
	protected ArrayList<MathFunction> getFunctions() {
		return functions;
	}
	
	public void addVariable(String nameVar, double evalVar)
	{
		paramsEq.put(nameVar, new Double(evalVar));
	}
	
	public void removeVariable(String nameVar) {
		paramsEq.remove(nameVar);
	}
	
	public void addFunction(MathFunction f) {
		functions.add(f);
	}
	
	public void removeFunction(MathFunction f) {
		functions.remove(f);
	}
	
	public void addFunctions(ArrayList<MathFunction> fs) {
		functions.addAll(fs);
	}
	
	public void removeFunctions(ArrayList<MathFunction> fs) {
		functions.removeAll(fs);
	}
	
	public String getEquation() {
		return equation;
	}

	public void setEquation(String equation) {
		this.equation = equation;
	}
	
	abstract public double evalEquation() throws ParserEvaluateException, BadNumberEvaluateException;
	
	@Override
	public String toString() {
		return this.getEquation();
	}
}
