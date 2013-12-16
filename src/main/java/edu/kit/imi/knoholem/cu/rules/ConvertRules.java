package edu.kit.imi.knoholem.cu.rules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import edu.kit.imi.knoholem.cu.rules.parser.RuleParser;
import edu.kit.imi.knoholem.cu.rules.parser.RuleParserConfiguration;
import edu.kit.imi.knoholem.cu.rules.parser.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rulesconversion.DefaultSWRLConverterConfiguration;
import edu.kit.imi.knoholem.cu.rulesconversion.SWRLConverter;
import edu.kit.imi.knoholem.cu.rulesconversion.SWRLRule;

public class ConvertRules {

	/**
	 * Reads a file containing sensitivity analysis rules, converts them, and prints 
	 * each converted SWRL rule on new line.
	 * 
	 * @param args a path to a text file containing the rules.
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("Please, provide a path to the sensitivity analysis rules.");
			System.exit(1);
		}
		File file = new File(args[0]);
		BufferedReader reader = new BufferedReader(new FileReader(file));

		RuleParser parser = new RuleParser(RuleParserConfiguration.getDefaultConfiguration());
		SWRLConverter converter = new SWRLConverter(new DefaultSWRLConverterConfiguration());

		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			String ruleLiteral = line.trim();
			SensitivityAnalysisRule rule = parser.parseRule(ruleLiteral);
			SWRLRule swrlRule = converter.convertRule(rule);
			System.out.println(swrlRule.getExpression());
		}

		reader.close();
	}

}
