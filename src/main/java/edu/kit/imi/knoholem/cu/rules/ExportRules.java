package edu.kit.imi.knoholem.cu.rules;

import edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rules.functions.Collect;
import edu.kit.imi.knoholem.cu.rules.functions.Monad;
import edu.kit.imi.knoholem.cu.rules.functions.Monads;
import edu.kit.imi.knoholem.cu.rules.ontology.OWLBinding;
import edu.kit.imi.knoholem.cu.rules.ontology.OntologyContext;
import edu.kit.imi.knoholem.cu.rules.ontology.RuleAnnotator;
import edu.kit.imi.knoholem.cu.rules.ontology.rulesprocessing.ClassifiedEntities;
import edu.kit.imi.knoholem.cu.rules.ontology.rulesprocessing.KnownEntities;
import edu.kit.imi.knoholem.cu.rules.ontology.rulesprocessing.RuleExporter;
import edu.kit.imi.knoholem.cu.rules.rulesconversion.OntologySWRLConverterConfiguration;
import edu.kit.imi.knoholem.cu.rules.rulesconversion.RuleConverter;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.SWRLRule;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Converts a set of sensitivity analysis rules to SWRL rules and saves them in an ontology.
 * <p>
 * CLI usage:
 * <code>
 *     java -cp &lt;path_to_jar&gt; ExportRules &lt;ontology_file&gt; &lt;export_file&gt; &lt;rule_file&gt;...
 * </code>
 * </p>
 *
 * <p>
 *     The API provides convenience methods for converting a list of sensitivity analysis rules to SWRL rules.
 * </p>
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class ExportRules {

    private final OntologyContext ontology;
    private final Monad<SensitivityAnalysisRule> rules;

    public ExportRules(OntologyContext ontology, Monad<SensitivityAnalysisRule> rules) {
        this.ontology = ontology;
        this.rules = rules;
    }

    public ExportRules(OntologyContext ontology, Collection<? extends SensitivityAnalysisRule> rules) {
        this.ontology = ontology;
        this.rules = Monads.list(rules);
    }
    public Monad<SensitivityAnalysisRule> getSensitivityAnalysisRules() {
        KnownEntities knownEntities = new KnownEntities(ontology);
        ClassifiedEntities classifiedEntities = new ClassifiedEntities(ontology);

        return rules.select(knownEntities).select(classifiedEntities);
    }

    public Monad<SWRLRule> getSWRLRules() {
        RuleConverter converter = new RuleConverter(new OntologySWRLConverterConfiguration(ontology));
        RuleAnnotator annotator = new RuleAnnotator(ontology);
        OWLBinding owlBinding = new OWLBinding(ontology, annotator);

        return getSensitivityAnalysisRules().map(converter).map(owlBinding);
    }

    public static void main(String[] args) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        // Arguments
        File ontologyFile = new File(args[0]);
        File exportFile = new File(args[1]);
        List<String> files = Arrays.asList(args).subList(2, args.length);

        // Initialization
        OntologyContext ontology = OntologyContext.load(ontologyFile);
        RuleExporter exporter = new RuleExporter(ontology);

        // Processing
        Collect collector = new ConvertRules.MultipleRuleFileParser(files).execute();
        System.err.println("Collected " + collector.getRules().size() + " sensitivity analysis rules.");
        System.err.println("Ignored " + collector.getErrors().size() + " erroneous rules.");

        Monad<SWRLRule> swrlRules = new ExportRules(ontology, collector.getRules()).getSWRLRules();

        System.err.println("Collected " + swrlRules.size() + " SWRL rules.");
        swrlRules.each(exporter);

        // Export
        ontology.saveOntologyAs(exportFile);
    }

}
