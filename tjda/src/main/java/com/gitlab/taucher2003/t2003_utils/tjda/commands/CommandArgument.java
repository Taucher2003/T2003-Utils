package com.gitlab.taucher2003.t2003_utils.tjda.commands;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandArgument {

    private final OptionType type;
    private final String name;
    private final String description;
    private final boolean required;
    private final boolean autocomplete;

    public CommandArgument(OptionType type, String name, String description) {
        this(type, name, description, false);
    }

    public CommandArgument(OptionType type, String name, String description, boolean required) {
        this(type, name, description, required, false);
    }

    public CommandArgument(OptionType type, String name, String description, boolean required, boolean autocomplete) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.required = required;
        this.autocomplete = autocomplete;
    }

    public OptionData asJdaObject() {
        return new OptionData(type, name, description, required, autocomplete);
    }
}
