package edu.kit.imi.knoholem.cu.rulesconversion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLDArgument;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.vocab.SWRLBuiltInsVocabulary;

import edu.kit.imi.knoholem.cu.rules.parser.Operator;
import edu.kit.imi.knoholem.cu.rules.parser.Predicate;
import edu.kit.imi.knoholem.cu.rules.parser.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.ontology.KnoHolEMOntology;
import edu.kit.imi.knoholem.ontology.OntologyUtils;
import edu.kit.imi.knoholem.ontology.RuleCounter;

public class SWRLRuleExporter {

	private final KnoHolEMOntology ontology;
	private SWRLConverterConfiguration configuration;
	
	public SWRLRuleExporter(KnoHolEMOntology ontology, SWRLConverterConfiguration configuration) {
		this.ontology = ontology;
		this.configuration = configuration;
	}
	
	
	public SWRLRule exportRuleToOntology(SensitivityAnalysisRule rule) {
		SWRLRule convertedRule = convertRule(rule);
		ontology.getManager().applyChange(new AddAxiom(ontology.getOwlOntology(), convertedRule));
		return convertedRule;
	}


	private SWRLRule convertRule(SensitivityAnalysisRule rule) {
		SWRLRule convertedRule = null;

//			System.out.println(rule.toString());
			
			Set<SWRLAtom> ruleBody = new HashSet<>(collectAntecedent(rule.getAntecedent()));
			Set<SWRLAtom> ruleHead = new HashSet<>(collectConsequent(rule.getConsequent()));
			
			String suggestionText = rule.getMetadata().getType() + " = " + rule.getMetadata().getReduction();
			convertedRule = ontology.getFactory().getSWRLRule(ruleBody, ruleHead, ruleAnnotations(suggestionText, new Float(rule.getMetadata().getWeight())));

		return convertedRule;
	}

	
	
	private List<SWRLAtom> collectAntecedent(List<Predicate> inputAntecedent) {
		List<SWRLAtom> result = new ArrayList<SWRLAtom>();
		// classify the predicates
		// convert each predicate and return a list
		for (PredicateMapEntry entry: new PredicateMap(inputAntecedent).byLeftOperand()) {
			if (entry.isSingular()) {
				result.addAll(convertSensorValuePredicate(entry.getFirstPredicate()));
			} else {
				result.addAll(convertSensorValuePredicates(entry));
			}
		}
		return result;
	}

	
	private List<SWRLAtom> convertSensorValuePredicates(PredicateMapEntry predicates) {
		List<SWRLAtom> swrlAtoms = new LinkedList<SWRLAtom>();
		
		String indName = predicates.getClassifier().asString();
		OWLNamedIndividual individual = ontology.getFactory().getOWLNamedIndividual(IRI.create(OntologyUtils.ONTOLOGY_NS + indName));
		SWRLIndividualArgument individualArgument = ontology.getFactory().getSWRLIndividualArgument(individual);
		
		SWRLVariable swrlVariable = ontology.getFactory().getSWRLVariable(IRI.create(OntologyUtils.ONTOLOGY_NS + RuleCounter.getInstance().getNextVariable()));

		OWLClass individualClass = (OWLClass) ontology.getReasoner().getTypes(individual, true).getFlattened().toArray()[0];
		swrlAtoms.add(ontology.getFactory().getSWRLClassAtom(individualClass, individualArgument));
		
		String hasValueType = configuration.sensorValueProperty();
		OWLDataProperty dataProperty = ontology.getFactory().getOWLDataProperty(IRI.create(OntologyUtils.ONTOLOGY_NS + hasValueType));
		swrlAtoms.add(ontology.getFactory().getSWRLDataPropertyAtom(dataProperty, individualArgument, swrlVariable));
		
		for (Predicate predicate : predicates.getPredicates()) {
			List<SWRLDArgument> args = new ArrayList<SWRLDArgument>();
			args.add(swrlVariable);
			OWLLiteral valueLiteral = ontology.getFactory().getOWLLiteral(	new Float(predicate.getRightOperand().asString())	);
			args.add(ontology.getFactory().getSWRLLiteralArgument(valueLiteral));
			swrlAtoms.add(ontology.getFactory().getSWRLBuiltInAtom(builtIn(predicate.getOperator()), args));
		}

		return swrlAtoms;
	}

	
	private List<SWRLAtom> collectConsequent(List<Predicate> inputConsequent) {
		List<SWRLAtom> result = new ArrayList<SWRLAtom>();
		for (Predicate predicate: inputConsequent) {
			result.addAll(convertConsequentPredicate(predicate));
		}
		return result;
	}

	
	private List<SWRLAtom> convertConsequentPredicate(Predicate predicate) {
		List<SWRLAtom> result = new ArrayList<SWRLAtom>();
		
		String hasValueType = configuration.sensorValueProperty();
		OWLDataProperty dataProperty = ontology.getFactory().getOWLDataProperty(IRI.create(OntologyUtils.ONTOLOGY_NS + hasValueType));
		
		String indName = predicate.getLeftOperand().asString();
		OWLNamedIndividual individual = ontology.getFactory().getOWLNamedIndividual(IRI.create(OntologyUtils.ONTOLOGY_NS + indName));
		SWRLIndividualArgument individualArgument = ontology.getFactory().getSWRLIndividualArgument(individual);
		
		OWLLiteral valueLiteral = ontology.getFactory().getOWLLiteral(	new Float(predicate.getRightOperand().asString())	);
		SWRLDArgument valueArg = ontology.getFactory().getSWRLLiteralArgument(valueLiteral);

		result.add(ontology.getFactory().getSWRLDataPropertyAtom(dataProperty, individualArgument, valueArg));

		return result;
	}

	
	private List<SWRLAtom> convertSensorValuePredicate(Predicate predicate) {
		List<SWRLAtom> result = new LinkedList<SWRLAtom>();
		
		String indName = predicate.getLeftOperand().asString();
		OWLNamedIndividual individual = ontology.getFactory().getOWLNamedIndividual(IRI.create(OntologyUtils.ONTOLOGY_NS + indName));
		SWRLIndividualArgument individualArgument = ontology.getFactory().getSWRLIndividualArgument(individual);
		
		SWRLVariable swrlVariable = ontology.getFactory().getSWRLVariable(IRI.create(OntologyUtils.ONTOLOGY_NS + RuleCounter.getInstance().getNextVariable()));

		OWLClass individualClass = (OWLClass) ontology.getReasoner().getTypes(individual, true).getFlattened().toArray()[0];
		result.add(ontology.getFactory().getSWRLClassAtom(individualClass, individualArgument));
		
		String hasValueType = configuration.sensorValueProperty();
		OWLDataProperty dataProperty = ontology.getFactory().getOWLDataProperty(IRI.create(OntologyUtils.ONTOLOGY_NS + hasValueType));
		result.add(ontology.getFactory().getSWRLDataPropertyAtom(dataProperty, individualArgument, swrlVariable));
		
		List<SWRLDArgument> args = new ArrayList<SWRLDArgument>();
		args.add(swrlVariable);
		OWLLiteral valueLiteral = ontology.getFactory().getOWLLiteral(	new Float(predicate.getRightOperand().asString())	);
		args.add(ontology.getFactory().getSWRLLiteralArgument(valueLiteral));
		result.add(ontology.getFactory().getSWRLBuiltInAtom(builtIn(predicate.getOperator()), args));
		
		return result;
	}
	
	
	private IRI builtIn(Operator operator) {
		switch (operator) {
		case GREATER_THAN_OR_EQUAL: return SWRLBuiltInsVocabulary.GREATER_THAN_OR_EQUAL.getIRI();
		case GREATER_THAN: return SWRLBuiltInsVocabulary.GREATER_THAN.getIRI();
		case LESS_THAN_OR_EQUAL: return SWRLBuiltInsVocabulary.LESS_THAN_OR_EQUAL.getIRI();
		case LESS_THAN: return SWRLBuiltInsVocabulary.LESS_THAN.getIRI();
		case EQUAL: return SWRLBuiltInsVocabulary.EQUAL.getIRI();
		default: throw new RuntimeException();
		}
	}
	
	
	private Set<OWLAnnotation> ruleAnnotations(String suggestionText, float weight) {
		Set<OWLAnnotation> annotations = new HashSet<>(2);
		annotations.addAll(ruleIDAnnotation());
		annotations.addAll(ruleSuggestionAnnotation(suggestionText));
		annotations.addAll(ruleWeightAnnotation(weight));
		return annotations;
	}

	
	private Set<OWLAnnotation> ruleIDAnnotation() {
		OWLAnnotation idLabel = ontology.getFactory().getOWLAnnotation(
				ontology.getFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()),
				ontology.getFactory().getOWLLiteral(RuleCounter.getInstance().getNextRuleIDNumber()));
		return Collections.singleton(idLabel);
	}

	
	private Set<OWLAnnotation> ruleSuggestionAnnotation(String suggestionText) {
		OWLAnnotation suggestion = ontology.getFactory().getOWLAnnotation(
				ontology.getFactory().getOWLAnnotationProperty(IRI.create(OntologyUtils.ONTOLOGY_NS + "hasSuggestion")),
				ontology.getFactory().getOWLLiteral(suggestionText));
		return Collections.singleton(suggestion);
	}
	
	
	private Set<OWLAnnotation> ruleWeightAnnotation(float weight) {
		OWLAnnotation suggestion = ontology.getFactory().getOWLAnnotation(
				ontology.getFactory().getOWLAnnotationProperty(IRI.create(OntologyUtils.ONTOLOGY_NS + "hasWeight")),
				ontology.getFactory().getOWLLiteral(weight));
		return Collections.singleton(suggestion);
	}
}
