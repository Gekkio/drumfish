package fi.gekkio.drumfish.lang;

/**
 * Side-effecting function that has no return value.
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
    void apply(T input);

}