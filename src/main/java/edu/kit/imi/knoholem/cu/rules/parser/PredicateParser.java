package edu.kit.imi.knoholem.cu.rules.parser;

class PredicateParser {

	private final String literal;
	private final int indexOfOperator;
	

	PredicateParser(String literal) {
		this.literal = literal.trim();
		this.indexOfOperator = indexOfOperator(this.literal);
	}
	
		
	public String getLeftLiteral() {
		return literal.substring(0, indexOfOperator).trim();
	}

	
	public String getOperator() {
		StringBuilder operator = new StringBuilder();
		if (indexOfOperator == -1) {
			throw new RuntimeException("Invalid Literal. No operator in predicate " + literal);
		}
		operator.append(literal.charAt(indexOfOperator));
		if (literal.charAt(indexOfOperator + 1) == '=') {
			operator.append(literal.charAt(indexOfOperator + 1));
		}
		return operator.toString();
	}

	
	public String getRightLiteral() {
		return literal.substring(indexOfOperator + getOperator().length()).trim();
	}

	
	@Override
	public String toString() {
		return new StringBuilder().append(getClass().getSimpleName()).append("{")
				.append("literal=\"").append(literal).append("\"")
				.append("}").toString();
	}

	
	private int indexOfOperator(String literal) {
		for (int i = 0; i < literal.length(); i++) {
			if (isPartOfOperator(literal, i)) {
				return i;
			}
		}
		return -1;
	}

	
	private boolean isPartOfOperator(String literal, int index) {
		return literal.charAt(index) == '>' || literal.charAt(index) == '<' || literal.charAt(index) == '=';
	}

}
