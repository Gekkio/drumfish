package fi.gekkio.drumfish.data;

import java.io.ObjectStreamException;
import java.io.Serializable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import fi.gekkio.drumfish.data.FingerTree.Deep;
import fi.gekkio.drumfish.data.FingerTree.Empty;
import fi.gekkio.drumfish.data.FingerTree.Single;
import fi.gekkio.drumfish.data.FingerTreeNode.Node2;
import fi.gekkio.drumfish.data.FingerTreeNode.Node3;
import fi.gekkio.drumfish.lang.Monoid;

@RequiredArgsConstructor
public final class FingerTreeFactory<V, T> implements Serializable {
    private static final long serialVersionUID = -3600209950347929148L;

    /**
     * Creates a new factory that uses the given monoid and measurement function.
     * 
     * @param monoid
     *            monoid
     * @param measurement
     *            measurement function
     * @return factory
     */
    public static <V, T> FingerTreeFactory<V, T> create(Monoid<V> monoid, Function<? super T, V> measurement) {
        return new FingerTreeFactory<V, T>(monoid, measurement);
    }

    @Getter
    private final Monoid<V> monoid;
    @Getter
    private final Function<? super T, V> measurement;

    final FingerTree<V, T> emptyTree = new EmptyTree();
    private volatile FingerTreeFactory<V, FingerTreeNode<V, T>> nodeFactory;

    /**
     * Returns an empty finger tree.
     * 
     * @return finger tree
     */
    public FingerTree<V, T> tree() {
        return emptyTree;
    }

    /**
     * Returns a finger tree containing the given element.
     * 
     * @param a
     *            element
     * @return finger tree
     */
    public FingerTree<V, T> tree(T a) {
        Preconditions.checkNotNull(a, "element cannot be null");
        return new Single<V, T>(this, a);
    }

    /**
     * Returns a finger tree containing the given elements.
     * 
     * @param elements
     *            elements
     * @return finger tree
     */
    public FingerTree<V, T> tree(T... elements) {
        FingerTree<V, T> tree = emptyTree;
        for (T a : elements) {
            tree = tree.append(a);
        }
        return tree;
    }

    /**
     * Returns a finger tree containing the given elements.
     * 
     * @param elements
     *            elements
     * @return finger tree
     */
    public FingerTree<V, T> tree(Iterable<T> elements) {
        FingerTree<V, T> tree = emptyTree;
        for (T a : elements) {
            tree = tree.append(a);
        }
        return tree;
    }

    /**
     * Downcasts this factory.
     * 
     * @return factory
     */
    @SuppressWarnings("unchecked")
    public <O extends T> FingerTreeFactory<V, O> cast() {
        return (FingerTreeFactory<V, O>) this;
    }

    /**
     * Downcasts this factory.
     * 
     * @param clazz
     *            element class (for convenience only)
     * @return factory
     */
    @SuppressWarnings("unchecked")
    public <O extends T> FingerTreeFactory<V, O> cast(Class<O> clazz) {
        return (FingerTreeFactory<V, O>) this;
    }

    FingerTreeFactory<V, FingerTreeNode<V, T>> nodeFactory() {
        if (nodeFactory == null) {
            synchronized (this) {
                if (nodeFactory == null)
                    nodeFactory = new FingerTreeFactory<V, FingerTreeNode<V, T>>(monoid, FtNodeMeasurement.<V, T> instance());
            }
        }
        return nodeFactory;
    }

    V mempty() {
        return monoid.mempty();
    }

    V mappend(V a, V b) {
        return monoid.mappend(a, b);
    }

    V mappend(V a, FingerTreeDigit<T> b) {
        return mappend(a, b.measure(this));
    }

    V mappend(FingerTreeDigit<T> a, FingerTree<V, ?> b) {
        return mappend(a.measure(this), b.measure());
    }

    V mappend(FingerTree<V, ?> a, FingerTreeDigit<T> b) {
        return mappend(a.measure(), b.measure(this));
    }

    V mappend(V a, FingerTree<V, ?> b) {
        return mappend(a, b.measure());
    }

    V mappend(V a, V b, V c) {
        return monoid.mappend(monoid.mappend(a, b), c);
    }

    V measure(T a) {
        return measurement.apply(a);
    }

    V measure(T a, T b) {
        return monoid.mappend(measure(a), measure(b));
    }

    V measure(T a, T b, T c) {
        return monoid.mappend(monoid.mappend(measure(a), measure(b)), measure(c));
    }

    V measure(T a, T b, T c, T d) {
        return monoid.mappend(monoid.mappend(monoid.mappend(measure(a), measure(b)), measure(c)), measure(d));
    }

    FingerTree<V, T> deep(FingerTreeDigit<T> left, FingerTree<V, FingerTreeNode<V, T>> middle, FingerTreeDigit<T> right) {
        return new Deep<V, T>(this, mappend(left.measure(this), middle.measure(), right.measure(this)), left, middle, right);
    }

    FingerTree<V, T> deep(V measure, FingerTreeDigit<T> left, FingerTree<V, FingerTreeNode<V, T>> middle, FingerTreeDigit<T> right) {
        return new Deep<V, T>(this, measure, left, middle, right);
    }

    FingerTree<V, FingerTreeNode<V, T>> concatNodes(FingerTree<V, FingerTreeNode<V, T>> left, T a, T b, FingerTree<V, FingerTreeNode<V, T>> right) {
        return left.concat(node(a, b), right);
    }

    FingerTree<V, FingerTreeNode<V, T>> concatNodes(FingerTree<V, FingerTreeNode<V, T>> left, T a, T b, T c, FingerTree<V, FingerTreeNode<V, T>> right) {
        return left.concat(node(a, b, c), right);
    }

    FingerTree<V, FingerTreeNode<V, T>> concatNodes(FingerTree<V, FingerTreeNode<V, T>> left, T a, T b, T c, T d, FingerTree<V, FingerTreeNode<V, T>> right) {
        return left.concat(node(a, b), node(c, d), right);
    }

    FingerTree<V, FingerTreeNode<V, T>> concatNodes(FingerTree<V, FingerTreeNode<V, T>> left, T a, T b, T c, T d, T e, FingerTree<V, FingerTreeNode<V, T>> right) {
        return left.concat(node(a, b, c), node(d, e), right);
    }

    FingerTree<V, FingerTreeNode<V, T>> concatNodes(FingerTree<V, FingerTreeNode<V, T>> left, T a, T b, T c, T d, T e, T f,
            FingerTree<V, FingerTreeNode<V, T>> right) {
        return left.concat(node(a, b, c), node(d, e, f), right);
    }

    FingerTree<V, FingerTreeNode<V, T>> concatNodes(FingerTree<V, FingerTreeNode<V, T>> left, T a, T b, T c, T d, T e, T f, T g,
            FingerTree<V, FingerTreeNode<V, T>> right) {
        return left.concat(node(a, b, c), node(d, e), node(f, g), right);
    }

    FingerTree<V, FingerTreeNode<V, T>> concatNodes(FingerTree<V, FingerTreeNode<V, T>> left, T a, T b, T c, T d, T e, T f, T g, T h,
            FingerTree<V, FingerTreeNode<V, T>> right) {
        return left.concat(node(a, b, c), node(d, e, f), node(g, h), right);
    }

    FingerTree<V, FingerTreeNode<V, T>> concatNodes(FingerTree<V, FingerTreeNode<V, T>> left, T a, T b, T c, T d, T e, T f, T g, T h, T i,
            FingerTree<V, FingerTreeNode<V, T>> right) {
        return left.concat(node(a, b, c), node(d, e, f), node(g, h, i), right);
    }

    FingerTree<V, FingerTreeNode<V, T>> concatNodes(FingerTree<V, FingerTreeNode<V, T>> left, T a, T b, T c, T d, T e, T f, T g, T h, T i, T j,
            FingerTree<V, FingerTreeNode<V, T>> right) {
        return left.concat(node(a, b, c), node(d, e, f), node(g, h), node(i, j), right);
    }

    FingerTree<V, FingerTreeNode<V, T>> concatNodes(FingerTree<V, FingerTreeNode<V, T>> left, T a, T b, T c, T d, T e, T f, T g, T h, T i, T j, T k,
            FingerTree<V, FingerTreeNode<V, T>> right) {
        return left.concat(node(a, b, c), node(d, e, f), node(g, h, i), node(j, k), right);
    }

    FingerTree<V, FingerTreeNode<V, T>> concatNodes(FingerTree<V, FingerTreeNode<V, T>> left, T a, T b, T c, T d, T e, T f, T g, T h, T i, T j, T k, T l,
            FingerTree<V, FingerTreeNode<V, T>> right) {
        return left.concat(node(a, b, c), node(d, e, f), node(g, h, i), node(j, k, l), right);
    }

    FingerTreeNode<V, T> node(T a, T b) {
        return new Node2<V, T>(measure(a, b), a, b);
    }

    FingerTreeNode<V, T> node(T a, T b, T c) {
        return new Node3<V, T>(measure(a, b, c), a, b, c);
    }

    private static final class FtNodeMeasurement<V, T> implements Function<FingerTreeNode<V, T>, V>, Serializable {
        private static final long serialVersionUID = -372309629884383219L;

        private static final FtNodeMeasurement<?, ?> INSTANCE = new FtNodeMeasurement<Object, Object>();

        @SuppressWarnings("unchecked")
        public static <V, T> FtNodeMeasurement<V, T> instance() {
            return (FtNodeMeasurement<V, T>) INSTANCE;
        }

        @Override
        public V apply(FingerTreeNode<V, T> input) {
            return input.measure();
        }

        private Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }

    private final class EmptyTree extends Empty<V, T> implements Serializable {
        private static final long serialVersionUID = -6437051207514239436L;

        @Override
        public FingerTreeFactory<V, T> getFactory() {
            return FingerTreeFactory.this;
        }
    }

}