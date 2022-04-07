package com.gitlab.taucher2003.t2003_utils.tjda.page;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.concurrent.CompletableFuture;

public interface Paginated {

    void moveNext();

    void movePrevious();

    boolean hasNext();

    boolean hasPrevious();

    int maxPages();

    int currentPage();

    void movePage(int page);

    CompletableFuture<MessageEmbed> getPageEmbed();

    boolean canInteract(User user);
}
