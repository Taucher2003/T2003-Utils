package com.gitlab.taucher2003.t2003_utils.tjda.commands.build.meta;

import com.gitlab.taucher2003.t2003_utils.tjda.commands.Permissible;
import com.gitlab.taucher2003.t2003_utils.tjda.commands.SubCommand;

import java.util.Collection;
import java.util.function.Consumer;

public class SubCommandableMeta<T> extends CommandMeta<T> {

    private final Collection<SubCommand> subCommands;

    public SubCommandableMeta(String name, String description, Permissible permissible, Consumer<T> configurator, Collection<SubCommand> subCommands) {
        super(name, description, permissible, configurator);
        this.subCommands = subCommands;
    }

    public Collection<SubCommand> getSubCommands() {
        return subCommands;
    }
}
