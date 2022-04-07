package com.gitlab.taucher2003.t2003_utils.tjda.page;

import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.User;

public abstract class UserAwarePagination extends DefaultPagination {

    private final long userId;

    public UserAwarePagination(int pageAmount, ISnowflake user) {
        this(pageAmount, user.getIdLong());
    }

    public UserAwarePagination(int pageAmount, long userId) {
        super(pageAmount);
        this.userId = userId;
    }

    @Override
    public boolean canInteract(User user) {
        return user.getIdLong() == userId;
    }
}
