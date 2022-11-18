package com.gitlab.taucher2003.t2003_utils.tjda.commands.build.meta;

import com.gitlab.taucher2003.t2003_utils.tjda.commands.CommandGroup;
import com.gitlab.taucher2003.t2003_utils.tjda.commands.Permissible;
import com.gitlab.taucher2003.t2003_utils.tjda.commands.SubCommand;

import java.util.Collection;

public interface SubCommandableCommandMetaBuilder {

    SubCommandableCommandMetaBuilder setGroups(Collection<CommandGroup> commandGroups);

    SubCommandableCommandMetaBuilder addGroup(CommandGroup group);

    SubCommandableCommandMetaBuilder setSubCommands(Collection<SubCommand> subCommands);

    SubCommandableCommandMetaBuilder addSubCommand(SubCommand subCommand);

    ArgumentCommandMetaBuilder setPermissible(Permissible permissible);

    SubCommandGroupableMeta build();
}
