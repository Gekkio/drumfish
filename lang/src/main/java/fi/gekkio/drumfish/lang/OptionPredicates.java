package fi.gekkio.drumfish.lang;

import java.io.Serializable;

import com.google.common.base.Predicate;

/**
 * Predicates for Option objects.
 * <p>
 * All objects returned by the methods are serializable if all the method parameters are too.
 */
public final class OptionPredicates {

    private OptionPredicates() {
    }

    private static final class IsDefinedPredicate implements Predicate<Option<? extends Object>>, Serializable {
        private static final long serialVersionUID = -4799134802946996403L;

        public static final IsDefinedPredicate INSTANCE = new IsDefinedPredicate();

        @Override
        public boolean apply(Option<? extends Object> input) {
            return input.isDefined();
        }

    }

    /**
     * Returns a predicate that calls isDefined() on input values.
     * 
     * @return predicate
     */
    public static Predicate<? super Option<?>> isDefined() {
        return IsDefinedPredicate.INSTANCE;
    }

}
