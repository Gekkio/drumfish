package fi.gekkio.drumfish.lang;

/**
 * Convenience interface for functions that are used for left folds.
 * <p>
 * They are essentially functions that take two arguments, where the first argument ("accumulator") has the same type as
 * the return type.
 * 
 * @param <T>
 *            value type
 * @param <R>
 *            result type
 */
public interface LeftFold<T, R> extends Function2<R, T, R> {
}
