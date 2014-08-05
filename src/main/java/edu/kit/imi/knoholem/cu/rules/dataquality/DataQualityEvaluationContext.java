package edu.kit.imi.knoholem.cu.rules.dataquality;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import edu.kit.imi.knoholem.cu.rules.functions.Function;
import edu.kit.imi.knoholem.cu.rules.ontology.OntologyContext;
import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.nio.file.Paths;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class DataQualityEvaluationContext implements Function<Criterion, Boolean> {

    private final OntologyContext ontologyContext;
    private final Model model;

    public DataQualityEvaluationContext(OntologyContext ontologyContext) {
        this.ontologyContext = ontologyContext;

        this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
        URI uri = ontologyContext.getDocumentIRI().toURI();
        FileInputStream fio;
        try {
            fio = new FileInputStream(Paths.get(uri).toFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.model.read(fio, ontologyContext.getOntologyNamespace(), getDocumentLanguage());
    }

    @Override
    public Boolean apply(Criterion input) {
        Query query = QueryFactory.create(input.asAskQuery(ontologyContext));
        QueryExecution queryExecution = QueryExecutionFactory.create(query, model);

        boolean result = queryExecution.execAsk();
        queryExecution.close();
        return result;
    }

    String getDocumentLanguage() {
        if (ontologyContext.getOntologyFormat().equals(new TurtleOntologyFormat())) {
            return "TURTLE";
        } else if (ontologyContext.getOntologyFormat().equals(new RDFXMLOntologyFormat())) {
            return "RDF/XML";
        } else {
            throw new RuntimeException("Ontology format not supported: " + ontologyContext.getOntologyFormat());
        }
    }
}
