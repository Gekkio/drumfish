package fi.gekkio.drumfish.lang;

/**
 * Convenience interface for functions that are used for left folds.
 * 
 * @param <T>
 *            value type
 * @param <R>
 *            result type
 */
public interface LeftFold<T, R> extends Function2<R, T, R> {
}
