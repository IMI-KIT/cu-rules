package edu.kit.imi.knoholem.cu.rules.ontology;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class RuleAnnotatorTest {

    @Test
    public void testRuleId() {
        Assert.assertEquals("#RULEID000001", new RuleAnnotator(null).createRuleId(1));
    }

}
