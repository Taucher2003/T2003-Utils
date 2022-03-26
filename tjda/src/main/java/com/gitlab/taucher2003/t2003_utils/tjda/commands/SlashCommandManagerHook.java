package com.gitlab.taucher2003.t2003_utils.tjda.commands;

import com.gitlab.taucher2003.t2003_utils.tjda.theme.Theme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

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

    default void handleUnpermitted(IReplyCallback interaction, Theme theme) {
        var noPermissionEmbed = new EmbedBuilder()
                .setDescription("You don't have permission to execute this command")
                .setColor(theme.danger())
                .build();
        interaction.deferReply(true)
                .flatMap(interactionHook -> interactionHook.editOriginalEmbeds(noPermissionEmbed))
                .queue();
    }
}
