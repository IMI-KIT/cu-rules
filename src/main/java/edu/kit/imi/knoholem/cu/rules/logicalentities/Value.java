package edu.kit.imi.knoholem.cu.rules.logicalentities;

public class Value implements Variable {

	private final String value;

	public Value(String name) {
		this.value = name;
	}

	@Override
	public String getExpression() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		Value other = (Value) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(getClass().getSimpleName()).append("{ value=")
				.append("\"").append(value).append("\" ").append("}").toString();
	}

}
