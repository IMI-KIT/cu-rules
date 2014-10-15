package edu.kit.imi.knoholem.cu.rules.parser;

import java.util.*;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
class DynamicRuleParserConfiguration extends RuleParserConfiguration {

    public final static String defaultIfLiteral = "IF";
    public final static String defaultAndLiteralPattern = "\\^";
    public final static String defaultAndLiteral = "^";
    public final static String defaultThenLiteral = "THEN";
    public final static String defaultZoneIdLiteral = "ZoneID";
    public final static String defaultTypeLiteral = "Type";
    public final static String defaultWeightLiteral = "Weight";
    public final static String defaultReductionLiteral = "Reduction";

    private final String ruleLiteral;
    private final String ifLiteral;
    private final String andLiteral;
    private final String thenLiteral;

    private final Properties configuration;

    private final List<String> antecedentTokens;

    public DynamicRuleParserConfiguration(String ruleLiteral, Properties configuration) {
        this(ruleLiteral, defaultIfLiteral, defaultAndLiteralPattern, defaultThenLiteral, configuration);
    }

    public DynamicRuleParserConfiguration(String ruleLiteral,
                                          String ifLiteral,
                                          String andLiteral,
                                          String thenLiteral,
                                          Properties configuration) {
        this.ruleLiteral = ruleLiteral;
        this.ifLiteral = ifLiteral;
        this.andLiteral = andLiteral;
        this.thenLiteral = thenLiteral;
        this.configuration = configuration;

        String[] ruleSplit = ruleLiteral.split(thenLiteral);
        assertSplitArraySize(ruleSplit, 2);
        this.antecedentTokens = ruleBodyTokens(ruleSplit[0]);
    }

    private List<String> antecedentTokens(String ruleAntecedent) {
        if (ruleAntecedent.startsWith(ifLiteral)) {
            return ruleBodyTokens(ruleAntecedent.substring(ifLiteral.length()));
        } else {
            return ruleBodyTokens(ruleAntecedent);
        }
    }

    private List<String> ruleBodyTokens(String ruleBody) {
        return trimItems(Arrays.asList(ruleBody.split(andLiteral)));
    }

    private List<String> trimItems(List<String> stringList) {
        List<String> result = new ArrayList<String>(stringList.size());

        for (String string : stringList) {
            result.add(string.trim());
        }

        return result;
    }

    private boolean assertSplitArraySize(Object[] array, int expectedSize) {
        if (array.length < expectedSize) {
            throw new ParserConfigurationError("Rule configuration for literal \"" + ruleLiteral + "\" incorrect or invalid rule given.");
        }

        return true;
    }

    private int antecedentWithPrefix(String prefix) {
        for (int i = 0; i < antecedentTokens.size(); i++) {
            if (antecedentTokens.get(i).startsWith(prefix)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int zoneIdIndex() {
        return antecedentWithPrefix(configuration.getProperty("zoneId"));
    }

    @Override
    public int weightIndex() {
        return antecedentWithPrefix(configuration.getProperty("weight"));
    }

    @Override
    public int typeIndex() {
        return antecedentWithPrefix(configuration.getProperty("type"));
    }

    @Override
    public int reductionIndex() {
        return antecedentWithPrefix(configuration.getProperty("reduction"));
    }

    @Override
    public String andLiteralPattern() {
        return andLiteral;
    }

    @Override
    public String ifLiteral() {
        return ifLiteral;
    }

    @Override
    public String thenLiteral() {
        return thenLiteral;
    }

    @Override
    public String zoneIdLiteral() {
        return defaultZoneIdLiteral;
    }

    @Override
    public String weightLiteral() {
        return defaultWeightLiteral;
    }

    @Override
    public String typeLiteral() {
        return defaultTypeLiteral;
    }

    @Override
    public String reductionIndexLiteral() {
        return defaultReductionLiteral;
    }

    @Override
    public String andLiteral() {
        return defaultAndLiteral;
    }

    @Override
    public List<Integer> metadataFields() {
        return nonNegativeIntegers(super.metadataFields());
    }

    private List<Integer> nonNegativeIntegers(Collection<Integer> integers) {
        List<Integer> result = new ArrayList<Integer>(integers.size());

        for (Integer integerElement : integers) {
            if (integerElement >= 0) {
                result.add(integerElement);
            }
        }

        return result;
    }

}