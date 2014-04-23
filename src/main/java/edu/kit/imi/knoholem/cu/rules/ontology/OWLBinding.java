package edu.kit.imi.knoholem.cu.rules.ontology;

import edu.kit.imi.knoholem.cu.rules.logicalentities.*;
import edu.kit.imi.knoholem.cu.rules.process.processors.Function;
import edu.kit.imi.knoholem.cu.rulesconversion.SWRLRule;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.SWRLBuiltInsVocabulary;

import java.util.*;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class OWLBinding implements Function<SWRLRule, org.semanticweb.owlapi.model.SWRLRule> {

    private final OntologyContext context;
    private final Function<SWRLRule, Set<OWLAnnotation>> ruleAnnotations;

    public OWLBinding(OntologyContext context, Function<SWRLRule, Set<OWLAnnotation>> ruleAnnotations) {
        this.context = context;
        this.ruleAnnotations = ruleAnnotations;
    }

    @Override
    public org.semanticweb.owlapi.model.SWRLRule apply(SWRLRule input) {
        Set<SWRLAtom> antecedentAtoms = collectAtoms(input.getAntecedent());
        Set<SWRLAtom> consequentAtoms = collectAtoms(input.getConsequent());
        Set<OWLAnnotation> annotations = ruleAnnotations.apply(input);
        return context.getFactory().getSWRLRule(antecedentAtoms, consequentAtoms, annotations);
    }

    private Set<SWRLAtom> collectAtoms(Collection<? extends Atom> inputAtoms) {
        Set<SWRLAtom> atoms = new HashSet<SWRLAtom>();
        for (Atom inputAtom : inputAtoms) {
            atoms.add(convertAtom(inputAtom));
        }
        return atoms;
    }

    private SWRLAtom convertAtom(Atom atom) {
        if (atom instanceof ClassAtom) {
            return convertClassAtom((ClassAtom) atom);
        } else if (atom instanceof SWRLBuiltIn) {
            return convertBuiltInAtom((SWRLBuiltIn) atom);
        } else if (atom instanceof PropertyAtom) {
            return convertPropertyAtom((PropertyAtom) atom);
        } else {
            throw new IllegalArgumentException("Atom type not supported: " + atom);
        }
    }

    private SWRLAtom convertBuiltInAtom(SWRLBuiltIn builtIn) {
        List<SWRLDArgument> arguments = Arrays.asList(
                getVariableArgument((Unknown) builtIn.getLeftOperand()),
                getAnalogSWRLDArgument((Value) builtIn.getRightOperand()));
        return context.getFactory().getSWRLBuiltInAtom(getBuiltInIRI(builtIn.getAtomName()), arguments);
    }

    private SWRLAtom convertClassAtom(ClassAtom atom) {
        SWRLIArgument argument = getSWRLIArgument(atom.getOperand());
        OWLClass owlClass = context.getFactory().getOWLClass(context.iri(atom.getAtomName()));
        return context.getFactory().getSWRLClassAtom(owlClass, argument);
    }

    private SWRLAtom convertPropertyAtom(PropertyAtom propertyAtom) {
        OWLDataProperty owlDataProperty = context.getFactory().getOWLDataProperty(context.iri(propertyAtom.getAtomName()));
        SWRLIArgument individualArgument = getSWRLIArgument(propertyAtom.getLeftOperand());
        SWRLDArgument dataArgument = getSWRLDArgument(propertyAtom);
        return context.getFactory().getSWRLDataPropertyAtom(owlDataProperty, individualArgument, dataArgument);
    }

    private SWRLDArgument getSWRLDArgument(PropertyAtom propertyAtom) {
        if (propertyAtom.getAtomName().equals("hasBinaryValue")) {
            return getBinarySWRLDArgument((Value) propertyAtom.getRightOperand());
        } else {
            return getAnalogSWRLDArgument((Value) propertyAtom.getRightOperand());
        }
    }

    private SWRLDArgument getAnalogSWRLDArgument(Value variable) {
        String value = variable.getValue();
        OWLLiteral literal = context.getFactory().getOWLLiteral(Double.parseDouble(value));
        return context.getFactory().getSWRLLiteralArgument(literal);
    }

    private SWRLDArgument getBinarySWRLDArgument(Value value) {
        String stringValue = value.getValue();
        OWLLiteral literal = context.getFactory().getOWLLiteral(stringValue.equals("1"));
        return context.getFactory().getSWRLLiteralArgument(literal);
    }

    private SWRLIArgument getSWRLIArgument(Variable variable) {
        if (variable instanceof Unknown) {
            return getVariableArgument((Unknown) variable);
        } else {
            return getNamedIndividualArgument((Individual) variable);
        }
    }

    private SWRLVariable getVariableArgument(Unknown unknown) {
        IRI variableIRI = context.iri(unknown.getName());
        return context.getFactory().getSWRLVariable(variableIRI);
    }

    private SWRLIndividualArgument getNamedIndividualArgument(Individual individual) {
        IRI individualIRI = context.iri(individual.getIndividualName());
        OWLNamedIndividual owlIndividual = context.getFactory().getOWLNamedIndividual(individualIRI);
        return context.getFactory().getSWRLIndividualArgument(owlIndividual);
    }

    private IRI getBuiltInIRI(String atomName) {
        if (atomName.equals(SWRLExpression.SWRL_EQUAL)) {
            return SWRLBuiltInsVocabulary.EQUAL.getIRI();
        } else if (atomName.equals(SWRLExpression.SWRL_GREATER_THAN)) {
            return SWRLBuiltInsVocabulary.GREATER_THAN.getIRI();
        } else if (atomName.equals(SWRLExpression.SWRL_GREATER_THAN_OR_EQUAL)) {
            return SWRLBuiltInsVocabulary.GREATER_THAN_OR_EQUAL.getIRI();
        } else if (atomName.equals(SWRLExpression.SWRL_LESS_THAN)) {
            return SWRLBuiltInsVocabulary.LESS_THAN.getIRI();
        } else if (atomName.equals(SWRLExpression.SWRL_LESS_THAN_OR_EQUAL)) {
            return SWRLBuiltInsVocabulary.LESS_THAN_OR_EQUAL.getIRI();
        } else {
            throw new IllegalArgumentException("Built in unknown: " + atomName);
        }
    }

}
