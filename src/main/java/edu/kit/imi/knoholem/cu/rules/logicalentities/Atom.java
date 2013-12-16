package edu.kit.imi.knoholem.cu.rules.logicalentities;

import java.util.ArrayList;
import java.util.List;

public abstract class Atom {

	public static final String CONNECTIVE = ", ";

	protected final List<Variable> variables;
	protected final int arity;
	protected final String name;

	protected Atom(String name, int arity) {
		this.name = name;
		this.arity = arity;
		this.variables = new ArrayList<Variable>(arity);
	}

	public String getExpression() {
		if (variables.isEmpty()) {
			return name + "()";
		}
		StringBuilder expression = new StringBuilder();
		expression.append(name).append("(");
		for (int i = 0; i < variables.size() - 1; i++) {
			expression.append(variables.get(i).getExpression()).append(CONNECTIVE);
		}
		expression.append(variables.get(variables.size() - 1).getExpression());
		expression.append(")");
		return expression.toString();
	}

}
