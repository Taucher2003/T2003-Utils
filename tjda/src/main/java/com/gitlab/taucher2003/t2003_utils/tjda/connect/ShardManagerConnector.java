package com.gitlab.taucher2003.t2003_utils.tjda.connect;

import com.gitlab.taucher2003.t2003_utils.tjda.commands.SlashCommandManager;
import com.gitlab.taucher2003.t2003_utils.tjda.page.PaginationManager;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class ShardManagerConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShardManagerConnector.class);

    private final DefaultShardManagerBuilder builder;
    private SlashCommandManager slashCommandManager;
    private PaginationManager paginationManager;

    public ShardManagerConnector(DefaultShardManagerBuilder builder) {
        this.builder = builder;
    }

    public ShardManagerConnector withSlashCommandManager(SlashCommandManager slashCommandManager) {
        this.slashCommandManager = slashCommandManager;
        return this;
    }

    public ShardManagerConnector withPaginationManager() {
        return withPaginationManager(new PaginationManager());
    }

    public ShardManagerConnector withPaginationManager(PaginationManager paginationManager) {
        this.paginationManager = paginationManager;
        return this;
    }

    public ShardManager build() throws LoginException, InterruptedException {
        return build(true);
    }

    public ShardManager build(boolean login) throws LoginException, InterruptedException {
        if (slashCommandManager != null) {
            builder.addEventListeners(new InteractionsListener(slashCommandManager));
        }

        if (paginationManager != null) {
            builder.addEventListeners(paginationManager);
        }

        var count = 1;
        while (true) {
            try {
                return builder.build(login);
            } catch (LoginException e) {
                throw e;
            } catch (Exception e) {
                var delay = Math.min(30, count++) * 1000L;
                LOGGER.error("Failed to login ShardManager.", e);
                LOGGER.info("Retrying in {}ms", delay);
                Thread.sleep(delay);
                Thread.onSpinWait();
            }
        }
    }
}
