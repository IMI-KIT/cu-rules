package edu.kit.imi.knoholem.cu.rules.swrlentities;

public interface SWRLExpression {

	public static final String THEN = " -> ";
	public static final String CONNECTIVE = ", ";

    public static final String SWRL_LESS_THAN = "lessThan";
    public static final String SWRL_LESS_THAN_OR_EQUAL = "lessThanOrEqual";
    public static final String SWRL_GREATER_THAN = "greaterThan";
    public static final String SWRL_GREATER_THAN_OR_EQUAL = "greaterThanOrEqual";
    public static final String SWRL_EQUAL = "equal";

	public String getExpression();

}
