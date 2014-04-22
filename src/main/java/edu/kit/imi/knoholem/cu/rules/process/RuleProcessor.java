package edu.kit.imi.knoholem.cu.rules.process;

import edu.kit.imi.knoholem.cu.rules.parser.RuleParseError;
import edu.kit.imi.knoholem.cu.rules.parser.SensitivityAnalysisRule;

/**
 * A rule parser target object.
 *
 * Receives the methods {@link edu.kit.imi.knoholem.cu.rules.process.RuleProcessor#onParse(edu.kit.imi.knoholem.cu.rules.parser.SensitivityAnalysisRule)}
 * and {@link edu.kit.imi.knoholem.cu.rules.process.RuleProcessor#onError(edu.kit.imi.knoholem.cu.rules.parser.RuleParseError)}}.
 *
 * @see edu.kit.imi.knoholem.cu.rules.process.RuleFileParser
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public interface RuleProcessor {

    /**
     * Processes the successfully parsed rule.
     *
     * @param rule the parsed rule.
     */
    public void onParse(SensitivityAnalysisRule rule);

    /**
     * Processes a parse error.
     *
     * @param error the error to process.
     */
    public void onError(RuleParseError error);

}