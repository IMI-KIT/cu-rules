package edu.kit.imi.knoholem.cu.rules.logicalentities;

public class PropertyAtom extends Atom {

    public PropertyAtom(String propertyName, Variable left, Variable right) {
        super(propertyName, 2);
        variables.add(left);
        variables.add(right);
    }

    public Variable getLeftOperand() {
        return variables.get(0);
    }

    public Variable getRightOperand() {
        return variables.get(1);
    }

}
