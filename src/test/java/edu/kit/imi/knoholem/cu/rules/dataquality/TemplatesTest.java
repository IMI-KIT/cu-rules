package edu.kit.imi.knoholem.cu.rules.dataquality;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class TemplatesTest {

    private STGroup templates;

    @Before
    public void setup() throws URISyntaxException {
        URL resourceURL = getClass().getResource("/templates/templates.stg");
        Path path = Paths.get(resourceURL.toURI());

        templates = new STGroupFile(path.toFile().getPath());
    }

    @Test
    public void testInitialized() {
        Assert.assertNotNull(templates);
        Assert.assertNotNull(templates.getInstanceOf("prefixes"));
    }

    @Test
    public void testPrefixes() {
        String expected = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX ontology: <http://example.com/ontology.owl#>";

        ST template = templates.getInstanceOf("prefixes");
        template.add("prefix", "ontology");
        template.add("namespace", "http://example.com/ontology.owl#");

        Assert.assertEquals(expected, template.render());
    }
}
