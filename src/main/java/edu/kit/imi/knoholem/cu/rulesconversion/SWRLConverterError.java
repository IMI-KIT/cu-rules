package edu.kit.imi.knoholem.cu.rulesconversion;

import edu.kit.imi.knoholem.cu.rules.parser.SensitivityAnalysisRule;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class SWRLConverterError extends Error {

    private final SensitivityAnalysisRule rule;

    public SWRLConverterError(SensitivityAnalysisRule rule, String message) {
        super(message);
        this.rule = rule;
    }

    public SWRLConverterError(SensitivityAnalysisRule rule, String message, Throwable cause) {
        super(message, cause);
        this.rule = rule;
    }

    public SWRLConverterError(SensitivityAnalysisRule rule, Throwable cause) {
        super(cause);
        this.rule = rule;
    }

    public SensitivityAnalysisRule getRule() {
        return rule;
    }

}
