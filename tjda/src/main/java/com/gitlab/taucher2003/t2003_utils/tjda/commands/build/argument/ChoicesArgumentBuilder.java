package com.gitlab.taucher2003.t2003_utils.tjda.commands.build.argument;

import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.Arrays;
import java.util.List;

public interface ChoicesArgumentBuilder extends BaseArgumentBuilder {

    @Override
    ChoicesArgumentBuilder setRequired(boolean required);

    ChoicesArgumentBuilder setAutoComplete(boolean autoComplete);

    ChoicesArgumentBuilder setChoices(List<Command.Choice> choices);

    default ChoicesArgumentBuilder setChoices(Command.Choice... choices) {
        return setChoices(Arrays.asList(choices));
    }
}
