package edu.kit.imi.knoholem.cu.rules.parser.processing;

import edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rules.parser.RuleParseError;

/**
 * A rule parser target object.
 *
 * Receives the methods {@link RuleProcessor#onParse(edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule)}
 * and {@link RuleProcessor#onError(edu.kit.imi.knoholem.cu.rules.parser.RuleParseError)}}.
 *
 * @see RuleFileParser
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public interface RuleProcessor {

    /**
     * Processes the successfully parsed rule.
     *
     * @param rule the parsed rule.
     */
    public RuleProcessorResponse onParse(SensitivityAnalysisRule rule);

    /**
     * Processes a parse error.
     *
     * @param error the error to process.
     */
    public RuleProcessorResponse onError(RuleParseError error);

}