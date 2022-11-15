package com.gitlab.taucher2003.t2003_utils.tjda.commands;

import com.gitlab.taucher2003.t2003_utils.tjda.commands.build.argument.CommandArgumentBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.function.Consumer;

public class CommandArgument {

    private final OptionType type;
    private final Consumer<OptionData> configurator;

    public CommandArgument(OptionType type, Consumer<OptionData> configurator) {
        this.type = type;
        this.configurator = configurator;
    }

    @Deprecated
    public CommandArgument(OptionType type, String name, String description) {
        this(type, name, description, false);
    }

    @Deprecated
    public CommandArgument(OptionType type, String name, String description, boolean required) {
        this(type, name, description, required, false);
    }

    @Deprecated
    public CommandArgument(OptionType type, String name, String description, boolean required, boolean autocomplete) {
        this.type = type;
        this.configurator = new CommandArgumentBuilder(type, name, description)
                .setRequired(required)
                .setAutoComplete(autocomplete)
                ::configureOption;
    }

    public OptionData asJdaObject() {
        var data = new OptionData(type, "dummy", "dummy");
        configurator.accept(data);
        return data;
    }
}
