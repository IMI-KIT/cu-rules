package edu.kit.imi.knoholem.cu.rules.ontology;

import edu.kit.imi.knoholem.cu.rules.atoms.Operator;
import edu.kit.imi.knoholem.cu.rules.atoms.Predicate;
import edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rules.atoms.processing.PredicateMap;
import edu.kit.imi.knoholem.cu.rules.atoms.processing.PredicateMapEntry;
import edu.kit.imi.knoholem.cu.rules.functions.Function;
import edu.kit.imi.knoholem.cu.rules.rulesconversion.SWRLConverterConfiguration;
import edu.kit.imi.knoholem.cu.rules.rulesconversion.SWRLConverterError;
import edu.kit.imi.knoholem.cu.rules.rulesconversion.Unknowns;
import edu.kit.imi.knoholem.cu.rules.swrlentities.Unknown;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.SWRLBuiltInsVocabulary;

import java.util.*;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class OWLBinding implements Function<SensitivityAnalysisRule, SWRLRule> {

    private final OntologyContext context;
    private final SWRLConverterConfiguration configuration;
    private final Function<SensitivityAnalysisRule, Set<OWLAnnotation>> ruleAnnotations;

    public OWLBinding(OntologyContext context, SWRLConverterConfiguration configuration,
                      Function<SensitivityAnalysisRule, Set<OWLAnnotation>> ruleAnnotations) {
        this.context = context;
        this.configuration = configuration;
        this.ruleAnnotations = ruleAnnotations;
    }

    @Override
    public SWRLRule apply(SensitivityAnalysisRule input) {
        try {
            Unknowns unknowns = new Unknowns();
            Set<SWRLAtom> antecedentAtoms = collectAtoms(input.getAntecedent(), unknowns);
            Set<SWRLAtom> consequentAtoms = collectConsequent(input);
            Set<OWLAnnotation> annotations = ruleAnnotations.apply(input);

            return context.getFactory().getSWRLRule(antecedentAtoms, consequentAtoms, annotations);
        } catch (Exception e) {
            throw new SWRLConverterError(input, e);
        }
    }

    private Set<SWRLAtom> collectConsequent(SensitivityAnalysisRule rule) {
        Predicate predicate = rule.getConsequent().iterator().next();
        String individualName = predicate.getLeftOperand().asString();
        String className = configuration.sensorClass(predicate);
        return Collections.<SWRLAtom>singleton(classAtom(className, individualName));
    }

    private Set<SWRLAtom> collectAtoms(List<Predicate> predicates, Unknowns unknowns) {
        Set<SWRLAtom> result = new HashSet<SWRLAtom>();

        for (PredicateMapEntry entry : new PredicateMap(predicates).byLeftOperand()) {
                result.addAll(convertSensorValuePredicates(entry, unknowns));
        }

        return result;
    }

    private List<SWRLAtom> convertSensorValuePredicates(PredicateMapEntry predicates, Unknowns unknowns) {
        List<SWRLAtom> atoms = new LinkedList<SWRLAtom>();

        String className = configuration.sensorClass(predicates.getFirstPredicate());
        String propertyName = configuration.sensorValueProperty(predicates.getFirstPredicate());
        String individualName = predicates.getClassifier().asString();

        Unknown unknown = unknowns.nextUnknown();

        atoms.add(classAtom(className, individualName));
        atoms.add(sensorValueProperty(propertyName, individualName, unknown));
        for (Predicate predicate : predicates.getPredicates()) {
            atoms.add(swrlBuiltIn(predicate, unknown));
        }

        return atoms;
    }

    private SWRLClassAtom classAtom(String className, String individualName) {
        if (className == null) {
            throw new IllegalArgumentException("Individual type not found: " + individualName);
        }

        SWRLIArgument argument = getNamedIndividualArgument(individualName);
        OWLClass owlClass = context.getOWLClass(className);
        return context.getFactory().getSWRLClassAtom(owlClass, argument);
    }

    private SWRLDataPropertyAtom sensorValueProperty(String propertyName, String individualName, Unknown unknown) {
        OWLDataProperty owlDataProperty = context.getDataProperty(propertyName);
        SWRLIArgument individualArgument = getNamedIndividualArgument(individualName);
        SWRLDArgument dataArgument = getVariableArgument(unknown);
        return context.getFactory().getSWRLDataPropertyAtom(owlDataProperty, individualArgument, dataArgument);
    }

    private SWRLAtom swrlBuiltIn(Predicate predicate, Unknown unknown) {
        List<SWRLDArgument> arguments = Arrays.asList(
                getVariableArgument(unknown),
                getLiteralArgument(configuration.sensorValueProperty(predicate), predicate.getRightOperand().asString()));
        return context.getFactory().getSWRLBuiltInAtom(getBuiltInIRI(predicate.getOperator()), arguments);

    }

    private SWRLDArgument getLiteralArgument(String propertyName, String value) {
        if (propertyName.equals("hasBinaryValue")) {
            return getBooleanLiteral(value);
        } else {
            return getDoubleLiteral(value);
        }
    }

    private SWRLLiteralArgument getDoubleLiteral(String value) {
        OWLLiteral literal = context.getFactory().getOWLLiteral(Double.parseDouble(value));
        return context.getFactory().getSWRLLiteralArgument(literal);
    }

    private SWRLLiteralArgument getBooleanLiteral(String value) {
        OWLLiteral literal = context.getFactory().getOWLLiteral(value.equals("1.00"));
        return context.getFactory().getSWRLLiteralArgument(literal);
    }

    private SWRLVariable getVariableArgument(Unknown unknown) {
        IRI variableIRI = context.iri(unknown.getName());
        return context.getFactory().getSWRLVariable(variableIRI);
    }

    private SWRLIndividualArgument getNamedIndividualArgument(String individualName) {
        OWLNamedIndividual owlIndividual = context.getIndividual(individualName);
        return context.getFactory().getSWRLIndividualArgument(owlIndividual);
    }

    private IRI getBuiltInIRI(Operator operator) {
        switch (operator) {
            case EQUAL: return SWRLBuiltInsVocabulary.EQUAL.getIRI();
            case LESS_THAN: return SWRLBuiltInsVocabulary.LESS_THAN.getIRI();
            case LESS_THAN_OR_EQUAL: return SWRLBuiltInsVocabulary.LESS_THAN_OR_EQUAL.getIRI();
            case GREATER_THAN: return SWRLBuiltInsVocabulary.GREATER_THAN.getIRI();
            case GREATER_THAN_OR_EQUAL: return SWRLBuiltInsVocabulary.GREATER_THAN_OR_EQUAL.getIRI();
            default: throw new IllegalArgumentException("Built in unknown: " + operator.name());
        }
    }

}
