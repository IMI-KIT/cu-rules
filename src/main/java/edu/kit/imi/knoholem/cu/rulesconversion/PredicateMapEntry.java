package edu.kit.imi.knoholem.cu.rulesconversion;

import java.util.Collections;
import java.util.List;

import edu.kit.imi.knoholem.cu.rules.parser.Literal;
import edu.kit.imi.knoholem.cu.rules.parser.Predicate;

class PredicateMapEntry {

	private final Literal classifier;
	private final List<Predicate> predicates;

	PredicateMapEntry(Literal classifier, List<Predicate> predicates) {
		this.classifier = classifier;
		this.predicates = predicates;
	}

	boolean isSingular() {
		return predicates.size() == 1;
	}

	public Predicate getFirstPredicate() {
		return predicates.get(0);
	}

	public List<Predicate> getPredicates() {
		return Collections.unmodifiableList(predicates);
	}

	public Literal getClassifier() {
		return classifier;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(getClass().getSimpleName()).append("{")
				.append(" classifier=\"").append(classifier.asString()).append("\" ")
				.append("size=").append(predicates.size()).append("}").toString();
	}

}
