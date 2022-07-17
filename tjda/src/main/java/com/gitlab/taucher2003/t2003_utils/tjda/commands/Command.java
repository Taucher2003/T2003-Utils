package com.gitlab.taucher2003.t2003_utils.tjda.commands;

import com.gitlab.taucher2003.t2003_utils.tjda.theme.Theme;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class Command {

    protected static final Permissible UNRESTRICTED = (context) -> true;

    @SuppressWarnings("AnonymousInnerClass")
    protected static final Permissible ADMINISTRATOR_ONLY = new Permissible() {
        @Override
        public List<Permission> defaultMemberPermissions() {
            return List.of(Permission.ADMINISTRATOR);
        }

        @Override
        public boolean permitted(PermissibleContext context) {
            return context.getMember()
                    .map(m -> m.hasPermission(Permission.ADMINISTRATOR))
                    .orElse(false);
        }
    };

    private final String name;
    private final String description;
    private final Collection<SubCommand> subCommands;
    private final Collection<CommandArgument> arguments;
    private final Permissible permissible;

    protected Command(String name, String description) {
        this(name, description, new SubCommand[0], new CommandArgument[0], UNRESTRICTED);
    }

    protected Command(String name, String description, Permissible permissible) {
        this(name, description, new SubCommand[0], new CommandArgument[0], permissible);
    }

    protected Command(String name, String description, CommandArgument[] arguments) {
        this(name, description, arguments, UNRESTRICTED);
    }

    protected Command(String name, String description, CommandArgument[] arguments, Permissible permissible) {
        this(name, description, new SubCommand[0], arguments, permissible);
    }

    protected Command(String name, String description, SubCommand[] subCommands) {
        this(name, description, subCommands, UNRESTRICTED);
    }

    protected Command(String name, String description, SubCommand[] subCommands, Permissible permissible) {
        this(name, description, subCommands, new CommandArgument[0], permissible);
    }

    private Command(String name, String description, SubCommand[] subCommands, CommandArgument[] arguments, Permissible permissible) {
        this.name = name;
        this.description = description;
        this.subCommands = Arrays.asList(subCommands);
        this.arguments = Arrays.asList(arguments);
        this.permissible = permissible;
    }

    public CommandData asJdaObject(LocalizationFunction localizationFunction) {
        var data = Commands.slash(name, description);
        if (!subCommands.isEmpty()) {
            data.addSubcommands(subCommands.stream().map(SubCommand::asJdaObject).collect(Collectors.toList()));
        }
        if (!arguments.isEmpty()) {
            data.addOptions(arguments.stream().map(CommandArgument::asJdaObject).collect(Collectors.toList()));
        }
        if (localizationFunction != null) {
            data.setLocalizationFunction(localizationFunction);
        }
        data.setDefaultPermissions(DefaultMemberPermissions.enabledFor(permissible.defaultMemberPermissions()));
        return data;
    }

    public String name() {
        return name;
    }

    public Permissible permissible() {
        return permissible;
    }

    public Collection<SubCommand> subCommands() {
        return subCommands;
    }

    public abstract void execute(CommandInteraction event, Theme theme, Permissible.PermissibleContext permissibleContext);

    // can be overridden
    public void autocomplete(CommandAutoCompleteInteraction event, Permissible.PermissibleContext permissibleContext) {
    }

    protected OptionMapping findOption(CommandInteractionPayload event, String name) {
        return event.getOption(name);
    }

    protected Optional<OptionMapping> findOptionOpt(CommandInteractionPayload event, String name) {
        return Optional.ofNullable(event.getOption(name));
    }

    protected void replyError(MessageEmbed embed, IReplyCallback interaction) {
        interaction.deferReply(true).flatMap(hook -> hook.editOriginalEmbeds(embed)).queue();
    }
}
