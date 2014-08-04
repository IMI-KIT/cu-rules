package edu.kit.imi.knoholem.cu.rules.dataquality;

import edu.kit.imi.knoholem.cu.rules.ontology.OntologyContext;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public interface Criterion {

    public String asAskQuery(OntologyContext context);

}
