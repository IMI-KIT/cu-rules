package edu.kit.imi.knoholem.cu.rules.dataquality;

import edu.kit.imi.knoholem.cu.rules.ontology.OntologyContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class IndividualInClassTest {

    private OntologyContext ontologyContext;

    @Before
    public void setup() throws URISyntaxException, OWLOntologyCreationException {
        URL resourceURL = getClass().getResource("/ontology.n3");
        Path path = Paths.get(resourceURL.toURI());
        ontologyContext = OntologyContext.load(path.toFile());
    }

    @Test
    public void testQuery() {
        IndividualInClass iic = new IndividualInClass("OWLClass", "OWLIndividual", Templates.loadTemplates());
        String expected = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX ontology: <http://www.semanticweb.org/ontologies/2012/9/knoholem.owl#>\n\n" +
                "ASK { ontology:OWLIndividual a ontology:OWLClass }";

        Assert.assertEquals(expected, iic.asAskQuery(ontologyContext));
    }
}
