package edu.kit.imi.knoholem.cu.rules.ontology.rulesprocessing;

import edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rules.atoms.processing.PredicateMap;
import edu.kit.imi.knoholem.cu.rules.atoms.processing.PredicateMapEntry;
import edu.kit.imi.knoholem.cu.rules.functions.Function;
import edu.kit.imi.knoholem.cu.rules.ontology.OntologyContext;

/**
 * Identifies rules referencing individuals that don't have a class assignment.
 *
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class ClassifiedEntities implements Function<SensitivityAnalysisRule, Boolean> {

    private final OntologyContext ontology;

    public ClassifiedEntities(OntologyContext ontology) {
        this.ontology = ontology;
    }

    /**
     * Answers if every predicate object in the rule body has a class membership in the given ontology.
     *
     * @param input function input
     * @return <code>true</code>, if all referenced individuals have a class membership, <code>false</code> otherwise.
     */
    @Override
    public Boolean apply(SensitivityAnalysisRule input) {
        return individualsInAntecedentClassified(input) && individualsInConsequentClassified(input);
    }

    boolean individualsInAntecedentClassified(SensitivityAnalysisRule rule) {
        return allIndividualsExist(new PredicateMap(rule.getAntecedent()));
    }

    boolean individualsInConsequentClassified(SensitivityAnalysisRule rule) {
        return allIndividualsExist(new PredicateMap(rule.getConsequent()));
    }

    boolean allIndividualsExist(PredicateMap map) {
        boolean allExist = true;
        for (PredicateMapEntry predicate : map.byLeftOperand()) {
            String individualName = predicate.getClassifier().asString();
            allExist = allExist && ontology.getRepresentativeClass(individualName) != null;
        }
        return allExist;
    }

}
