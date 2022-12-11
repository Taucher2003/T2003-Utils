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
import java.util.Optional;
import java.util.stream.Collectors;

public class SlashCommandManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlashCommandManager.class);

    private final ThemeProvider themeProvider;
    private final SlashCommandManagerHook hook;
    private final Collection<Command> commands = new ArrayList<>();

    public SlashCommandManager(Theme theme) {
        this(() -> theme);
    }

    public SlashCommandManager(ThemeProvider themeProvider) {
        //noinspection AnonymousInnerClass
        this(themeProvider, new SlashCommandManagerHook() {});
    }

    public SlashCommandManager(ThemeProvider themeProvider, SlashCommandManagerHook hook) {
        this.themeProvider = themeProvider;
        this.hook = hook;
    }

    public void updateCommands(ShardManager shardManager) {
        shardManager.getGuildCache().forEach(this::updateCommands);
    }

    public void upsertCommands(ShardManager shardManager) {
        shardManager.getGuildCache().forEach(this::upsertCommands);
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

    public void upsertCommands(Guild guild) {
        if (!hook.useSlashCommands(guild)) {
            LOGGER.info("Commands for {}/{} are disabled with {} hook, skipping upsert", guild.getName(), guild.getId(), hook.getClass().getCanonicalName());
            return;
        }

        LOGGER.info("Upserting commands for {}/{}", guild.getName(), guild.getId());
        commands.stream()
                .map(command -> command.asJdaObject(hook.getLocalizationFunction()))
                .map(guild::upsertCommand)
                .forEach(action -> action.queue(
                                ignored -> LOGGER.info("Upserted command {} for {}/{}", ignored.getName(), guild.getName(), guild.getId()),
                                throwable -> LOGGER.error("Failed to upsert command for {}/{}", guild.getName(), guild.getId(), throwable)
                        )
                );
    }

    public Optional<Command> getCommandByName(String name) {
        return commands.stream().filter(command -> command.name().equalsIgnoreCase(name)).findFirst();
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
            LOGGER.warn("Received interaction for unknown command '{}'", event.getFullCommandName());
            return;
        }
        var command = commandOpt.get();
        command.doExecuteRouting(event, getTheme(), permissibleContext, this::buildPermissible, hook);
    }

    public void autocomplete(CommandAutoCompleteInteraction event) {
        var permissibleContext = new Permissible.PermissibleContext(event.getGuild(), event.getMember(), event.getUser());
        var commandOpt = getCommandByName(event.getName());
        if (commandOpt.isEmpty()) {
            LOGGER.warn("Received autocomplete for unknown command '{}'", event.getFullCommandName());
            return;
        }
        var command = commandOpt.get();
        command.doAutocompleteRouting(event, permissibleContext, this::buildPermissible);
    }

    private Permissible buildPermissible(Permissible base) {
        return base.and(hook.getGlobalPermissible()).or(hook.getBypassPermissible());
    }
}
