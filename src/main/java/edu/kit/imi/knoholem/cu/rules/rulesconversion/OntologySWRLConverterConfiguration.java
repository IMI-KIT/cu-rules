package edu.kit.imi.knoholem.cu.rules.rulesconversion;

import edu.kit.imi.knoholem.cu.rules.atoms.Predicate;
import edu.kit.imi.knoholem.cu.rules.ontology.OntologyContext;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class OntologySWRLConverterConfiguration extends SWRLConverterConfiguration {

    private final OntologyContext ontology;

    public OntologySWRLConverterConfiguration(OntologyContext ontology) {
        this.ontology = ontology;
    }

    public OntologyContext getOntologyContext() {
        return ontology;
    }

    @Override
    public String sensorClass(Predicate predicate) {
        String individualName = predicate.getLeftOperand().asString();

        if (ontology.containsIndividual(individualName)) {
            return ontology.getRepresentativeClass(individualName);
        } else {
            return null;
        }
    }

    @Override
    public String sensorValueProperty(Predicate predicate) {
        String sensorClass = sensorClass(predicate);

        if (sensorClass == null) {
            throw new IllegalArgumentException("Class membership of individual undefined: " + predicate.getLeftOperand().asString());
        }

        return sensorValueProperty(sensorClass);
    }

    public boolean isToggable(String individualName) {
        String representativeClass = ontology.getRepresentativeClass(individualName);

        if (representativeClass == null) {
            throw new IllegalArgumentException("Class membership of individual undefined: " + individualName);
        }

        return ToggableSensors.names().contains(representativeClass);
    }

    String sensorValueProperty(String sensorClass) {
        if (ToggableSensors.names().contains(sensorClass)) {
            return "hasBinaryValue";
        } else {
            return "hasAnalogValue";
        }
    }
}
