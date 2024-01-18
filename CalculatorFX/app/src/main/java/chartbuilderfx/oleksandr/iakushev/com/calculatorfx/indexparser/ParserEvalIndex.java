package chartbuilderfx.oleksandr.iakushev.com.calculatorfx.indexparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import chartbuilderfx.oleksandr.iakushev.com.calculatorfx.indexparser.exceptions.BadNumberEvaluateException;
import chartbuilderfx.oleksandr.iakushev.com.calculatorfx.indexparser.exceptions.ParserEvaluateException;
import chartbuilderfx.oleksandr.iakushev.com.calculatorfx.indexparser.functions.MathFunction;

public class ParserEvalIndex extends ParserEval {
	
	public ParserEvalIndex() {
		super();
		this.setEquation("0.0");
	}
	
	public ParserEvalIndex(String eq) {
		super();
		this.setEquation(eq);
	}

	private abstract class ItemEquation {
		public abstract Object getValueItem();
	}
	
	private class ItemEquationOperator extends ItemEquation {
		private Character value;
		
		public ItemEquationOperator(char v) {
			value = new Character(v);
		}
		
		public Character getValueItem() {
			return value;
		}
		
		public String toString() {
			return value.toString();
		}
	}
	
	private class ItemEquationDigit extends ItemEquation {
		private Double value;
		
		public ItemEquationDigit(double v) {
			value = new Double(v);
		}
	
		public Double getValueItem() {
			return value;
		}
		
		public String toString() {
			return value.toString();
		}
	}
	
	private class ItemEquationLixema extends ItemEquation {
		private String value;
		
		public ItemEquationLixema(String v) {
			value = new String(v);
		}
		
		public String getValueItem() {
			return value;
		}
		
		public String toString() {
			return value.toString();
		}
	}
	
	private ArrayList<ItemEquation> StringEqToArrayEq(String eq) {
		ArrayList<ItemEquation> itemsEq = new ArrayList<ItemEquation> ();
		
		ItemEquation lastItem = null;
		String lixema = "";
		String digit = "";
		
		for (int i=0;i<eq.length();i++) {
			char ch = eq.charAt(i);
			
			switch (ch) {
				case '(':
				case ')':
				case '+':
				case '-':
				case '*':
				case '/':
				case '^':
					if (lixema.length() > 0) {
						lastItem = new ItemEquationLixema(lixema);
						itemsEq.add(lastItem);
						lixema = "";
					}
					
					if (digit.length() > 0) {
						lastItem = new ItemEquationDigit(Double.parseDouble(digit));
						itemsEq.add(lastItem);
						digit = "";
					}
					
					lastItem = new ItemEquationOperator(ch);
					itemsEq.add(lastItem);
				break;
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
				case '.':
					if (lixema.length() > 0) {
						lastItem = new ItemEquationLixema(lixema);
						itemsEq.add(lastItem);
						lixema = "";
					}
					
					digit = digit + ch;
				break;
				default:
					if (digit.length() > 0) {
						lastItem = new ItemEquationDigit(Double.parseDouble(digit));
						itemsEq.add(lastItem);
						digit = "";
					}
					
					if ((ch != ' ')&&(ch != '\t')) {
						lixema = lixema + ch;
					}
			}
		}
		
		if (lixema.length() > 0) {
			lastItem = new ItemEquationLixema(lixema);
			itemsEq.add(lastItem);
		}
		
		if (digit.length() > 0) {
			lastItem = new ItemEquationDigit(Double.parseDouble(digit));
			itemsEq.add(lastItem);
		}
		
		return itemsEq;
	}
	
	@Override
	public double evalEquation() throws ParserEvaluateException, BadNumberEvaluateException {
		ArrayList<ItemEquation> itemsEq = StringEqToArrayEq(this.getEquation());
		HashMap<String,Double> variables = this.getParamsEq();
		
		//set variables
		for (ItemEquation item : itemsEq) {
			int indexItem = itemsEq.indexOf(item);
			
			if (item instanceof ItemEquationLixema) {
				ItemEquationLixema itemLex = (ItemEquationLixema) item;
				Double evalVar = variables.get(itemLex.getValueItem());
				if (evalVar != null) {
					ItemEquationDigit itemDigit = new ItemEquationDigit(evalVar);
					itemsEq.set(indexItem, itemDigit);
				}
			}
		}
		
		try {
			//eval equation
			while (true) {
				//find end breaks
				int indexEndBreak = -1;
				int indexBegBreak = -1;
				for (ItemEquation itemEq : itemsEq) {
					if (itemEq instanceof ItemEquationOperator) {
						ItemEquationOperator itemOp = (ItemEquationOperator) itemEq;
						if (itemOp.getValueItem() == ')') {
							indexEndBreak = itemsEq.indexOf(itemEq);
							break;
						}
						else if (itemOp.getValueItem() == '(') {
							indexBegBreak = itemsEq.indexOf(itemEq);
						}
					}
				}
				
				if ((indexEndBreak < 0) && (indexBegBreak < 0)) {
					return evalEquationWBReaks(itemsEq);
				}
				
				if (indexBegBreak < 0) {
					throw new ParserEvaluateException("Not found (:" + ArrayEqToString(itemsEq));
				}
				
				if (indexEndBreak < 0) {
					throw new ParserEvaluateException("Not found ):" + ArrayEqToString(itemsEq));
				}
				
				ArrayList<ItemEquation> itemsEqWBreaks = new ArrayList<ItemEquation> ();
				for (int i=indexBegBreak+1;i<=indexEndBreak-1;i++) {
					itemsEqWBreaks.add(itemsEq.get(i));
				}
				
				double valueWBreaks = evalEquationWBReaks(itemsEqWBreaks);
				
				ItemEquationDigit itemDigit = new ItemEquationDigit(valueWBreaks);
				
				itemsEq.set(indexBegBreak, itemDigit);
				indexBegBreak++;
				
				while (true) {
					if (itemsEq.get(indexBegBreak) instanceof ItemEquationOperator) {
						ItemEquationOperator itemOp = (ItemEquationOperator) itemsEq.get(indexBegBreak);
						if (itemOp.getValueItem() == ')') {
							itemsEq.remove(indexBegBreak);
							break;
						}
						else {
							itemsEq.remove(indexBegBreak);
						}
					}
					else {
						itemsEq.remove(indexBegBreak);
					}
				}
			}
		} catch (IndexOutOfBoundsException e) {
			throw new ParserEvaluateException("Wrong expression:"+this.getEquation());
		}
	}

	private void checkNumber(double d) throws BadNumberEvaluateException {
		if (Double.isInfinite(d)) {
			throw new BadNumberEvaluateException("Infinite result!");
		}
	}
	
	private double evalEquationWBReaks(List<ItemEquation> eqWBreaks) throws ParserEvaluateException, BadNumberEvaluateException {
		ArrayList<MathFunction> functions = this.getFunctions();
		//eval equation
		boolean isEvalExec = true;
		while (isEvalExec) {
			String lastEqStr = ArrayEqToString(eqWBreaks);
					
			//find functions
			boolean isFoundedFunc = true;
					
			while (isFoundedFunc) {
				isFoundedFunc = false;
				for (ItemEquation item : eqWBreaks) {
					if (item instanceof ItemEquationLixema) {
						ItemEquationLixema itemLex = (ItemEquationLixema) item;
						int indexLex = eqWBreaks.indexOf(item);
								
						if ((indexLex<eqWBreaks.size()-1) && 
							(eqWBreaks.get(indexLex+1) instanceof ItemEquationDigit)) {
							double valueA = ((ItemEquationDigit) eqWBreaks.get(indexLex+1)).getValueItem();
							String funcStr = itemLex.getValueItem();
									
							for (MathFunction function : functions) {
								if (funcStr.equalsIgnoreCase(function.getName())) {
									double resultFA = function.eval(valueA);
									checkNumber(resultFA);
									ItemEquationDigit itemResFA = new ItemEquationDigit(resultFA);
											
									eqWBreaks.set(indexLex, itemResFA);
									eqWBreaks.remove(indexLex+1);
											
									isFoundedFunc = true;
									break;
								}
							}
									
							break;
						}
					}
				}
			}
					
			//find ^
			boolean isFoundPower = true;
			
			while (isFoundPower) {
				isFoundPower = false;
				for (ItemEquation item : eqWBreaks) {
					if (item instanceof ItemEquationOperator) {
						ItemEquationOperator itemOp = (ItemEquationOperator) item;
						int indexOp = eqWBreaks.indexOf(item);
						
						if ((itemOp.getValueItem() == '^') && (indexOp>0) && 
							(eqWBreaks.get(indexOp-1) instanceof ItemEquationDigit) 
							&& (eqWBreaks.get(indexOp+1) instanceof ItemEquationDigit)) {
							double valueA = ((ItemEquationDigit) eqWBreaks.get(indexOp-1)).getValueItem();
							double valueB = ((ItemEquationDigit) eqWBreaks.get(indexOp+1)).getValueItem();
							double resAB = Math.pow(valueA,valueB);
							checkNumber(resAB);
							
							ItemEquationDigit itemResAB = new ItemEquationDigit(resAB);
							
							eqWBreaks.set(indexOp-1, itemResAB);
							eqWBreaks.remove(indexOp);
							eqWBreaks.remove(indexOp);
							
							isFoundPower = true;
							break;
						}
					}
				}
			}
					
			//find * /
			boolean isFoundedMD = true;
			
			while (isFoundedMD) {
				isFoundedMD = false;
				for (ItemEquation item : eqWBreaks) {
					if (item instanceof ItemEquationOperator) {
						ItemEquationOperator itemOp = (ItemEquationOperator) item;
						int indexOp = eqWBreaks.indexOf(item);
						
						if ((itemOp.getValueItem() == '*') && (indexOp>0) && (indexOp<eqWBreaks.size()-1) &&
							(eqWBreaks.get(indexOp-1) instanceof ItemEquationDigit) 
							&& (eqWBreaks.get(indexOp+1) instanceof ItemEquationDigit)) {
							double valueA = ((ItemEquationDigit) eqWBreaks.get(indexOp-1)).getValueItem();
							double valueB = ((ItemEquationDigit) eqWBreaks.get(indexOp+1)).getValueItem();
							double resAB = valueA*valueB;
							
							ItemEquationDigit itemResAB = new ItemEquationDigit(resAB);
							
							eqWBreaks.set(indexOp-1, itemResAB);
							eqWBreaks.remove(indexOp);
							eqWBreaks.remove(indexOp);
							
							isFoundedMD = true;
							break;
						}
						
						if ((itemOp.getValueItem() == '/') && (indexOp>0) && (indexOp<eqWBreaks.size()-1) && 
							(eqWBreaks.get(indexOp-1) instanceof ItemEquationDigit) 
							&& (eqWBreaks.get(indexOp+1) instanceof ItemEquationDigit)) {
							double valueA = ((ItemEquationDigit) eqWBreaks.get(indexOp-1)).getValueItem();
							double valueB = ((ItemEquationDigit) eqWBreaks.get(indexOp+1)).getValueItem();
							double resAB = valueA/valueB;
							if (valueB == 0) {
								throw new BadNumberEvaluateException("Divine by zero!");
							}
							
							ItemEquationDigit itemResAB = new ItemEquationDigit(resAB);
							
							eqWBreaks.set(indexOp-1, itemResAB);
							eqWBreaks.remove(indexOp);
							eqWBreaks.remove(indexOp);
							
							isFoundedMD = true;
							break;
						}
					}
				}
			}
					
			//find + -
			boolean isFoundedMP = true;
			
			while (isFoundedMP) {
				isFoundedMP = false;
				for (ItemEquation item : eqWBreaks) {
					if (item instanceof ItemEquationOperator) {
						ItemEquationOperator itemOp = (ItemEquationOperator) item;
						int indexOp = eqWBreaks.indexOf(item);
						
						if ((itemOp.getValueItem() == '+') && (indexOp>0) && (indexOp<eqWBreaks.size()-1) && 
							(eqWBreaks.get(indexOp-1) instanceof ItemEquationDigit) 
							&& (eqWBreaks.get(indexOp+1) instanceof ItemEquationDigit)) {
							double valueA = ((ItemEquationDigit) eqWBreaks.get(indexOp-1)).getValueItem();
							double valueB = ((ItemEquationDigit) eqWBreaks.get(indexOp+1)).getValueItem();
							double resAB = valueA+valueB;
							
							ItemEquationDigit itemResAB = new ItemEquationDigit(resAB);
							
							eqWBreaks.set(indexOp-1, itemResAB);
							eqWBreaks.remove(indexOp);
							eqWBreaks.remove(indexOp);
							
							isFoundedMP = true;
							break;
						}
						
						if ((itemOp.getValueItem() == '-') && (indexOp>0) && (indexOp<eqWBreaks.size()-1) &&
							(eqWBreaks.get(indexOp-1) instanceof ItemEquationDigit) 
							&& (eqWBreaks.get(indexOp+1) instanceof ItemEquationDigit)) {
							double valueA = ((ItemEquationDigit) eqWBreaks.get(indexOp-1)).getValueItem();
							double valueB = ((ItemEquationDigit) eqWBreaks.get(indexOp+1)).getValueItem();
							double resAB = valueA-valueB;
							
							ItemEquationDigit itemResAB = new ItemEquationDigit(resAB);
							
							eqWBreaks.set(indexOp-1, itemResAB);
							eqWBreaks.remove(indexOp);
							eqWBreaks.remove(indexOp);
							
							isFoundedMP = true;
							break;
						}
					}
				}
			}
					
			isEvalExec = (eqWBreaks.size() > 1) && (!ArrayEqToString(eqWBreaks).equals(lastEqStr));
		}
		
		if ((eqWBreaks.size() == 2) && (eqWBreaks.get(0) instanceof ItemEquationOperator) &&
				(eqWBreaks.get(1) instanceof ItemEquationDigit)) {
			ItemEquationOperator itemOp = (ItemEquationOperator) eqWBreaks.get(0);
			if (itemOp.getValueItem() == '-') {
				double valueRes = (-1) * ((ItemEquationDigit) eqWBreaks.get(1)).getValueItem();
				eqWBreaks.remove(0);
				eqWBreaks.remove(0);
				
				eqWBreaks.add(new ItemEquationDigit(valueRes));
			}
		}
		
		if (eqWBreaks.size() > 1) {
			throw new ParserEvaluateException("Wrong expression:" + ArrayEqToString(eqWBreaks));
		}
		
		ItemEquation resItem = eqWBreaks.get(0);
		
		if (!(resItem instanceof ItemEquationDigit)) {
			throw new ParserEvaluateException("Bad result:" + ArrayEqToString(eqWBreaks));
		}
		
		ItemEquationDigit itemDigit = (ItemEquationDigit) resItem;
			
		return itemDigit.getValueItem();
	}
	
	private static String ArrayEqToString(List<ItemEquation> arrEq) {
		String res = "";
		
		for (ItemEquation item : arrEq) {
			res = res + item;
		}
		
		return res;
	}
}
