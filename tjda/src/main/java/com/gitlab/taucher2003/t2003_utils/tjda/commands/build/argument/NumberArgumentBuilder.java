package com.gitlab.taucher2003.t2003_utils.tjda.commands.build.argument;

import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;

public interface NumberArgumentBuilder extends ChoicesArgumentBuilder {

    @Override
    NumberArgumentBuilder setRequired(boolean required);

    @Override
    NumberArgumentBuilder setAutoComplete(boolean autoComplete);

    @Override
    NumberArgumentBuilder setChoices(List<Command.Choice> choices);

    NumberArgumentBuilder setMinValue(Number min);

    NumberArgumentBuilder setMaxValue(Number max);
}
