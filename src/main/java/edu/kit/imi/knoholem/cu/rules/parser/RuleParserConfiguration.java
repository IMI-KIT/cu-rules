package edu.kit.imi.knoholem.cu.rules.parser;

import java.util.*;

public abstract class RuleParserConfiguration {

    private final static RuleParserConfiguration defaultConfiguration = new DefaultRuleParserConfiguration();

    public static RuleParserConfiguration getDefaultConfiguration() {
        return defaultConfiguration;
    }

    public abstract int zoneIdIndex();

    public abstract int weightIndex();

    public abstract int typeIndex();

    public abstract int reductionIndex();

    public abstract String andLiteral();

    public abstract String ifLiteral();

    public abstract String thenLiteral();

    public int ifOffset() {
        return ifLiteral().length();
    }

    public List<Integer> metadataFields() {
        List<Integer> fieldsList = Arrays.asList(zoneIdIndex(), weightIndex(), typeIndex(), reductionIndex());
        List<Integer> nonNegativeIndices = nonNegativeIntegers(fieldsList);
        return Collections.unmodifiableList(nonNegativeIndices);
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