package com.gitlab.taucher2003.t2003_utils.tjda.i18n;

import com.gitlab.taucher2003.t2003_utils.common.i18n.Localizer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;

public interface DiscordLocalizer extends Localizer {

    String localize(String key, Guild guild);

    default String localize(String key, GuildMessageChannel channel) {
        return localize(key, channel.getGuild());
    }

    default String localize(String key, Interaction interaction) {
        if(interaction.isFromGuild()) {
            return localize(key, interaction.getGuild());
        }
        return localize(key);
    }

    default String localize(String key, MessageReceivedEvent event) {
        if(event.isFromGuild()) {
            return localize(key, event.getGuild());
        }
        return localize(key);
    }

    default String localize(String key, GuildChannel channel) {
        return localize(key, channel.getGuild());
    }
}
