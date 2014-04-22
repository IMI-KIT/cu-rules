package edu.kit.imi.knoholem.cu.rules.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A rule literal with instructions for parsing. Instances allow the querying of the rule atoms.
 *
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 * @see edu.kit.imi.knoholem.cu.rules.parser.SensitivityAnalysisRule
 * @see edu.kit.imi.knoholem.cu.rules.parser.RuleParserConfiguration
 */
class RuleLiteral {

    private final String ruleLiteral;
    private final RuleParserConfiguration configuration;

    private final List<String> antecedentTokens;
    private final List<String> consequentTokens;

    public RuleLiteral(String ruleLiteral, RuleParserConfiguration configuration) {
        this.ruleLiteral = ruleLiteral;
        this.configuration = configuration;

        String[] ruleSplit = ruleLiteral.split(configuration.thenLiteral());
        assertSplitArraySize(ruleSplit, 2);
        this.antecedentTokens = antecedentTokens(ruleSplit[0]);
        this.consequentTokens = consequentTokens(ruleSplit[1]);
    }

    /**
     * @return the rule literal.
     */
    public String getRuleLiteral() {
        return ruleLiteral;
    }

    /**
     * @return all non-metadata rule tokens in the antecedent.
     */
    public List<String> getAntecedentAtoms() {
        List<Integer> metadataFields = configuration.metadataFields();
        List<String> result = new ArrayList<String>(antecedentTokens.size());

        for (int i = 0; i < antecedentTokens.size(); i++) {
            if (!metadataFields.contains(i)) {
                result.add(antecedentTokens.get(i));
            }
        }

        return Collections.unmodifiableList(result);
    }

    public List<String> getConsequentAtoms() {
        return Collections.unmodifiableList(consequentTokens);
    }

    public String getZoneIdAtom() {
        return getMetadataToken(configuration.zoneIdIndex());
    }

    public String getMonthAtom() {
        return getMetadataToken(configuration.monthIndex());
    }

    public String getDayAtom() {
        return getMetadataToken(configuration.dayIndex());
    }

    public String getHourAtom() {
        return getMetadataToken(configuration.hourIndex());
    }

    public String getReductionAtom() {
        return getMetadataToken(configuration.reductionIndex());
    }

    public String getRuleTypeAtom() {
        return getMetadataToken(configuration.typeIndex());
    }

    public String getRuleWeightAtom() {
        return getMetadataToken(configuration.weightIndex());
    }

    private String getMetadataToken(int index) {
        if (index < 0) {
            return null;
        } else {
            return antecedentTokens.get(index);
        }
    }

    private List<String> antecedentTokens(String ruleAntecedent) {
        if (ruleAntecedent.startsWith(configuration.ifLiteral())) {
            return ruleBodyTokens(ruleAntecedent.substring(configuration.ifOffset()));
        } else {
            return ruleBodyTokens(ruleAntecedent);
        }
    }

    private List<String> consequentTokens(String ruleConsequent) {
        return ruleBodyTokens(ruleConsequent);
    }

    /**
     * Returns trimmed rule tokens for the given rule body.
     *
     * @param ruleBody a rule antecedent or consequent
     * @return a list of tokens representing the rule atoms.
     */
    private List<String> ruleBodyTokens(String ruleBody) {
        return trimItems(Arrays.asList(ruleBody.split(configuration.andLiteral())));
    }

    /**
     * Trims every element of the given list of strings.
     *
     * @param stringList the strings to process
     * @return a new list with trimmed elements.
     */
    private List<String> trimItems(List<String> stringList) {
        List<String> result = new ArrayList<String>(stringList.size());

        for (String string : stringList) {
            result.add(string.trim());
        }

        return result;
    }

    private boolean assertSplitArraySize(Object[] array, int expectedSize) {
        if (array.length < expectedSize) {
            throw new ParserConfigurationError(ruleLiteral, "Rule configuration incorrect or invalid rule given.");
        }

        return true;
    }

}
