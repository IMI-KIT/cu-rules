package edu.kit.imi.knoholem.cu.rules;

import edu.kit.imi.knoholem.cu.rules.ontology.OntologyContext;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Kiril Tonev</a>
 */
public class OntologySensors {

    public static void main(String[] args) {
        String ontology = args[0];

        OntologyContext ontologyContext = null;
        try {
            ontologyContext = OntologyContext.load(new File(ontology));
        } catch (OWLOntologyCreationException e) {
            System.err.println("Couldn't load the ontology in `" + ontology + "'. Reason: " + e.getMessage());
            System.exit(1);
        }

        for (String individual : ontologyContext.individuals()) {
            System.out.println(individual + ";" + ontologyContext.getRepresentativeClass(individual));
        }
    }
}
