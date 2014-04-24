package edu.kit.imi.knoholem.cu.rules.process.processors;

import java.util.Collection;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public interface Monad<T> {

    public <O> Monad<O> map(Function<T, O> function);
    public Monad<T> select(Function<T, Boolean> predicate);
    public Monad<T> reject(Function<T, Boolean> predicate);
    public void each(Function<T, ?> function);
    public Monad<T> take(int howMany);
    public Monad<T> take();
    public Collection<? extends T> getElements();
    public boolean isSingular();
    public boolean any();
    public boolean isEmpty();
    public int size();

}
