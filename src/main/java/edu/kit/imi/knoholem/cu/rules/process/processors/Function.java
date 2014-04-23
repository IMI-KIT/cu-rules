package edu.kit.imi.knoholem.cu.rules.process.processors;

/**
 * Maps an input to an output object.
 *
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public interface Function<I, O> {

    /**
     * Maps an input to an output object.
     *
     * @param input function input
     * @return function output.
     */
    public O apply(I input);

}
