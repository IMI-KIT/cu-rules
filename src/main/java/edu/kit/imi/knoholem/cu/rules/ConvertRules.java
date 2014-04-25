package edu.kit.imi.knoholem.cu.rules;

import edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rules.functions.Collect;
import edu.kit.imi.knoholem.cu.rules.parser.RuleParseError;
import edu.kit.imi.knoholem.cu.rules.parser.RuleParserConfiguration;
import edu.kit.imi.knoholem.cu.rules.parser.processing.RuleFileParser;
import edu.kit.imi.knoholem.cu.rules.parser.processing.RuleProcessor;
import edu.kit.imi.knoholem.cu.rules.parser.processing.RuleProcessorResponse;
import edu.kit.imi.knoholem.cu.rules.rulesconversion.SWRLConverter;
import edu.kit.imi.knoholem.cu.rules.rulesconversion.SWRLConverterConfiguration;
import edu.kit.imi.knoholem.cu.rules.rulesconversion.SWRLRule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * Main library class. Useful for sampling the rules which the library produces.
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
            System.err.println("Please, provide at least one path to the sensitivity analysis rules.");
            System.exit(1);
        }

        Minigun minigun = new Minigun(Arrays.asList(args));

        ConvertPrint processor = new ConvertPrint(SWRLConverterConfiguration.getDefaultConfiguration(), 100, System.out);
        minigun.process(RuleParserConfiguration.getDefaultConfiguration(), processor);

        System.err.printf("\n---\nRules converted: %d\nErrors encountered: %d\n",
                processor.getRuleCount(), processor.getFailCount());

        System.err.printf("\nLast %d errors:\n", processor.getFailCapacity());
        for (RuleParseError error : processor.getErrors()) {
            System.err.println(error.getMessage() + " (" + error.getRuleLiteral() + ")");
        }
    }

    /**
     * A utility for processing rule files passed from the command line.
     */
    static class Minigun {

        private final List<File> ruleFiles;

        Minigun(List<String> filePaths) {
            this.ruleFiles = new ArrayList<File>(filePaths.size());
            for (String path : filePaths) {
                ruleFiles.add(new File(path));
            }
        }

        /**
         * Processes each given file sequentially, logging the activity on the standard out.
         *
         * @param configuration the rule parser configuration to use.
         * @param processor     the processor to be invoked on each file.
         * @throws IOException in case of a read failure.
         */
        public void process(RuleParserConfiguration configuration, RuleProcessor processor) throws IOException {
            for (File file : ruleFiles) {
                System.err.println("Parsing rules in " + file.getName());
                RuleFileParser parser = new RuleFileParser(file, configuration);
                try {
                    parser.process(processor);
                } catch (FileNotFoundException e) {
                    System.err.println("File " + file.getName() + " not found!");
                }
            }
        }
    }

    /**
     * A utility for gathering the rules distributed in multiple files. This works with
     * {@link edu.kit.imi.knoholem.cu.rules.ConvertRules.Minigun}, so it pollutes the standard error.
     */
    static class MultipleRuleFileParser {

        private final List<String> ruleFiles;
        private final RuleParserConfiguration configuration;
        private final int rulesCap;
        private final int errorsCap;

        /**
         * Default constructor. Collects all encountered rules using the default rule parser configuration.
         *
         * @param ruleFiles paths to the files to parse.
         */
        MultipleRuleFileParser(List<String> ruleFiles) {
            this(-1, -1, RuleParserConfiguration.getDefaultConfiguration(), ruleFiles);
        }

        /**
         * Instantiates a parser that collects all encountered rules using a given a rule parser configuration.
         *
         * @param configuration rule parser configuration.
         * @param ruleFiles     paths to the files to parse.
         */
        MultipleRuleFileParser(RuleParserConfiguration configuration, List<String> ruleFiles) {
            this(-1, -1, configuration, ruleFiles);
        }

        /**
         * Instantiates a parser that collects a limited number of encountered rules with a parser configuration.
         *
         * @param rulesCap      the maximum number of rules to collect. A negative number collects all encountered rules.
         * @param errorsCap     the maximum number of errors to buffer. A negative number collects all encountered errors.
         * @param configuration rule parser configuration.
         * @param ruleFiles     paths to the files to parse.
         */
        MultipleRuleFileParser(int rulesCap, int errorsCap, RuleParserConfiguration configuration, List<String> ruleFiles) {
            this.rulesCap = rulesCap;
            this.errorsCap = errorsCap;

            this.configuration = configuration;
            this.ruleFiles = ruleFiles;
        }

        /**
         * Processes the given files and returns a {@link edu.kit.imi.knoholem.cu.rules.functions.Collect} instance with the gathered rules.
         *
         * @return a collector instance.
         * @throws IOException in case of a failure when reading a given file.
         */
        public Collect execute() throws IOException {
            Collect processor = new Collect(rulesCap, errorsCap);
            new Minigun(ruleFiles).process(configuration, processor);
            return processor;
        }

    }

    /**
     * A rule processor that converts each parsed rule and prints it out on a stream. It also collects encountered
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
         * @param failCapacity           maximal numbers of errors to buffer.
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
            return RuleProcessorResponse.OK;
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