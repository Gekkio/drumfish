package fi.gekkio.drumfish.data;

import java.io.Serializable;
import java.util.Iterator;

import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Value;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import fi.gekkio.drumfish.data.FingerTree.Printer;
import fi.gekkio.drumfish.lang.Function2;
import fi.gekkio.drumfish.lang.Option;

abstract class FingerTreeDigit<V, T> implements Iterable<T>, Serializable {
    private static final long serialVersionUID = 751532813120711150L;

    private FingerTreeDigit() {
    }

    public abstract FingerTreeDigit<V, T> prepend(FingerTreeFactory<V, T> factory, T value);

    public abstract FingerTreeDigit<V, T> append(FingerTreeFactory<V, T> factory, T value);

    public abstract V measure();

    public abstract <U, O> FingerTreeDigit<U, O> map(FingerTreeFactory<U, O> factory, Function<? super T, O> f);

    public abstract T getHead();

    public abstract FingerTreeDigit<V, T> getTail(FingerTreeFactory<V, T> factory);

    public abstract FingerTreeDigit<V, T> getInit(FingerTreeFactory<V, T> factory);

    public abstract T getLast();

    public abstract DigitSplit<V, T> split(FingerTreeFactory<V, T> factory, Predicate<? super V> p, V accum);

    public abstract FingerTreeNode<V, T> toTailNode(FingerTreeFactory<V, T> factory);

    public abstract FingerTreeNode<V, T> toInitNode(FingerTreeFactory<V, T> factory);

    public abstract FingerTree<V, T> toTree(FingerTreeFactory<V, T> factory);

    public abstract void print(StringBuilder sb, String padding, Printer<? super T> printer);

    public abstract Iterator<T> reverseIterator();

    public abstract FingerTreeDigit<V, T> reverseAndMap(FingerTreeFactory<V, T> factory, Function<T, T> f);

    public abstract <U> U foldLeft(@Nullable U initial, Function2<U, T, U> f);

    public abstract Option<T> find(FingerTreeFactory<V, T> factory, Predicate<? super V> p);

    @Value
    static class DigitSplit<V, T> implements Serializable {
        private static final long serialVersionUID = -5364760129719273207L;

        @Nullable
        public final FingerTreeDigit<V, T> left;
        public final T pivot;
        @Nullable
        public final FingerTreeDigit<V, T> right;
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    @EqualsAndHashCode(callSuper = false)
    @ToString(callSuper = false)
    static final class Digit1<V, T> extends FingerTreeDigit<V, T> {
        private static final long serialVersionUID = -667222115592102172L;

        final V measure;
        final T a;

        @Override
        public FingerTreeDigit<V, T> prepend(FingerTreeFactory<V, T> factory, T value) {
            return factory.digit(value, a);
        }

        @Override
        public FingerTreeDigit<V, T> append(FingerTreeFactory<V, T> factory, T value) {
            return factory.digit(a, value);
        }

        @Override
        public <U, O> FingerTreeDigit<U, O> map(FingerTreeFactory<U, O> factory, Function<? super T, O> f) {
            return factory.digit(f.apply(a));
        }

        @Override
        public V measure() {
            return measure;
        }

        @Override
        public Iterator<T> iterator() {
            return Iterators.singletonIterator(a);
        }

        @Override
        public Iterator<T> reverseIterator() {
            return Iterators.singletonIterator(a);
        }

        @Override
        public T getHead() {
            return a;
        }

        @Override
        public FingerTreeDigit<V, T> getTail(FingerTreeFactory<V, T> factory) {
            throw new UnsupportedOperationException("Cannot get tail from Digit1");
        }

        @Override
        public FingerTreeNode<V, T> toTailNode(FingerTreeFactory<V, T> factory) {
            throw new UnsupportedOperationException("Cannot get tail node from Digit1");
        }

        @Override
        public FingerTreeDigit<V, T> getInit(FingerTreeFactory<V, T> factory) {
            throw new UnsupportedOperationException("Cannot get init from Digit1");
        }

        @Override
        public FingerTreeNode<V, T> toInitNode(FingerTreeFactory<V, T> factory) {
            throw new UnsupportedOperationException("Cannot get init node from Digit1");
        }

        @Override
        public T getLast() {
            return a;
        }

        @Override
        public DigitSplit<V, T> split(FingerTreeFactory<V, T> factory, Predicate<? super V> p, V accum) {
            return new DigitSplit<V, T>(null, a, null);
        }

        @Override
        public FingerTree<V, T> toTree(FingerTreeFactory<V, T> factory) {
            return factory.tree(a);
        }

        @Override
        public void print(StringBuilder sb, String padding, Printer<? super T> printer) {
            sb.append('\n');

            sb.append(padding);
            sb.append("\\-");
            printer.print(sb, padding + "  ", a);
        }

        @Override
        public FingerTreeDigit<V, T> reverseAndMap(FingerTreeFactory<V, T> factory, Function<T, T> f) {
            return factory.digit(f.apply(a));
        }

        @Override
        public <U> U foldLeft(@Nullable U initial, Function2<U, T, U> f) {
            U accum = initial;
            accum = f.apply(accum, a);
            return accum;
        }

        @Override
        public Option<T> find(FingerTreeFactory<V, T> factory, Predicate<? super V> p) {
            if (p.apply(factory.measure(a)))
                return Option.some(a);
            return Option.none();
        }

    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    @EqualsAndHashCode(callSuper = false)
    @ToString(callSuper = false)
    static final class Digit2<V, T> extends FingerTreeDigit<V, T> {
        private static final long serialVersionUID = 3093918006332086954L;

        final V measure;
        final T a;
        final T b;

        @Override
        public FingerTreeDigit<V, T> prepend(FingerTreeFactory<V, T> factory, T value) {
            return factory.digit(value, a, b);
        }

        @Override
        public FingerTreeDigit<V, T> append(FingerTreeFactory<V, T> factory, T value) {
            return factory.digit(a, b, value);
        }

        @Override
        public <U, O> FingerTreeDigit<U, O> map(FingerTreeFactory<U, O> factory, Function<? super T, O> f) {
            return factory.digit(f.apply(a), f.apply(b));
        }

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
        public T getHead() {
            return a;
        }

        @Override
        public FingerTreeDigit<V, T> getTail(FingerTreeFactory<V, T> factory) {
            return factory.digit(b);
        }

        @Override
        public FingerTreeNode<V, T> toTailNode(FingerTreeFactory<V, T> factory) {
            throw new UnsupportedOperationException("Cannot get tail node from Digit2");
        }

        @Override
        public FingerTreeDigit<V, T> getInit(FingerTreeFactory<V, T> factory) {
            return factory.digit(a);
        }

        @Override
        public FingerTreeNode<V, T> toInitNode(FingerTreeFactory<V, T> factory) {
            throw new UnsupportedOperationException("Cannot get init node from Digit2");
        }

        @Override
        public T getLast() {
            return b;
        }

        @Override
        public DigitSplit<V, T> split(FingerTreeFactory<V, T> factory, Predicate<? super V> p, V accum) {
            V accumA = factory.mappend(accum, factory.measure(a));
            if (p.apply(accumA))
                return new DigitSplit<V, T>(null, a, factory.digit(b));
            return new DigitSplit<V, T>(factory.digit(a), b, null);
        }

        @Override
        public FingerTree<V, T> toTree(FingerTreeFactory<V, T> factory) {
            return factory.deep(factory.digit(a), factory.nodeFactory().emptyTree, factory.digit(b));
        }

        @Override
        public void print(StringBuilder sb, String padding, Printer<? super T> printer) {
            sb.append('\n');

            sb.append(padding);
            sb.append("|-");
            printer.print(sb, padding + "  ", a);
            sb.append('\n');

            sb.append(padding);
            sb.append("\\-");
            printer.print(sb, padding + "  ", b);
        }

        @Override
        public FingerTreeDigit<V, T> reverseAndMap(FingerTreeFactory<V, T> factory, Function<T, T> f) {
            return factory.digit(f.apply(b), f.apply(a));
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

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    @EqualsAndHashCode(callSuper = false)
    @ToString(callSuper = false)
    static final class Digit3<V, T> extends FingerTreeDigit<V, T> {
        private static final long serialVersionUID = -782252146232630363L;

        final V measure;
        final T a;
        final T b;
        final T c;

        @Override
        public FingerTreeDigit<V, T> prepend(FingerTreeFactory<V, T> factory, T value) {
            return factory.digit(value, a, b, c);
        }

        @Override
        public FingerTreeDigit<V, T> append(FingerTreeFactory<V, T> factory, T value) {
            return factory.digit(a, b, c, value);
        }

        @Override
        public <U, O> FingerTreeDigit<U, O> map(FingerTreeFactory<U, O> factory, Function<? super T, O> f) {
            return factory.digit(f.apply(a), f.apply(b), f.apply(c));
        }

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
        public T getHead() {
            return a;
        }

        @Override
        public FingerTreeDigit<V, T> getTail(FingerTreeFactory<V, T> factory) {
            return factory.digit(b, c);
        }

        @Override
        public FingerTreeNode<V, T> toTailNode(FingerTreeFactory<V, T> factory) {
            return factory.node(b, c);
        }

        @Override
        public FingerTreeDigit<V, T> getInit(FingerTreeFactory<V, T> factory) {
            return factory.digit(a, b);
        }

        @Override
        public FingerTreeNode<V, T> toInitNode(FingerTreeFactory<V, T> factory) {
            return factory.node(a, b);
        }

        @Override
        public T getLast() {
            return c;
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
        public FingerTree<V, T> toTree(FingerTreeFactory<V, T> factory) {
            return factory.deep(factory.digit(a, b), factory.nodeFactory().emptyTree, factory.digit(c));
        }

        @Override
        public void print(StringBuilder sb, String padding, Printer<? super T> printer) {
            sb.append('\n');

            sb.append(padding);
            sb.append("|-");
            printer.print(sb, padding + "  ", a);
            sb.append('\n');

            sb.append(padding);
            sb.append("|-");
            printer.print(sb, padding + "  ", b);
            sb.append('\n');

            sb.append(padding);
            sb.append("\\-");
            printer.print(sb, padding + "  ", c);
        }

        @Override
        public FingerTreeDigit<V, T> reverseAndMap(FingerTreeFactory<V, T> factory, Function<T, T> f) {
            return factory.digit(f.apply(c), f.apply(b), f.apply(a));
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

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    @EqualsAndHashCode(callSuper = false)
    @ToString(callSuper = false)
    static final class Digit4<V, T> extends FingerTreeDigit<V, T> {
        private static final long serialVersionUID = 1441409605217019592L;

        final V measure;
        final T a;
        final T b;
        final T c;
        final T d;

        @Override
        public FingerTreeDigit<V, T> prepend(FingerTreeFactory<V, T> factory, T value) {
            throw new UnsupportedOperationException("Cannot prepend to Digit4");
        }

        @Override
        public FingerTreeDigit<V, T> append(FingerTreeFactory<V, T> factory, T value) {
            throw new UnsupportedOperationException("Cannot append to Digit4");
        }

        @Override
        public <U, O> FingerTreeDigit<U, O> map(FingerTreeFactory<U, O> factory, Function<? super T, O> f) {
            return factory.digit(f.apply(a), f.apply(b), f.apply(c), f.apply(d));
        }

        @Override
        public V measure() {
            return measure;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Iterator<T> iterator() {
            return Iterators.forArray(a, b, c, d);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Iterator<T> reverseIterator() {
            return Iterators.forArray(d, c, b, a);
        }

        @Override
        public T getHead() {
            return a;
        }

        @Override
        public FingerTreeDigit<V, T> getTail(FingerTreeFactory<V, T> factory) {
            return factory.digit(b, c, d);
        }

        @Override
        public FingerTreeNode<V, T> toTailNode(FingerTreeFactory<V, T> factory) {
            return factory.node(b, c, d);
        }

        @Override
        public FingerTreeDigit<V, T> getInit(FingerTreeFactory<V, T> factory) {
            return factory.digit(a, b, c);
        }

        @Override
        public FingerTreeNode<V, T> toInitNode(FingerTreeFactory<V, T> factory) {
            return factory.node(a, b, c);
        }

        @Override
        public T getLast() {
            return d;
        }

        @Override
        public DigitSplit<V, T> split(FingerTreeFactory<V, T> factory, Predicate<? super V> p, V accum) {
            V accumA = factory.mappend(accum, factory.measure(a));
            if (p.apply(accumA))
                return new DigitSplit<V, T>(null, a, factory.digit(b, c, d));
            V accumB = factory.mappend(accumA, factory.measure(b));
            if (p.apply(accumB))
                return new DigitSplit<V, T>(factory.digit(a), b, factory.digit(c, d));
            V accumC = factory.mappend(accumB, factory.measure(c));
            if (p.apply(accumC))
                return new DigitSplit<V, T>(factory.digit(a, b), c, factory.digit(d));
            return new DigitSplit<V, T>(factory.digit(a, b, c), d, null);
        }

        @Override
        public FingerTree<V, T> toTree(FingerTreeFactory<V, T> factory) {
            return factory.deep(factory.digit(a, b), factory.nodeFactory().emptyTree, factory.digit(c, d));
        }

        @Override
        public void print(StringBuilder sb, String padding, Printer<? super T> printer) {
            sb.append('\n');

            sb.append(padding);
            sb.append("|-");
            printer.print(sb, padding + "  ", a);
            sb.append('\n');

            sb.append(padding);
            sb.append("|-");
            printer.print(sb, padding + "  ", b);
            sb.append('\n');

            sb.append(padding);
            sb.append("|-");
            printer.print(sb, padding + "  ", c);
            sb.append('\n');

            sb.append(padding);
            sb.append("\\-");
            printer.print(sb, padding + "  ", d);
        }

        @Override
        public FingerTreeDigit<V, T> reverseAndMap(FingerTreeFactory<V, T> factory, Function<T, T> f) {
            return factory.digit(f.apply(d), f.apply(c), f.apply(b), f.apply(a));
        }

        @Override
        public <U> U foldLeft(@Nullable U initial, Function2<U, T, U> f) {
            U accum = initial;
            accum = f.apply(accum, a);
            accum = f.apply(accum, b);
            accum = f.apply(accum, c);
            accum = f.apply(accum, d);
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
            if (p.apply(factory.measure(d)))
                return Option.some(d);
            return Option.none();
        }
    }

}
