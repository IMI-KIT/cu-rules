package edu.kit.imi.knoholem.cu.rules.atoms.processing;

import edu.kit.imi.knoholem.cu.rules.atoms.Literal;
import edu.kit.imi.knoholem.cu.rules.atoms.Predicate;
import edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rules.functions.Monad;
import edu.kit.imi.knoholem.cu.rules.functions.Monads;

import java.util.ArrayList;
import java.util.List;

/**
 * Queries the predicates from a rule body.
 *
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 * @see edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule
 */
public class RulePredicates {

    private final SensitivityAnalysisRule rule;

    public RulePredicates(SensitivityAnalysisRule rule) {
        this.rule = rule;
    }

    public Monad<Predicate> getPredicatesAsMonad() {
        return Monads.list(getPredicatesAsList());
    }

    public List<Predicate> getPredicatesAsList() {
        List<Predicate> predicateList = new ArrayList<Predicate>(rule.getAntecedent().size() + rule.getConsequent().size());
        predicateList.addAll(rule.getAntecedent());
        predicateList.addAll(rule.getConsequent());
        return predicateList;
    }

    public List<Literal> getSubjectsAsList() {
        List<PredicateMapEntry> mapEntries = new PredicateMap(getPredicatesAsList()).byLeftOperand();
        return getClassifiers(mapEntries);
    }

    public Monad<Literal> getSubjectsAsMonad() {
        return Monads.list(getSubjectsAsList());
    }

    public List<Literal> getObjectsAsList() {
        List<PredicateMapEntry> mapEntries = new PredicateMap(getPredicatesAsList()).byRightOperand();
        return getClassifiers(mapEntries);
    }

    public Monad<Literal> getObjectsAsMonad() {
        return Monads.list(getObjectsAsList());
    }

    private List<Literal> getClassifiers(List<PredicateMapEntry> predicateMapEntries) {
        List<Literal> result = new ArrayList<Literal>(predicateMapEntries.size());
        for (PredicateMapEntry entry : predicateMapEntries) {
            result.add(entry.getClassifier());
        }
        return result;
    }

}
