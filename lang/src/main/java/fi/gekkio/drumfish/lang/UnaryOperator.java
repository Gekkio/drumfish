package fi.gekkio.drumfish.lang;

import com.google.common.base.Function;

/**
 * Convenience interface for functions that are unary operators.
 * <p>
 * Roughly equivalent to Java 8 {@code java.util.function.UnaryOperator}.
 * 
 * @param <T>
 *            value type
 */
public interface UnaryOperator<T> extends Function<T, T> {
}
