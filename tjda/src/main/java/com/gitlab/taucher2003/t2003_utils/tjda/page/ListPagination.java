package com.gitlab.taucher2003.t2003_utils.tjda.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ListPagination<T> extends DefaultPagination {

    private final List<T> items;

    public ListPagination(Collection<T> items) {
        super(items.size());
        this.items = new ArrayList<>(items);
    }

    public T element() {
        return items.get(currentPage());
    }
}
