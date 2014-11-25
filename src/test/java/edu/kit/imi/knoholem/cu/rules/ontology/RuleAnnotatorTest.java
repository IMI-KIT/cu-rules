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

    @Test
    public void testReadableSensorType() {
        RuleAnnotator annotator = new RuleAnnotator(null);
        Assert.assertEquals("", annotator.readableSensorType(null));
        Assert.assertEquals("", annotator.readableSensorType(""));
        Assert.assertEquals("temperature set point", annotator.readableSensorType("TemperatureSetPoint"));
        Assert.assertEquals("temperature setpoint", annotator.readableSensorType("TemperatureSetpoint"));
        Assert.assertEquals("wind sensor", annotator.readableSensorType("WindSensor"));
        Assert.assertEquals("sensor", annotator.readableSensorType("Sensor"));
    }
}
