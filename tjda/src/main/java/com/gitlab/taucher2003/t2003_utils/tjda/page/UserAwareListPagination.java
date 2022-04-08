package com.gitlab.taucher2003.t2003_utils.tjda.page;

import net.dv8tion.jda.api.entities.ISnowflake;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class UserAwareListPagination<T> extends UserAwarePagination {

	private final List<T> items;

    public UserAwareListPagination(Collection<T> items, ISnowflake user) {
        this(items, user.getIdLong());
    }

    public UserAwareListPagination(Collection<T> items, long userId) {
        super(items.size(), userId);
        this.items = new ArrayList<>(items);
    }

    public T element() {
        return items.get(currentPage());
    }
}
