package fi.gekkio.drumfish.validation;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.ParametersAreNonnullByDefault;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import fi.gekkio.drumfish.lang.Tuple2;

/**
 * Represents a validation result, which is either a success containing an object of type T, or a failure containing an
 * immutable list of type E errors.
 * 
 * A validation object is serializable if all contained objects are too.
 * 
 * @param <E>
 *            error type
 * @param <T>
 *            success type
 */
@ParametersAreNonnullByDefault
public abstract class Validation<E, T> implements Serializable {

    private static final long serialVersionUID = -5573828311815317365L;

    /**
     * Creates a new successful validation object.
     * 
     * @param object
     *            target type
     * @return validation object
     */
    public static <E, T> Validation<E, T> success(T object) {
        Preconditions.checkNotNull(object, "object cannot be null");
        return new Success<E, T>(object);
    }

    /**
     * Creates a new failing validation object with the given non-empty collection of errors.
     * 
     * @param errors
     *            non-empty collection of errors
     * @return validation object
     */
    public static <E, T> Validation<E, T> failure(Collection<? extends E> errors) {
        Preconditions.checkNotNull(errors, "errors cannot be null");
        Preconditions.checkArgument(!errors.isEmpty(), "errors cannot be empty");
        return new Failure<E, T>(ImmutableList.copyOf(errors));
    }

    /**
     * Creates a new validation object, which will be a failure if the given collection contains any errors, or the
     * given object if the collection is empty
     * 
     * @param object
     *            target object
     * @param errors
     *            collection of errors (can be empty)
     * @return validation object
     */
    public static <E, T> Validation<E, T> create(T object, Collection<? extends E> errors) {
        Preconditions.checkNotNull(errors, "errors cannot be null");
        if (errors.isEmpty())
            return success(object);
        return failure(errors);
    }

    /**
     * Returns true if the validation was successful, and the success value is available.
     * 
     * @return boolean
     */
    public boolean isValue() {
        return false;
    }

    /**
     * Returns the success value if it is available, or throws an exception.
     * 
     * @throws IllegalStateException
     *             if this validation is a failure
     * @return success value
     */
    public T getValue() {
        throw new IllegalStateException("Validation has no value");
    }

    /**
     * Returns the error list if it is available, or throws an exception.
     * 
     * @throws IllegalStateException
     *             if this validation is a success
     * @return error list
     */
    public ImmutableList<E> getErrors() {
        throw new IllegalStateException("Validation has no errors");
    }

    /**
     * Returns true if the validation was a failure, and the error list is available.
     * 
     * @return boolean
     */
    public boolean isErrors() {
        return false;
    }

    /**
     * Transforms the success value with the given function, if this validation is a success. If this validation is a
     * failure, it has no effect other than the type change.
     * 
     * @param f
     *            mapper function
     * @return transformed validation
     */
    public abstract <O> Validation<E, O> map(Function<? super T, O> f);

    /**
     * Prints the error list as a multi-line string, or throws an exception if the error list is not available.
     * 
     * 
     * @throws IllegalStateException
     *             if this validation is a success
     * @return multi-line string
     */
    public String printErrors() {
        return Joiner.on('\n').join(getErrors());
    }

    /**
     * Zips this validation with another validation with a matching error type. If both validations are successes, the
     * resulting validation will be a success and contain a tuple of both input validation values. Otherwise the
     * resulting validation will be a failure, and the error list will contain all errors from both input validations.
     * 
     * @param other
     *            other validation with matching error type
     * @return zipped validation
     */
    public abstract <O> Validation<E, Tuple2<T, O>> zipWith(Validation<E, O> other);

    private Validation() {
    }

    @RequiredArgsConstructor
    static class Success<E, T> extends Validation<E, T> {

        private static final long serialVersionUID = -7242935179686595621L;

        @Getter
        private final T value;

        @Override
        public boolean isValue() {
            return true;
        }

        @Override
        public <O> Validation<E, O> map(Function<? super T, O> f) {
            Preconditions.checkNotNull(f, "function cannot be null");
            return success(f.apply(value));
        }

        @Override
        public <O> Validation<E, Tuple2<T, O>> zipWith(Validation<E, O> other) {
            Preconditions.checkNotNull(other, "other validation cannot be null");
            if (other.isErrors())
                return failure(other.getErrors());
            return success(Tuple2.of(value, other.getValue()));
        }
    }

    @RequiredArgsConstructor
    static class Failure<E, T> extends Validation<E, T> {

        private static final long serialVersionUID = -1174711172350634743L;

        @Getter
        private final ImmutableList<E> errors;

        @Override
        public boolean isErrors() {
            return true;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public <O> Validation<E, O> map(Function<? super T, O> f) {
            Preconditions.checkNotNull(f, "function cannot be null");
            return (Validation) this;
        }

        @Override
        public <O> Validation<E, Tuple2<T, O>> zipWith(Validation<E, O> other) {
            Preconditions.checkNotNull(other, "other validation cannot be null");
            if (other.isErrors())
                return failure(ImmutableList.<E> builder().addAll(errors).addAll(other.getErrors()).build());
            return failure(errors);
        }
    }

}
