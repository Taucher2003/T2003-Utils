package com.gitlab.taucher2003.t2003_utils.tjda.page;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PaginationManager extends ListenerAdapter {

    private static final String PREVIOUS_PAGE_ID = "pagination:previous";
    private static final String NEXT_PAGE_ID = "pagination:next";

    private final Map<Long, Paginated> paginations = new ConcurrentHashMap<>();

    public void paginate(IReplyCallback interaction, Paginated paginated) {
        paginated.movePage(0);
        paginated.getPageEmbed().thenAccept(embed -> {
            interaction.replyEmbeds(embed)
                    .addActionRow(getPaginationButtons(paginated))
                    .flatMap(InteractionHook::retrieveOriginal)
                    .queue(message -> paginations.put(message.getIdLong(), paginated));
        });
    }

    public void paginate(MessageChannel channel, Paginated paginated) {
        paginated.movePage(0);
        paginated.getPageEmbed().thenAccept(embed -> {
            channel.sendMessageEmbeds(embed)
                    .setActionRow(getPaginationButtons(paginated))
                    .queue(message -> paginations.put(message.getIdLong(), paginated));
        });
    }

    private List<ActionComponent> getPaginationButtons(Paginated paginated) {
        return List.of(
                Button.of(ButtonStyle.PRIMARY, PREVIOUS_PAGE_ID, Emoji.fromUnicode("⬅")).withDisabled(!paginated.hasPrevious()),
                Button.of(ButtonStyle.SECONDARY, "pagination:page", (paginated.currentPage() + 1) + "/" + paginated.maxPages()),
                Button.of(ButtonStyle.PRIMARY, NEXT_PAGE_ID, Emoji.fromUnicode("➡️")).withDisabled(!paginated.hasNext())
        );
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        event.deferEdit().queue();
        event.getHook().retrieveOriginal().queue(message -> {
            if (!paginations.containsKey(message.getIdLong())) {
                return;
            }
            var paginated = paginations.get(message.getIdLong());
            if (!paginated.canInteract(event.getUser())) {
                return;
            }

            var buttonId = event.getButton().getId();

            if (NEXT_PAGE_ID.equals(buttonId) && paginated.hasNext()) {
                paginated.moveNext();
            } else if (PREVIOUS_PAGE_ID.equals(buttonId) && paginated.hasPrevious()) {
                paginated.movePrevious();
            }
            sendPage(paginated, message);
        });
    }

    private void sendPage(Paginated paginated, Message message) {
        paginated.getPageEmbed().thenAccept(embed -> {
            message.editMessageEmbeds()
                    .setEmbeds(embed)
                    .setActionRow(getPaginationButtons(paginated))
                    .queue();
        });
    }
}
