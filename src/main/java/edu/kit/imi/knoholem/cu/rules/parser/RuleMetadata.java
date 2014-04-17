package edu.kit.imi.knoholem.cu.rules.parser;

import java.util.Calendar;

/**
 * The rule metadata contains the calendar date, on which the rule has to be
 * triggered, its type, weight and reduction rate.
 * 
 * @see SensitivityAnalysisRule
 * @author <a href="mailto:kiril.tonev@kit.edu">Kiril Tonev</a>
 */
public class RuleMetadata {

    private final String zoneId;
	private final Calendar date;
	private final String type;
	private final Double weight;
	private final Double reduction;

	/**
	 * Metadata constructor.
	 * 
	 * @param date
	 * @param type
	 * @param weight
	 * @param reduction
	 */
	public RuleMetadata(String zoneId, Calendar date, String type, Double weight, Double reduction) {
        this.zoneId = zoneId;
		this.date = date;
		this.type = type;
		this.weight = weight;
		this.reduction = reduction;
	}

    public String getZoneId() {
        return zoneId;
    }

    /**
	 * Returns the rule date. The {@link Calendar} object returned has only its
	 * month, day and hour fields set.
	 * 
	 * @return a calendar object.
	 */
	public Calendar getDate() {
		return date;
	}

	/**
	 * The type of the rule, indicating the reduction it affects.
	 * 
	 * @return a string indicating the reduction type of the rule.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns the rule importance in the range 0..100.
	 * 
	 * @return the rule importance.
	 */
	public Double getWeight() {
		return weight;
	}

	/**
	 * Returns the rate (in percentega) which the rule targets.
	 * 
	 * @return the reduction rate of the rule.
	 */
	public Double getReduction() {
		return reduction;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RuleMetadata that = (RuleMetadata) o;

        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (reduction != null ? !reduction.equals(that.reduction) : that.reduction != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (weight != null ? !weight.equals(that.weight) : that.weight != null) return false;
        if (zoneId != null ? !zoneId.equals(that.zoneId) : that.zoneId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = zoneId != null ? zoneId.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (weight != null ? weight.hashCode() : 0);
        result = 31 * result + (reduction != null ? reduction.hashCode() : 0);
        return result;
    }

    @Override
	public String toString() {
		return getClass().getSimpleName() + "{ "
                + "zoneId=\"" + zoneId + "\"" + " "
				+ "weight=" + weight + " "
				+ "date=" + date + " "
				+ "type=\"" + type + "\"" + " "
				+ "reduction=" + reduction + " "
				+ "}";
	}

}
