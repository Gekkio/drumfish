package fi.gekkio.drumfish.data;

import static fi.gekkio.drumfish.data.FingerTreeDigit.digit;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Iterator;

import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.val;
import lombok.experimental.Value;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import fi.gekkio.drumfish.data.FingerTreeDigit.Digit1;
import fi.gekkio.drumfish.data.FingerTreeDigit.Digit2;
import fi.gekkio.drumfish.data.FingerTreeDigit.Digit3;
import fi.gekkio.drumfish.data.FingerTreeDigit.Digit4;
import fi.gekkio.drumfish.data.FingerTreeNode.NodeIterator;
import fi.gekkio.drumfish.data.FingerTreeNode.NodeMapper;
import fi.gekkio.drumfish.data.FingerTreeNode.NodePrinter;
import fi.gekkio.drumfish.lang.Option;
import fi.gekkio.drumfish.lang.Tuple2;

public abstract class FingerTree<V, T> implements Iterable<T>, Serializable {
    private static final long serialVersionUID = 738107141098774175L;

    private FingerTree() {
    }

    public abstract FingerTree<V, T> append(T value);

    public abstract FingerTree<V, T> prepend(T value);

    public abstract FingerTree<V, T> concat(FingerTree<V, T> tree);

    public abstract FingerTree<V, T> concat(T a, FingerTree<V, T> tree);

    public abstract FingerTree<V, T> concat(T a, T b, FingerTree<V, T> tree);

    public abstract FingerTree<V, T> concat(T a, T b, T c, FingerTree<V, T> tree);

    public abstract FingerTree<V, T> concat(T a, T b, T c, T d, FingerTree<V, T> tree);

    public abstract V measure();

    public abstract boolean isEmpty();

    public abstract <U, O> FingerTree<U, O> map(FingerTreeFactory<U, O> factory, Function<? super T, O> f);

    public abstract Option<T> getHead();

    public abstract Option<T> getLast();

    public abstract Split<V, T> split(Predicate<? super V> p, V accum);

    public abstract int getSize();

    protected abstract FingerTreeFactory<V, T> getFactory();

    public Tuple2<FingerTree<V, T>, FingerTree<V, T>> split(Predicate<? super V> p) {
        if (this.isEmpty() || !p.apply(measure()))
            return Tuple2.of(this, this);

        val split = split(p, getFactory().mempty());

        return Tuple2.of(split.left, split.right.prepend(split.pivot));
    }

    public abstract ViewL<V, T> viewL();

    public abstract ViewR<V, T> viewR();

    protected FingerTree<V, T> unwrap() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || !(obj instanceof FingerTree))
            return false;
        FingerTree<?, ?> other = (FingerTree<?, ?>) obj;
        return elementsEqual(other);
    }

    @Override
    public abstract int hashCode();

    public abstract boolean elementsEqual(FingerTree<?, ?> other);

    @Override
    public String toString() {
        return Iterables.toString(this);
    }

    public String printToString() {
        return print(ToStringPrinter.instance());
    }

    public String print(Printer<? super T> printer) {
        StringBuilder sb = new StringBuilder();
        print(sb, printer);
        return sb.toString();
    }

    public void print(StringBuilder sb, Printer<? super T> printer) {
        print(sb, "", printer);
    }

    protected abstract void print(StringBuilder sb, String padding, Printer<? super T> printer);

    public static interface Printer<T> {
        void print(StringBuilder sb, String padding, T value);
    }

    static class ToStringPrinter<T> implements Printer<T> {

        public static final ToStringPrinter<?> INSTANCE = new ToStringPrinter<Object>();

        @SuppressWarnings("unchecked")
        public static <T> Printer<T> instance() {
            return (Printer<T>) INSTANCE;
        }

        @Override
        public void print(StringBuilder sb, String padding, T value) {
            sb.append(value);
        }

    }

    @Value
    public static class Split<V, T> implements Serializable {
        private static final long serialVersionUID = 3756440491306281717L;

        public final FingerTree<V, T> left;
        public final T pivot;
        public final FingerTree<V, T> right;
    }

    public static abstract class ViewL<V, T> implements Serializable {
        private static final long serialVersionUID = -523782058608865730L;

        private ViewL() {
        }

        public abstract boolean isEmpty();

        public abstract T getLeft();

        public abstract FingerTree<V, T> getRight();

        @SuppressWarnings("unchecked")
        public static <V, T> ViewL<V, T> empty() {
            return (ViewL<V, T>) EmptyL.INSTANCE;
        }

        @EqualsAndHashCode(callSuper = false)
        @ToString(callSuper = false)
        static final class EmptyL<V, T> extends ViewL<V, T> {
            private static final long serialVersionUID = 6288499694674481118L;

            public static final EmptyL<?, ?> INSTANCE = new EmptyL<Object, Object>();

            @Override
            public boolean isEmpty() {
                return true;
            }

            @Override
            public T getLeft() {
                throw new UnsupportedOperationException("Cannot get left from an empty ViewL");
            }

            @Override
            public FingerTree<V, T> getRight() {
                throw new UnsupportedOperationException("Cannot get right from an empty ViewL");
            }

            private Object readResolve() throws ObjectStreamException {
                return INSTANCE;
            }
        }

        @RequiredArgsConstructor
        @Getter
        @EqualsAndHashCode(callSuper = false)
        @ToString(callSuper = false)
        static final class FullL<V, T> extends ViewL<V, T> {
            private static final long serialVersionUID = -5150578465060372593L;

            private final T left;
            private final FingerTree<V, T> right;

            @Override
            public boolean isEmpty() {
                return false;
            }

        }
    }

    public static abstract class ViewR<V, T> implements Serializable {
        private static final long serialVersionUID = 271772950906322978L;

        private ViewR() {
        }

        public abstract boolean isEmpty();

        public abstract FingerTree<V, T> getLeft();

        public abstract T getRight();

        @SuppressWarnings("unchecked")
        public static <V, T> ViewR<V, T> empty() {
            return (ViewR<V, T>) EmptyR.INSTANCE;
        }

        @EqualsAndHashCode(callSuper = false)
        @ToString(callSuper = false)
        static final class EmptyR<V, T> extends ViewR<V, T> {
            private static final long serialVersionUID = 10359204507379837L;

            public static final EmptyR<?, ?> INSTANCE = new EmptyR<Object, Object>();

            @Override
            public boolean isEmpty() {
                return true;
            }

            @Override
            public FingerTree<V, T> getLeft() {
                throw new UnsupportedOperationException("Cannot get left from an empty ViewR");
            }

            @Override
            public T getRight() {
                throw new UnsupportedOperationException("Cannot get right from an empty ViewR");
            }

            private Object readResolve() throws ObjectStreamException {
                return INSTANCE;
            }
        }

        @RequiredArgsConstructor
        @Getter
        @EqualsAndHashCode(callSuper = false)
        @ToString(callSuper = false)
        static final class FullR<V, T> extends ViewR<V, T> {
            private static final long serialVersionUID = -8168083994639793450L;

            private final FingerTree<V, T> left;
            private final T right;

            @Override
            public boolean isEmpty() {
                return false;
            }

        }
    }

    @RequiredArgsConstructor
    static abstract class Empty<V, T> extends FingerTree<V, T> {
        private static final long serialVersionUID = -3454110724117336401L;

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public V measure() {
            return getFactory().mempty();
        }

        @Override
        public FingerTree<V, T> prepend(T value) {
            return getFactory().tree(value);
        }

        @Override
        public FingerTree<V, T> append(T value) {
            return getFactory().tree(value);
        }

        @Override
        public Iterator<T> iterator() {
            return Iterators.emptyIterator();
        }

        @Override
        public <U, O> FingerTree<U, O> map(FingerTreeFactory<U, O> factory, Function<? super T, O> f) {
            return factory.emptyTree;
        }

        @Override
        public Option<T> getHead() {
            return Option.none();
        }

        @Override
        public Option<T> getLast() {
            return Option.none();
        }

        @Override
        public Split<V, T> split(Predicate<? super V> p, V accum) {
            throw new UnsupportedOperationException("Cannot split an empty finger tree");
        }

        @Override
        public ViewL<V, T> viewL() {
            return ViewL.empty();
        }

        @Override
        public ViewR<V, T> viewR() {
            return ViewR.empty();
        }

        @Override
        public void print(StringBuilder sb, String padding, Printer<? super T> printer) {
            sb.append(padding);
            sb.append("()");
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean elementsEqual(FingerTree<?, ?> other) {
            return other instanceof Empty;
        }

        @Override
        public FingerTree<V, T> concat(FingerTree<V, T> tree) {
            return tree;
        }

        @Override
        public FingerTree<V, T> concat(T a, FingerTree<V, T> tree) {
            return tree.prepend(a);
        }

        @Override
        public FingerTree<V, T> concat(T a, T b, FingerTree<V, T> tree) {
            return tree.prepend(b).prepend(a);
        }

        @Override
        public FingerTree<V, T> concat(T a, T b, T c, FingerTree<V, T> tree) {
            return tree.prepend(c).prepend(b).prepend(a);
        }

        @Override
        public FingerTree<V, T> concat(T a, T b, T c, T d, FingerTree<V, T> tree) {
            return tree.prepend(d).prepend(c).prepend(b).prepend(a);
        }

        @Override
        public int getSize() {
            return 0;
        }

    }

    @RequiredArgsConstructor
    static final class Single<V, T> extends FingerTree<V, T> {
        private static final long serialVersionUID = -567427169446878029L;

        @Getter(AccessLevel.PROTECTED)
        private final FingerTreeFactory<V, T> factory;
        private final T a;

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public V measure() {
            return factory.measure(a);
        }

        @Override
        public FingerTree<V, T> prepend(T value) {
            val left = digit(value);
            val middle = factory.nodeFactory().emptyTree;
            val right = digit(a);

            return factory.deep(left, middle, right);
        }

        @Override
        public FingerTree<V, T> append(T value) {
            val left = digit(a);
            val middle = factory.nodeFactory().emptyTree;
            val right = digit(value);

            return factory.deep(left, middle, right);
        }

        @Override
        public Iterator<T> iterator() {
            return Iterators.singletonIterator(a);
        }

        @Override
        public <U, O> FingerTree<U, O> map(FingerTreeFactory<U, O> factory, Function<? super T, O> f) {
            return factory.tree(f.apply(a));
        }

        @Override
        public Option<T> getHead() {
            return Option.some(a);
        }

        @Override
        public Option<T> getLast() {
            return Option.some(a);
        }

        @Override
        public Split<V, T> split(Predicate<? super V> p, V accum) {
            return new Split<V, T>(factory.emptyTree, a, factory.emptyTree);
        }

        @Override
        public ViewL<V, T> viewL() {
            return new ViewL.FullL<V, T>(a, factory.emptyTree);
        }

        @Override
        public ViewR<V, T> viewR() {
            return new ViewR.FullR<V, T>(factory.emptyTree, a);
        }

        @Override
        public void print(StringBuilder sb, String padding, Printer<? super T> printer) {
            sb.append(padding);
            sb.append("(\n");

            sb.append(padding);
            sb.append(' ');
            printer.print(sb, padding + " ", a);
            sb.append('\n');

            sb.append(padding);
            sb.append(')');
        }

        @Override
        public int hashCode() {
            return a.hashCode();
        }

        @Override
        public boolean elementsEqual(FingerTree<?, ?> other) {
            if (other == this)
                return true;
            if (!(other instanceof Single))
                return false;
            Single<?, ?> single = (Single<?, ?>) other;
            return a.equals(single.a);
        }

        @Override
        public FingerTree<V, T> concat(FingerTree<V, T> tree) {
            return tree.prepend(a);
        }

        @Override
        public FingerTree<V, T> concat(T a, FingerTree<V, T> tree) {
            return tree.prepend(a).prepend(this.a);
        }

        @Override
        public FingerTree<V, T> concat(T a, T b, FingerTree<V, T> tree) {
            return tree.prepend(b).prepend(a).prepend(this.a);
        }

        @Override
        public FingerTree<V, T> concat(T a, T b, T c, FingerTree<V, T> tree) {
            return tree.prepend(c).prepend(b).prepend(a).prepend(this.a);
        }

        @Override
        public FingerTree<V, T> concat(T a, T b, T c, T d, FingerTree<V, T> tree) {
            return tree.prepend(d).prepend(c).prepend(b).prepend(a).prepend(this.a);
        }

        @Override
        public int getSize() {
            return 1;
        }

    }

    @RequiredArgsConstructor
    static final class Deep<V, T> extends FingerTree<V, T> {
        private static final long serialVersionUID = 2406435051250366159L;

        @Getter(AccessLevel.PROTECTED)
        private final FingerTreeFactory<V, T> factory;
        private final V measure;
        private final FingerTreeDigit<T> left;
        private final FingerTree<V, FingerTreeNode<V, T>> middle;
        private final FingerTreeDigit<T> right;

        private int hashCode;

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public V measure() {
            return measure;
        }

        @Override
        public FingerTree<V, T> prepend(T value) {
            if (left instanceof Digit4) {
                val left = digit(value, this.left.getHead());
                val middle = this.middle.prepend(this.left.toTailNode(factory));
                return factory.deep(factory.mappend(factory.measure(value), measure), left, middle, right);
            }
            val left = this.left.prepend(value);
            return factory.deep(factory.mappend(factory.measure(value), measure), left, middle, right);
        }

        @Override
        public FingerTree<V, T> append(T value) {
            if (right instanceof Digit4) {
                val middle = this.middle.append(this.right.toInitNode(factory));
                val right = digit(this.right.getLast(), value);
                return factory.deep(factory.mappend(measure, factory.measure(value)), left, middle, right);
            }
            val right = this.right.append(value);
            return factory.deep(factory.mappend(measure, factory.measure(value)), left, middle, right);
        }

        @Override
        public Iterator<T> iterator() {
            return Iterators.concat(left.iterator(), new NodeIterator<T>(middle), right.iterator());
        }

        @Override
        public <U, O> FingerTree<U, O> map(FingerTreeFactory<U, O> factory, Function<? super T, O> f) {
            return factory.deep(left.map(f), middle.map(factory.nodeFactory(), new NodeMapper<T, U, O>(factory, f)), right.map(f));
        }

        @Override
        public Option<T> getHead() {
            return Option.some(left.getHead());
        }

        @Override
        public Option<T> getLast() {
            return Option.some(right.getLast());
        }

        @Override
        public Split<V, T> split(Predicate<? super V> p, V accum) {
            FingerTree<V, T> left;
            T pivot;
            FingerTree<V, T> right;

            V accumL = factory.mappend(accum, this.left);
            if (p.apply(accumL)) {
                val lSplit = this.left.split(factory, p, accum);

                if (lSplit.left != null)
                    left = lSplit.left.toTree(factory);
                else
                    left = factory.emptyTree;
                pivot = lSplit.pivot;
                right = deepL(lSplit.right, middle, this.right);
            } else {
                V accumM = factory.mappend(accumL, this.middle);
                if (p.apply(accumM)) {
                    val mSplit = this.middle.split(p, accumL);
                    val nSplit = mSplit.pivot.split(factory, p, factory.mappend(accumL, mSplit.left));

                    left = deepR(this.left, mSplit.left, nSplit.left);
                    pivot = nSplit.pivot;
                    right = deepL(nSplit.right, mSplit.right, this.right);
                } else {
                    val rSplit = this.right.split(factory, p, accumM);

                    left = deepR(this.left, this.middle, rSplit.left);
                    pivot = rSplit.pivot;
                    if (rSplit.right != null)
                        right = rSplit.right.toTree(factory);
                    else
                        right = factory.emptyTree;
                }
            }
            return new Split<V, T>(left, pivot, right);
        }

        protected FingerTree<V, T> deepL(@Nullable FingerTreeDigit<T> left, FingerTree<V, FingerTreeNode<V, T>> center, FingerTreeDigit<T> right) {
            if (left != null)
                return factory.deep(left, center, right);
            val cView = center.viewL();
            if (!cView.isEmpty())
                return factory.deep(cView.getLeft().toDigit(), cView.getRight(), right);
            return right.toTree(factory);
        }

        protected FingerTree<V, T> deepR(FingerTreeDigit<T> left, FingerTree<V, FingerTreeNode<V, T>> center, @Nullable FingerTreeDigit<T> right) {
            if (right != null)
                return factory.deep(left, center, right);
            val cView = center.viewR();
            if (!cView.isEmpty())
                return factory.deep(left, cView.getLeft(), cView.getRight().toDigit());
            return left.toTree(factory);
        }

        @Override
        public ViewL<V, T> viewL() {
            class ViewLTree extends LazyTree<V, T> {
                private static final long serialVersionUID = -7520792642423138663L;

                @Override
                protected FingerTree<V, T> constructTree() {
                    Deep<V, T> me = Deep.this;
                    if (me.left instanceof Digit1) {
                        return me.rotateLeft();
                    }
                    return me.factory.deep(me.left.getTail(), me.middle, me.right);
                }
            }
            return new ViewL.FullL<V, T>(left.getHead(), new ViewLTree());
        }

        @Override
        public ViewR<V, T> viewR() {
            class ViewRTree extends LazyTree<V, T> {
                private static final long serialVersionUID = 5263935514395804782L;

                @Override
                protected FingerTree<V, T> constructTree() {
                    Deep<V, T> me = Deep.this;
                    if (me.right instanceof Digit1) {
                        return me.rotateRight();
                    }
                    return me.factory.deep(me.left, me.middle, me.right.getInit());
                }
            }
            return new ViewR.FullR<V, T>(new ViewRTree(), right.getLast());
        }

        protected FingerTree<V, T> rotateLeft() {
            val mView = middle.viewL();
            if (mView.isEmpty())
                return right.toTree(factory);
            return factory.deep(factory.mappend(middle, right), mView.getLeft().toDigit(), mView.getRight(), right);
        }

        protected FingerTree<V, T> rotateRight() {
            val mView = middle.viewR();
            if (mView.isEmpty())
                return left.toTree(factory);
            return factory.deep(factory.mappend(left, middle), left, mView.getLeft(), mView.getRight().toDigit());
        }

        @Override
        public void print(StringBuilder sb, final String padding, final Printer<? super T> printer) {
            sb.append(padding);
            sb.append("(\n");

            sb.append(padding);
            sb.append(" L");
            left.print(sb, padding + "  ", printer);
            sb.append('\n');

            sb.append(padding);
            sb.append(" M\n");
            middle.print(sb, padding + "  ", new NodePrinter<T>(printer));
            sb.append('\n');

            sb.append(padding);
            sb.append(" R");
            right.print(sb, padding + "  ", printer);
            sb.append('\n');

            sb.append(padding);
            sb.append(')');
        }

        @Override
        public int hashCode() {
            if (hashCode == 0) {
                int result = 1;
                result = result * 31 + left.hashCode();
                result = result * 31 + middle.hashCode();
                result = result * 31 + right.hashCode();
                hashCode = result;
            }
            return hashCode;
        }

        @Override
        public FingerTree<V, T> concat(FingerTree<V, T> tree) {
            if (tree instanceof LazyTree)
                return concat(tree.unwrap());
            if (tree instanceof Empty)
                return this;
            if (tree instanceof Single)
                return append(((Single<V, T>) tree).a);
            Deep<V, T> other = (Deep<V, T>) tree;
            val m1 = middle;
            val d1 = right;
            val d2 = other.left;
            val m2 = other.middle;

            FingerTree<V, FingerTreeNode<V, T>> newMiddle;
            if (d1 instanceof Digit1) {
                val dl = (Digit1<T>) d1;
                if (d2 instanceof Digit1) {
                    val dr = (Digit1<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dr.a, m2);
                } else if (d2 instanceof Digit2) {
                    val dr = (Digit2<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dr.a, dr.b, m2);
                } else if (d2 instanceof Digit3) {
                    val dr = (Digit3<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dr.a, dr.b, dr.c, m2);
                } else {
                    val dr = (Digit4<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dr.a, dr.b, dr.c, dr.d, m2);
                }
            } else if (d1 instanceof Digit2) {
                val dl = (Digit2<T>) d1;
                if (d2 instanceof Digit1) {
                    val dr = (Digit1<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dr.a, m2);
                } else if (d2 instanceof Digit2) {
                    val dr = (Digit2<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dr.a, dr.b, m2);
                } else if (d2 instanceof Digit3) {
                    val dr = (Digit3<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dr.a, dr.b, dr.c, m2);
                } else {
                    val dr = (Digit4<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dr.a, dr.b, dr.c, dr.d, m2);
                }
            } else if (d1 instanceof Digit3) {
                val dl = (Digit3<T>) d1;
                if (d2 instanceof Digit1) {
                    val dr = (Digit1<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dr.a, m2);
                } else if (d2 instanceof Digit2) {
                    val dr = (Digit2<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dr.a, dr.b, m2);
                } else if (d2 instanceof Digit3) {
                    val dr = (Digit3<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dr.a, dr.b, dr.c, m2);
                } else {
                    val dr = (Digit4<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dr.a, dr.b, dr.c, dr.d, m2);
                }
            } else {
                val dl = (Digit4<T>) d1;
                if (d2 instanceof Digit1) {
                    val dr = (Digit1<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dl.d, dr.a, m2);
                } else if (d2 instanceof Digit2) {
                    val dr = (Digit2<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dl.d, dr.a, dr.b, m2);
                } else if (d2 instanceof Digit3) {
                    val dr = (Digit3<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dl.d, dr.a, dr.b, dr.c, m2);
                } else {
                    val dr = (Digit4<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dl.d, dr.a, dr.b, dr.c, dr.d, m2);
                }
            }

            return factory.deep(left, newMiddle, other.right);
        }

        @Override
        public FingerTree<V, T> concat(T a, FingerTree<V, T> tree) {
            if (tree instanceof LazyTree)
                return concat(a, tree.unwrap());
            if (!(tree instanceof Deep))
                return append(a).concat(tree);
            if (tree instanceof Empty)
                return append(a);
            if (tree instanceof Single)
                return append(a).append(((Single<V, T>) tree).a);
            Deep<V, T> other = (Deep<V, T>) tree;
            val m1 = middle;
            val d1 = right;
            val d2 = other.left;
            val m2 = other.middle;

            FingerTree<V, FingerTreeNode<V, T>> newMiddle;
            if (d1 instanceof Digit1) {
                val dl = (Digit1<T>) d1;
                if (d2 instanceof Digit1) {
                    val dr = (Digit1<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, a, dr.a, m2);
                } else if (d2 instanceof Digit2) {
                    val dr = (Digit2<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, a, dr.a, dr.b, m2);
                } else if (d2 instanceof Digit3) {
                    val dr = (Digit3<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, a, dr.a, dr.b, dr.c, m2);
                } else {
                    val dr = (Digit4<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, a, dr.a, dr.b, dr.c, dr.d, m2);
                }
            } else if (d1 instanceof Digit2) {
                val dl = (Digit2<T>) d1;
                if (d2 instanceof Digit1) {
                    val dr = (Digit1<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, a, dr.a, m2);
                } else if (d2 instanceof Digit2) {
                    val dr = (Digit2<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, a, dr.a, dr.b, m2);
                } else if (d2 instanceof Digit3) {
                    val dr = (Digit3<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, a, dr.a, dr.b, dr.c, m2);
                } else {
                    val dr = (Digit4<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, a, dr.a, dr.b, dr.c, dr.d, m2);
                }
            } else if (d1 instanceof Digit3) {
                val dl = (Digit3<T>) d1;
                if (d2 instanceof Digit1) {
                    val dr = (Digit1<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, a, dr.a, m2);
                } else if (d2 instanceof Digit2) {
                    val dr = (Digit2<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, a, dr.a, dr.b, m2);
                } else if (d2 instanceof Digit3) {
                    val dr = (Digit3<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, a, dr.a, dr.b, dr.c, m2);
                } else {
                    val dr = (Digit4<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, a, dr.a, dr.b, dr.c, dr.d, m2);
                }
            } else {
                val dl = (Digit4<T>) d1;
                if (d2 instanceof Digit1) {
                    val dr = (Digit1<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dl.d, a, dr.a, m2);
                } else if (d2 instanceof Digit2) {
                    val dr = (Digit2<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dl.d, a, dr.a, dr.b, m2);
                } else if (d2 instanceof Digit3) {
                    val dr = (Digit3<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dl.d, a, dr.a, dr.b, dr.c, m2);
                } else {
                    val dr = (Digit4<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dl.d, a, dr.a, dr.b, dr.c, dr.d, m2);
                }
            }
            return factory.deep(left, newMiddle, other.right);
        }

        @Override
        public FingerTree<V, T> concat(T a, T b, FingerTree<V, T> tree) {
            if (tree instanceof LazyTree)
                return concat(a, b, tree.unwrap());
            if (tree instanceof Empty)
                return append(a).append(b);
            if (tree instanceof Single)
                return append(a).append(b).append(((Single<V, T>) tree).a);
            Deep<V, T> other = (Deep<V, T>) tree;
            val m1 = middle;
            val d1 = right;
            val d2 = other.left;
            val m2 = other.middle;

            FingerTree<V, FingerTreeNode<V, T>> newMiddle;
            if (d1 instanceof Digit1) {
                val dl = (Digit1<T>) d1;
                if (d2 instanceof Digit1) {
                    val dr = (Digit1<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, a, b, dr.a, m2);
                } else if (d2 instanceof Digit2) {
                    val dr = (Digit2<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, a, b, dr.a, dr.b, m2);
                } else if (d2 instanceof Digit3) {
                    val dr = (Digit3<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, a, b, dr.a, dr.b, dr.c, m2);
                } else {
                    val dr = (Digit4<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, a, b, dr.a, dr.b, dr.c, dr.d, m2);
                }
            } else if (d1 instanceof Digit2) {
                val dl = (Digit2<T>) d1;
                if (d2 instanceof Digit1) {
                    val dr = (Digit1<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, a, b, dr.a, m2);
                } else if (d2 instanceof Digit2) {
                    val dr = (Digit2<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, a, b, dr.a, dr.b, m2);
                } else if (d2 instanceof Digit3) {
                    val dr = (Digit3<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, a, b, dr.a, dr.b, dr.c, m2);
                } else {
                    val dr = (Digit4<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, a, b, dr.a, dr.b, dr.c, dr.d, m2);
                }
            } else if (d1 instanceof Digit3) {
                val dl = (Digit3<T>) d1;
                if (d2 instanceof Digit1) {
                    val dr = (Digit1<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, a, b, dr.a, m2);
                } else if (d2 instanceof Digit2) {
                    val dr = (Digit2<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, a, b, dr.a, dr.b, m2);
                } else if (d2 instanceof Digit3) {
                    val dr = (Digit3<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, a, b, dr.a, dr.b, dr.c, m2);
                } else {
                    val dr = (Digit4<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, a, b, dr.a, dr.b, dr.c, dr.d, m2);
                }
            } else {
                val dl = (Digit4<T>) d1;
                if (d2 instanceof Digit1) {
                    val dr = (Digit1<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dl.d, a, b, dr.a, m2);
                } else if (d2 instanceof Digit2) {
                    val dr = (Digit2<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dl.d, a, b, dr.a, dr.b, m2);
                } else if (d2 instanceof Digit3) {
                    val dr = (Digit3<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dl.d, a, b, dr.a, dr.b, dr.c, m2);
                } else {
                    val dr = (Digit4<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dl.d, a, b, dr.a, dr.b, dr.c, dr.d, m2);
                }
            }
            return factory.deep(left, newMiddle, other.right);
        }

        @Override
        public FingerTree<V, T> concat(T a, T b, T c, FingerTree<V, T> tree) {
            if (tree instanceof LazyTree)
                return concat(a, b, c, tree.unwrap());
            if (tree instanceof Empty)
                return append(a).append(b).append(c);
            if (tree instanceof Single)
                return append(a).append(b).append(c).append(((Single<V, T>) tree).a);
            Deep<V, T> other = (Deep<V, T>) tree;
            val m1 = middle;
            val d1 = right;
            val d2 = other.left;
            val m2 = other.middle;

            FingerTree<V, FingerTreeNode<V, T>> newMiddle;
            if (d1 instanceof Digit1) {
                val dl = (Digit1<T>) d1;
                if (d2 instanceof Digit1) {
                    val dr = (Digit1<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, a, b, c, dr.a, m2);
                } else if (d2 instanceof Digit2) {
                    val dr = (Digit2<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, a, b, c, dr.a, dr.b, m2);
                } else if (d2 instanceof Digit3) {
                    val dr = (Digit3<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, a, b, c, dr.a, dr.b, dr.c, m2);
                } else {
                    val dr = (Digit4<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, a, b, c, dr.a, dr.b, dr.c, dr.d, m2);
                }
            } else if (d1 instanceof Digit2) {
                val dl = (Digit2<T>) d1;
                if (d2 instanceof Digit1) {
                    val dr = (Digit1<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, a, b, c, dr.a, m2);
                } else if (d2 instanceof Digit2) {
                    val dr = (Digit2<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, a, b, c, dr.a, dr.b, m2);
                } else if (d2 instanceof Digit3) {
                    val dr = (Digit3<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, a, b, c, dr.a, dr.b, dr.c, m2);
                } else {
                    val dr = (Digit4<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, a, b, c, dr.a, dr.b, dr.c, dr.d, m2);
                }
            } else if (d1 instanceof Digit3) {
                val dl = (Digit3<T>) d1;
                if (d2 instanceof Digit1) {
                    val dr = (Digit1<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, a, b, c, dr.a, m2);
                } else if (d2 instanceof Digit2) {
                    val dr = (Digit2<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, a, b, c, dr.a, dr.b, m2);
                } else if (d2 instanceof Digit3) {
                    val dr = (Digit3<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, a, b, c, dr.a, dr.b, dr.c, m2);
                } else {
                    val dr = (Digit4<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, a, b, c, dr.a, dr.b, dr.c, dr.d, m2);
                }
            } else {
                val dl = (Digit4<T>) d1;
                if (d2 instanceof Digit1) {
                    val dr = (Digit1<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dl.d, a, b, c, dr.a, m2);
                } else if (d2 instanceof Digit2) {
                    val dr = (Digit2<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dl.d, a, b, c, dr.a, dr.b, m2);
                } else if (d2 instanceof Digit3) {
                    val dr = (Digit3<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dl.d, a, b, c, dr.a, dr.b, dr.c, m2);
                } else {
                    val dr = (Digit4<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dl.d, a, b, c, dr.a, dr.b, dr.c, dr.d, m2);
                }
            }
            return factory.deep(left, newMiddle, other.right);
        }

        @Override
        public FingerTree<V, T> concat(T a, T b, T c, T d, FingerTree<V, T> tree) {
            if (tree instanceof LazyTree)
                return concat(a, b, c, d, tree.unwrap());
            if (tree instanceof Empty)
                return append(a).append(b).append(c).append(d);
            if (tree instanceof Single)
                return append(a).append(b).append(c).append(d).append(((Single<V, T>) tree).a);
            Deep<V, T> other = (Deep<V, T>) tree;
            val m1 = middle;
            val d1 = right;
            val d2 = other.left;
            val m2 = other.middle;

            FingerTree<V, FingerTreeNode<V, T>> newMiddle;
            if (d1 instanceof Digit1) {
                val dl = (Digit1<T>) d1;
                if (d2 instanceof Digit1) {
                    val dr = (Digit1<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, a, b, c, d, dr.a, m2);
                } else if (d2 instanceof Digit2) {
                    val dr = (Digit2<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, a, b, c, d, dr.a, dr.b, m2);
                } else if (d2 instanceof Digit3) {
                    val dr = (Digit3<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, a, b, c, d, dr.a, dr.b, dr.c, m2);
                } else {
                    val dr = (Digit4<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, a, b, c, d, dr.a, dr.b, dr.c, dr.d, m2);
                }
            } else if (d1 instanceof Digit2) {
                val dl = (Digit2<T>) d1;
                if (d2 instanceof Digit1) {
                    val dr = (Digit1<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, a, b, c, d, dr.a, m2);
                } else if (d2 instanceof Digit2) {
                    val dr = (Digit2<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, a, b, c, d, dr.a, dr.b, m2);
                } else if (d2 instanceof Digit3) {
                    val dr = (Digit3<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, a, b, c, d, dr.a, dr.b, dr.c, m2);
                } else {
                    val dr = (Digit4<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, a, b, c, d, dr.a, dr.b, dr.c, dr.d, m2);
                }
            } else if (d1 instanceof Digit3) {
                val dl = (Digit3<T>) d1;
                if (d2 instanceof Digit1) {
                    val dr = (Digit1<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, a, b, c, d, dr.a, m2);
                } else if (d2 instanceof Digit2) {
                    val dr = (Digit2<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, a, b, c, d, dr.a, dr.b, m2);
                } else if (d2 instanceof Digit3) {
                    val dr = (Digit3<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, a, b, c, d, dr.a, dr.b, dr.c, m2);
                } else {
                    val dr = (Digit4<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, a, b, c, d, dr.a, dr.b, dr.c, dr.d, m2);
                }
            } else {
                val dl = (Digit4<T>) d1;
                if (d2 instanceof Digit1) {
                    val dr = (Digit1<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dl.d, a, b, c, d, dr.a, m2);
                } else if (d2 instanceof Digit2) {
                    val dr = (Digit2<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dl.d, a, b, c, d, dr.a, dr.b, m2);
                } else if (d2 instanceof Digit3) {
                    val dr = (Digit3<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dl.d, a, b, c, d, dr.a, dr.b, dr.c, m2);
                } else {
                    val dr = (Digit4<T>) d2;
                    newMiddle = factory.concatNodes(m1, dl.a, dl.b, dl.c, dl.d, a, b, c, d, dr.a, dr.b, dr.c, dr.d, m2);
                }
            }
            return factory.deep(left, newMiddle, other.right);
        }

        @Override
        public boolean elementsEqual(FingerTree<?, ?> other) {
            if (other == this)
                return true;
            if (!(other instanceof Deep))
                return false;
            Deep<?, ?> deep = (Deep<?, ?>) other;

            if (left.getClass() == deep.left.getClass()) {
                if (!middle.elementsEqual(deep.middle))
                    return false;
                return right.equals(deep.right);
            }

            return Iterables.elementsEqual(this, other);
        }

        @Override
        public int getSize() {
            return left.getSize() + middle.getSize() + right.getSize();
        }

    }

    @RequiredArgsConstructor
    static abstract class LazyTree<V, T> extends FingerTree<V, T> {
        private static final long serialVersionUID = -6236367981343796216L;

        private volatile FingerTree<V, T> tree;

        protected FingerTree<V, T> unwrap() {
            if (tree == null)
                synchronized (this) {
                    if (tree == null)
                        tree = constructTree();
                }
            return tree;
        }

        protected abstract FingerTree<V, T> constructTree();

        @Override
        protected FingerTreeFactory<V, T> getFactory() {
            return unwrap().getFactory();
        }

        @Override
        public Iterator<T> iterator() {
            return unwrap().iterator();
        }

        @Override
        public FingerTree<V, T> prepend(T value) {
            return unwrap().prepend(value);
        }

        @Override
        public FingerTree<V, T> append(T value) {
            return unwrap().append(value);
        }

        @Override
        public V measure() {
            return unwrap().measure();
        }

        @Override
        public boolean isEmpty() {
            return unwrap().isEmpty();
        }

        @Override
        public <U, O> FingerTree<U, O> map(FingerTreeFactory<U, O> factory, Function<? super T, O> f) {
            return unwrap().map(factory, f);
        }

        @Override
        public Option<T> getHead() {
            return unwrap().getHead();
        }

        @Override
        public Option<T> getLast() {
            return unwrap().getLast();
        }

        @Override
        public Split<V, T> split(Predicate<? super V> p, V accum) {
            return unwrap().split(p, accum);
        }

        @Override
        public ViewL<V, T> viewL() {
            return unwrap().viewL();
        }

        @Override
        public ViewR<V, T> viewR() {
            return unwrap().viewR();
        }

        @Override
        public boolean equals(Object obj) {
            return unwrap().equals(obj);
        }

        @Override
        public boolean elementsEqual(FingerTree<?, ?> other) {
            return unwrap().elementsEqual(other);
        }

        @Override
        public int hashCode() {
            return unwrap().hashCode();
        }

        @Override
        public String toString() {
            return unwrap().toString();
        }

        @Override
        public void print(StringBuilder sb, String padding, Printer<? super T> printer) {
            unwrap().print(sb, padding, printer);
        }

        @Override
        public FingerTree<V, T> concat(FingerTree<V, T> tree) {
            return unwrap().concat(tree);
        }

        @Override
        public FingerTree<V, T> concat(T a, FingerTree<V, T> tree) {
            return unwrap().concat(a, tree);
        }

        @Override
        public FingerTree<V, T> concat(T a, T b, FingerTree<V, T> tree) {
            return unwrap().concat(a, b, tree);
        }

        @Override
        public FingerTree<V, T> concat(T a, T b, T c, FingerTree<V, T> tree) {
            return unwrap().concat(a, b, c, tree);
        }

        @Override
        public FingerTree<V, T> concat(T a, T b, T c, T d, FingerTree<V, T> tree) {
            return unwrap().concat(a, b, c, d, tree);
        }

        @Override
        public int getSize() {
            return unwrap().getSize();
        }

    }

}
