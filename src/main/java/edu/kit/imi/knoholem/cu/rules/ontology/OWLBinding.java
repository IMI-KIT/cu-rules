package edu.kit.imi.knoholem.cu.rules.ontology;

import edu.kit.imi.knoholem.cu.rules.process.processors.Function;
import edu.kit.imi.knoholem.cu.rulesconversion.SWRLRule;
import org.semanticweb.owlapi.model.OWLAnnotation;

import java.util.Set;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class OWLBinding implements Function<SWRLRule, org.semanticweb.owlapi.model.SWRLRule> {

    private final OntologyContext context;
    private final Function<SWRLRule, Set<OWLAnnotation>> ruleAnnotations;

    public OWLBinding(OntologyContext context) {
        this.context = context;
        this.ruleAnnotations = null;
    }

    @Override
    public org.semanticweb.owlapi.model.SWRLRule apply(SWRLRule input) {
        return null;
    }

}
