package edu.kit.imi.knoholem.cu.rules.parser;

import edu.kit.imi.knoholem.cu.rules.atoms.Predicate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvaluatingParser extends AlignedRuleParser {

    private int firedRules;

    private final Map<String, Double> sensorValues;
    private final Map<String, Integer> warnings;
    private final Map<String, List<Predicate>> failedPredicates;

    public EvaluatingParser(RuleParserConfiguration configuration, Map<String, Double> sensorValues) {
        super(configuration, sensorValues.keySet());
        this.sensorValues = sensorValues;
        this.warnings = new HashMap<String, Integer>();
        this.failedPredicates = new HashMap<String, List<Predicate>>();
    }

    @Override
    protected List<Predicate> collectPredicates(List<String> ruleBodyTokens) {
        List<Predicate> predicates = super.collectPredicates(ruleBodyTokens);

        boolean evaluation = true;
        for (Predicate predicate : predicates) {
            evaluation = evaluation && evaluatePredicate(predicate);
        }
        if (evaluation) {
            firedRules = firedRules + 1;
        }

        return predicates;
    }

    public int getFiredRules() {
        return firedRules;
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
