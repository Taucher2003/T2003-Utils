package com.gitlab.taucher2003.t2003_utils.tjda.commands;

import com.gitlab.taucher2003.t2003_utils.tjda.theme.Theme;
import com.gitlab.taucher2003.t2003_utils.tjda.theme.ThemeProvider;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class SlashCommandManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlashCommandManager.class);

    private final ShardManager shardManager;
    private final ThemeProvider themeProvider;
    private final SlashCommandManagerHook hook;
    private final Collection<Command> commands = new ArrayList<>();

    public SlashCommandManager(ShardManager shardManager, Theme theme) {
        this(shardManager, () -> theme);
    }

    public SlashCommandManager(ShardManager shardManager, ThemeProvider themeProvider) {
        //noinspection AnonymousInnerClass
        this(shardManager, themeProvider, new SlashCommandManagerHook() {});
    }

    public SlashCommandManager(ShardManager shardManager, ThemeProvider themeProvider, SlashCommandManagerHook hook) {
        this.shardManager = shardManager;
        this.themeProvider = themeProvider;
        this.hook = hook;
    }

    public void updateCommands() {
        shardManager.getGuildCache().forEach(this::updateCommands);
    }

    public void updateCommands(Guild guild) {
        if (!hook.useSlashCommands(guild)) {
            LOGGER.info("Deleting commands for {}/{} due to {} hook", guild.getName(), guild.getId(), hook.getClass().getCanonicalName());
            guild.updateCommands().queue();
            return;
        }
        LOGGER.info("Updating commands for {}/{}", guild.getName(), guild.getId());
        guild.updateCommands()
                .addCommands(commands.stream()
                        .map(command -> command.asJdaObject(hook.getLocalizationFunction()))
                        .collect(Collectors.toList()))
                .queue(
                        commandList -> LOGGER.info("Updated {} commands for {}/{}", commandList.size(), guild.getName(), guild.getId()),
                        throwable -> LOGGER.error("Failed to update commands for {}/{}", guild.getName(), guild.getId(), throwable)
                );
    }

    public Optional<Command> getCommandByName(String name) {
        return commands.stream().filter(command -> command.name().equalsIgnoreCase(name)).findFirst();
    }

    public Optional<SubCommand> getSubCommandByName(Command parent, String name) {
        return parent.subCommands().stream().filter(subCommand -> subCommand.name().equalsIgnoreCase(name)).findFirst();
    }

    public Theme getTheme() {
        return themeProvider.get();
    }

    public ThemeProvider getThemeProvider() {
        return themeProvider;
    }

    public boolean registerCommand(Command command) {
        return commands.add(command);
    }

    public boolean unregisterCommand(Command command) {
        return commands.remove(command);
    }

    public void unregisterAllCommands() {
        commands.clear();
    }

    public void dispatch(CommandInteraction event) {
        var permissibleContext = new Permissible.PermissibleContext(event.getGuild(), event.getMember(), event.getUser());
        var commandOpt = getCommandByName(event.getName());
        if (commandOpt.isEmpty()) {
            LOGGER.warn("Received interaction for unknown command {}", event.getCommandPath());
            return;
        }
        var command = commandOpt.get();
        var permissible = buildPermissible(command.permissible());
        if (permissible.permitted(permissibleContext)) {
            if (event.getSubcommandName() != null) {
                dispatchSubcommand(command, event, permissibleContext);
                return;
            }
            command.execute(event, getTheme(), permissibleContext);
            return;
        }

        hook.handleUnpermitted(event, getTheme());
    }

    private void dispatchSubcommand(Command parent, CommandInteraction event, Permissible.PermissibleContext permissibleContext) {
        var subCommandOpt = getSubCommandByName(parent, event.getSubcommandName());
        if (subCommandOpt.isEmpty()) {
            LOGGER.warn("Received interaction for unknown sub-command {}", event.getCommandPath());
            return;
        }
        var subCommand = subCommandOpt.get();
        var subcommandPermissible = buildPermissible(subCommand.permissible());
        if (subcommandPermissible.permitted(permissibleContext)) {
            subCommand.execute(event, getTheme(), permissibleContext);
            return;
        }

        hook.handleUnpermitted(event, getTheme());
    }

    public void autocomplete(CommandAutoCompleteInteraction event) {
        var permissibleContext = new Permissible.PermissibleContext(event.getGuild(), event.getMember(), event.getUser());
        var commandOpt = getCommandByName(event.getName());
        if (commandOpt.isEmpty()) {
            LOGGER.warn("Received autocomplete for unknown command {}", event.getCommandPath());
            return;
        }
        var command = commandOpt.get();
        var permissible = buildPermissible(command.permissible());
        if (permissible.permitted(permissibleContext)) {
            if (event.getSubcommandName() != null) {
                autocompleteSubcommand(command, event, permissibleContext);
                return;
            }
            command.autocomplete(event, permissibleContext);
            return;
        }
        event.replyChoices(Collections.emptyList()).queue();
    }

    private void autocompleteSubcommand(Command parent, CommandAutoCompleteInteraction event, Permissible.PermissibleContext permissibleContext) {
        var subCommandOpt = getSubCommandByName(parent, event.getSubcommandName());
        if (subCommandOpt.isEmpty()) {
            LOGGER.warn("Received autocomplete for unknown sub-command {}", event.getCommandPath());
            return;
        }
        var subCommand = subCommandOpt.get();
        var subcommandPermissible = buildPermissible(subCommand.permissible());
        if (subcommandPermissible.permitted(permissibleContext)) {
            subCommand.autocomplete(event, permissibleContext);
            return;
        }

        event.replyChoices(Collections.emptyList()).queue();
    }

    private Permissible buildPermissible(Permissible base) {
        return base.and(hook.getGlobalPermissible()).or(hook.getBypassPermissible());
    }
}
