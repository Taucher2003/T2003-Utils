package com.gitlab.taucher2003.t2003_utils.tjda.commands.build.argument;

import com.gitlab.taucher2003.t2003_utils.tjda.commands.CommandArgument;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public interface BaseArgumentBuilder {

    BaseArgumentBuilder setRequired(boolean required);

    CommandArgument build();

    static NumberArgumentBuilder number(String name, String description) {
        return new CommandArgumentBuilder(OptionType.NUMBER, name, description);
    }

    static NumberArgumentBuilder integer(String name, String description) {
        return new CommandArgumentBuilder(OptionType.INTEGER, name, description);
    }

    static StringArgumentBuilder text(String name, String description) {
        return new CommandArgumentBuilder(OptionType.STRING, name, description);
    }
}
