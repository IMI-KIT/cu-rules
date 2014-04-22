package edu.kit.imi.knoholem.cu.rules.process.processors;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public interface Function<I, O> {
    public O apply(I input);
}
