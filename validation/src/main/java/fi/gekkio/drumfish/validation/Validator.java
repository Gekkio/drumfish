package fi.gekkio.drumfish.validation;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

/**
 * A reusable and composable validator, which validates objects of type T, and may produce errors of type E.
 * 
 * @param <E>
 *            error type
 * @param <T>
 *            input type
 */
@ParametersAreNonnullByDefault
public interface Validator<E, T> {

    /**
     * Validates the given object, and returns a Validation object representing the result.
     * 
     * @param object
     *            input object
     * @return validation object
     */
    Validation<E, T> validate(T object);

    /**
     * Validates the given object, and adds errors if any to the given builder. <strong>This method is mostly meant as a
     * performance optimization for internal use, and should not generally be used in application code</strong>
     * 
     * @param object
     *            input object
     * @param errors
     *            error list builder
     */
    void validate(T object, ImmutableList.Builder<E> errors);

    /**
     * Returns a new validator, which will validate an input object normally with this validator, but only applies the
     * second validator if the first validation was a success.
     * 
     * @param other
     *            other validator
     * @return guarded validator
     */
    Validator<E, T> andThen(Validator<E, ? super T> other);

    /**
     * Returns a new validator, which guards this validator with a predicate. This validator will be applied to an input
     * object only if the predicate returns true for the object.
     * 
     * @param predicate
     *            guard predicate
     * @return guarded validator
     */
    Validator<E, T> filter(Predicate<? super T> predicate);

    /**
     * Returns a new validator, which applies both this and the given validator to input objects.
     * 
     * @param other
     *            other validator
     * @return combined validator
     */
    Validator<E, T> union(Validator<E, ? super T> other);

}
