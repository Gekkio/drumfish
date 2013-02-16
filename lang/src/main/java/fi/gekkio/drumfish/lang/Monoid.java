package fi.gekkio.drumfish.lang;

public interface Monoid<T> {

    T mempty();

    T mappend(T a, T b);

}
