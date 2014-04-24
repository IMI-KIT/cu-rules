package edu.kit.imi.knoholem.cu.rules.atoms.processing;

import edu.kit.imi.knoholem.cu.rules.atoms.Literal;
import edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rules.functions.Function;
import edu.kit.imi.knoholem.cu.rules.functions.Monad;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class RuleSubjects implements Function<SensitivityAnalysisRule, Monad<Literal>> {

    @Override
    public Monad<Literal> apply(SensitivityAnalysisRule input) {
        return new RulePredicates(input).getSubjectsAsMonad();
    }

}
