package fi.gekkio.drumfish.lang;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * Variance compilation tests for {@link Option}.
 * 
 * These are meant to just be compiled, and are not actually executed as part of the test suite.
 */
public class OptionTest {

    private static class Base {
    }

    private static class Subclass extends Base {
    }

    private final Option<Base> option = Option.none();

    public void verifyFilterVariance() {
        {
            Predicate<Object> p = new Predicate<Object>() {
                @Override
                public boolean apply(@Nullable Object input) {
                    return false;
                }
            };
            option.filter(p);
        }
        {
            Predicate<? super Base> p = Predicates.alwaysTrue();
            option.filter(p);
        }
    }

    public void verifyFlatmapVariance() {
        {
            Function<Object, Option<Object>> f = new Function<Object, Option<Object>>() {
                @Override
                public Option<Object> apply(Object input) {
                    return Option.none();
                }
            };
            Option<Object> o = option.flatMap(f);
            o.getOrNull();
        }
        {
            Function<? super Base, Option<Subclass>> f = new Function<Base, Option<Subclass>>() {
                @Override
                public Option<Subclass> apply(Base input) {
                    return Option.none();
                }
            };
            Option<Subclass> o = option.flatMap(f);
            o.getOrNull();
        }
    }

    public void verifyForeachVariance() {
        {
            Effect<Object> e = Effects.noop(Object.class);
            option.foreach(e);
        }

        {
            Effect<? super Object> e = Effects.noop();
            option.foreach(e);
        }
    }

    public void verifyGetOrElseVariance() {
        {
            Supplier<Subclass> s = Suppliers.ofInstance(null);
            option.getOrElse(s);
        }
    }

    public void verifyMapVariance() {
        {
            Function<Object, Subclass> f = new Function<Object, Subclass>() {
                @Override
                public Subclass apply(Object input) {
                    return null;
                }

            };
            Option<Subclass> o = option.map(f);
            o.getOrNull();
        }
    }

    public void verifyOrElseVariance() {
        {
            Option<Subclass> other = Option.none();
            Option<Base> o = option.orElse(other);
            o.getOrNull();
        }
    }

}
