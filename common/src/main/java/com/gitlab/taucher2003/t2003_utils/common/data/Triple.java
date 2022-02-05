package com.gitlab.taucher2003.t2003_utils.common.data;

import java.util.Objects;

/**
 * Class to store three objects. Useful as return type to return multiple objects
 *
 * @param <F> type of the first object
 * @param <S> type of the second object
 * @param <T> type of the third object
 */
public class Triple<F, S, T> {

    private final F first;
    private final S second;
    private final T third;

    /**
     * Creates a new Pair
     *
     * @param first  the first element
     * @param second the second element
     */
    public Triple(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    /**
     * Returns the first element of this triple
     *
     * @return the element
     */
    public F first() {
        return first;
    }

    /**
     * Returns the second element of this triple
     *
     * @return the element
     */
    public S second() {
        return second;
    }

    /**
     * Returns the third element of this triple
     *
     * @return the element
     */
    public T third() {
        return third;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var triple = (Triple<?, ?, ?>) o;
        return Objects.equals(first, triple.first) && Objects.equals(second, triple.second) && Objects.equals(third, triple.third);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "DataPair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
