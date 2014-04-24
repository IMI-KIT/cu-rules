package edu.kit.imi.knoholem.cu.rules.ontology.rulesprocessing;

import edu.kit.imi.knoholem.cu.rules.atoms.Literal;
import edu.kit.imi.knoholem.cu.rules.functions.Function;
import edu.kit.imi.knoholem.cu.rules.ontology.OntologyContext;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class KnownEntity implements Function<Literal, Boolean> {
    private final OntologyContext ontology;

    public KnownEntity(OntologyContext ontology) {
        this.ontology = ontology;
    }

    /**
     * @param input function input
     * @return <code>true</code> if the literal could be found in the individuals signature of the ontology.
     */
    @Override
    public Boolean apply(Literal input) {
        return ontology.containsIndividual(input.asString());
    }

}
