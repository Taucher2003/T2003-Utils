package com.gitlab.taucher2003.t2003_utils.tjda.commands;

import com.gitlab.taucher2003.t2003_utils.tjda.commands.build.meta.CommandMetaBuilder;
import com.gitlab.taucher2003.t2003_utils.tjda.commands.build.meta.SubCommandGroupableMeta;
import com.gitlab.taucher2003.t2003_utils.tjda.theme.Theme;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;

import static com.gitlab.taucher2003.t2003_utils.tjda.commands.Permissible.UNRESTRICTED;

public abstract class Command implements Routable, CommandMixin {

    private static final Logger LOGGER = LoggerFactory.getLogger(Command.class);

    private final SubCommandGroupableMeta meta;

    @Deprecated
    protected Command(String name, String description) {
        this(name, description, new SubCommand[0], new CommandArgument[0], UNRESTRICTED);
    }

    @Deprecated
    protected Command(String name, String description, Permissible permissible) {
        this(name, description, new SubCommand[0], new CommandArgument[0], permissible);
    }

    @Deprecated
    protected Command(String name, String description, CommandArgument[] arguments) {
        this(name, description, arguments, UNRESTRICTED);
    }

    @Deprecated
    protected Command(String name, String description, CommandArgument[] arguments, Permissible permissible) {
        this(name, description, new SubCommand[0], arguments, permissible);
    }

    @Deprecated
    protected Command(String name, String description, SubCommand[] subCommands) {
        this(name, description, subCommands, UNRESTRICTED);
    }

    @Deprecated
    protected Command(String name, String description, SubCommand[] subCommands, Permissible permissible) {
        this(name, description, subCommands, new CommandArgument[0], permissible);
    }

    private Command(String name, String description, SubCommand[] subCommands, CommandArgument[] arguments, Permissible permissible) {
        this(
                createMeta(name, description)
                        .setSubCommands(Arrays.asList(subCommands))
                        .setArguments(Arrays.asList(arguments))
                        .setPermissible(permissible)
                        .build()
        );
    }

    protected Command(SubCommandGroupableMeta meta) {
        this.meta = meta;
    }

    public CommandData asJdaObject(LocalizationFunction localizationFunction) {
        var data = Commands.slash(meta.getName(), meta.getDescription());
        meta.getConfigurator().accept(data);
        if (localizationFunction != null) {
            data.setLocalizationFunction(localizationFunction);
        }
        return data;
    }

    @Override
    public String name() {
        return meta.getName();
    }

    public SubCommandGroupableMeta meta() {
        return meta;
    }

    public abstract void execute(CommandInteraction event, Theme theme, Permissible.PermissibleContext permissibleContext);

    // can be overridden
    public void autocomplete(CommandAutoCompleteInteraction event, Permissible.PermissibleContext permissibleContext) {
    }

    @Override
    public void doExecuteRouting(CommandInteraction event, Theme theme, Permissible.PermissibleContext permissibleContext,
                                 Function<Permissible, Permissible> permissibleCreator, SlashCommandManagerHook hook) {
        var permissible = permissibleCreator.apply(meta.getPermissible());
        if (!permissible.permitted(permissibleContext)) {
            hook.handleUnpermitted(event, theme);
            return;
        }

        var path = event.getCommandPath().split("/");

        if (path.length == 1) {
            execute(event, theme, permissibleContext);
            return;
        }

        if (path.length == 2) {
            meta.getSubCommands()
                    .stream()
                    .filter(command -> command.name().equals(path[1]))
                    .findFirst()
                    .ifPresentOrElse(
                            command -> {
                                var innerPermissible = permissibleCreator.apply(command.meta().getPermissible());
                                if (!innerPermissible.permitted(permissibleContext)) {
                                    hook.handleUnpermitted(event, theme);
                                    return;
                                }

                                command.execute(event, theme, permissibleContext);
                            },
                            () -> LOGGER.warn("Received interaction for unknown sub-command {}", event.getCommandPath())
                    );
            return;
        }

        meta.getCommandGroups()
                .stream()
                .filter(group -> group.name().equals(path[1]))
                .findFirst()
                .ifPresentOrElse(
                        group -> {
                            var innerPermissible = permissibleCreator.apply(group.meta().getPermissible());
                            if (!innerPermissible.permitted(permissibleContext)) {
                                hook.handleUnpermitted(event, theme);
                                return;
                            }

                            group.doExecuteRouting(event, theme, permissibleContext, permissibleCreator, hook);
                        },
                        () -> LOGGER.warn("Received interaction for unknown sub-group {}", event.getCommandPath())
                );
    }

    @Override
    public void doAutocompleteRouting(CommandAutoCompleteInteraction event, Permissible.PermissibleContext permissibleContext,
                                      Function<Permissible, Permissible> permissibleCreator) {
        var permissible = permissibleCreator.apply(meta.getPermissible());
        if (!permissible.permitted(permissibleContext)) {
            event.replyChoices(Collections.emptyList()).queue();
            return;
        }

        var path = event.getCommandPath().split("/");

        if (path.length == 1) {
            autocomplete(event, permissibleContext);
            return;
        }

        if (path.length == 2) {
            meta.getSubCommands()
                    .stream()
                    .filter(command -> command.name().equals(path[1]))
                    .findFirst()
                    .ifPresentOrElse(
                            command -> {
                                var innerPermissible = permissibleCreator.apply(command.meta().getPermissible());
                                if (!innerPermissible.permitted(permissibleContext)) {
                                    event.replyChoices(Collections.emptyList()).queue();
                                    return;
                                }

                                command.autocomplete(event, permissibleContext);
                            },
                            () -> LOGGER.warn("Received autocomplete for unknown sub-command {}", event.getCommandPath())
                    );
            return;
        }

        meta.getCommandGroups()
                .stream()
                .filter(group -> group.name().equals(path[1]))
                .findFirst()
                .ifPresentOrElse(
                        group -> {
                            var innerPermissible = permissibleCreator.apply(group.meta().getPermissible());
                            if (!innerPermissible.permitted(permissibleContext)) {
                                event.replyChoices(Collections.emptyList()).queue();
                                return;
                            }

                            group.doAutocompleteRouting(event, permissibleContext, permissibleCreator);
                        },
                        () -> LOGGER.warn("Received autocomplete for unknown sub-group {}", event.getCommandPath())
                );
    }

    public static CommandMetaBuilder createMeta(String name, String description) {
        return new CommandMetaBuilder(name, description);
    }
}
