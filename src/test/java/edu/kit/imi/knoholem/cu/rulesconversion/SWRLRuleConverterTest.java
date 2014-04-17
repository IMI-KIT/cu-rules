package edu.kit.imi.knoholem.cu.rulesconversion;

import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.kit.imi.knoholem.cu.rules.logicalentities.Atom;
import edu.kit.imi.knoholem.cu.rules.logicalentities.ClassAtom;
import edu.kit.imi.knoholem.cu.rules.logicalentities.Individual;
import edu.kit.imi.knoholem.cu.rules.logicalentities.PropertyAtom;
import edu.kit.imi.knoholem.cu.rules.logicalentities.Unknown;
import edu.kit.imi.knoholem.cu.rules.logicalentities.Value;
import edu.kit.imi.knoholem.cu.rules.parser.RuleParser;
import edu.kit.imi.knoholem.cu.rules.parser.RuleParserConfiguration;

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
