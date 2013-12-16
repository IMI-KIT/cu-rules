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
	public RuleMetadata(Calendar date, String type, Double weight, Double reduction) {
		this.date = date;
		this.type = type;
		this.weight = weight;
		this.reduction = reduction;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((reduction == null) ? 0 : reduction.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((weight == null) ? 0 : weight.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RuleMetadata other = (RuleMetadata) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (reduction == null) {
			if (other.reduction != null)
				return false;
		} else if (!reduction.equals(other.reduction))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (weight == null) {
			if (other.weight != null)
				return false;
		} else if (!weight.equals(other.weight))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(getClass().getSimpleName()).append("{")
				.append("weight=").append(weight)
				.append(" date=").append(date)
				.append(" type=\"").append(type).append("\"")
				.append(" reduction=").append(reduction)
				.append("}").toString();
	}

}
