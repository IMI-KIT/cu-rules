package edu.kit.imi.knoholem.cu.rules.logicalentities;

public class PropertyAtom extends Atom {

	public PropertyAtom(String propertyName, Variable left, Variable right) {
		super(propertyName, 2);
		variables.add(left);
		variables.add(right);
	}

}
