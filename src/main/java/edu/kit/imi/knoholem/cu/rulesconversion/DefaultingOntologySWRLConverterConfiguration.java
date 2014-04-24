package edu.kit.imi.knoholem.cu.rulesconversion;

import edu.kit.imi.knoholem.cu.rules.ontology.OntologyContext;
import edu.kit.imi.knoholem.cu.rules.parser.Predicate;

/**
 * An SWRL converter configuration which always returns a non-null sensor class.
 *
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class DefaultingOntologySWRLConverterConfiguration extends OntologySWRLConverterConfiguration {
    private final String defaultClass;

    public DefaultingOntologySWRLConverterConfiguration(OntologyContext ontology, String defaultClass) {
        super(ontology);
        this.defaultClass = defaultClass;
    }

    @Override
    public String sensorClass(Predicate predicate) {
        String superClass = super.sensorClass(predicate);
        if (superClass == null) {
            return defaultClass;
        } else {
            return superClass;
        }
    }

}
