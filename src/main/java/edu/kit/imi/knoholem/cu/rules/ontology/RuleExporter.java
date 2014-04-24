package edu.kit.imi.knoholem.cu.rules.ontology;

import edu.kit.imi.knoholem.cu.rules.process.processors.Function;
import org.semanticweb.owlapi.model.SWRLRule;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class RuleExporter implements Function<SWRLRule, Object> {

    private final OntologyContext ontology;

    public RuleExporter(OntologyContext ontology) {
        this.ontology = ontology;
    }

    @Override
    public Object apply(SWRLRule input) {
        return ontology.addRule(input);
    }

}
