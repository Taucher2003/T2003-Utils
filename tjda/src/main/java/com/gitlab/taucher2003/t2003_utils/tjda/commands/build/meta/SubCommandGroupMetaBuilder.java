package com.gitlab.taucher2003.t2003_utils.tjda.commands.build.meta;

import com.gitlab.taucher2003.t2003_utils.tjda.commands.Permissible;
import com.gitlab.taucher2003.t2003_utils.tjda.commands.SubCommand;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class SubCommandGroupMetaBuilder {

    private final String name;
    private final String description;
    private final Collection<SubCommand> subCommands = new ArrayList<>();

    private Permissible permissible = Permissible.UNRESTRICTED;

    public SubCommandGroupMetaBuilder(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public SubCommandGroupMetaBuilder setSubCommands(Collection<SubCommand> subCommands) {
        this.subCommands.clear();
        this.subCommands.addAll(subCommands);
        return this;
    }

    public SubCommandGroupMetaBuilder addSubCommand(SubCommand subCommand) {
        this.subCommands.add(subCommand);
        return this;
    }

    public SubCommandGroupMetaBuilder setPermissible(Permissible permissible) {
        this.permissible = permissible;
        return this;
    }

    public void configureSubCommandGroup(SubcommandGroupData data) {
        data.setName(name);
        data.setDescription(description);
        if (!subCommands.isEmpty()) {
            data.addSubcommands(subCommands.stream().map(SubCommand::asJdaObject).collect(Collectors.toList()));
        }
    }

    public SubCommandableMeta<SubcommandGroupData> build() {
        return new SubCommandableMeta<>(name, description, permissible, this::configureSubCommandGroup, subCommands);
    }
}
