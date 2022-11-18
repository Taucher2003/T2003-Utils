package com.gitlab.taucher2003.t2003_utils.tjda.commands.build.argument;

import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;

public interface StringArgumentBuilder extends ChoicesArgumentBuilder {

    @Override
    StringArgumentBuilder setRequired(boolean required);

    @Override
    StringArgumentBuilder setAutoComplete(boolean autoComplete);

    @Override
    StringArgumentBuilder setChoices(List<Command.Choice> choices);

    StringArgumentBuilder setMinLength(int min);

    StringArgumentBuilder setMaxLength(int max);
}
