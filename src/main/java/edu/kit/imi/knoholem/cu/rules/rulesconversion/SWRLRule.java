package edu.kit.imi.knoholem.cu.rules.rulesconversion;

import edu.kit.imi.knoholem.cu.rules.atoms.RuleMetadata;
import edu.kit.imi.knoholem.cu.rules.swrlentities.Atom;
import edu.kit.imi.knoholem.cu.rules.swrlentities.SWRLExpression;

import java.util.*;

public class SWRLRule implements SWRLExpression {

	private final List<Atom> antecedent;
	private final List<Atom> consequent;

	private RuleMetadata metadata;

	SWRLRule() {
		this.antecedent = new ArrayList<Atom>();
		this.consequent = new ArrayList<Atom>();
	}

	public List<Atom> getAntecedent() {
		return Collections.unmodifiableList(antecedent);
	}

	public List<Atom> getConsequent() {
		return Collections.unmodifiableList(consequent);
	}

	public RuleMetadata getMetadata() {
		return metadata;
	}

	@Override
	public String getExpression() {
		StringBuilder ruleExpression = new StringBuilder();
		ruleExpression.append(joinAtoms(antecedent, CONNECTIVE));
		ruleExpression.append(THEN);
		ruleExpression.append(joinAtoms(consequent, CONNECTIVE));
		return ruleExpression.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((antecedent == null) ? 0 : antecedent.hashCode());
		result = prime * result
				+ ((consequent == null) ? 0 : consequent.hashCode());
		result = prime * result
				+ ((metadata == null) ? 0 : metadata.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SWRLRule other = (SWRLRule) obj;
		if (antecedent == null) {
			if (other.antecedent != null)
				return false;
		} else if (!antecedent.equals(other.antecedent))
			return false;
		if (consequent == null) {
			if (other.consequent != null)
				return false;
		} else if (!consequent.equals(other.consequent))
			return false;
		if (metadata == null) {
			if (other.metadata != null)
				return false;
		} else if (!metadata.equals(other.metadata))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" + "}";
	}

	void setMetadata(RuleMetadata metadata) {
		this.metadata = metadata;
	}

	void setAntecedent(Collection<Atom> atoms) {
		antecedent.clear();
		antecedent.addAll(uniqueAtomsList(atoms));
	}

	void setConsequent(Collection<Atom> atoms) {
		consequent.clear();
		consequent.addAll(uniqueAtomsList(atoms));
	}

	void addAntecedent(Atom atom) {
		antecedent.add(atom);
	}

	void addConsequenet(Atom atom) {
		consequent.add(atom);
	}

	private List<Atom> uniqueAtomsList(Collection<? extends Atom> atoms) {
		List<Atom> uniques = new ArrayList<Atom>(atoms.size());
		Set<Atom> atomSet = new LinkedHashSet<Atom>();
		for (Atom atom : atoms) {
			atomSet.add(atom);
		}
		uniques.addAll(atomSet);
		return uniques;
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

}
