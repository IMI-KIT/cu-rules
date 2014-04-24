package edu.kit.imi.knoholem.cu.rules;

import edu.kit.imi.knoholem.cu.rules.ontology.*;
import edu.kit.imi.knoholem.cu.rules.parser.RuleParserConfiguration;
import edu.kit.imi.knoholem.cu.rules.parser.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rules.process.RuleFileParser;
import edu.kit.imi.knoholem.cu.rules.process.processors.Collect;
import edu.kit.imi.knoholem.cu.rules.process.processors.Monad;
import edu.kit.imi.knoholem.cu.rulesconversion.OntologySWRLConverterConfiguration;
import edu.kit.imi.knoholem.cu.rulesconversion.RuleConverter;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.SWRLRule;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class ExportRules {

    public static void main(String[] args) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        // Arguments
        File ontologyFile = new File(args[0]);
        File exportFile = new File(args[1]);
        List<String> files = Arrays.asList(args).subList(2, args.length);

        // Initialization
        OntologyContext ontology = OntologyContext.load(ontologyFile);
        Collect collector = new Collect();
        KnownEntities knownEntities = new KnownEntities(ontology);
        ClassifiedEntities classifiedEntities = new ClassifiedEntities(ontology);
        RuleConverter converter = new RuleConverter(new OntologySWRLConverterConfiguration(ontology));
        RuleAnnotator annotator = new RuleAnnotator(ontology);
        OWLBinding owlBinding = new OWLBinding(ontology, annotator);
        RuleExporter exporter = new RuleExporter(ontology);

        // Processing
        for (String filePath : files) {
            File ruleFile = new File(filePath);
            System.err.println("Parsing rules in " + ruleFile.getName());
            RuleFileParser parser = new RuleFileParser(ruleFile, RuleParserConfiguration.getDefaultConfiguration());
            parser.process(collector);
        }
        Monad<SensitivityAnalysisRule> collectedRules = collector.getRules();
        System.err.println("Collected " + collectedRules.size() + " sensitivity analysis rules.");
        System.err.println("Ignored " + collector.getErrors().size() + " erroneous rules.");

        Monad<SWRLRule> swrlRules = collectedRules.select(knownEntities).select(classifiedEntities).map(converter).map(owlBinding);

        System.err.println("Collected " + swrlRules.size() + " SWRL rules.");
        swrlRules.each(exporter);

        // Export
        ontology.saveOntologyAs(exportFile);
    }

}
