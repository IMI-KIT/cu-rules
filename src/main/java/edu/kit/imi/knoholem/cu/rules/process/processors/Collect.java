package edu.kit.imi.knoholem.cu.rules.process.processors;

import edu.kit.imi.knoholem.cu.rules.parser.RuleParseError;
import edu.kit.imi.knoholem.cu.rules.parser.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rules.process.RuleProcessor;
import edu.kit.imi.knoholem.cu.rules.process.RuleProcessorResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A rule processor which collects the inputs in a list.
 *
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class Collect implements RuleProcessor {

    private final int rulesCapacity;
    private final int errorsCapacity;

    private final List<SensitivityAnalysisRule> rules;
    private final LinkedList<RuleParseError> errors;

    /**
     * Collects <em>every</em> given rule and all encountered errors.
     */
    public Collect() {
        this(-1, -1);
    }

    /**
     * Default constructor.
     *
     * @param rulesCapacity  the number of rules to collect.
     *                       A positive integer will collect the first encountered rules while a negative will collect all given rules.
     * @param errorsCapacity the number of errors to collect.
     *                       A positive integer will collect only the last encountered errors while a negative will collect all encountered errors.
     */
    public Collect(int rulesCapacity, int errorsCapacity) {
        if (rulesCapacity < 0) {
            this.rules = new LinkedList<SensitivityAnalysisRule>();
        } else {
            this.rules = new ArrayList<SensitivityAnalysisRule>(rulesCapacity);
        }
        this.errors = new LinkedList<RuleParseError>();
        this.errorsCapacity = errorsCapacity;
        this.rulesCapacity = rulesCapacity;
    }

    @Override
    public RuleProcessorResponse onParse(SensitivityAnalysisRule rule) {
        if (rulesCapacity < 0) {
            rules.add(rule);
            return RuleProcessorResponse.OK;
        } else {
            if (rules.size() >= rulesCapacity) {
                return RuleProcessorResponse.HALT;
            } else {
                rules.add(rule);
                return RuleProcessorResponse.OK;
            }
        }
    }

    @Override
    public RuleProcessorResponse onError(RuleParseError error) {
        if (errorsCapacity > 0 && errors.size() >= errorsCapacity) {
            errors.poll();
        }
        errors.push(error);
        return RuleProcessorResponse.OK;
    }

    public Monad<SensitivityAnalysisRule> getRules() {
        return new ListMonad<SensitivityAnalysisRule>(rules);
    }

    public List<RuleParseError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

}
