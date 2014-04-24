package edu.kit.imi.knoholem.cu.rules.rulesconversion;

import edu.kit.imi.knoholem.cu.rules.atoms.Predicate;

class DefaultSWRLConverterConfiguration extends SWRLConverterConfiguration {

    @Override
    public String sensorClass(Predicate predicate) {
        return "Sensor";
    }

    @Override
    public String sensorValueProperty(Predicate predicate) {
        return "hasAnalogValue";
    }

}
