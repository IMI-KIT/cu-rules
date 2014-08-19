package edu.kit.imi.knoholem.cu.rules.rulesconversion;

import edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rules.functions.Function;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class RuleConverter implements Function<SensitivityAnalysisRule, SWRLRule> {

    private final SWRLConverter converter;

    public RuleConverter(OntologySWRLConverterConfiguration configuration) {
        this.converter = new SWRLConverter(configuration);
    }

    @Override
    public SWRLRule apply(SensitivityAnalysisRule input) {
        return converter.convertRule(input);
    }

}
