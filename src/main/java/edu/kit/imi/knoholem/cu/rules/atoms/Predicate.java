package edu.kit.imi.knoholem.cu.rules.atoms;

/**
 * A predicate is an abstraction of a binary relation between two objects, as
 * defined by its {@link Operator}.
 *
 * @author <a href="mailto:kiril.tonev@kit.edu">Kiril Tonev</a>
 * @see Literal
 * @see SensitivityAnalysisRule
 */
public class Predicate {

    private String leftOperand;
    private final String rightOperand;
    private final Operator operator;

    public Predicate(String leftOperand, String operator, String rightOperand) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.operator = parseOperator(operator);
    }

    public Literal getLeftOperand() {
        return new Literal(leftOperand);
    }

    public Literal getRightOperand() {
        return new Literal(rightOperand);
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{ "
                + "leftOperand=" + leftOperand + " "
                + " operator=" + operator + " "
                + " rightOperand=" + rightOperand + " "
                + "}";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((operator == null) ? 0 : operator.hashCode());
        result = prime * result
                + ((leftOperand == null) ? 0 : leftOperand.hashCode());
        result = prime * result
                + ((rightOperand == null) ? 0 : rightOperand.hashCode());
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
        Predicate other = (Predicate) obj;
        if (operator != other.operator)
            return false;
        if (leftOperand == null) {
            if (other.leftOperand != null)
                return false;
        } else if (!leftOperand.equals(other.leftOperand))
            return false;
        if (rightOperand == null) {
            if (other.rightOperand != null)
                return false;
        } else if (!rightOperand.equals(other.rightOperand))
            return false;
        return true;
    }

    private Operator parseOperator(String operator) {
        if (operator.equals("<=")) {
            return Operator.LESS_THAN_OR_EQUAL;
        } else if (operator.equals(">=")) {
            return Operator.GREATER_THAN_OR_EQUAL;
        } else if (operator.equals(">")) {
            return Operator.GREATER_THAN;
        } else if (operator.equals("=")) {
            return Operator.EQUAL;
        } else if (operator.equals("<")) {
            return Operator.LESS_THAN;
        } else {
            throw new RuntimeException("Operator unknown: " + operator);
        }
    }

}
