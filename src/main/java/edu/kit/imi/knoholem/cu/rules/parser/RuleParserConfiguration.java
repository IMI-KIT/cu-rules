package edu.kit.imi.knoholem.cu.rules.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class RuleParserConfiguration {

	private final static RuleParserConfiguration defaultConfiguration = new DefaultRuleParserConfiguration();

	public abstract int weightIndex();
	public abstract int typeIndex();
	public abstract int reductionIndex();

	public abstract int monthIndex();
	public abstract int dayIndex();
	public abstract int hourIndex();

	public abstract String andLiteral();
	public abstract String ifLiteral();
	public abstract String thenLiteral();

	public static RuleParserConfiguration getDefaultConfiguration() {
		return defaultConfiguration;
	}

	public List<Integer> metadataFields() {
		List<Integer> metadataFields = new ArrayList<Integer>(6);
		metadataFields.add(weightIndex());
		metadataFields.add(typeIndex());
		metadataFields.add(reductionIndex());
		metadataFields.add(monthIndex());
		metadataFields.add(dayIndex());
		metadataFields.add(hourIndex());
		return Collections.unmodifiableList(metadataFields);
	}

	public int ifOffset() {
		return ifLiteral().length();
	}

}
