package com.gitlab.taucher2003.t2003_utils.tjda.commands.build.argument;

import com.gitlab.taucher2003.t2003_utils.tjda.commands.CommandArgument;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

public class CommandArgumentBuilder implements NumberArgumentBuilder, StringArgumentBuilder {

    private final OptionType type;
    private final String name;
    private final String description;

    private boolean isRequired;
    private boolean isAutoComplete;
    private EnumSet<ChannelType> channelTypes;
    private Number minValue;
    private Number maxValue;
    private List<Command.Choice> choices;

    public CommandArgumentBuilder(OptionType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    @Override
    public CommandArgumentBuilder setRequired(boolean required) {
        isRequired = required;
        return this;
    }

    @Override
    public CommandArgumentBuilder setAutoComplete(boolean autoComplete) {
        isAutoComplete = autoComplete;
        return this;
    }

    public CommandArgumentBuilder setChannelTypes(Collection<ChannelType> channelTypes) {
        if (this.channelTypes == null) {
            this.channelTypes = EnumSet.noneOf(ChannelType.class);
        }
        this.channelTypes.clear();
        this.channelTypes.addAll(channelTypes);
        return this;
    }

    @Override
    public CommandArgumentBuilder setMinValue(Number minValue) {
        this.minValue = minValue;
        return this;
    }

    @Override
    public CommandArgumentBuilder setMaxValue(Number maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    @Override
    public CommandArgumentBuilder setMinLength(int minLength) {
        return setMinValue(minLength);
    }

    @Override
    public CommandArgumentBuilder setMaxLength(int maxLength) {
        return setMaxValue(maxLength);
    }

    @Override
    public CommandArgumentBuilder setChoices(List<Command.Choice> choices) {
        this.choices = choices;
        return this;
    }

    public void configureOption(OptionData data) {
        data.setName(name);
        data.setDescription(description);

        data.setRequired(isRequired);
        if (type.canSupportChoices()) {
            data.setAutoComplete(isAutoComplete);
        }
        if (type == OptionType.CHANNEL && channelTypes != null) {
            data.setChannelTypes(channelTypes);
        }

        if (type == OptionType.INTEGER || type == OptionType.NUMBER) {
            if (minValue != null) {
                data.setMinValue(minValue.longValue());
            }
            if (maxValue != null) {
                data.setMaxValue(maxValue.longValue());
            }
        }

        if (type == OptionType.STRING) {
            if (minValue != null) {
                data.setMinLength(minValue.intValue());
            }
            if (maxValue != null) {
                data.setMaxLength(maxValue.intValue());
            }
        }

        if (type.canSupportChoices() && !isAutoComplete && choices != null) {
            data.addChoices(choices);
        }
    }

    @Override
    public CommandArgument build() {
        return new CommandArgument(type, this::configureOption);
    }
}
