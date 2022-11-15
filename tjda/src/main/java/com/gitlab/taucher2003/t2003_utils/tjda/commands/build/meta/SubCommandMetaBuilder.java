package com.gitlab.taucher2003.t2003_utils.tjda.commands.build.meta;

import com.gitlab.taucher2003.t2003_utils.tjda.commands.CommandArgument;
import com.gitlab.taucher2003.t2003_utils.tjda.commands.Permissible;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class SubCommandMetaBuilder {

    private final String name;
    private final String description;
    private final Collection<CommandArgument> arguments = new ArrayList<>();

    private Permissible permissible;

    public SubCommandMetaBuilder(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public SubCommandMetaBuilder setArguments(Collection<CommandArgument> arguments) {
        this.arguments.clear();
        this.arguments.addAll(arguments);
        return this;
    }

    public SubCommandMetaBuilder addArgument(CommandArgument argument) {
        this.arguments.add(argument);
        return this;
    }

    public SubCommandMetaBuilder setPermissible(Permissible permissible) {
        this.permissible = permissible;
        return this;
    }

    public void configureSubCommand(SubcommandData data) {
        data.setName(name);
        data.setDescription(description);
        if (!arguments.isEmpty()) {
            data.addOptions().addOptions(arguments.stream().map(CommandArgument::asJdaObject).collect(Collectors.toList()));
        }
    }

    public CommandMeta<SubcommandData> build() {
        return new CommandMeta<>(name, description, permissible, this::configureSubCommand);
    }
}
