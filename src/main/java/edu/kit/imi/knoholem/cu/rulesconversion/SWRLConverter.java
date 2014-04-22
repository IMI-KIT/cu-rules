package edu.kit.imi.knoholem.cu.rulesconversion;

import edu.kit.imi.knoholem.cu.rules.logicalentities.*;
import edu.kit.imi.knoholem.cu.rules.parser.Operator;
import edu.kit.imi.knoholem.cu.rules.parser.Predicate;
import edu.kit.imi.knoholem.cu.rules.parser.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rules.process.PredicateMap;
import edu.kit.imi.knoholem.cu.rules.process.PredicateMapEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SWRLConverter {

    private SWRLConverterConfiguration configuration;

    public SWRLConverter(SWRLConverterConfiguration configuration) {
        this.configuration = configuration;
    }

    public SWRLRule convertRule(SensitivityAnalysisRule rule) {
        Unknowns unknowns = new Unknowns();
        List<Atom> antecedent = collectAntecedent(rule.getAntecedent(), unknowns);
        List<Atom> consequent = collectConsequent(rule.getConsequent(), unknowns);
        SWRLRule swrlRule = new SWRLRule();
        swrlRule.setAntecedent(antecedent);
        swrlRule.setConsequent(consequent);
        swrlRule.setMetadata(rule.getMetadata());
        return swrlRule;
    }

    private List<Atom> collectAntecedent(List<Predicate> inputAntecedent, Unknowns unknowns) {
        List<Atom> result = new ArrayList<Atom>();
        // classify the predicates
        // convert each predicate and return a list
        for (PredicateMapEntry entry : new PredicateMap(inputAntecedent).byLeftOperand()) {
            if (entry.isSingular()) {
                result.addAll(convertSensorValuePredicate(entry.getFirstPredicate(), unknowns));
            } else {
                result.addAll(convertSensorValuePredicates(entry, unknowns));
            }
        }
        return result;
    }

    private Collection<? extends Atom> convertSensorValuePredicates(PredicateMapEntry predicates, Unknowns unknowns) {
        List<Atom> atoms = new LinkedList<Atom>();

        Individual individual = new Individual(predicates.getClassifier().asString());
        Unknown unknown = unknowns.nextUnknown();

        atoms.add(new ClassAtom(configuration.sensorClass(), individual));
        atoms.add(new PropertyAtom(configuration.sensorValueProperty(), individual, unknown));
        for (Predicate predicate : predicates.getPredicates()) {
            Value value = new Value(predicate.getRightOperand().asString());
            atoms.add(new PropertyAtom(builtIn(predicate.getOperator()), unknown, value));
        }

        return atoms;
    }

    private List<Atom> collectConsequent(List<Predicate> inputConsequent, Unknowns unknowns) {
        List<Atom> result = new ArrayList<Atom>();
        for (Predicate predicate : inputConsequent) {
            result.addAll(convertConsequentPredicate(predicate, unknowns));
        }
        return result;
    }

    private List<Atom> convertConsequentPredicate(Predicate predicate, Unknowns unknowns) {
        List<Atom> result = new ArrayList<Atom>();

        Individual individual = new Individual(predicate.getLeftOperand().asString());
        Value value = new Value(predicate.getRightOperand().asString());

        result.add(new PropertyAtom(configuration.sensorValueProperty(), individual, value));

        return result;
    }

    private List<Atom> convertSensorValuePredicate(Predicate predicate, Unknowns unknowns) {
        List<Atom> result = new LinkedList<Atom>();

        Individual individual = new Individual(predicate.getLeftOperand().asString());
        Value value = new Value(predicate.getRightOperand().asString());
        Unknown unknown = unknowns.nextUnknown();

        result.add(new ClassAtom(configuration.sensorClass(), individual));
        result.add(new PropertyAtom(configuration.sensorValueProperty(), individual, unknown));
        result.add(new PropertyAtom(builtIn(predicate.getOperator()), unknown, value));
        return result;
    }

    private String builtIn(Operator operator) {
        switch (operator) {
            case GREATER_THAN_OR_EQUAL:
                return "greaterThanOrEqual";
            case GREATER_THAN:
                return "greaterThan";
            case LESS_THAN_OR_EQUAL:
                return "lessThanOrEqual";
            case LESS_THAN:
                return "lessThan";
            case EQUAL:
                return "equal";
            default:
                throw new RuntimeException();
        }
    }

}
