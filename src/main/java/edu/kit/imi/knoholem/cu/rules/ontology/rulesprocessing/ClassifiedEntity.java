package edu.kit.imi.knoholem.cu.rules.ontology.rulesprocessing;

import edu.kit.imi.knoholem.cu.rules.atoms.Literal;
import edu.kit.imi.knoholem.cu.rules.functions.Function;
import edu.kit.imi.knoholem.cu.rules.ontology.OntologyContext;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class ClassifiedEntity implements Function<Literal, Boolean> {

    private OntologyContext ontology;

    public ClassifiedEntity(OntologyContext ontology) {
        this.ontology = ontology;
    }

    /**
     * @param input function input
     * @return <code>true</code>, iff the literal has a class membership assigned, as per the ontology signature.
     */
    @Override
    public Boolean apply(Literal input) {
        return ontology.getRepresentativeClass(input.asString()) != null;
    }
}
