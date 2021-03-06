package edu.kit.imi.knoholem.cu.rules.parser;

import edu.kit.imi.knoholem.cu.rules.atoms.Predicate;
import edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class AlignedRuleParser extends RuleParser {

    private final Collection<String> sensorsInDatabase;
    private boolean silent;

    public AlignedRuleParser(RuleParserConfiguration configuration, Collection<String> sensorsInDatabase) {
        super(configuration);
        this.sensorsInDatabase = sensorsInDatabase;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    @Override
    public SensitivityAnalysisRule parseRule(String ruleLiteral) {
        try {
            RuleLiteral parsedLiteral = new RuleLiteral(ruleLiteral, configuration);

            SensitivityAnalysisRule rule = new SensitivityAnalysisRule();
            rule.setAntecedent(collectPredicates(parsedLiteral.getAntecedentAtoms()));
            rule.setConsequent(super.collectPredicates(parsedLiteral.getConsequentAtoms()));
            rule.setMetadata(parseRuleMetadata(parsedLiteral));

            return rule;
        } catch (Throwable t) {
            throw new RuleParseError(ruleLiteral, t);
        }
    }

    @Override
    protected List<Predicate> collectPredicates(List<String> ruleBodyTokens) {
        List<Predicate> predicates = new LinkedList<Predicate>();
        for (String token : ruleBodyTokens) {
            Predicate predicate = parsePredicate(token);
            String sensorName = predicate.getLeftOperand().asString();
            if (sensorsInDatabase.contains(sensorName)) {
                predicates.add(predicate);
            } else {
                if (!silent) {
                    System.err.println("Filtered missing sensor: `" + sensorName + "'.");
                }
            }
        }
        return predicates;
    }
}