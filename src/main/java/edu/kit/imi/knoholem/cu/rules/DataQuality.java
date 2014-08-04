package edu.kit.imi.knoholem.cu.rules;

import edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule;
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
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class DataQuality {

    public static void main(String[] args) throws OWLOntologyCreationException, IOException {
        Templates templates = Templates.loadTemplates();
        RuleParserConfiguration parserConfiguration = RuleParserConfiguration.getDefaultConfiguration();
        OntologyContext ontologyContext = OntologyContext.load(new File(args[0]));
        DataQualityEvaluationContext evaluationContext = new DataQualityEvaluationContext(ontologyContext);

        Collect collect = new ConvertRules.MultipleRuleFileParser(parserConfiguration, Arrays.asList(args).subList(1, args.length)).execute();
        Monad<SWRLRule> swrlRules = new SensitivityAnalysisRulesConverter(ontologyContext, collect.getRules()).convertRules();
        Monad<Criterion> evaluatedCriteria = new RuleCriteria(templates).apply(swrlRules).reject(evaluationContext);
        evaluatedCriteria.each(new OffencesReporter(System.out));

        for (Individual individual : new ConsequentCollector().apply(swrlRules).getElements()) {
            System.out.println(individual.getIndividualName());
        }
    }

    static class RuleCriteria implements Function<Monad<SWRLRule>, Monad<Criterion>> {
        private final RuleQuery ruleQuery;
        private final Templates templates;

        RuleCriteria(Templates templates) {
            this.ruleQuery = new RuleQuery();
            this.templates = templates;
        }

        @Override
        public Monad<Criterion> apply(Monad<SWRLRule> input) {
            Set<Criterion> criteria = new HashSet<Criterion>();

            for (SWRLRule rule : input.getElements()) {
                criteria.addAll(declaredIndividualsSet(rule));
                criteria.addAll(individualInClassSet(rule));
            }

            return Monads.list(criteria);
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
    }

    static class SensitivityAnalysisRulesConverter {
        private final RuleConverter ruleConverter;
        private final Monad<SensitivityAnalysisRule> sensitivityAnalysisRules;

        /**
         * Default constructor.
         *
         * @param ontology ontology document used when transforming the rules (resolves class membership and property relations).
         * @param sensitivityAnalysisRules    the rules to transform.
         */
        public SensitivityAnalysisRulesConverter(OntologyContext ontology, Monad<SensitivityAnalysisRule> sensitivityAnalysisRules) {
            this.ruleConverter = new RuleConverter(new OntologySWRLConverterConfiguration(ontology));
            this.sensitivityAnalysisRules = sensitivityAnalysisRules;
        }

        public Monad<SWRLRule> convertRules() {
            return sensitivityAnalysisRules.map(ruleConverter);
        }
    }

    static class ConsequentCollector implements Function<Monad<SWRLRule>, Monad<Individual>> {

        private final RuleQuery ruleQuery;

        ConsequentCollector() {
            this.ruleQuery = new RuleQuery();
        }

        @Override
        public Monad<Individual> apply(Monad<SWRLRule> input) {
            Set<Individual> result = new HashSet<Individual>();

            for (SWRLRule rule : input.getElements()) {
                result.addAll(ruleQuery.getIndividualsInConsequent(rule));
            }

            return Monads.list(result);
        }
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
