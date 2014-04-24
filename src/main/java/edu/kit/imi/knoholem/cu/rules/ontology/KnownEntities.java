package edu.kit.imi.knoholem.cu.rules.ontology;

import edu.kit.imi.knoholem.cu.rules.parser.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rules.process.PredicateMap;
import edu.kit.imi.knoholem.cu.rules.process.PredicateMapEntry;
import edu.kit.imi.knoholem.cu.rules.process.processors.Function;

/**
 * A predicate that identifies the rules that are not contained in the signature of a given {@link edu.kit.imi.knoholem.cu.rules.ontology.OntologyContext}.
 *
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class KnownEntities implements Function<SensitivityAnalysisRule, Boolean> {

    private final OntologyContext ontology;

    public KnownEntities(OntologyContext ontology) {
        this.ontology = ontology;
    }

    /**
     * Answers if every predicate object in the rule is contained in the given ontology.
     *
     * @param input function input
     * @return <code>true</code>, if all individuals referenced in the rule could be found in the signature of theontology, <code>false</code> otherwise.
     */
    @Override
    public Boolean apply(SensitivityAnalysisRule input) {
        return individualsInAntecedentExist(input) && individualsInConsequentExist(input);
    }

    boolean individualsInAntecedentExist(SensitivityAnalysisRule rule) {
        return allIndividualsExist(new PredicateMap(rule.getAntecedent()));
    }

    boolean individualsInConsequentExist(SensitivityAnalysisRule rule) {
        return allIndividualsExist(new PredicateMap(rule.getConsequent()));
    }

    boolean allIndividualsExist(PredicateMap map) {
        boolean allExist = true;
        for (PredicateMapEntry predicate : map.byLeftOperand()) {
            String individualName = predicate.getClassifier().asString();
            allExist = allExist && ontology.containsIndividual(individualName);
        }
        return allExist;
    }

}
