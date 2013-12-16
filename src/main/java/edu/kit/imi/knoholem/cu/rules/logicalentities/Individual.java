package edu.kit.imi.knoholem.cu.rules.logicalentities;

public class Individual extends Variable {

	private final String name;

	public Individual(String name) {
		this.name = name;
	}

	@Override
	public String getExpression() {
		return name;
	}

}
