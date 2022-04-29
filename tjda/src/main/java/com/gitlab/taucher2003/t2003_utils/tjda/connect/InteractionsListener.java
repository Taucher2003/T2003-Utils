package com.gitlab.taucher2003.t2003_utils.tjda.connect;

import com.gitlab.taucher2003.t2003_utils.tjda.commands.SlashCommandManager;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class InteractionsListener extends ListenerAdapter {

    private final SlashCommandManager slashCommandManager;

    public InteractionsListener(SlashCommandManager slashCommandManager) {
        this.slashCommandManager = slashCommandManager;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        slashCommandManager.dispatch(event);
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        slashCommandManager.autocomplete(event);
    }
}
