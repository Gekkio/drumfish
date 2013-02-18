package fi.gekkio.drumfish.lang;

import java.io.ObjectStreamException;
import java.io.Serializable;

import lombok.val;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

public final class Monoids {

    private Monoids() {
    }

    static class IntegerSum implements Monoid<Integer>, Serializable {
        private static final long serialVersionUID = -1195014438740746631L;

        private static final IntegerSum INSTANCE = new IntegerSum();

        @Override
        public Integer mempty() {
            return 0;
        }

        @Override
        public Integer mappend(Integer a, Integer b) {
            return a + b;
        }

        private Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }

    public static Monoid<Integer> integerSum() {
        return IntegerSum.INSTANCE;
    }

    static class IntegerProduct implements Monoid<Integer>, Serializable {
        private static final long serialVersionUID = -1983917666390119257L;

        private static final IntegerProduct INSTANCE = new IntegerProduct();

        @Override
        public Integer mempty() {
            return 1;
        }

        @Override
        public Integer mappend(Integer a, Integer b) {
            return a * b;
        }

        private Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }

    public static Monoid<Integer> integerProduct() {
        return IntegerProduct.INSTANCE;
    }

    public static <T> void verifyMonoidLaws(Monoid<T> monoid, Supplier<T> generator) {
        {
            // Associativity
            val a = generator.get();
            val b = generator.get();
            val c = generator.get();

            val r1 = monoid.mappend(monoid.mappend(a, b), c);
            val r2 = monoid.mappend(a, monoid.mappend(b, c));

            Preconditions.checkState(Objects.equal(r1, r2),
                    "Monoid is not associative: (a • b) • c != a • (b • c). Counter-example: (%s • %s) • %s == %s, %s == %s • (%s • %s)", a, b, c, r1, r2, a,
                    b, c);
        }

        {
            // Identity element
            val a = generator.get();
            val i = monoid.mempty();

            val r1 = monoid.mappend(i, a);
            val r2 = monoid.mappend(a, i);

            Preconditions.checkState(Objects.equal(r1, a), "Monoid identity element is invalid: i • a != a. Counter-example: %s • %s == %s", i, a, r1);
            Preconditions.checkState(Objects.equal(r2, a), "Monoid identity element is invalid: a • i != a. Counter-example: %s • %s == %s", a, i, r2);
        }
    }

}
