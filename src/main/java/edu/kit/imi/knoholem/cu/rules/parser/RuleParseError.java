package edu.kit.imi.knoholem.cu.rules.parser;

/**
 * Indicates an error at the parsing of a rule literal.
 *
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class RuleParseError extends Error {

    private final String ruleLiteral;

    public RuleParseError(String ruleLiteral) {
        super("Error parsing literal: " + ruleLiteral);
        this.ruleLiteral = ruleLiteral;
    }

    public RuleParseError(String ruleLiteral, String message) {
        super(message);
        this.ruleLiteral = ruleLiteral;
    }

    public RuleParseError(String ruleLiteral, Throwable cause) {
        super(cause);
        this.ruleLiteral = ruleLiteral;
    }

    public RuleParseError(String ruleLiteral, String message, Throwable cause) {
        super(message, cause);
        this.ruleLiteral = ruleLiteral;
    }

    public String getRuleLiteral() {
        return ruleLiteral;
    }
}
