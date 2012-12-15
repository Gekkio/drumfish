package fi.gekkio.drumfish.lang;

import java.io.Serializable;

import javax.annotation.Nullable;

import com.google.common.base.Function;

/**
 * Functions for Tuple2 objects.
 * <p>
 * All objects returned by the methods are serializable if all the method parameters are too.
 */
public final class Tuple2Functions {

    private Tuple2Functions() {
    }

    /**
     * Returns a function that updates the first elements of input tuples with the given value.
     * 
     * 
     * @param a
     *            new first element value
     * @return function
     */
    public static <A, B> Function<Tuple2<A, B>, Tuple2<A, B>> withA(@Nullable final A a) {
        final class WithAFunction implements Function<Tuple2<A, B>, Tuple2<A, B>>, Serializable {
            private static final long serialVersionUID = 1349590163638658121L;

            @Override
            public Tuple2<A, B> apply(Tuple2<A, B> input) {
                return input.withA(a);
            }
        }
        return new WithAFunction();
    }

    /**
     * Returns a function that updates the first elements of input tuples with the given value.
     * 
     * @param a
     *            new first element value
     * @param clazzB
     *            second element type (for convenience only)
     * @return function
     */
    public static <A, B> Function<Tuple2<A, B>, Tuple2<A, B>> withA(@Nullable A a, @Nullable Class<B> clazzB) {
        return withA(a);
    }

    /**
     * Returns a function that updates the second elements of input tuples with the given value.
     * 
     * @param b
     *            second value
     * @return function
     */
    public static <A, B> Function<Tuple2<A, B>, Tuple2<A, B>> withB(@Nullable final B b) {
        final class WithBFunction implements Function<Tuple2<A, B>, Tuple2<A, B>>, Serializable {
            private static final long serialVersionUID = -3772229372311193449L;

            @Override
            public Tuple2<A, B> apply(Tuple2<A, B> input) {
                return input.withB(b);
            }
        }
        return new WithBFunction();
    }

    /**
     * Returns a function that updates the second elements of input tuples with the given value.
     * 
     * @param clazzA
     *            first element type (for convenience only)
     * @param b
     *            new second element value
     * @return function
     */
    public static <A, B> Function<Tuple2<A, B>, Tuple2<A, B>> withB(@Nullable Class<A> clazzA, @Nullable B b) {
        return withB(b);
    }

}
