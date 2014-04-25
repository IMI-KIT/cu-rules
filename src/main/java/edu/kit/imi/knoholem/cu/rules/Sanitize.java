package edu.kit.imi.knoholem.cu.rules;

import edu.kit.imi.knoholem.cu.rules.atoms.Literal;
import edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rules.atoms.processing.RuleSubjects;
import edu.kit.imi.knoholem.cu.rules.functions.Monad;
import edu.kit.imi.knoholem.cu.rules.functions.Monads;
import edu.kit.imi.knoholem.cu.rules.ontology.OntologyContext;
import edu.kit.imi.knoholem.cu.rules.ontology.rulesprocessing.ClassifiedEntity;
import edu.kit.imi.knoholem.cu.rules.ontology.rulesprocessing.KnownEntity;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Prints the individuals referenced in a set of rules that are not found in the signature of a given ontology or that
 * don't have defined classes.
 *
 * <p>
 * CLI usage:
 * <code>
 *     java -cp &lt;path_to_jar&gt; Sanitize &lt;ontology_file&gt; &lt;rule_file&gt;...
 * </code>
 * </p>
 *
 * <p>The API exposes the said offenders through monads.</p>
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class Sanitize {

    private final OntologyContext ontologyContext;
    private final Monad<SensitivityAnalysisRule> rules;

    private boolean processed;

    public Sanitize(OntologyContext ontologyContext, Collection<? extends SensitivityAnalysisRule> rules) {
        this.ontologyContext = ontologyContext;
        this.rules = Monads.list(rules);
    }

    public Sanitize(OntologyContext ontologyContext, Monad<SensitivityAnalysisRule> rules) {
        this.ontologyContext = ontologyContext;
        this.rules = rules;
    }

    public Monad<Literal> getSubjects() {
        return rules.flatMap(new RuleSubjects());
    }

    public Monad<Literal> getUnknownEntities() {
        KnownEntity knownEntities = new KnownEntity(ontologyContext);
        return getSubjects().reject(knownEntities).unique();

    }

    public Monad<Literal> getUnclassifiedEntities() {
        KnownEntity knownEntities = new KnownEntity(ontologyContext);
        ClassifiedEntity classifiedEntities = new ClassifiedEntity(ontologyContext);

        return getSubjects().select(knownEntities).reject(classifiedEntities).unique();
    }

    /**
     * Main method.
     *
     * @param args a path to an existing ontology document as a first argument and paths to existing rules as consequent varargs.
     * @throws OWLOntologyCreationException if the ontology could not be loaded.
     * @throws IOException                  in case of IO error when reading the rule files.
     */
    public static void main(String[] args) throws OWLOntologyCreationException, IOException {
        // Arguments
        File ontologyFile = new File(args[0]);
        List<String> files = Arrays.asList(args).subList(1, args.length);

        // Initialization
        OntologyContext ontology = OntologyContext.load(ontologyFile);

        ConvertRules.MultipleRuleFileParser parser = new ConvertRules.MultipleRuleFileParser(files);
        Monad<SensitivityAnalysisRule> collectedRules = parser.execute().getRules();
        Sanitize sanitize = new Sanitize(ontology, collectedRules);

        System.err.println("Collected " + collectedRules.size() + " sensitivity analysis rules.");

        System.out.println("Unknown entities:");
        for (Literal literal : sanitize.getUnknownEntities().getElements()) {
            System.out.printf("\t%s\n", literal.asString());
        }

        System.out.println("Unclassified entities:");
        for (Literal literal : sanitize.getUnclassifiedEntities().getElements()) {
            System.out.printf("\t%s\n", literal.asString());
        }

    }

}
