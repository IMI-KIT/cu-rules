package edu.kit.imi.knoholem.cu.rules.parser;

import edu.kit.imi.knoholem.cu.rules.atoms.Predicate;
import edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RuleParserTest {

    private static final String ruleLiteral = "IF ZoneID= RC0.13ATRMGRND ^ Weight= 0.00 ^ Type= Tot_Cool_Reduc ^ Reduction= 5.00% ^ Month= 7.00 ^ Day= 1.00 ^ Hour= 8.00 ^ 6>= 14.31 ^ 6<= 15.04 THEN Temperature_Set= 16.09";

    @Test
    public void testPredicateNumber() {
        RuleParser parser = new RuleParser(new DefaultRuleParserConfiguration());
        SensitivityAnalysisRule rule = parser.parseRule(ruleLiteral);
        assertEquals(2, rule.getAntecedent().size());
        assertEquals(1, rule.getConsequent().size());
    }

    @Test
    public void testPredicateContents() {
        RuleParser parser = new RuleParser(new DefaultRuleParserConfiguration());
        SensitivityAnalysisRule rule = parser.parseRule(ruleLiteral);
        assertTrue(rule.getAntecedent().contains(new Predicate("6", ">=", "14.31")));
        assertTrue(rule.getAntecedent().contains(new Predicate("6", "<=", "15.04")));
    }

    @Test
    public void testRuleMetadata() {
        RuleParser parser = new RuleParser(new DefaultRuleParserConfiguration());
        SensitivityAnalysisRule rule = parser.parseRule(ruleLiteral);
        assertEquals(6, rule.getMetadata().getDate().get(Calendar.MONTH));
        assertEquals(1, rule.getMetadata().getDate().get(Calendar.DAY_OF_MONTH));
        assertEquals(8, rule.getMetadata().getDate().get(Calendar.HOUR));
        assertEquals(5.00, rule.getMetadata().getReduction(), 0.001);
        assertEquals(0, rule.getMetadata().getWeight(), 0.001);
        assertEquals("Tot_Cool_Reduc", rule.getMetadata().getType());
    }

}
