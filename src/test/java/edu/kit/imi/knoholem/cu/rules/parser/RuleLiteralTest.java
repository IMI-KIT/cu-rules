package edu.kit.imi.knoholem.cu.rules.parser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class RuleLiteralTest {

    private RuleLiteral ruleLiteral;

    @Before
    public void setUp() {
        String ruleLiteral = "IF ZoneID= RC0.13ATRMGRND ^ Weight= 0.00 ^ Type= Tot_Cool_Reduc ^ Reduction= 5.00% ^ 6>= 14.31 ^ 6<= 15.04 THEN Temperature_Set= 16.09";
        this.ruleLiteral = new RuleLiteral(ruleLiteral, RuleParserConfiguration.getDefaultConfiguration());
    }

    @Test
    public void testGetAntecedentAtoms() {
        Assert.assertArrayEquals(new String[] { "6>= 14.31", "6<= 15.04" }, ruleLiteral.getAntecedentAtoms().toArray());
    }

    @Test
    public void testGetConsequentAtoms() {
        Assert.assertArrayEquals(new String[] { "Temperature_Set= 16.09" }, ruleLiteral.getConsequentAtoms().toArray());
    }

    @Test
    public void testZoneId() {
        Assert.assertEquals("ZoneID= RC0.13ATRMGRND", ruleLiteral.getZoneIdAtom());
    }

    @Test
    public void testWeight() {
        Assert.assertEquals("Weight= 0.00", ruleLiteral.getRuleWeightAtom());
    }

    @Test
    public void testType() {
        Assert.assertEquals("Type= Tot_Cool_Reduc", ruleLiteral.getRuleTypeAtom());
    }

    @Test
    public void testReduction() {
        Assert.assertEquals("Reduction= 5.00%", ruleLiteral.getReductionAtom());
    }

}