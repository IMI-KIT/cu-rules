package edu.kit.imi.knoholem.cu.rules.logicalentities;

public class Value extends Variable {

	private final String value;

	public Value(String name) {
		this.value = name;
	}

	@Override
	public String getExpression() {
		return value;
	}

}
