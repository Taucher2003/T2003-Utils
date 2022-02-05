package com.gitlab.taucher2003.t2003_utils.common.data;

import java.util.Objects;

/**
 * Class to store two objects. Useful as return type to return multiple objects
 *
 * @param <F> type of the first object
 * @param <S> type of the second object
 */
public class Pair<F, S> {

    private final F first;
    private final S second;

    /**
     * Creates a new Pair
     *
     * @param first  the first element
     * @param second the second element
     */
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first element of this pair
     *
     * @return the element
     */
    public F first() {
        return first;
    }

    /**
     * Returns the second element of this pair
     *
     * @return the element
     */
    public S second() {
        return second;
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
        var pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
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
