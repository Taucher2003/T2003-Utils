package com.gitlab.taucher2003.t2003_utils.tjda.page;

import net.dv8tion.jda.api.entities.User;

public abstract class DefaultPagination implements Paginated {

    private final int pageAmount;
    private int currentPage;

    public DefaultPagination(int pageAmount) {
        this.pageAmount = pageAmount;
    }

    @Override
    public void moveNext() {
        if (currentPage + 1 >= pageAmount) {
            currentPage = 0;
            return;
        }
        currentPage++;
    }

    @Override
    public void movePrevious() {
        if (currentPage <= 0) {
            currentPage = pageAmount - 1;
            return;
        }
        currentPage--;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public boolean hasPrevious() {
        return true;
    }

    @Override
    public int maxPages() {
        return pageAmount;
    }

    @Override
    public int currentPage() {
        return currentPage;
    }

    @Override
    public void movePage(int page) {
        if (page < 0 || page >= pageAmount) {
            return;
        }
        currentPage = page;
    }

    @Override
    public boolean canInteract(User user) {
        return true;
    }
}
