package fi.gekkio.drumfish.lang;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import lombok.RequiredArgsConstructor;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

/**
 * Utilities for Effect objects.
 * <p>
 * All objects returned by the methods are serializable if all the method parameters are too.
 */
@ParametersAreNonnullByDefault
public final class Effects {

    private Effects() {
    }

    /**
     * Converts an effect to a function that performs the given effect.
     * <p>
     * The returned function returns null as the function result value.
     * 
     * @param e
     *            effect
     * @return function
     */
    public static <T> Function<T, Void> toFunction(final Effect<T> e) {
        Preconditions.checkNotNull(e, "effect cannot be null");
        final class EffectFunction implements Function<T, Void>, Serializable {
            private static final long serialVersionUID = -4601876026894597505L;

            @Override
            public Void apply(T input) {
                e.apply(input);
                return null;
            }
        }
        return new EffectFunction();
    }

    /**
     * Converts a function to an effect that applies the given function and discards the result.
     * 
     * @param f
     *            function
     * @return effect
     */
    public static <T> Effect<T> fromFunction(final Function<? super T, ?> f) {
        Preconditions.checkNotNull(f, "function cannot be null");
        final class FromFunctionEffect implements Effect<T>, Serializable {
            private static final long serialVersionUID = -8381630600993099590L;

            @Override
            public void apply(T input) {
                f.apply(input);
            }
        }
        return new FromFunctionEffect();
    }

    /**
     * Converts a runnable to an effect that discards the input and runs the runnable.
     * 
     * @param r
     *            runnable
     * @return effect
     */
    public static Effect<Object> fromRunnable(final Runnable r) {
        Preconditions.checkNotNull(r, "runnable cannot be null");
        final class FromRunnableEffect implements Effect<Object>, Serializable {
            private static final long serialVersionUID = 7869061388649838159L;

            @Override
            public void apply(Object input) {
                r.run();
            }
        }
        return new FromRunnableEffect();
    }

    private static final class NoopEffect implements Effect<Object>, Serializable {
        private static final long serialVersionUID = -6608178536760540031L;

        public static final NoopEffect INSTANCE = new NoopEffect();

        @Override
        public void apply(Object input) {
        }

    }

    /**
     * Returns an effect that does nothing.
     * 
     * @return effect
     */
    public static Effect<? super Object> noop() {
        return NoopEffect.INSTANCE;
    }

    /**
     * Returns an effect that does nothing.
     * 
     * @param clazz
     *            class of input type (for convenience only)
     * @return effect
     */
    @SuppressWarnings("unchecked")
    public static <T> Effect<T> noop(@Nullable Class<T> clazz) {
        return (Effect<T>) NoopEffect.INSTANCE;
    }

    @RequiredArgsConstructor
    private static final class SystemOutEffect implements Effect<Object>, Serializable {
        private static final long serialVersionUID = 5986895017772281905L;

        public final String prefix;

        public static final SystemOutEffect INSTANCE = new SystemOutEffect("");

        @Override
        public void apply(Object input) {
            System.out.print(prefix);
            System.out.println(input);
        }

    }

    /**
     * Returns an effect that logs the input to System.out.
     * 
     * @return effect
     */
    public static Effect<? super Object> systemOut() {
        return SystemOutEffect.INSTANCE;
    }

    /**
     * Returns an effect that logs the input to System.out with the given prefix string.
     * 
     * @return effect
     */
    public static Effect<? super Object> systemOut(String prefix) {
        Preconditions.checkNotNull(prefix, "prefix cannot be null");
        return new SystemOutEffect(prefix);
    }

    @RequiredArgsConstructor
    private static final class SystemErrEffect implements Effect<Object>, Serializable {
        private static final long serialVersionUID = -635378664192791635L;

        public final String prefix;

        public static final SystemErrEffect INSTANCE = new SystemErrEffect("");

        @Override
        public void apply(Object input) {
            System.err.print(prefix);
            System.err.println(input);
        }

    }

    /**
     * Returns an effect that logs the input to System.err.
     * 
     * @return effect
     */
    public static Effect<? super Object> systemErr() {
        return SystemErrEffect.INSTANCE;
    }

    /**
     * Returns an effect that logs the input to System.err with the given prefix string.
     * 
     * @return effect
     */
    public static Effect<? super Object> systemErr(String prefix) {
        Preconditions.checkNotNull(prefix, "prefix cannot be null");
        return new SystemErrEffect(prefix);
    }

}
