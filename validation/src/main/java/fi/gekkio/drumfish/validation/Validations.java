package fi.gekkio.drumfish.validation;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * Utilities Validation objects.
 */
@ParametersAreNonnullByDefault
public final class Validations {

    private Validations() {
    }

    /**
     * Collects all errors from the given validations into a single list.
     * 
     * @param validations
     *            varargs sequence of validations
     * @return error list, which can be empty
     */
    public static <E> ImmutableList<E> collectErrors(Validation<E, ?>... validations) {
        Preconditions.checkNotNull(validations, "validations cannot be null");
        ImmutableList.Builder<E> errors = ImmutableList.builder();
        for (Validation<E, ?> v : validations) {
            Preconditions.checkNotNull(v, "validation cannot be null");
            if (v.isErrors())
                errors.addAll(v.getErrors());
        }
        return errors.build();
    }

    /**
     * Collects all errors from the given validations into a single list.
     * 
     * @param validations
     *            iterable sequence of validations
     * @return error list, which can be empty
     */
    public static <E> ImmutableList<E> collectErrors(Iterable<? extends Validation<E, ?>> validations) {
        Preconditions.checkNotNull(validations, "validations cannot be null");
        ImmutableList.Builder<E> errors = ImmutableList.builder();
        for (Validation<E, ?> v : validations) {
            Preconditions.checkNotNull(v, "validation cannot be null");
            if (v.isErrors())
                errors.addAll(v.getErrors());
        }
        return errors.build();
    }

    /**
     * Returns true if any of the given validations contains errors
     * 
     * @param validations
     *            varargs sequence of validation objects
     * @return boolean
     */
    public static boolean hasErrors(Validation<?, ?>... validations) {
        Preconditions.checkNotNull(validations, "validations cannot be null");
        for (Validation<?, ?> v : validations) {
            Preconditions.checkNotNull(v, "validation cannot be null");
            if (v.isErrors())
                return true;
        }
        return false;
    }

}
