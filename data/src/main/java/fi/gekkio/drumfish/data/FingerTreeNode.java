package fi.gekkio.drumfish.data;

import java.io.Serializable;
import java.util.Iterator;

import javax.annotation.Nullable;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;

import fi.gekkio.drumfish.data.FingerTree.Printer;
import fi.gekkio.drumfish.data.FingerTreeDigit.DigitSplit;
import fi.gekkio.drumfish.lang.Function2;
import fi.gekkio.drumfish.lang.LeftFold;
import fi.gekkio.drumfish.lang.Option;

abstract class FingerTreeNode<V, T> implements Iterable<T>, Serializable {
    private static final long serialVersionUID = -9130857121651032905L;

    private FingerTreeNode() {
    }

    public abstract V measure();

    public abstract <U, O> FingerTreeNode<U, O> map(FingerTreeFactory<U, O> factory, Function<? super T, O> f);

    public abstract DigitSplit<V, T> split(FingerTreeFactory<V, T> factory, Predicate<? super V> p, V accum);

    public abstract FingerTreeDigit<V, T> toDigit(FingerTreeFactory<V, T> factory);

    public abstract void print(StringBuilder sb, String padding, Printer<? super T> printer);

    public abstract Iterator<T> reverseIterator();

    public abstract FingerTreeNode<V, T> reverseAndMap(FingerTreeFactory<V, T> factory, Function<T, T> f);

    public abstract <U> U foldLeft(@Nullable U initial, Function2<U, T, U> f);

    public abstract Option<T> find(FingerTreeFactory<V, T> factory, Predicate<? super V> p);

    @RequiredArgsConstructor
    @EqualsAndHashCode(callSuper = false, exclude = "measure")
    @ToString(callSuper = false)
    static final class Node2<V, T> extends FingerTreeNode<V, T> {
        private static final long serialVersionUID = 7669550720868602973L;

        public final V measure;
        public final T a;
        public final T b;

        @Override
        public V measure() {
            return measure;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Iterator<T> iterator() {
            return Iterators.forArray(a, b);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Iterator<T> reverseIterator() {
            return Iterators.forArray(b, a);
        }

        @Override
        public <U, O> FingerTreeNode<U, O> map(FingerTreeFactory<U, O> factory, Function<? super T, O> f) {
            return factory.node(f.apply(a), f.apply(b));
        }

        @Override
        public DigitSplit<V, T> split(FingerTreeFactory<V, T> factory, Predicate<? super V> p, V accum) {
            V accumA = factory.mappend(accum, factory.measure(a));
            if (p.apply(accumA))
                return new DigitSplit<V, T>(null, a, factory.digit(b));
            return new DigitSplit<V, T>(factory.digit(a), b, null);
        }

        @Override
        public FingerTreeDigit<V, T> toDigit(FingerTreeFactory<V, T> factory) {
            return factory.digit(a, b);
        }

        @Override
        public void print(StringBuilder sb, String padding, Printer<? super T> printer) {
            sb.append("*\n");

            sb.append(padding);
            sb.append("|+");
            printer.print(sb, padding + "  ", a);
            sb.append('\n');

            sb.append(padding);
            sb.append("\\+");
            printer.print(sb, padding + "  ", b);
        }

        @Override
        public FingerTreeNode<V, T> reverseAndMap(FingerTreeFactory<V, T> factory, Function<T, T> f) {
            return factory.node(f.apply(b), f.apply(a));
        }

        @Override
        public <U> U foldLeft(@Nullable U initial, Function2<U, T, U> f) {
            U accum = initial;
            accum = f.apply(accum, a);
            accum = f.apply(accum, b);
            return accum;
        }

        @Override
        public Option<T> find(FingerTreeFactory<V, T> factory, Predicate<? super V> p) {
            if (p.apply(factory.measure(a)))
                return Option.some(a);
            if (p.apply(factory.measure(b)))
                return Option.some(b);
            return Option.none();
        }

    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(callSuper = false, exclude = "measure")
    @ToString(callSuper = false)
    static final class Node3<V, T> extends FingerTreeNode<V, T> {
        private static final long serialVersionUID = -3973692276446284002L;

        public final V measure;
        public final T a;
        public final T b;
        public final T c;

        @Override
        public V measure() {
            return measure;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Iterator<T> iterator() {
            return Iterators.forArray(a, b, c);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Iterator<T> reverseIterator() {
            return Iterators.forArray(c, b, a);
        }

        @Override
        public <U, O> FingerTreeNode<U, O> map(FingerTreeFactory<U, O> factory, Function<? super T, O> f) {
            return factory.node(f.apply(a), f.apply(b), f.apply(c));
        }

        @Override
        public DigitSplit<V, T> split(FingerTreeFactory<V, T> factory, Predicate<? super V> p, V accum) {
            V accumA = factory.mappend(accum, factory.measure(a));
            if (p.apply(accumA))
                return new DigitSplit<V, T>(null, a, factory.digit(b, c));
            V accumB = factory.mappend(accumA, factory.measure(b));
            if (p.apply(accumB))
                return new DigitSplit<V, T>(factory.digit(a), b, factory.digit(c));
            return new DigitSplit<V, T>(factory.digit(a, b), c, null);
        }

        @Override
        public FingerTreeDigit<V, T> toDigit(FingerTreeFactory<V, T> factory) {
            return factory.digit(a, b, c);
        }

        @Override
        public void print(StringBuilder sb, String padding, Printer<? super T> printer) {
            sb.append("*\n");

            sb.append(padding);
            sb.append("|+");
            printer.print(sb, padding + "  ", a);
            sb.append('\n');

            sb.append(padding);
            sb.append("|+");
            printer.print(sb, padding + "  ", b);
            sb.append('\n');

            sb.append(padding);
            sb.append("\\+");
            printer.print(sb, padding + "  ", c);
        }

        @Override
        public FingerTreeNode<V, T> reverseAndMap(FingerTreeFactory<V, T> factory, Function<T, T> f) {
            return factory.node(f.apply(c), f.apply(b), f.apply(a));
        }

        @Override
        public <U> U foldLeft(@Nullable U initial, Function2<U, T, U> f) {
            U accum = initial;
            accum = f.apply(accum, a);
            accum = f.apply(accum, b);
            accum = f.apply(accum, c);
            return accum;
        }

        @Override
        public Option<T> find(FingerTreeFactory<V, T> factory, Predicate<? super V> p) {
            if (p.apply(factory.measure(a)))
                return Option.some(a);
            if (p.apply(factory.measure(b)))
                return Option.some(b);
            if (p.apply(factory.measure(c)))
                return Option.some(c);
            return Option.none();
        }

    }

    @RequiredArgsConstructor
    static final class NodeMapper<T, U, O> implements Function<FingerTreeNode<?, T>, FingerTreeNode<U, O>>, Serializable {
        private static final long serialVersionUID = 71975552025750738L;

        private final FingerTreeFactory<U, O> factory;
        private final Function<? super T, O> f;

        @Override
        public FingerTreeNode<U, O> apply(FingerTreeNode<?, T> input) {
            return input.map(factory, f);
        }

    }

    @RequiredArgsConstructor
    static final class NodeReverser<V, T> implements Function<FingerTreeNode<V, T>, FingerTreeNode<V, T>> {
        private final FingerTreeFactory<V, T> factory;
        private final Function<T, T> f;

        @Override
        public FingerTreeNode<V, T> apply(FingerTreeNode<V, T> input) {
            return input.reverseAndMap(factory, f);
        }
    }

    @RequiredArgsConstructor
    static final class NodeIterator<T> extends UnmodifiableIterator<T> {
        private final Iterator<? extends FingerTreeNode<?, T>> nodes;

        private Iterator<T> elements;

        @Override
        public boolean hasNext() {
            if (elements == null)
                return nodes.hasNext();
            return elements.hasNext() || nodes.hasNext();
        }

        @Override
        public T next() {
            if (elements == null || !elements.hasNext())
                elements = nodes.next().iterator();
            return elements.next();
        }
    }

    @RequiredArgsConstructor
    static final class NodeLeftFold<V, T, U> implements LeftFold<FingerTreeNode<V, T>, U> {
        private final Function2<U, T, U> f;

        @Override
        public U apply(U first, FingerTreeNode<V, T> second) {
            return second.foldLeft(first, f);
        }

    }

    @RequiredArgsConstructor
    static final class NodeReverseIterator<T> extends UnmodifiableIterator<T> {
        private final Iterator<? extends FingerTreeNode<?, T>> nodes;

        private Iterator<T> elements;

        @Override
        public boolean hasNext() {
            if (elements == null)
                return nodes.hasNext();
            return elements.hasNext() || nodes.hasNext();
        }

        @Override
        public T next() {
            if (elements == null || !elements.hasNext())
                elements = nodes.next().reverseIterator();
            return elements.next();
        }
    }

    @RequiredArgsConstructor
    static final class NodePrinter<T> implements Printer<FingerTreeNode<?, T>> {
        private final Printer<? super T> printer;

        @Override
        public void print(StringBuilder sb, String padding, FingerTreeNode<?, T> value) {
            value.print(sb, padding, printer);
        }
    }

}
