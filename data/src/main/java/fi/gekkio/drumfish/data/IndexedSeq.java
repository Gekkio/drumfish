package fi.gekkio.drumfish.data;

import java.util.Iterator;

import lombok.val;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

import fi.gekkio.drumfish.lang.Monoids;

public class IndexedSeq<T> implements Iterable<T> {

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

    @Override
    public Iterator<T> iterator() {
        return tree.iterator();
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
