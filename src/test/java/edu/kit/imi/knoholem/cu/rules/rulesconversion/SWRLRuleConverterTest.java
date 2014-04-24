package edu.kit.imi.knoholem.cu.rules.rulesconversion;

import edu.kit.imi.knoholem.cu.rules.parser.RuleParser;
import edu.kit.imi.knoholem.cu.rules.parser.RuleParserConfiguration;
import edu.kit.imi.knoholem.cu.rules.swrlentities.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;

public class SWRLRuleConverterTest {

	@Test
	public void testFiltersRepeatingAtoms() {
		String ruleLiteral = "IF ZoneID= ZONE ^ Weight= 0.00 ^ Type= Tot_Cool_Reduc ^ Reduction= 5.00% ^ Month= 7.00 ^ Day= 1.00 ^ Hour= 8.00 ^ 6>= 14.31 ^ 6<= 15.04 THEN Temperature_Set= 16.09";
		List<Atom> expectedAntecedent = new ArrayList<Atom>();
		expectedAntecedent.add(new ClassAtom("Sensor", new Individual("6")));
		expectedAntecedent.add(new PropertyAtom("hasAnalogValue", new Individual("6"), new Unknown("a")));
		expectedAntecedent.add(new PropertyAtom("greaterThanOrEqual", new Unknown("a"), new Value("14.31")));
		expectedAntecedent.add(new PropertyAtom("lessThanOrEqual", new Unknown("a"), new Value("15.04")));
		SWRLConverter converter = new SWRLConverter(SWRLConverterConfiguration.defaultConfiguration);
		RuleParser parser = new RuleParser(RuleParserConfiguration.getDefaultConfiguration());
		assertArrayEquals(expectedAntecedent.toArray(), converter.convertRule(parser.parseRule(ruleLiteral)).getAntecedent().toArray());
	}

}
