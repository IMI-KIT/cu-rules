package edu.kit.imi.knoholem.cu.rules;

import edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rules.parser.RuleParseError;
import edu.kit.imi.knoholem.cu.rules.parser.RuleParserConfiguration;
import edu.kit.imi.knoholem.cu.rules.parser.processing.RuleFileParser;
import edu.kit.imi.knoholem.cu.rules.parser.processing.RuleProcessor;
import edu.kit.imi.knoholem.cu.rules.parser.processing.RuleProcessorResponse;
import edu.kit.imi.knoholem.cu.rules.rulesconversion.SWRLConverter;
import edu.kit.imi.knoholem.cu.rules.rulesconversion.SWRLConverterConfiguration;
import edu.kit.imi.knoholem.cu.rules.rulesconversion.SWRLRule;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Main library class.
 *
 * @see edu.kit.imi.knoholem.cu.rules.ConvertRules::main(String[])
 */
public class ConvertRules {

    /**
     * Reads a file containing sensitivity analysis rules, converts them using the default configuration, and prints
     * each converted SWRL rule on a new line.
     *
     * @param args a path to a text file containing the rules.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Please, provide a path to the sensitivity analysis rules.");
            System.exit(1);
        }

        File file = new File(args[0]);
        RuleFileParser fileParser = new RuleFileParser(file, RuleParserConfiguration.getDefaultConfiguration());
        ConvertPrint processor = new ConvertPrint(SWRLConverterConfiguration.getDefaultConfiguration(), 100, System.out);

        fileParser.process(processor);

        System.err.printf("\n---\nRules converted: %d\nErrors encountered: %d\n", processor.getRuleCount(), processor.getFailCount());

        System.err.printf("\nLast %d errors:\n", processor.getFailCapacity());
        for (RuleParseError error : processor.getErrors()) {
            System.err.println(error.getMessage() + " (" + error.getRuleLiteral() + ")");
        }
    }

    /**
     * A rule processor that converts each parsed rule and prints it on the standard out. It also collects encountered
     * errors for later reference.
     */
    static class ConvertPrint implements RuleProcessor {

        private int ruleCount;
        private int failCount;

        private final PrintStream stream;
        private final int failCapacity;
        private final LinkedList<RuleParseError> failList;
        private final SWRLConverter converter;

        /**
         * Default constructor.
         *
         * @param converterConfiguration the SWRL converter configuration to use.
         * @param failCapacity maximal numbers of errors to buffer.
         */
        ConvertPrint(SWRLConverterConfiguration converterConfiguration, int failCapacity, PrintStream stream) {
            this.ruleCount = 0;
            this.failCount = 0;

            this.stream = stream;
            this.converter = new SWRLConverter(converterConfiguration);
            this.failCapacity = failCapacity;
            this.failList = new LinkedList<RuleParseError>();
        }

        @Override
        public RuleProcessorResponse onParse(SensitivityAnalysisRule rule) {
            SWRLRule swrlRule = converter.convertRule(rule);
            stream.println(swrlRule.getExpression());
            ruleCount++;
            return new Response(swrlRule);
        }

        @Override
        public RuleProcessorResponse onError(RuleParseError error) {
            if (failList.size() >= failCapacity) {
                failList.poll();
            }
            failList.push(error);
            failCount++;
            return new Response(null);
        }

        /**
         * Returns the encountered number of errors.
         *
         * @return the encountered errors stat.
         */
        public int getFailCount() {
            return failCount;
        }

        /**
         * Returns the error buffer size setting.
         *
         * @return the maximal number of errors in buffer.
         */
        public int getFailCapacity() {
            return failCapacity;
        }

        /**
         * Returns the rule count stat.
         *
         * @return number of rules that have been successfully processed.
         */
        public int getRuleCount() {
            return ruleCount;
        }

        /**
         * Returns the errors encountered so far.
         *
         * @return an unmodifiable list of the encountered errors buffered in the processor.
         */
        public List<RuleParseError> getErrors() {
            return Collections.unmodifiableList(failList);
        }

        private class Response extends RuleProcessorResponse {

            private final Object data;

            private Response(Object data) {
                this.data = data;
            }

            @Override
            public Object getData() {
                return data;
            }

            @Override
            public boolean canContinue() {
                return true;
            }
        }
    }

}