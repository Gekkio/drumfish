package fi.gekkio.drumfish.lang;

/**
 * Convenience interface for binary operators, which are really just functions that take two values of the same type.
 * <p>
 * Roughly equivalent to Java 8 {@code java.util.function.BinaryOperator}.
 * <p>
 * If you are designing an API, it's generally better to take {@code Function2<T, T, R>} values as parameters instead of
 * explicitly requiring {@code BinaryOperator<T, R>}.
 * 
 * @param <T>
 *            input type
 * @param <R>
 *            return type
 */
public interface BinaryOperator<T, R> extends Function2<T, T, R> {
}
