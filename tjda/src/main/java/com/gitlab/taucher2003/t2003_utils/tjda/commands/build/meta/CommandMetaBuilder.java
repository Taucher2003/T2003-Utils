package com.gitlab.taucher2003.t2003_utils.tjda.commands.build.meta;

import com.gitlab.taucher2003.t2003_utils.tjda.commands.CommandArgument;
import com.gitlab.taucher2003.t2003_utils.tjda.commands.CommandGroup;
import com.gitlab.taucher2003.t2003_utils.tjda.commands.Permissible;
import com.gitlab.taucher2003.t2003_utils.tjda.commands.SubCommand;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class CommandMetaBuilder {

    private final String name;
    private final String description;

    private final Collection<CommandArgument> arguments = new ArrayList<>();
    private final Collection<CommandGroup> groups = new ArrayList<>();
    private final Collection<SubCommand> subCommands = new ArrayList<>();
    private Permissible permissible = Permissible.UNRESTRICTED;

    public CommandMetaBuilder(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public CommandMetaBuilder setArguments(Collection<CommandArgument> arguments) {
        this.arguments.clear();
        this.arguments.addAll(arguments);
        return this;
    }

    public CommandMetaBuilder addArgument(CommandArgument argument) {
        this.arguments.add(argument);
        return this;
    }

    public CommandMetaBuilder setGroups(Collection<CommandGroup> arguments) {
        this.groups.clear();
        this.groups.addAll(arguments);
        return this;
    }

    public CommandMetaBuilder addGroup(CommandGroup group) {
        this.groups.add(group);
        return this;
    }

    public CommandMetaBuilder setSubCommands(Collection<SubCommand> arguments) {
        this.subCommands.clear();
        this.subCommands.addAll(arguments);
        return this;
    }

    public CommandMetaBuilder addSubCommand(SubCommand argument) {
        this.subCommands.add(argument);
        return this;
    }

    public CommandMetaBuilder setPermissible(Permissible permissible) {
        this.permissible = permissible;
        return this;
    }

    public void configureCommand(SlashCommandData data) {
        data.setName(name);
        data.setDescription(description);

        if (!arguments.isEmpty()) {
            data.addOptions().addOptions(arguments.stream().map(CommandArgument::asJdaObject).collect(Collectors.toList()));
        }

        if (!groups.isEmpty()) {
            data.addSubcommandGroups(groups.stream().map(CommandGroup::asJdaObject).collect(Collectors.toList()));
        }

        if (!subCommands.isEmpty()) {
            data.addSubcommands(subCommands.stream().map(SubCommand::asJdaObject).collect(Collectors.toList()));
        }

        data.setDefaultPermissions(DefaultMemberPermissions.enabledFor(permissible.defaultMemberPermissions()));
    }

    public SubCommandGroupableMeta build() {
        return new SubCommandGroupableMeta(name, description, permissible, this::configureCommand, subCommands, groups);
    }
}
