package edu.kit.imi.knoholem.cu.rules.parser;

import java.io.*;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class FileParser {

    private final File file;
    private final RuleParserConfiguration configuration;

    public FileParser(File file, RuleParserConfiguration configuration) {
        this.file = file;
        this.configuration = configuration;
    }

    public void process(RuleProcessor ruleProcessor) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

        for (String ruleLiteral = reader.readLine(); ruleLiteral != null; ruleLiteral = reader.readLine()) {
            RuleParser parser = new RuleParser(configuration);
            try {
                ruleProcessor.onParse(parser.parseRule(ruleLiteral));
            } catch (RuleParseError error) {
                ruleProcessor.onError(ruleLiteral, error);
            }
        }

        reader.close();
    }
}
