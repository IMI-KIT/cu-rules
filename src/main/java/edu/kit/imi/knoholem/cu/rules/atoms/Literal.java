package edu.kit.imi.knoholem.cu.rules.atoms;

/**
 * A literal is a building block of a {@link Predicate}.
 *
 * @author <a href="mailto:kiril.tonev@kit.edu">Kiril Tonev</a>
 */
public class Literal {

    private final String value;

    Literal(String value) {
        this.value = value;
    }

    public String asString() {
        return value;
    }

    public boolean canAsDouble() {
        try {
            asDouble();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Double asDouble() {
        return Double.parseDouble(value);
    }

    public Integer asInteger() {
        return (int) Double.parseDouble(value);
    }

    public boolean canAsInteger() {
        try {
            asDouble();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String toString() {
        return this.getClass().getSimpleName() + "{ " + "value=\"" + value + "\"" + " }";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        Literal other = (Literal) obj;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

}
