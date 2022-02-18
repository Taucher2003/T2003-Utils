package com.gitlab.taucher2003.t2003_utils.tjda.commands.shared;

import com.gitlab.taucher2003.t2003_utils.common.data.Pair;
import com.gitlab.taucher2003.t2003_utils.tjda.commands.Command;
import com.gitlab.taucher2003.t2003_utils.tjda.commands.Permissible;
import com.gitlab.taucher2003.t2003_utils.tjda.theme.Theme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class PingCommand extends Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(PingCommand.class);

    public PingCommand() {
        super("ping", "The ping of the bot to Discord");
    }

    @Override
    public void execute(CommandInteraction event, Theme theme, Permissible.PermissibleContext permissibleContext) {
        var hookFuture = event.replyEmbeds(
                new EmbedBuilder()
                        .setDescription("Pinging ...")
                        .setColor(theme.primary())
                        .build()
        ).submit();

        var restPingFuture = event.getJDA().getRestPing().submit();

        composeFutures(hookFuture, restPingFuture).whenComplete((pair, ex) -> {
            if (ex != null) {
                LOGGER.error("Failed to execute {}", getClass().getSimpleName(), ex);
                event.replyEmbeds(
                        new EmbedBuilder()
                                .setDescription("A fatal error occured while executing ping")
                                .setColor(theme.danger())
                                .build()
                ).queue();
                return;
            }

            var hook = pair.first();
            var restPing = pair.second();
            var gatewayPing = event.getJDA().getGatewayPing();
            var hostPing = getHostPing();

            hook.editOriginalEmbeds(
                    new EmbedBuilder()
                            .setDescription(String.format(
                                    "**Pong! :ping_pong:**\n\nREST Ping: `%dms`\nGateway Ping: `%dms`\nHost Ping: `%dms`",
                                    restPing,
                                    gatewayPing,
                                    hostPing
                            )).setColor(theme.primary())
                            .setFooter("hosted in Falkenstein, Germany")
                            .build()
            ).queue();
        });
    }

    private long getHostPing() {
        long start = 1;
        var connected = false;
        long end = 0;
        try (var socket = new Socket()) {
            start = System.currentTimeMillis();
            socket.connect(new InetSocketAddress(InetAddress.getByName("discord.com"), 80));
            connected = true;
            end = System.currentTimeMillis();
        } catch (IOException e) {
            LOGGER.error("An exception occurred while executing ping", e);
        }

        if (!connected) {
            return -1;
        }

        return end - start;
    }

    private <F, S> CompletableFuture<Pair<F, S>> composeFutures(CompletionStage<F> firstFuture, CompletionStage<S> secondFuture) {
        var future = new CompletableFuture<Pair<F, S>>();
        firstFuture.whenComplete((val, ex) -> {
            if (ex != null) {
                future.completeExceptionally(ex);
                return;
            }
            secondFuture.whenComplete((val2, ex2) -> {
                if (ex2 != null) {
                    future.completeExceptionally(ex2);
                    return;
                }
                future.complete(new Pair<>(val, val2));
            });
        });
        return future;
    }
}
