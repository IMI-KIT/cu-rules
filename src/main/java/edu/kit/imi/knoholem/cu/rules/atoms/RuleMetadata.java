package edu.kit.imi.knoholem.cu.rules.atoms;

/**
 * The rule metadata contains the calendar date, on which the rule has to be
 * triggered, its type, weight and reduction rate.
 *
 * @author <a href="mailto:kiril.tonev@kit.edu">Kiril Tonev</a>
 * @see SensitivityAnalysisRule
 */
public class RuleMetadata {

    private final String zoneId;
    private final String type;
    private final Double weight;
    private final Double reduction;

    /**
     * Metadata constructor.
     *
     * @param type
     * @param weight
     * @param reduction
     */
    public RuleMetadata(String zoneId, String type, Double weight, Double reduction) {
        this.zoneId = zoneId;
        this.type = type;
        this.weight = weight;
        this.reduction = reduction;
    }

    public String getZoneId() {
        return zoneId;
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

        if (!reduction.equals(that.reduction)) return false;
        if (!type.equals(that.type)) return false;
        if (!weight.equals(that.weight)) return false;
        if (!zoneId.equals(that.zoneId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = zoneId.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + weight.hashCode();
        result = 31 * result + reduction.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RuleMetadata{" +
                "zoneId='" + zoneId + '\'' +
                ", type='" + type + '\'' +
                ", weight=" + weight +
                ", reduction=" + reduction +
                '}';
    }
}
