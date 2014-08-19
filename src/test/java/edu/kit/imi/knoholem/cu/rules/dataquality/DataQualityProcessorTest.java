package edu.kit.imi.knoholem.cu.rules.dataquality;

import edu.kit.imi.knoholem.cu.rules.ontology.OntologyContext;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class DataQualityProcessorTest {

    private static Templates templates;
    private static DataQualityEvaluationContext processor;

    @BeforeClass
    public static void setup() throws URISyntaxException, OWLOntologyCreationException, MalformedURLException, FileNotFoundException {
        URL resourceURL = DataQualityProcessorTest.class.getResource("/ontology.n3");
        Path path = Paths.get(resourceURL.toURI());
        OntologyContext ontologyContext = OntologyContext.load(path.toFile());
        templates = Templates.loadTemplates();
        processor = new DataQualityEvaluationContext(ontologyContext);
    }

    @Test
    public void testDocumentLanguage() {
        Assert.assertEquals("TURTLE", processor.getDocumentLanguage());
    }

    @Test
    public void testPositiveIndividualInClass() {
        Assert.assertTrue(processor.apply(new IndividualInClass("TemperatureSensor", "17_10_GRFMET_5", templates)));
        Assert.assertTrue(processor.apply(new IndividualInClass("BuildingControl", "17_10_GRFMET_5", templates)));
        Assert.assertTrue(processor.apply(new IndividualInClass("Sensor", "17_10_GRFMET_5", templates)));
    }

    @Test
    public void testNegativeIndividualClass() {
        Assert.assertFalse(processor.apply(new IndividualInClass("WindSensor", "17_10_GRFMET_5", templates)));
    }

    @Test
    public void testPositiveDeclaredIndividual() {
        Assert.assertTrue(processor.apply(new DeclaredIndividual("17_10_GRFMET_5", templates)));
    }

    @Test
    public void testNegativeDeclaredIndividual() {
        Assert.assertFalse(processor.apply(new DeclaredIndividual("PankoPernik", templates)));
    }
}
