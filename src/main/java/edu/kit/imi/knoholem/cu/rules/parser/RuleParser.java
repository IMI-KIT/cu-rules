package edu.kit.imi.knoholem.cu.rules.parser;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class RuleParser {

	private final RuleParserConfiguration configuration;
	private final List<Integer> metadataFields;

	public RuleParser(RuleParserConfiguration configuration) {
		this.configuration = configuration;
		this.metadataFields = this.configuration.metadataFields();
	}

	public SensitivityAnalysisRule parseRule(String ruleLiteral) {
		List<Predicate> premisePredicates = collectPremisePredicates(premiseTokens(ruleLiteral));
		List<Predicate> conclusionPredicates = collectConclusionPredicates(conclusionTokens(ruleLiteral));
		SensitivityAnalysisRule rule = new SensitivityAnalysisRule();
		rule.setAntecedent(premisePredicates);
		rule.setConsequent(conclusionPredicates);
		rule.setMetadata(parseRuleMetadata(premiseTokens(ruleLiteral)));
		return rule;
	}

	private RuleMetadata parseRuleMetadata(String[] premiseTokens) {
		double weight = parseRuleWeight(premiseTokens);
		double reduction = parseReductionRate(premiseTokens);
		String type = parseRuleType(premiseTokens);
		Calendar date = parseRuleDate(premiseTokens);
		return new RuleMetadata(date, type, weight, reduction);
	}

	private Double parseRuleWeight(String[] premiseTokens) {
		return parsePredicate(premiseTokens[configuration.weightIndex()]).getRightOperand().asDouble();
	}

	private Double parseReductionRate(String[] premiseTokens) {
		return parsePredicate(premiseTokens[configuration.reductionIndex()]).getRightOperand().asDouble();
	}

	private String parseRuleType(String[] premiseTokens) {
		return parsePredicate(premiseTokens[configuration.typeIndex()]).getRightOperand().asString();
	}

	private Calendar parseRuleDate(String[] premiseTokens) {
		Calendar calendar = Calendar.getInstance();
		int day = parsePredicate(premiseTokens[configuration.dayIndex()]).getRightOperand().asInteger();
		int month = parsePredicate(premiseTokens[configuration.monthIndex()]).getRightOperand().asInteger();
		int hour = parsePredicate(premiseTokens[configuration.hourIndex()]).getRightOperand().asInteger();
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.HOUR, hour);
		return calendar;
	}

	private String[] premiseTokens(String ruleLiteral) {
		String[] split = ruleLiteral.split(configuration.thenLiteral());
		if (split.length != 2) {
			throw new RuntimeException("Invalid rule format in literal: " + ruleLiteral);
		}
		return splitInTokens(split[0]);
	}

	private String[] splitInTokens(String rulePartLiteral) {
		return rulePartLiteral.split(configuration.andLiteral());
	}

	private String[] conclusionTokens(String ruleLiteral) {
		String[] split = ruleLiteral.split(configuration.thenLiteral());
		if (split.length != 2) {
			throw new RuntimeException("Invalid rule format in literal: " + ruleLiteral);
		}
		return splitInTokens(split[1]);
	}

	private List<Predicate> collectPremisePredicates(String[] premiseTokens) {
		List<Predicate> predicates = new LinkedList<Predicate>();
		for (int i = 0; i < premiseTokens.length; i++) {
			String token = premiseTokens[i];
			if (!metadataFields.contains(i)) {
				if (i == 0) {
					String ifFilteredOut = token.substring(configuration.ifOffset());
					predicates.add(parsePredicate(ifFilteredOut));
				} else {
					predicates.add(parsePredicate(token));
				}
			}
		}
		return predicates;
	}

	private List<Predicate> collectConclusionPredicates(String[] conclusionTokens) {
		List<Predicate> predicates = new LinkedList<Predicate>();
		for (String token : conclusionTokens) {
			predicates.add(parsePredicate(token));
		}
		return predicates;
	}

	private Predicate parsePredicate(String predicateLiteral) {
		PredicateParser parser = new PredicateParser(predicateLiteral);
		Predicate predicate = new Predicate(parser.getLeftLiteral(), parser.getOperator(), parser.getRightLiteral());
		return predicate;
	}

}
