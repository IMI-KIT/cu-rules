package edu.kit.imi.knoholem.cu.rulesconversion;

import edu.kit.imi.knoholem.cu.rules.ontology.OntologyContext;
import edu.kit.imi.knoholem.cu.rules.parser.Predicate;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class OntologySWRLConverterConfiguration extends SWRLConverterConfiguration {

    private final OntologyContext ontology;

    public OntologySWRLConverterConfiguration(OntologyContext ontology) {
        this.ontology = ontology;
    }

    @Override
    public String sensorClass(Predicate predicate) {
        String individualName = predicate.getLeftOperand().asString();

        if (!ontology.containsIndividual(individualName)) {
            return ontology.getRepresentativeClass(individualName);
        } else {
            return null;
        }
    }

    @Override
    public String sensorValueProperty(Predicate predicate) {
        if (sensorClass(predicate).equals("OccupancySensor") || sensorClass(predicate).equals("OpeningSensor")) {
            return "hasBinaryValue";
        } else {
            return "hasAnalogValue";
        }
    }

}
