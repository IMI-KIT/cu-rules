package edu.kit.imi.knoholem.cu.rules.ontology;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Set;

/**
 * Encapsulates the objects relevant for ontology manipulation and query.
 * <p>
 * Exposes a manager, factory and (a structural) reasoner associated with an ontology.
 * </p>
 * <p>
 * Provides shorthands for accessing individuals, properties and classes in the namespace of the ontology document.
 * </p>
 *
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 * @see org.semanticweb.owlapi.model.OWLOntology
 * @see org.semanticweb.owlapi.model.OWLOntologyManager
 * @see org.semanticweb.owlapi.model.OWLDataFactory
 * @see org.semanticweb.owlapi.reasoner.OWLReasoner
 */
public class OntologyContext {

    private final IRI ontologyIRI;
    private final IRI documentIRI;
    private final OWLOntology ontology;
    private final OWLOntologyManager manager;
    private final OWLDataFactory factory;
    private final OWLReasoner reasoner;

    /**
     * Default constructor.
     *
     * @param ontologyFile ontology document.
     * @throws OWLOntologyCreationException in case of ontology parsing error.
     */
    public static OntologyContext load(File ontologyFile) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);
        return new OntologyContext(ontologyFile, manager, ontology);
    }

    public static OntologyContext create(File ontologyFile, IRI ontologyIRI) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        IRI documentIRI = IRI.create(ontologyFile);
        SimpleIRIMapper mapper = new SimpleIRIMapper(ontologyIRI, documentIRI);
        manager.addIRIMapper(mapper);
        return new OntologyContext(ontologyFile, manager, manager.createOntology(ontologyIRI));
    }

    private OntologyContext(File ontologyFile, OWLOntologyManager manager, OWLOntology ontology) {
        this.manager = manager;
        this.ontology = ontology;
        this.factory = manager.getOWLDataFactory();
        this.reasoner = new StructuralReasonerFactory().createReasoner(ontology);
        this.ontologyIRI = ontology.getOntologyID().getOntologyIRI();
        this.documentIRI = IRI.create(ontologyFile);
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
     * @param individualName <em>short</em> individual name. Not null.
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

    public OWLNamedIndividual getIndividual(String name) {
        return factory.getOWLNamedIndividual(iri(name));
    }

    public OWLDataProperty getDataProperty(String name) {
        return factory.getOWLDataProperty(iri(name));
    }

    public OWLObjectProperty getObjectProperty(String name) {
        return factory.getOWLObjectProperty(iri(name));
    }

    public OWLClass getOWLClass(String name) {
        return factory.getOWLClass(iri(name));
    }

    public List<OWLOntologyChange> addRule(SWRLRule rule) {
        return manager.applyChange(new AddAxiom(ontology, rule));
    }

    /**
     * Persists changes in the ontology.
     *
     * @throws OWLOntologyStorageException
     */
    public void saveOntology() throws OWLOntologyStorageException {
        manager.saveOntology(ontology);
    }

    /**
     * Saves the ontology into another file.
     *
     * @param file
     * @throws OWLOntologyStorageException
     * @throws FileNotFoundException       if the given file is a directory, and not a file.
     */
    public void saveOntologyAs(File file) throws OWLOntologyStorageException, FileNotFoundException {
        saveOntologyAs(file, manager.getOntologyFormat(ontology));
    }

    /**
     * Saves the ontology into another file.
     *
     * @param file   output file path.
     * @param format the ontology format to use.
     * @throws OWLOntologyStorageException
     * @throws FileNotFoundException       if the given file is a directory, and not a file.
     */
    public void saveOntologyAs(File file, OWLOntologyFormat format) throws OWLOntologyStorageException, FileNotFoundException {
        FileOutputStream outputStream = new FileOutputStream(file);
        manager.saveOntology(ontology, format, outputStream);
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

    public OWLOntologyFormat getOntologyFormat() {
        return manager.getOntologyFormat(ontology);
    }

    public String getOntologyNamespace() {
        return getOntologyIRI().toString() + "#";
    }
}
