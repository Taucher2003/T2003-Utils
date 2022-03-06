package com.gitlab.taucher2003.t2003_utils.tjda.commands;

import com.gitlab.taucher2003.t2003_utils.tjda.theme.Theme;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@FunctionalInterface
public interface CommandExecutor {

    void execute(CommandInteraction event, Theme theme, Permissible.PermissibleContext permissibleContext);

    default void autocomplete(CommandAutoCompleteInteraction event, Permissible.PermissibleContext permissibleContext) {
    }
}
