package fi.gekkio.drumfish.lang;

import java.io.Serializable;

import com.google.common.base.Predicate;

/**
 * Predicates for String objects.
 * <p>
 * All objects returned by the methods are serializable if all the method parameters are too.
 */
public final class StringPredicates {

    private StringPredicates() {
    }

    private static class IsEmptyPredicate implements Predicate<String>, Serializable {
        private static final long serialVersionUID = -1705328419251983539L;

        public static final IsEmptyPredicate INSTANCE = new IsEmptyPredicate();

        @Override
        public boolean apply(String input) {
            return input.isEmpty();
        }
    }

    /**
     * Returns a predicate that calls isEmpty() on input values.
     * 
     * @return predicate
     */
    public static Predicate<? super String> isEmpty() {
        return IsEmptyPredicate.INSTANCE;
    }

}
