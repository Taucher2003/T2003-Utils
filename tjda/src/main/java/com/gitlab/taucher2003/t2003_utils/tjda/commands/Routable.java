package com.gitlab.taucher2003.t2003_utils.tjda.commands;

import com.gitlab.taucher2003.t2003_utils.tjda.theme.Theme;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.util.function.Function;

public interface Routable {

    String name();

    void doExecuteRouting(CommandInteraction event, Theme theme, Permissible.PermissibleContext permissibleContext,
                          Function<Permissible, Permissible> permissibleCreator, SlashCommandManagerHook hook);

    void doAutocompleteRouting(CommandAutoCompleteInteraction event, Permissible.PermissibleContext permissibleContext,
                               Function<Permissible, Permissible> permissibleCreator);
}
