package com.gitlab.taucher2003.t2003_utils.tjda.commands.build.meta;

import com.gitlab.taucher2003.t2003_utils.tjda.commands.CommandGroup;
import com.gitlab.taucher2003.t2003_utils.tjda.commands.Permissible;
import com.gitlab.taucher2003.t2003_utils.tjda.commands.SubCommand;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Collection;
import java.util.function.Consumer;

public class SubCommandGroupableMeta extends SubCommandableMeta<SlashCommandData> {

    private final Collection<CommandGroup> commandGroups;

    public SubCommandGroupableMeta(
            String name,
            String description,
            Permissible permissible,
            Consumer<SlashCommandData> configurator,
            Collection<SubCommand> subCommands,
            Collection<CommandGroup> commandGroups
    ) {
        super(name, description, permissible, configurator, subCommands);
        this.commandGroups = commandGroups;
    }

    public Collection<CommandGroup> getCommandGroups() {
        return commandGroups;
    }
}
