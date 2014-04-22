package edu.kit.imi.knoholem.cu.rules.process;

/**
 * Encapsulates a rule processor response.
 *
 * @see edu.kit.imi.knoholem.cu.rules.process.RuleProcessor
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public abstract class RuleProcessorResponse {

    public static final RuleProcessorResponse OK = OK();
    public static final RuleProcessorResponse HALT = HALT();

    /**
     * Processed payload. Can be null.
     *
     * @return the data that has just been processed.
     */
    public abstract Object getData();

    /**
     * Indicates whether the parent processor can accept further input.
     *
     * @return <code>true</code>, if subsequent inputs should be given, <code>false</code> otherwise.
     */
    public abstract boolean canContinue();

    /**
     * Returns a new OK response.
     *
     * @return a generic, null response object that always continues.
     */
    public static RuleProcessorResponse OK() {
        return new RuleProcessorResponse() {
            @Override
            public Object getData() {
                return null;
            }

            @Override
            public boolean canContinue() {
                return true;
            }
        };
    }

    /**
     * Returns a new HALT response object.
     *
     * @return a generic, null response object which does not continue.
     */
    public static RuleProcessorResponse HALT() {
        return new RuleProcessorResponse() {
            @Override
            public Object getData() {
                return null;
            }

            @Override
            public boolean canContinue() {
                return false;
            }
        };
    }

}