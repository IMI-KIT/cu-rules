package edu.kit.imi.knoholem.cu.rules.parser.processing;

import edu.kit.imi.knoholem.cu.rules.parser.RuleParseError;
import edu.kit.imi.knoholem.cu.rules.parser.RuleParser;
import edu.kit.imi.knoholem.cu.rules.parser.RuleParserConfiguration;

import java.io.*;

/**
 * Parses the sensitivity analysis rules from a file passing each parsed rule to a {@link RuleProcessor}.
 *
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class RuleFileParser {

    private final File file;
    private final RuleParser ruleParser;

    /**
     * Default constructor.
     *
     * @param file                the file to process.
     * @param parserConfiguration the parser configuration to use.
     */
    public RuleFileParser(File file, RuleParserConfiguration parserConfiguration) {
        this.file = file;
        this.ruleParser = new RuleParser(parserConfiguration);
    }

    public RuleFileParser(File file, RuleParser ruleParser) {
        this.file = file;
        this.ruleParser = ruleParser;
    }

    /**
     * Parses each line of the given file, passing it to the rule processor.
     *
     * @param ruleProcessor the rule processor to invoke.
     * @throws java.io.FileNotFoundException if the file given to the parser doesn't exist.
     * @throws java.io.IOException           in case of read failure.
     */
    public void process(RuleProcessor ruleProcessor) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

        {
            RuleProcessorResponse response = RuleProcessorResponse.OK;
            for (String ruleLiteral = reader.readLine(); ruleLiteral != null && response.canContinue(); ruleLiteral = reader.readLine()) {
                try {
                    response = ruleProcessor.onParse(ruleParser.parseRule(ruleLiteral));
                } catch (RuleParseError error) {
                    response = ruleProcessor.onError(new RuleFileParserError(file, error));
                }
            }
        }

        reader.close();
    }
}