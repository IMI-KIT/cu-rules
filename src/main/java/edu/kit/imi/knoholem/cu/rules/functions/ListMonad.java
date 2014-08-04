package edu.kit.imi.knoholem.cu.rules.functions;

import java.util.*;

/**
 * A list monad implementation.
 *
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
class ListMonad<T> implements Monad<T> {

    private final List<T> elements;

    ListMonad(List<T> elements) {
        this.elements = elements;
    }

    public ListMonad(Monad<T> monad) {
        this.elements = new ArrayList<T>(monad.size());
        this.elements.addAll(monad.getElements());
    }

    public List<T> getElements() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    public <O> Monad<O> map(Function<T, O> function) {
        List<O> result = new LinkedList<O>();
        for (T element : elements) {
            result.add(function.apply(element));
        }
        return new ListMonad<O>(result);
    }

    @Override
    public <O> Monad<O> flatMap(Function<T, Monad<O>> function) {
        List<O> result = new LinkedList<O>();
        for (T element : elements) {
            result.addAll(function.apply(element).getElements());
        }
        return new ListMonad<O>(result);
    }

    @Override
    public Monad<T> select(Function<T, Boolean> predicate) {
        List<T> result = new LinkedList<T>();
        for (T element : elements) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return new ListMonad<T>(result);
    }

    @Override
    public Monad<T> reject(Function<T, Boolean> predicate) {
        List<T> result = new LinkedList<T>();
        for (T element : elements) {
            if (!predicate.apply(element)) {
                result.add(element);
            }
        }
        return new ListMonad<T>(result);
    }

    @Override
    public Monad<T> take(int howMany) {
        List<T> result = new ArrayList<T>(howMany);
        for (int i = 0; i < elements.size() && i < howMany; i++) {
            result.add(elements.get(i));
        }
        return new ListMonad<T>(result);
    }

    @Override
    public void each(Function<T, ?> function) {
        for (T element : elements) {
            function.apply(element);
        }
    }

    @Override
    public Monad<T> take() {
        return take(1);
    }

    @Override
    public Monad<T> unique() {
        Set<T> set = new LinkedHashSet<T>(elements.size());
        set.addAll(elements);

        List<T> result = new ArrayList<T>(set.size());
        result.addAll(set);
        return new ListMonad<T>(result);
    }

    @Override
    public boolean isSingular() {
        return elements.size() == 1;
    }

    @Override
    public boolean any() {
        return !elements.isEmpty();
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

}
