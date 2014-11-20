package edu.kit.imi.knoholem.cu.rules.parser;

import edu.kit.imi.knoholem.cu.rules.atoms.Predicate;
import edu.kit.imi.knoholem.cu.rules.atoms.processing.PredicateMap;
import edu.kit.imi.knoholem.cu.rules.atoms.processing.PredicateMapEntry;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FalsePredicatesRecorder extends AlignedRuleParser {

    private final Map<String, Double> sensorValues;
    private final Map<String, Integer> warnings;
    private final Map<String, List<Predicate>> failedPredicates;

    public FalsePredicatesRecorder(RuleParserConfiguration configuration, Map<String, Double> sensorValues) {
        super(configuration, sensorValues.keySet());

        this.sensorValues = sensorValues;
        this.warnings = new HashMap<String, Integer>();
        this.failedPredicates = new HashMap<String, List<Predicate>>();
    }

    public Map<String, Integer> getWarnings() {
        return warnings;
    }

    @Override
    protected List<Predicate> collectPredicates(List<String> ruleBodyTokens) {
        List<Predicate> predicates = super.collectPredicates(ruleBodyTokens);
        for (PredicateMapEntry mapEntry : new PredicateMap(predicates).byLeftOperand()) {
            for (Predicate predicate : mapEntry.getPredicates()) {
                if (!evaluatePredicate(predicate)) {
                    System.err.println("False evaluation: " + predicate.toString());

                    recordFail(predicate);
                    incrementWarning(predicate.getLeftOperand().asString());
                }
            }
        }
        return predicates;
    }

    public Map<String, List<Predicate>> getFailedPredicates() {
        return failedPredicates;
    }

    private boolean recordFail(Predicate predicate) {
        String sensorName = predicate.getLeftOperand().asString();
        List<Predicate> recordedPredicates;
        if (failedPredicates.containsKey(sensorName)) {
            recordedPredicates = failedPredicates.get(sensorName);
        } else {
            recordedPredicates = new LinkedList<Predicate>();
            failedPredicates.put(sensorName, recordedPredicates);
        }
        return recordedPredicates.add(predicate);
    }

    protected void incrementWarning(String sensorName) {
        if (warnings.containsKey(sensorName)) {
            warnings.put(sensorName, warnings.get(sensorName) + 1);
        } else {
            warnings.put(sensorName, 1);
        }
    }

    protected boolean evaluatePredicate(Predicate predicate) {
        Double sensorValue = sensorValues.get(predicate.getLeftOperand().asString());
        Double rightOperand = predicate.getRightOperand().asDouble();
        switch (predicate.getOperator()) {
            case GREATER_THAN_OR_EQUAL:
                return sensorValue >= rightOperand;
            case LESS_THAN_OR_EQUAL:
                return sensorValue <= rightOperand;
            default:
                throw new RuntimeException("Operator not implementeed: " + predicate.getOperator().literal);
        }
    }
}
