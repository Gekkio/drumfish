package fi.gekkio.drumfish.lang;

import javax.annotation.Nullable;

/**
 * Function that takes two input parameters.
 * <p>
 * Roughly equivalent to Java 8 {@code java.util.function.BiFunction}.
 * 
 * @param <A>
 *            first parameter type
 * @param <B>
 *            second parameter type
 * @param <T>
 *            result type
 */
public interface Function2<A, B, T> {

    /**
     * Evaluates this function with the given input values.
     * 
     * @param first
     *            first input value
     * @param second
     *            second input value
     */
    T apply(@Nullable A first, @Nullable B second);

}
