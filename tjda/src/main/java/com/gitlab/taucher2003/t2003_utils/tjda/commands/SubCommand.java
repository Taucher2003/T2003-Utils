package com.gitlab.taucher2003.t2003_utils.tjda.commands;

import com.gitlab.taucher2003.t2003_utils.tjda.theme.Theme;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public abstract class SubCommand implements CommandExecutor {

    private final String name;
    private final String description;
    private final Collection<CommandArgument> arguments;
    private final Permissible permissible;

    protected SubCommand(String name, String description) {
        this(name, description, new CommandArgument[0]);
    }

    protected SubCommand(String name, String description, CommandArgument[] arguments) {
        this(name, description, arguments, Command.UNRESTRICTED);
    }

    protected SubCommand(String name, String description, Permissible permissible) {
        this(name, description, new CommandArgument[0], permissible);
    }

    protected SubCommand(String name, String description, CommandArgument[] arguments, Permissible permissible) {
        this.name = name;
        this.description = description;
        this.arguments = Arrays.asList(arguments);
        this.permissible = permissible;
    }

    public SubcommandData asJdaObject() {
        var data = new SubcommandData(name, description);
        if(!arguments.isEmpty()) {
            data.addOptions().addOptions(arguments.stream().map(CommandArgument::asJdaObject).collect(Collectors.toList()));
        }
        return data;
    }

    public String name() {
        return name;
    }

    public Permissible permissible() {
        return permissible;
    }

    @Override
    public abstract void execute(CommandInteraction event, Theme theme, Permissible.PermissibleContext permissibleContext);

    protected OptionMapping findOption(CommandInteractionPayload event, String name) {
        return event.getOption(name);
    }

    protected void replyError(MessageEmbed embed, IReplyCallback interaction) {
        interaction.deferReply(true).flatMap(hook -> hook.editOriginalEmbeds(embed)).queue();
    }
}
