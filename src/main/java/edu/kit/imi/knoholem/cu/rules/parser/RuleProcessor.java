package edu.kit.imi.knoholem.cu.rules.parser;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public interface RuleProcessor {

    public void onParse(SensitivityAnalysisRule rule);
    public void onError(String ruleLiteral, RuleParseError error);

}
