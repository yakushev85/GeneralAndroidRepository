package com.oiakushev.silesabot.model.datamodel.reaction;


import com.oiakushev.silesabot.model.evalparser.ParserEval;
import com.oiakushev.silesabot.model.evalparser.ParserEvalIndex;
import com.oiakushev.silesabot.model.evalparser.exceptions.BadNumberEvaluateException;
import com.oiakushev.silesabot.model.evalparser.exceptions.ParserEvaluateException;
import com.oiakushev.silesabot.model.evalparser.functions.BasicFunctions;

public class MathCustomReaction extends CustomReaction {
	private static final String TXT_ERRORPARSER = "Синтаксически неправильное выражение. ";
	private static final String TXT_ERRORBADNUMBER = "Неправильное числовое значение. ";

	public MathCustomReaction(Reaction creaction) {
		super(creaction);
	}

	@Override
	public String getCustomAnswer(String msg) {
		String equation = this.getSubValue(msg);

		if (equation.endsWith("!") || equation.endsWith(".") || equation.endsWith("?")) {
			equation = equation.substring(0, equation.length()-1);
		}

		ParserEval parserEval = new ParserEvalIndex(equation);
		parserEval.addFunctions(BasicFunctions.getInstance());
		
		double result = 0.0;
		
		try {
			result = parserEval.evalEquation();
		} catch (ParserEvaluateException e) {
			return TXT_ERRORPARSER + e.getMessage();
		} catch (BadNumberEvaluateException e) {
			return TXT_ERRORBADNUMBER + e.getMessage();
		}
		
		return this.customReaction.getReaction() + " " + equation + " = " + result;
	}

}
