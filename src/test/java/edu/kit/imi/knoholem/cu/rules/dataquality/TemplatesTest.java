package edu.kit.imi.knoholem.cu.rules.dataquality;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class TemplatesTest {

    private STGroup templates;

    @Before
    public void setup() throws URISyntaxException {
        System.setProperty("line.separator", "\n");
        templates = new STGroupFile("templates/templates.stg");
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

    @Test
    public void testIndividualInClass() {
        String expected = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX ontology: <http://example.com/ontology.owl#>\n" +
                "\n" +
                "ASK WHERE {\n" +
                "  { ontology:individualName a ontology:classOne }\n" +
                "}";

        ST template = templates.getInstanceOf("individualInClasses");
        template.add("prefix", "ontology");
        template.add("namespace", "http://example.com/ontology.owl#");
        template.add("individualName", "individualName");
        template.add("classes", Arrays.asList("classOne"));

        Assert.assertEquals(expected, template.render());
    }

    @Test
    public void testIndividualInClasses() {
        String expected = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX ontology: <http://example.com/ontology.owl#>\n" +
                "\n" +
                "ASK WHERE {\n" +
                "  { ontology:individualName a ontology:classOne } UNION\n" +
                "  { ontology:individualName a ontology:classTwo } UNION\n" +
                "  { ontology:individualName a ontology:classThree }\n" +
                "}";

        ST template = templates.getInstanceOf("individualInClasses");
        template.add("prefix", "ontology");
        template.add("namespace", "http://example.com/ontology.owl#");
        template.add("individualName", "individualName");
        template.add("classes", Arrays.asList("classOne", "classTwo", "classThree"));

        Assert.assertEquals(expected, template.render());
    }
}
