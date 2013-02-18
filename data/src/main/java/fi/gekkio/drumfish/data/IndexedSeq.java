package fi.gekkio.drumfish.data;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import lombok.val;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.UnmodifiableListIterator;

import fi.gekkio.drumfish.lang.Monoids;

public class IndexedSeq<T> implements Iterable<T>, Serializable {

    private static final long serialVersionUID = -1323354912381469298L;

    private static final FingerTreeFactory<Integer, Object> FACTORY = new FingerTreeFactory<Integer, Object>(Monoids.integerSum(), Functions.constant(1));

    private final FingerTree<Integer, T> tree;

    private IndexedSeq(FingerTree<Integer, T> tree) {
        this.tree = tree;
    }

    public static <T> IndexedSeq<T> of(T e) {
        return new IndexedSeq<T>(FACTORY.<T> cast().tree(e));
    }

    public static <T> IndexedSeq<T> of(T... elements) {
        return new IndexedSeq<T>(FACTORY.<T> cast().tree(elements));
    }

    public static <T> IndexedSeq<T> of(Iterable<T> elements) {
        return new IndexedSeq<T>(FACTORY.<T> cast().tree(elements));
    }

    public List<T> asList() {
        class ListAdapter extends AbstractList<T> {
            @Override
            public T get(int index) {
                return IndexedSeq.this.get(index);
            }

            @Override
            public int size() {
                return IndexedSeq.this.size();
            }
        }
        return new ListAdapter();
    }

    @Override
    public Iterator<T> iterator() {
        return tree.iterator();
    }

    public int indexOf(T value) {
        Preconditions.checkNotNull(value, "value cannot be null");

        val it = listIterator();
        while (it.hasNext())
            if (value.equals(it.next()))
                return it.previousIndex();

        return -1;
    }

    public int lastIndexOf(T value) {
        Preconditions.checkNotNull(value, "value cannot be null");

        val it = listIterator();
        while (it.hasPrevious())
            if (value.equals(it.previous()))
                return it.nextIndex();

        return -1;
    }

    public ListIterator<T> listIterator() {
        return listIterator(0);
    }

    public ListIterator<T> listIterator(final int index) {
        Preconditions.checkElementIndex(index, tree.measure());

        class ListIterator extends UnmodifiableListIterator<T> {
            private int cursor = index;

            @Override
            public boolean hasNext() {
                return cursor != size();
            }

            @Override
            public T next() {
                int i = cursor + 1;
                T e = get(i);
                cursor = i;
                return e;
            }

            @Override
            public boolean hasPrevious() {
                return cursor != 0;
            }

            @Override
            public T previous() {
                int i = cursor - 1;
                T e = get(i);
                cursor = i;
                return e;
            }

            @Override
            public int nextIndex() {
                return cursor;
            }

            @Override
            public int previousIndex() {
                return cursor - 1;
            }
        }
        return new ListIterator();
    }

    public boolean contains(T value) {
        for (T e : this) {
            if (Objects.equal(e, value))
                return true;
        }
        return false;
    }

    public int size() {
        return tree.measure();
    }

    public IndexedSeq<T> append(T e) {
        return new IndexedSeq<T>(tree.append(e));
    }

    public IndexedSeq<T> prepend(T e) {
        return new IndexedSeq<T>(tree.prepend(e));
    }

    public <O> IndexedSeq<O> map(Function<? super T, O> f) {
        return new IndexedSeq<O>(tree.map(FACTORY.<O> cast(), f));
    }

    public boolean isEmpty() {
        return tree.isEmpty();
    }

    public T get(int index) {
        Preconditions.checkElementIndex(index, tree.measure());
        val split = tree.split(byIndex(index), 0);
        return split.pivot;
    }

    public IndexedSeq<T> set(int index, T e) {
        Preconditions.checkElementIndex(index, tree.measure());
        val split = tree.split(byIndex(index), 0);

        return new IndexedSeq<T>(split.left.append(e).concat(split.right));
    }

    public IndexedSeq<T> concat(IndexedSeq<T> other) {
        if (other.isEmpty())
            return this;
        if (this.isEmpty())
            return other;
        return new IndexedSeq<T>(tree.concat(other.tree));
    }

    private static Predicate<Integer> byIndex(final int index) {
        class ByIndexPredicate implements Predicate<Integer> {
            @Override
            public boolean apply(Integer input) {
                return input > index;
            }
        }
        return new ByIndexPredicate();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || !(obj.getClass() == this.getClass()))
            return false;
        IndexedSeq<?> other = (IndexedSeq<?>) obj;
        return this.tree.equals(other.tree);
    }

    @Override
    public int hashCode() {
        return tree.hashCode();
    }

    @Override
    public String toString() {
        return tree.toString();
    }

}
