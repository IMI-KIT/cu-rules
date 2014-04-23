package edu.kit.imi.knoholem.cu.rules.ontology;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import java.io.File;
import java.util.Set;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class OntologyContext {

    private final IRI ontologyIRI;
    private final IRI documentIRI;
    private final OWLOntology ontology;
    private final OWLOntologyManager manager;
    private final OWLDataFactory factory;
    private final OWLReasoner reasoner;

    public OntologyContext(File ontologyFile) throws OWLOntologyCreationException {
        manager = OWLManager.createOWLOntologyManager();
        ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);
        factory = manager.getOWLDataFactory();
        reasoner = new StructuralReasonerFactory().createReasoner(ontology);
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
        IRI individualIRI = IRI.create(ontologyIRI.toString(), resource(name));
        return ontology.containsIndividualInSignature(individualIRI);
    }

    /**
     * Returns the first class in the declaration of an individual.
     *
     * @param individualName <em>short</em> individual name.
     * @return <code>null</code>, if the individual could not be found in the ontology signature, or no types declared.
     * Else the short class name.
     */
    public String getRepresentativeClass(String individualName) {
        if (containsIndividual(individualName)) {
            Set<OWLClass> classes = reasoner.getTypes(factory.getOWLNamedIndividual(iri(individualName)), true).getFlattened();
            if (classes.isEmpty()) {
                return null;
            } else {
                return classes.iterator().next().getIRI().getFragment();
            }
        } else {
            return null;
        }
    }

    public OWLIndividual getIndividual(String name) {
        return factory.getOWLNamedIndividual(iri(name));
    }

    public OWLDataProperty getDataProperty(String name) {
        return factory.getOWLDataProperty(iri(name));
    }

    public OWLObjectProperty getObjectProperty(String name) {
        return factory.getOWLObjectProperty(iri(name));
    }

    /**
     * Returns an IRI formed by prepending the ontology IRI to the resource name.
     *
     * @param resourceName short name of the resource (e.g. class, property, individual).
     * @return resource IRI.
     */
    public IRI iri(String resourceName) {
        return IRI.create(ontologyIRI.toString(), resource(resourceName));
    }

    private String resource(String resourceName) {
        return "#" + resourceName;
    }

}
