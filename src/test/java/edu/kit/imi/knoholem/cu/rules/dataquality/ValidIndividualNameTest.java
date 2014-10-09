package edu.kit.imi.knoholem.cu.rules.dataquality;

import edu.kit.imi.knoholem.cu.rules.functions.Function;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Kiril Tonev</a>
 */
public class ValidIndividualNameTest {

    @Test
    public void testInvalidIndividualName() {
        Assert.assertFalse(new ValidIndividualName().apply("Pb1839 OS17 2 GRFMET 9"));
    }

    private static class ValidIndividualName implements Function<String, Boolean> {
        @Override
        public Boolean apply(String input) {
            boolean containsWhitespace = false;
            for (int i = 0; i < input.length() && !containsWhitespace; i++) {
                if (Character.isWhitespace(input.charAt(i))) {
                    containsWhitespace = true;
                }
            }
            return !containsWhitespace;
        }
    }
}
