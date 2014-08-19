package edu.kit.imi.knoholem.cu.rules.parser;

import edu.kit.imi.knoholem.cu.rules.atoms.Predicate;
import edu.kit.imi.knoholem.cu.rules.atoms.RuleMetadata;
import edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule;

import java.util.LinkedList;
import java.util.List;

public class RuleParser {

    private final RuleParserConfiguration configuration;

    public RuleParser(RuleParserConfiguration configuration) {
        this.configuration = configuration;
    }

    public SensitivityAnalysisRule parseRule(String ruleLiteral) {
        try {
            RuleLiteral parsedLiteral = new RuleLiteral(ruleLiteral, configuration);

            SensitivityAnalysisRule rule = new SensitivityAnalysisRule();
            rule.setAntecedent(collectPredicates(parsedLiteral.getAntecedentAtoms()));
            rule.setConsequent(collectPredicates(parsedLiteral.getConsequentAtoms()));
            rule.setMetadata(parseRuleMetadata(parsedLiteral));

            return rule;
        } catch (Throwable t) {
            throw new RuleParseError(ruleLiteral, t);
        }
    }

    private RuleMetadata parseRuleMetadata(RuleLiteral ruleLiteral) {
        String zoneId = parseZoneId(ruleLiteral);
        double weight = parseRuleWeight(ruleLiteral);
        double reduction = parseReductionRate(ruleLiteral);
        String type = parseRuleType(ruleLiteral);
        return new RuleMetadata(zoneId, type, weight, reduction);
    }

    private String parseZoneId(RuleLiteral ruleLiteral) {
        return parsePredicate(ruleLiteral.getZoneIdAtom()).getRightOperand().asString();
    }

    private Double parseRuleWeight(RuleLiteral ruleLiteral) {
        return parsePredicate(ruleLiteral.getRuleWeightAtom()).getRightOperand().asDouble();
    }

    private Double parseReductionRate(RuleLiteral ruleLiteral) {
        String reductionAtom = ruleLiteral.getReductionAtom();
        reductionAtom = removePercentSign(reductionAtom);
        return parsePredicate(reductionAtom).getRightOperand().asDouble();
    }

    private String parseRuleType(RuleLiteral ruleLiteral) {
        return parsePredicate(ruleLiteral.getRuleTypeAtom()).getRightOperand().asString();
    }

    private List<Predicate> collectPredicates(List<String> ruleBodyTokens) {
        List<Predicate> predicates = new LinkedList<Predicate>();
        for (String token : ruleBodyTokens) {
            predicates.add(parsePredicate(token));
        }
        return predicates;
    }

    private Predicate parsePredicate(String predicateLiteral) {
        PredicateParser parser = new PredicateParser(predicateLiteral);
        return new Predicate(parser.getLeftLiteral(), parser.getOperator(), parser.getRightLiteral());
    }

    private String removePercentSign(String originalValue) {
        if (originalValue.charAt(originalValue.length() - 1) == '%') {
            return originalValue.substring(0, originalValue.length() - 1);
        }
        return originalValue;
    }

}
