package edu.kit.imi.knoholem.cu.rules.logicalentities;

public class ClassAtom extends Atom {

    public ClassAtom(String className, Variable variable) {
        super(className, 1);
        variables.add(variable);
    }

    public Variable getOperand() {
        return variables.get(0);
    }

}
