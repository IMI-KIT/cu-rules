package edu.kit.imi.knoholem.cu.rulesconversion;

import edu.kit.imi.knoholem.cu.rules.parser.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rules.process.processors.Function;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class RuleConverter implements Function<SensitivityAnalysisRule, SWRLRule> {

    private final SWRLConverter converter;

    public RuleConverter(SWRLConverterConfiguration configuration) {
        this.converter = new SWRLConverter(configuration);
    }

    @Override
    public SWRLRule apply(SensitivityAnalysisRule input) {
        return converter.convertRule(input);
    }

}
