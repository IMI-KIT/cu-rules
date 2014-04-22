package edu.kit.imi.knoholem.cu.rules.parser;

/**
 * Indicates a parsing error due to erroneous parser configuration.
 *
 * @see edu.kit.imi.knoholem.cu.rules.parser.RuleParserConfiguration
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class ParserConfigurationError extends RuleParseError {

    public ParserConfigurationError(String ruleLiteral) {
        super(ruleLiteral);
    }

    public ParserConfigurationError(String ruleLiteral, String message) {
        super(ruleLiteral, message);
    }

    public ParserConfigurationError(String ruleLiteral, Throwable cause) {
        super(ruleLiteral, cause);
    }

    public ParserConfigurationError(String ruleLiteral, String message, Throwable cause) {
        super(ruleLiteral, message, cause);
    }

}
