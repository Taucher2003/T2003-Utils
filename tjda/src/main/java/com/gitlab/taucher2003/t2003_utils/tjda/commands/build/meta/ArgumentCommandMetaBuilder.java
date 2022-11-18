package com.gitlab.taucher2003.t2003_utils.tjda.commands.build.meta;

import com.gitlab.taucher2003.t2003_utils.tjda.commands.CommandArgument;
import com.gitlab.taucher2003.t2003_utils.tjda.commands.Permissible;

import java.util.Collection;

public interface ArgumentCommandMetaBuilder {

    ArgumentCommandMetaBuilder setArguments(Collection<CommandArgument> arguments);

    ArgumentCommandMetaBuilder addArgument(CommandArgument argument);

    ArgumentCommandMetaBuilder setPermissible(Permissible permissible);

    SubCommandGroupableMeta build();
}
