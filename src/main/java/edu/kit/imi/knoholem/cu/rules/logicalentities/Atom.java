package edu.kit.imi.knoholem.cu.rules.logicalentities;

import java.util.ArrayList;
import java.util.List;

public abstract class Atom implements SWRLExpression {

	protected final List<Variable> variables;
	protected final int arity;
	protected final String name;

	protected Atom(String name, int arity) {
		this.name = name;
		this.arity = arity;
		this.variables = new ArrayList<Variable>(arity);
	}

	@Override
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

	@Override
	public String toString() {
		return new StringBuilder().append(getClass().getSimpleName()).append("{ ")
				.append("expression=\"").append(getExpression()).append("\" }").toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + arity;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((variables == null) ? 0 : variables.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Atom other = (Atom) obj;
		if (arity != other.arity)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (variables == null) {
			if (other.variables != null)
				return false;
		} else if (!variables.equals(other.variables))
			return false;
		return true;
	}

}
