package fi.gekkio.drumfish.lang;

import javax.annotation.Nullable;

/**
 * Side-effecting function that has no return value.
 * <p>
 * Roughly equivalent to Java 8 {@code java.util.function.Block}.
 * 
 * @param <T>
 *            input type
 */
public interface Effect<T> {

    /**
     * Executes this side effect with the given input value.
     * 
     * @param input
     *            input value
     */
    void apply(@Nullable T input);

}