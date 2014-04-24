package edu.kit.imi.knoholem.cu.rules.ontology;

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
public class OntologyContextTest {

    private OntologyContext context;

    @Before
    public void setUp() throws OWLOntologyCreationException, URISyntaxException {
        URL resourceURL = getClass().getResource("/ontology.n3");
        Path resourcePath = Paths.get(resourceURL.toURI());
        this.context = OntologyContext.load(resourcePath.toFile());
    }

    @Test
    public void testOntologyIRI() {
        Assert.assertEquals("http://www.semanticweb.org/ontologies/2012/9/knoholem.owl", context.getOntologyIRI().toString());
    }

    @Test
    public void testContainsIndividual() {
        Assert.assertTrue(context.containsIndividual("17_1_GRFMET_6"));
        Assert.assertFalse(context.containsIndividual("foobar"));
    }

    @Test
    public void testRepresentativeClass() {
        Assert.assertEquals("TemperatureSensor", context.getRepresentativeClass("17_1_GRFMET_6"));
    }

}
