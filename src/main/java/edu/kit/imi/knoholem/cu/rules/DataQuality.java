package edu.kit.imi.knoholem.cu.rules;

import edu.kit.imi.knoholem.cu.rules.dataquality.*;
import edu.kit.imi.knoholem.cu.rules.functions.Collect;
import edu.kit.imi.knoholem.cu.rules.functions.Function;
import edu.kit.imi.knoholem.cu.rules.functions.Monad;
import edu.kit.imi.knoholem.cu.rules.functions.Monads;
import edu.kit.imi.knoholem.cu.rules.ontology.OntologyContext;
import edu.kit.imi.knoholem.cu.rules.parser.RuleParserConfiguration;
import edu.kit.imi.knoholem.cu.rules.rulesconversion.OntologySWRLConverterConfiguration;
import edu.kit.imi.knoholem.cu.rules.rulesconversion.RuleConverter;
import edu.kit.imi.knoholem.cu.rules.rulesconversion.SWRLRule;
import edu.kit.imi.knoholem.cu.rules.swrlentities.ClassAtom;
import edu.kit.imi.knoholem.cu.rules.swrlentities.Individual;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Performs checks on the validity of the assumptions posed by a set of sensitivity analysis rules against an ontology:
 *
 * <ul>
 * <li>every individual referenced in the rule is declared in the ontology</li>
 * <li>individuals are member of the classes asserted in the rules</li>
 * <li>individuals in the rules' consequents are members of the setpoint class.</li>
 * </ul>
 *
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class DataQuality {

    public static final String SET_POINT = "SetPoint";

    private final RuleQuery ruleQuery;
    private final Templates templates;

    private final DataQualityEvaluationContext evaluationContext;

    public DataQuality(Templates templates, OntologyContext ontologyContext) {
        this.ruleQuery = new RuleQuery();
        this.templates = templates;
        this.evaluationContext = new DataQualityEvaluationContext(ontologyContext);
    }

    public Monad<Criterion> ruleCriteria(Monad<SWRLRule> input) {
        Set<Criterion> criteria = new HashSet<Criterion>();

        for (SWRLRule rule : input.getElements()) {
            criteria.addAll(declaredIndividualsSet(rule));
            criteria.addAll(individualInClassSet(rule));
            criteria.addAll(setpointsSet(rule));
        }

        return Monads.list(criteria);
    }

    public Monad<Criterion> getOffences(Monad<SWRLRule> rules) {
        return ruleCriteria(rules).reject(evaluationContext);
    }

    private Set<DeclaredIndividual> declaredIndividualsSet(SWRLRule rule) {
        Set<DeclaredIndividual> result = new HashSet<DeclaredIndividual>();

        for (Individual individual : ruleQuery.getIndividuals(rule)) {
            result.add(new DeclaredIndividual(individual.getIndividualName(), templates));
        }

        return result;
    }

    private Set<IndividualInClass> individualInClassSet(SWRLRule rule) {
        Set<IndividualInClass> result = new HashSet<IndividualInClass>();

        for (ClassAtom atom : ruleQuery.getClassAtomsWithIndividuals(rule)) {
            Individual individual = (Individual) atom.getOperand();
            String className = atom.getAtomName();
            result.add(new IndividualInClass(className, individual.getIndividualName(), templates));
        }

        return result;
    }

    private Set<IndividualInClass> setpointsSet(SWRLRule rule) {
        Set<IndividualInClass> result = new HashSet<IndividualInClass>();

        for (Individual individual : ruleQuery.getIndividualsInConsequent(rule)) {
            result.add(new IndividualInClass(SET_POINT, individual.getIndividualName(), templates));
        }

        return result;
    }

    /**
     * Prints the offences detected in the rule lists on the standard out.
     *
     * @param args <ul>
     *             <li><code>args[0]</code>: a path to an existing ontology document,</li>
     *             <li><code>args[1..]</code>: paths to existing rules listings.</li>
     *             </ul>
     * @throws OWLOntologyCreationException if the ontology could not be loaded.
     * @throws IOException                  in case of an IO error when reading the rule files.
     */
    public static void main(String[] args) throws OWLOntologyCreationException, IOException {
        if (args.length < 2) {
            System.err.println("Please, provide at least 2 arguments.");
            System.exit(1);
        }

        Templates templates = Templates.loadTemplates();
        RuleParserConfiguration parserConfiguration = RuleParserConfiguration.getDefaultConfiguration();

        OntologyContext ontologyContext = OntologyContext.load(new File(args[0]));

        Collect collect = new ConvertRules.MultipleRuleFileParser(parserConfiguration, Arrays.asList(args).subList(1, args.length)).execute();
        RuleConverter ruleConverter = new RuleConverter(new OntologySWRLConverterConfiguration(ontologyContext));
        Monad<SWRLRule> swrlRules = collect.getRules().map(ruleConverter);

        new DataQuality(templates, ontologyContext).getOffences(swrlRules).each(new OffencesReporter(System.out));
    }

    static class OffencesReporter implements Function<Criterion, Object> {

        private final PrintStream printStream;

        OffencesReporter(PrintStream printStream) {
            this.printStream = printStream;
        }

        @Override
        public Object apply(Criterion input) {
            report(input);
            return null;
        }

        public void report(Criterion criterion) {
            if (criterion instanceof IndividualInClass) {
                report((IndividualInClass) criterion);
            } else if (criterion instanceof DeclaredIndividual) {
                report((DeclaredIndividual) criterion);
            } else {
                printStream.println("Offended criterion " + criterion.toString());
            }
        }

        public void report(IndividualInClass individualInClass) {
            printStream.println("Individual '" + individualInClass.getIndividualName() + "' is not in '" + individualInClass.getClassName() + "'.");
        }

        public void report(DeclaredIndividual declaredIndividual) {
            printStream.println("Individual '" + declaredIndividual.getIndividualName() + "' is not declared in the ontology.");
        }
    }
}
