package edu.kit.imi.knoholem.cu.rules;

import edu.kit.imi.knoholem.cu.rules.atoms.Literal;
import edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rules.atoms.processing.RuleSubjects;
import edu.kit.imi.knoholem.cu.rules.functions.Collect;
import edu.kit.imi.knoholem.cu.rules.functions.Monad;
import edu.kit.imi.knoholem.cu.rules.ontology.OntologyContext;
import edu.kit.imi.knoholem.cu.rules.ontology.rulesprocessing.ClassifiedEntity;
import edu.kit.imi.knoholem.cu.rules.ontology.rulesprocessing.KnownEntity;
import edu.kit.imi.knoholem.cu.rules.parser.RuleParserConfiguration;
import edu.kit.imi.knoholem.cu.rules.parser.processing.RuleFileParser;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class Sanitize {

    public static void main(String[] args) throws OWLOntologyCreationException, IOException {
        // Arguments
        File ontologyFile = new File(args[0]);
        List<String> files = Arrays.asList(args).subList(1, args.length);

        // Initialization
        OntologyContext ontology = OntologyContext.load(ontologyFile);
        Collect collector = new Collect();
        RuleSubjects ruleSubjectsExtractor = new RuleSubjects();
        KnownEntity knownEntityFilter = new KnownEntity(ontology);
        ClassifiedEntity classifiedEntityFilter = new ClassifiedEntity(ontology);

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

        Monad<Literal> subjects = collectedRules.flatMap(ruleSubjectsExtractor);

        System.out.println("Unknown entities:");
        for (Literal literal : subjects.reject(knownEntityFilter).unique().getElements()) {
            System.out.printf("\t%s\n", literal.asString());
        }

        System.out.println("Unclassified entities:");
        for (Literal literal : subjects.select(knownEntityFilter).reject(classifiedEntityFilter).unique().getElements()) {
            System.out.printf("\t%s\n", literal.asString());
        }

    }

}
