package com.gitlab.taucher2003.t2003_utils.common;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BucketData {

    private final int limit;
    private final int resetAfter;
    private final TimeUnit resetAfterUnit;

    public BucketData(int limit, int resetAfter, TimeUnit resetAfterUnit) {
        this.limit = limit;
        this.resetAfter = resetAfter;
        this.resetAfterUnit = resetAfterUnit;
    }

    public int limit() {
        return limit;
    }

    public int resetAfter() {
        return resetAfter;
    }

    public TimeUnit resetAfterUnit() {
        return resetAfterUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var data = (BucketData) o;
        return limit == data.limit && resetAfter == data.resetAfter && resetAfterUnit == data.resetAfterUnit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(limit, resetAfter, resetAfterUnit);
    }

    @Override
    public String toString() {
        return "BucketData{" +
                "limit=" + limit +
                ", resetAfter=" + resetAfter +
                ", resetAfterUnit=" + resetAfterUnit +
                '}';
    }
}
