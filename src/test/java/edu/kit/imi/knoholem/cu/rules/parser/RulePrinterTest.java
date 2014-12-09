package edu.kit.imi.knoholem.cu.rules.parser;

import edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Kiril Tonev</a>
 */
public class RulePrinterTest {

    private String ruleLiteral;
    private SensitivityAnalysisRule rule;
    private RulePrinter rulePrinter;

    @Before
    public void setUp() throws Exception {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);

        RuleParserConfiguration configuration = RuleParserConfiguration.getDefaultConfiguration();
        this.ruleLiteral = "IF ZoneID=RC0.13ATRMGRND ^ Weight=0.00 ^ Type=Tot_Cool_Reduc ^ Reduction=5.00% ^ 6>=14.31 ^ 6<=15.04 THEN Temperature_Set=16.09";
        this.rulePrinter = new RulePrinter(configuration, numberFormat);
        this.rule = new RuleParser(configuration).parseRule(ruleLiteral);
    }

    @Test
    public void testZoneIdToken() {
        Assert.assertEquals("ZoneID=RC0.13ATRMGRND", rulePrinter.zoneIdAtom(rule));
    }

    @Test
    public void testWeightToken() {
        Assert.assertEquals("Weight=0.00", rulePrinter.weightLiteral(rule));
    }

    @Test
    public void testReductionToken() {
        Assert.assertEquals("Reduction=5.00%", rulePrinter.reductionLiteral(rule));
    }

    @Test
    public void testTypeToken() {
        Assert.assertEquals("Type=Tot_Cool_Reduc", rulePrinter.typeLiteral(rule));
    }

    @Test
    public void testPrintRuleLiteral() {
        Assert.assertEquals(ruleLiteral, rulePrinter.ruleLiteral(rule));
    }
}
