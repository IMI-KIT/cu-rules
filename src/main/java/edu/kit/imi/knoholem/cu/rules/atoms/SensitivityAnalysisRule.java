package edu.kit.imi.knoholem.cu.rules.atoms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A sensitivity analysis rule consists of two sets of {@link Predicate}s: an
 * antecedent and a consequent. Additionally, it has a set of properties, as
 * defined by {@link RuleMetadata}.
 *
 * @author <a href="mailto:kiril.tonev@kit.edu">Kiril Tonev</a>
 */
public class SensitivityAnalysisRule {

    private RuleMetadata metadata;
    private final List<Predicate> consequent;
    private final List<Predicate> antecedent;

    public SensitivityAnalysisRule() {
        this.antecedent = new ArrayList<Predicate>();
        this.consequent = new ArrayList<Predicate>();
    }

    public List<Predicate> getAntecedent() {
        return Collections.unmodifiableList(antecedent);
    }

    public List<Predicate> getConsequent() {
        return Collections.unmodifiableList(consequent);
    }

    public void setConsequent(List<Predicate> conclusionPredicates) {
        consequent.addAll(conclusionPredicates);
    }

    public void setAntecedent(List<Predicate> antecedentPredicates) {
        antecedent.addAll(antecedentPredicates);
    }

    public void setMetadata(RuleMetadata metadata) {
        this.metadata = metadata;
    }

    public RuleMetadata getMetadata() {
        return metadata;
    }

}
