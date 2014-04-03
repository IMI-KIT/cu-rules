package edu.kit.imi.knoholem.cu.rulesconversion;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Test;

import edu.kit.imi.knoholem.cu.rules.parser.DefaultRuleParserConfiguration;
import edu.kit.imi.knoholem.cu.rules.parser.RuleParser;
import edu.kit.imi.knoholem.cu.rules.parser.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rulesconversion.SWRLConverterConfiguration;
import edu.kit.imi.knoholem.ontology.KnoHolEMOntology;

public class BigTestCase {

	private static final String ruleLiteral = "IF ZoneID= RC0.13ATRMGRND ^ Weight= 0.00 ^ Type= Tot_Cool_Reduc ^ Reduction= 5.00% ^ Month= 7.00 ^ Day= 1.00 ^ Hour= 8.00 ^ 6>= 14.31 ^ 6<= 15.04 ^ 7>= 9.34 ^ 7<= 9.81 ^ 8>= 98475.00 ^ 8<= 103525.00 ^ 9>= 4.36 ^ 9<= 4.59 ^ 10>= 219.38 ^ 10<= 230.63 ^ 11>= 164.04 ^ 11<= 172.46 ^ 12>= 7.80 ^ 12<= 8.20 ^ 13>= 84.66 ^ 13<= 89.00 ^ 14>= 27.13 ^ 14<= 28.52 ^ 2221>= 0.00 ^ 2221<= 0.00 ^ 2223>= 0.02 ^ 2223<= 0.02 ^ 2226>= 0.04 ^ 2226<= 0.04 ^ 2241>=  0.00 ^ 2241<= 0.00 ^ 2248>= 0.00 ^ 2248<= 0.00 ^ 2257>= 0.00 ^ 2257<= 0.00 ^ 2258>=  0.02 ^ 2258<=  0.02 ^ 3277>=  0.00 ^ 3277<= 0.00 ^ 5555>= 0.00 ^ 5555<= 0.00 ^ 5565>= 0.00 ^ 5565<= 0.00 ^ 5805>= 0.00 ^ 5805<= 0.00 ^ 5825>= 0.00 ^ 5825<= 0.00 ^ 5835>= 0.00 ^ 5835<= 0.00 ^ Occupancy>= 19.50 ^ Occupancy<= 20.50 THEN Temperature_Set= 16.09";
	private static final String ontologySource = null;
	private static final String ontologyTarget = null;

	@Test
	public void test() {
		KnoHolEMOntology.getInstance().setOntologySource(ontologySource);
		KnoHolEMOntology.getInstance().setOntologyTarget(ontologyTarget);
		KnoHolEMOntology.getInstance().loadOWLOntology();
		
		Properties mappingProps = getDefaultMappingProperties();
		RuleParser parser = new RuleParser(new DefaultRuleParserConfiguration(), mappingProps);
		SensitivityAnalysisRule rule = parser.parseRule(ruleLiteral);
		assertEquals(1, rule.getConsequent().size());
		assertNotNull(rule);
		
		KnoHolEMOntology.getInstance().setOntologySource(ontologySource);
		KnoHolEMOntology.getInstance().setOntologyTarget(ontologyTarget);
		KnoHolEMOntology.getInstance().loadOWLOntology();
		SWRLRuleExporter swrlRuleExporter = new SWRLRuleExporter(KnoHolEMOntology.getInstance(), SWRLConverterConfiguration.getDefaultConfiguration());
		org.semanticweb.owlapi.model.SWRLRule swrlRule = swrlRuleExporter.exportRuleToOntology(rule);
		KnoHolEMOntology.getInstance().saveToOntologyTarget();
		System.out.println(swrlRule.toString());
	}
	
	
	private Properties getDefaultMappingProperties(){
		Properties props = new Properties();
		InputStream file = getClass().getResourceAsStream("/app.properties");
		System.out.println(file == null ? "Application properties file not found." : "Application properties file found");
		try {
			props.load(file);
		} catch (IOException e) {e.printStackTrace(); }
		return props;
	}

}
