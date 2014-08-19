package edu.kit.imi.knoholem.cu.rules.rulesconversion;

import edu.kit.imi.knoholem.cu.rules.atoms.Predicate;

public abstract class SWRLConverterConfiguration {

    public static final String HAS_BINARY_VALUE = "hasBinaryValue";
    public static final String HAS_ANALOG_VALUE = "hasAnalogValue";

    final static SWRLConverterConfiguration defaultConfiguration = new DefaultSWRLConverterConfiguration();

    public static SWRLConverterConfiguration getDefaultConfiguration() {
        return defaultConfiguration;
    }

    public abstract String sensorClass(Predicate predicate);

    public abstract String sensorValueProperty(Predicate predicate);

    public boolean isToggable(Predicate predicate) {
        String representativeClass = sensorClass(predicate);
        String individualName = predicate.getLeftOperand().asString();

        if (representativeClass == null) {
            throw new IllegalArgumentException("Class membership of individual undefined: " + individualName);
        }

        return ToggableActuators.names().contains(representativeClass);
    }

    String sensorValueProperty(String sensorClass) {
        if (ToggableActuators.names().contains(sensorClass)) {
            return HAS_BINARY_VALUE;
        } else {
            return HAS_ANALOG_VALUE;
        }
    }
}
