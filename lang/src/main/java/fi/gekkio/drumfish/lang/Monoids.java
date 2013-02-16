package fi.gekkio.drumfish.lang;

import java.io.ObjectStreamException;
import java.io.Serializable;

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

}
