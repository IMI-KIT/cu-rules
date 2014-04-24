package edu.kit.imi.knoholem.cu.rules.atoms.processing;

import edu.kit.imi.knoholem.cu.rules.atoms.Literal;
import edu.kit.imi.knoholem.cu.rules.atoms.Predicate;

import java.util.Collections;
import java.util.List;

public class PredicateMapEntry {

    private final Literal classifier;
    private final List<Predicate> predicates;

    PredicateMapEntry(Literal classifier, List<Predicate> predicates) {
        this.classifier = classifier;
        this.predicates = predicates;
    }

    public boolean isSingular() {
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
        return getClass().getSimpleName() + "{ classifier=\"" + classifier.asString() + "\""
                + " " + "size=" + predicates.size()
                + " }";
    }

}
