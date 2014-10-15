package edu.kit.imi.knoholem.cu.rules.parser;

import edu.kit.imi.knoholem.cu.rules.atoms.Operator;
import edu.kit.imi.knoholem.cu.rules.atoms.Predicate;
import edu.kit.imi.knoholem.cu.rules.atoms.RuleMetadata;
import edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rules.functions.Function;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Kiril Tonev</a>
 */
public class RulePrinter implements Function<SensitivityAnalysisRule, String> {

    private final RuleParserConfiguration configuration;
    private final NumberFormat numberFormat;

    public RulePrinter(RuleParserConfiguration configuration, NumberFormat numberFormat) {
        this.configuration = configuration;
        this.numberFormat = numberFormat;
    }

    @Override
    public String apply(SensitivityAnalysisRule input) {
        return ruleLiteral(input);
    }

    public String ruleLiteral(SensitivityAnalysisRule rule) {
        String connective = " " + configuration.andLiteral() + " ";

        List<String> antecedentLiterals = new ArrayList<String>();
        antecedentLiterals.addAll(metadataTokens(rule));
        antecedentLiterals.addAll(atomTokens(rule.getAntecedent()));

        return configuration.ifLiteral()
                + " " + joinTokens(antecedentLiterals, connective)
                + " " + configuration.thenLiteral() + " "
                + joinTokens(atomTokens(rule.getConsequent()), connective);
    }

    protected List<String> metadataTokens(SensitivityAnalysisRule rule) {
        return metadataTokens(rule.getMetadata());
    }

    protected List<String> atomTokens(List<Predicate> predicates) {
        List<String> tokens = new ArrayList<String>(predicates.size());

        for (Predicate predicate : predicates) {
            tokens.add(predicate.getLeftOperand().asString() + predicate.getOperator().literal + predicate.getRightOperand().asString());
        }

        return tokens;
    }

    protected List<String> metadataTokens(RuleMetadata metadata) {
        String[] tokens = new String[4];
        tokens[configuration.zoneIdIndex()] = zoneIdLiteral(metadata);
        tokens[configuration.reductionIndex()] = reductionLiteral(metadata);
        tokens[configuration.typeIndex()] = typeLiteral(metadata);
        tokens[configuration.weightIndex()] = weightLiteral(metadata);
        return Arrays.asList(tokens);
    }

    protected String zoneIdAtom(SensitivityAnalysisRule rule) {
        return zoneIdLiteral(rule.getMetadata());
    }

    protected String zoneIdLiteral(RuleMetadata ruleMetadata) {
        return configuration.zoneIdLiteral() + Operator.EQUAL.literal + ruleMetadata.getZoneId();
    }

    protected String reductionLiteral(SensitivityAnalysisRule rule) {
        return reductionLiteral(rule.getMetadata());
    }

    protected String reductionLiteral(RuleMetadata ruleMetadata) {
        return configuration.reductionIndexLiteral() + Operator.EQUAL.literal + numberFormat.format(ruleMetadata.getReduction()) + "%";
    }

    protected String typeLiteral(SensitivityAnalysisRule rule) {
        return typeLiteral(rule.getMetadata());
    }

    protected String typeLiteral(RuleMetadata metadata) {
        return configuration.typeLiteral() + Operator.EQUAL.literal + metadata.getType();
    }

    protected String weightLiteral(SensitivityAnalysisRule rule) {
        return weightLiteral(rule.getMetadata());
    }

    protected String weightLiteral(RuleMetadata metadata) {
        return configuration.weightLiteral() + Operator.EQUAL.literal + numberFormat.format(metadata.getWeight());
    }

    private String joinTokens(List<String> ruleLiteralTokens, String connective) {
        StringBuilder builder = new StringBuilder();
        if (ruleLiteralTokens.isEmpty()) {
            return "";
        }
        for (int i = 0; i < ruleLiteralTokens.size() - 1; i++) {
            builder.append(ruleLiteralTokens.get(i));
            builder.append(connective);
        }
        builder.append(ruleLiteralTokens.get(ruleLiteralTokens.size() - 1));
        return builder.toString();
    }
}
