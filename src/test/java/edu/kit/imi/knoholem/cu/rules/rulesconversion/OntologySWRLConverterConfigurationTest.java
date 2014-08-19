package edu.kit.imi.knoholem.cu.rules.rulesconversion;

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
public class OntologySWRLConverterConfigurationTest {

    private OntologySWRLConverterConfiguration configuration;

    @Before
    public void setUp() throws OWLOntologyCreationException, URISyntaxException {
        URL resourceURL = getClass().getResource("/ontology.n3");
        Path resourcePath = Paths.get(resourceURL.toURI());
        OntologyContext context = OntologyContext.load(resourcePath.toFile());
        this.configuration = new OntologySWRLConverterConfiguration(context);
    }

    @Test
    public void testToggableSensors() {
        String actualValueProperty = configuration.sensorValueProperty(ToggableActuators.OpeningSensor.name());
        Assert.assertEquals(actualValueProperty, "hasBinaryValue");
    }
}
