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
 * <p>
 * Converts a set of sensitivity analysis rules to SWRL rules and saves them in an ontology.
 * </p>
 *
 * <p>
 * CLI usage:
 * <code>
 * java -cp &lt;path_to_jar&gt; ExportRules &lt;ontology_file&gt; &lt;export_file&gt; &lt;rule_file&gt;...
 * </code>
 * </p>
 *
 * <p>
 * The API provides convenience methods for converting a list of sensitivity analysis rules to SWRL rules.
 * </p>
 *
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class ExportRules {

    private final OntologyContext ontology;
    private final Monad<SensitivityAnalysisRule> rules;

    /**
     * Default constructor.
     *
     * @param ontology ontology document used when transforming the rules (resolves class membership and property relations).
     * @param rules    the rules to transform.
     */
    public ExportRules(OntologyContext ontology, Monad<SensitivityAnalysisRule> rules) {
        this.ontology = ontology;
        this.rules = rules;
    }

    /**
     * Default constructor.
     *
     * @param ontology ontology document used when transforming the rules (resolves class membership and property relations).
     * @param rules    the rules to transform.
     */
    public ExportRules(OntologyContext ontology, Collection<? extends SensitivityAnalysisRule> rules) {
        this.ontology = ontology;
        this.rules = Monads.list(rules);
    }

    /**
     * Returns a subset of the rules which are covered by the ontology.
     *
     * @return a sublist of the rules with the unknown and unclassified individuals subtracted.
     */
    public Monad<SensitivityAnalysisRule> getSensitivityAnalysisRules() {
        KnownEntities knownEntities = new KnownEntities(ontology);
        ClassifiedEntities classifiedEntities = new ClassifiedEntities(ontology);

        return rules.select(knownEntities).select(classifiedEntities);
    }

    /**
     * Returns a collection of SWRL rules.
     *
     * @return the rules returned by {@link #getSensitivityAnalysisRules()} converted in SWRL rules.
     */
    public Monad<SWRLRule> getSWRLRules() {
        RuleConverter converter = new RuleConverter(new OntologySWRLConverterConfiguration(ontology));
        RuleAnnotator annotator = new RuleAnnotator(ontology);
        OWLBinding owlBinding = new OWLBinding(ontology, annotator);

        return getSensitivityAnalysisRules().map(converter).map(owlBinding);
    }

    /**
     * Main method.
     *
     * @param args a path to the reference ontology as a first argument, a pathname to
     *             export the rules to as a second argument and the input rule files as subsequent varargs.
     * @throws OWLOntologyCreationException
     * @throws IOException                  indicates an error when reading from a the rules files.
     * @throws OWLOntologyStorageException  indicates an error when writing the rules.
     */
    public static void main(String[] args) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        if (args.length < 3) {
            System.err.println("Please, provide at least 3 arguments.");
        }

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
