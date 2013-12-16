package edu.kit.imi.knoholem.cu.rulesconversion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.kit.imi.knoholem.cu.rules.logicalentities.Atom;
import edu.kit.imi.knoholem.cu.rules.parser.RuleMetadata;

public class SWRLRule {

	private static final String THEN = " -> ";
	private static final String CONNECTIVE = ", ";

	private final List<Atom> antecedent;
	private final List<Atom> consequent;
	private RuleMetadata metadata;

	SWRLRule() {
		this.antecedent = new ArrayList<Atom>();
		this.consequent = new ArrayList<Atom>();
	}

	public List<Atom> getAntecenent() {
		return Collections.unmodifiableList(antecedent);
	}

	public List<Atom> getConsequent() {
		return Collections.unmodifiableList(consequent);
	}

	public RuleMetadata getMetadata() {
		return metadata;
	}

	public String getExpression() {
		StringBuilder ruleExpression = new StringBuilder();
		ruleExpression.append(joinAtoms(antecedent, CONNECTIVE));
		ruleExpression.append(THEN);
		ruleExpression.append(joinAtoms(consequent, CONNECTIVE));
		return ruleExpression.toString();
	}

	private String joinAtoms(List<Atom> atomList, String connective) {
		if (atomList.isEmpty()) {
			return "";
		}
		StringBuilder expression = new StringBuilder();
		for (int i = 0; i < atomList.size() - 1; i++) {
			expression.append(atomList.get(i).getExpression()).append(connective);
		}
		expression.append(atomList.get(atomList.size() - 1).getExpression());
		return expression.toString();
	}

	@Override
	public String toString() {
		return new StringBuilder().append(getClass()).append("{").append("}").toString();
	}

	void setMetadata(RuleMetadata metadata) {
		this.metadata = metadata;
	}

	void setAntecedent(Collection<Atom> atoms) {
		antecedent.addAll(atoms);
	}

	void setConsequent(Collection<Atom> atoms) {
		consequent.addAll(atoms);
	}

}
