package edu.kit.imi.knoholem.cu.rules.logicalentities;

public class Individual implements Variable {

	private final String name;

	public Individual(String name) {
		this.name = name;
	}

	@Override
	public String getExpression() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Individual other = (Individual) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(getClass()).append("{")
				.append(" name=").append("\"").append(name).append("\"")
				.append(" }").toString();
	}

}
