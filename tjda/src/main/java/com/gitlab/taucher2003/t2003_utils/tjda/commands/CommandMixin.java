package com.gitlab.taucher2003.t2003_utils.tjda.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Optional;

public interface CommandMixin {

    default OptionMapping findOption(CommandInteractionPayload event, String name) {
        return event.getOption(name);
    }

    default Optional<OptionMapping> findOptionOpt(CommandInteractionPayload event, String name) {
        return Optional.ofNullable(event.getOption(name));
    }

    default void replyError(MessageEmbed embed, IReplyCallback interaction) {
        interaction.deferReply(true).flatMap(hook -> hook.editOriginalEmbeds(embed)).queue();
    }
}
