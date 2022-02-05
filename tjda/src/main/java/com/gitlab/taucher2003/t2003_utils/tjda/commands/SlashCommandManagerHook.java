package com.gitlab.taucher2003.t2003_utils.tjda.commands;

import net.dv8tion.jda.api.entities.Guild;

public interface SlashCommandManagerHook {

    default boolean useSlashCommands(Guild guild) {
        return true;
    }

    default Permissible getBypassPermissible() {
        return (context) -> false;
    }

    default Permissible getGlobalPermissible() {
        return (context) -> true;
    }
}
