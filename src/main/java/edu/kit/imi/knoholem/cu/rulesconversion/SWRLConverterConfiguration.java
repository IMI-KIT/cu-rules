package edu.kit.imi.knoholem.cu.rulesconversion;

import edu.kit.imi.knoholem.cu.rules.parser.Predicate;

public abstract class SWRLConverterConfiguration {

    final static SWRLConverterConfiguration defaultConfiguration = new DefaultSWRLConverterConfiguration();

    public static SWRLConverterConfiguration getDefaultConfiguration() {
        return defaultConfiguration;
    }

    public abstract String sensorClass(Predicate predicate);

    public abstract String sensorValueProperty(Predicate predicate);

}
