package fi.gekkio.drumfish.lang;

import java.util.Iterator;

public abstract class LazyIterator<T> implements Iterator<T> {

    private Iterator<T> inner;

    protected abstract Iterator<T> iterator();

    @Override
    public boolean hasNext() {
        if (inner == null)
            inner = iterator();
        return inner.hasNext();
    }

    @Override
    public T next() {
        if (inner == null)
            inner = iterator();
        return inner.next();
    }

    @Override
    public void remove() {
        if (inner == null)
            inner = iterator();
        inner.remove();
    }

}
