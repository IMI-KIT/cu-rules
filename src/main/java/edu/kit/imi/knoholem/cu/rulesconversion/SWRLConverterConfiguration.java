package edu.kit.imi.knoholem.cu.rulesconversion;

public abstract class SWRLConverterConfiguration {

	final static SWRLConverterConfiguration defaultConfiguration = new DefaultSWRLConverterConfiguration();

	public static SWRLConverterConfiguration getDefaultConfiguration() {
		return defaultConfiguration;
	}

	public abstract String zoneKey();
	public abstract String occupancyKey();

	public abstract String zoneClass();
	public abstract String sensorClass();
	public abstract String sensorValueProperty();
	public abstract String occupancySensorClass();
	public abstract String occupancySensorProperty();

}
