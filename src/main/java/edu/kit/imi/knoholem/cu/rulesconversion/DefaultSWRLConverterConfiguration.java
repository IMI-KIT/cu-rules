package edu.kit.imi.knoholem.cu.rulesconversion;

public class DefaultSWRLConverterConfiguration extends SWRLConverterConfiguration {

	@Override
	public String zoneKey() {
		return "ZoneID";
	}

	@Override
	public String occupancyKey() {
		return "Occupancy";
	}

	@Override
	public String zoneClass() {
		return "Zone";
	}

	@Override
	public String sensorClass() {
		return "Sensor";
	}

	@Override
	public String sensorValueProperty() {
		return "hasAnalogValue";
	}

	@Override
	public String occupancySensorClass() {
		return "OccupancySensor";
	}

	@Override
	public String occupancySensorProperty() {
		return "hasBinaryValue";
	}

}
