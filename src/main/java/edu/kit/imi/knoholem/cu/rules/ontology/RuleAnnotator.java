package edu.kit.imi.knoholem.cu.rules.ontology;

import edu.kit.imi.knoholem.cu.rules.atoms.Predicate;
import edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rules.functions.Function;
import edu.kit.imi.knoholem.cu.rules.rulesconversion.OntologySWRLConverterConfiguration;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class RuleAnnotator implements Function<SensitivityAnalysisRule, Set<OWLAnnotation>> {

    private final OntologyContext ontology;
    private final OntologySWRLConverterConfiguration configuration;

    private int ruleCounter;

    public RuleAnnotator(OntologyContext ontologyContext) {
        this(ontologyContext, 0);
    }

    public RuleAnnotator(OntologyContext ontologyContext, int initialCount) {
        this.ontology = ontologyContext;
        this.configuration = new OntologySWRLConverterConfiguration(ontology);
        this.ruleCounter = initialCount;
    }

    @Override
    public Set<OWLAnnotation> apply(SensitivityAnalysisRule input) {
        Set<OWLAnnotation> annotations = new HashSet<OWLAnnotation>();
        annotations.add(ruleIDAnnotation(incrementRuleId()));
        annotations.add(ruleSuggestionAnnotation(getSuggestionText(input)));
        annotations.add(ruleWeightAnnotation(input.getMetadata().getWeight()));
        annotations.add(ruleZoneIdAnnotation(input.getMetadata().getZoneId()));
        annotations.add(ruleReductionAnnotation((float) input.getMetadata().getReduction().doubleValue()));
        annotations.add(ruleTypeAnnotation(input.getMetadata().getType()));
        return annotations;
    }

    public int incrementRuleId() {
        ruleCounter++;
        return ruleCounter;
    }

    protected String getSuggestionText(SensitivityAnalysisRule rule) {
        Predicate predicate = rule.getConsequent().iterator().next();
        String individualName = predicate.getLeftOperand().asString();
        String sensorType = readableSensorType(ontology.getRepresentativeClass(individualName));
        if (configuration.isToggable(predicate)) {
            String toggleValue = predicate.getRightOperand().asDouble() == 0d ? "off" : "on";
            return "Switch " + sensorType + " \"" + individualName + "\" " + toggleValue + ".";
        } else {
            return "Set " + sensorType + " \"" + individualName + "\" to \"" + predicate.getRightOperand().asString() + "\".";
        }
    }

    protected String readableSensorType(String representativeClass) {
        if (representativeClass == null) {
            return "";
        } else {
            StringBuilder result = new StringBuilder();
            for (int index = 0; index < representativeClass.length(); index++) {
                char currentCharacter = representativeClass.charAt(index);
                if (Character.isUpperCase(currentCharacter)) {
                    result.append(" ");
                }
                result.append(Character.toLowerCase(currentCharacter));
            }
            return result.toString().trim();
        }
    }

    protected String createRuleId(int ruleId) {
        return "#RULEID" + String.format("%06d", ruleId);
    }

    protected String suggestionPropertyName() {
        return "hasSuggestion";
    }

    protected String reductionPropertyName() {
        return "hasReduction";
    }

    protected String weightPropertyName() {
        return "hasWeight";
    }

    private String ruleTypePropertyName() {
        return "hasRuleType";
    }

    private String zoneIdPropertyName() {
        return "hasZoneId";
    }

    private OWLAnnotation ruleIDAnnotation(int ruleId) {
        return ontology.getFactory().getOWLAnnotation(
                ontology.getFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()),
                ontology.getFactory().getOWLLiteral(createRuleId(ruleId)));
    }

    private OWLAnnotation ruleZoneIdAnnotation(String zoneId) {
        return ontology.getFactory().getOWLAnnotation(
                ontology.getFactory().getOWLAnnotationProperty(ontology.iri(zoneIdPropertyName())),
                ontology.getFactory().getOWLLiteral(zoneId));
    }

    private OWLAnnotation ruleReductionAnnotation(Float reductionRate) {
        return ontology.getFactory().getOWLAnnotation(
                ontology.getFactory().getOWLAnnotationProperty(ontology.iri(reductionPropertyName())),
                ontology.getFactory().getOWLLiteral(reductionRate));
    }

    private OWLAnnotation ruleTypeAnnotation(String ruleType) {
        return ontology.getFactory().getOWLAnnotation(
                ontology.getFactory().getOWLAnnotationProperty(ontology.iri(ruleTypePropertyName())),
                ontology.getFactory().getOWLLiteral(ruleType));
    }

    private OWLAnnotation ruleSuggestionAnnotation(String suggestionText) {
        return ontology.getFactory().getOWLAnnotation(
                ontology.getFactory().getOWLAnnotationProperty(ontology.iri(suggestionPropertyName())),
                ontology.getFactory().getOWLLiteral(suggestionText));
    }

    private OWLAnnotation ruleWeightAnnotation(double weight) {
        return ontology.getFactory().getOWLAnnotation(
                ontology.getFactory().getOWLAnnotationProperty(ontology.iri(weightPropertyName())),
                ontology.getFactory().getOWLLiteral(weight));
    }
}
