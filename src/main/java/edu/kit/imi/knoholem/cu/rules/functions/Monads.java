package edu.kit.imi.knoholem.cu.rules.functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A factory for monads.
 *
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class Monads {

    public static <T> Monad<T> list(Collection<? extends T> elementsCollection) {
        List<T> list = new ArrayList<T>(elementsCollection.size());
        list.addAll(elementsCollection);
        return new ListMonad<T>(list);
    }

}
