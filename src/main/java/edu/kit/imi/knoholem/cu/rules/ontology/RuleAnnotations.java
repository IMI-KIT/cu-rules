package edu.kit.imi.knoholem.cu.rules.ontology;

import edu.kit.imi.knoholem.cu.rules.process.processors.Function;
import edu.kit.imi.knoholem.cu.rulesconversion.SWRLRule;
import org.semanticweb.owlapi.model.OWLAnnotation;

import java.util.Set;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class RuleAnnotations implements Function<SWRLRule, Set<OWLAnnotation>> {

    @Override
    public Set<OWLAnnotation> apply(SWRLRule input) {
        return null;
    }

}
