package edu.kit.imi.knoholem.cu.rules.ontology;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class OntologyContext {

    private final IRI ontologyIRI;
    private final IRI documentIRI;
    private final OWLOntology ontology;
    private final OWLOntologyManager manager;
    private final OWLDataFactory factory;

    public OntologyContext(File ontologyFile) throws OWLOntologyCreationException {
        manager = OWLManager.createOWLOntologyManager();
        ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);
        factory = manager.getOWLDataFactory();
        ontologyIRI = ontology.getOntologyID().getOntologyIRI();
        documentIRI = IRI.create(ontologyFile);
    }

    public IRI getOntologyIRI() {
        return ontologyIRI;
    }

    public IRI getDocumentIRI() {
        return documentIRI;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public OWLOntologyManager getManager() {
        return manager;
    }

    public OWLDataFactory getFactory() {
        return factory;
    }

    public boolean containsIndividual(String name) {
        IRI individualIRI = IRI.create(ontologyIRI.toString(), name);
        return ontology.containsIndividualInSignature(individualIRI);
    }

}
