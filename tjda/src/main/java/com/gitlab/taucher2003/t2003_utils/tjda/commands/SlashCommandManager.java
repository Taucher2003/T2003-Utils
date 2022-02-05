package com.gitlab.taucher2003.t2003_utils.tjda.commands;

import com.gitlab.taucher2003.t2003_utils.tjda.theme.Theme;
import com.gitlab.taucher2003.t2003_utils.tjda.theme.ThemeProvider;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
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
        this(shardManager, themeProvider, null);
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
        if(!hook.useSlashCommands(guild)) {
            LOGGER.info("Deleting commands for {}/{} due to {} hook", guild.getName(), guild.getId(), hook.getClass().getCanonicalName());
            guild.updateCommands().queue();
            return;
        }
        LOGGER.debug("Updating commands for {}/{}", guild.getName(), guild.getId());
        guild.updateCommands()
                .addCommands(commands.stream()
                        .map(Command::asJdaObject)
                        .collect(Collectors.toList()))
                .queue(
                        commands -> LOGGER.debug("Updated {} commands for {}/{}", commands.size(), guild.getName(), guild.getId()),
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

    public void dispatch(SlashCommandEvent event) {
        var permissibleContext = new Permissible.PermissibleContext(event.getGuild(), event.getMember(), event.getUser());
        var commandOpt = getCommandByName(event.getName());
        if(commandOpt.isEmpty()) {
            LOGGER.warn("Received interaction for unknown command {}", event.getCommandString());
            return;
        }
        var command = commandOpt.get();
        var permissible = buildPermissible(command.permissible());
        if(permissible.permitted(permissibleContext)) {
            if(event.getSubcommandName() != null) {
                dispatchSubcommand(command, event, permissibleContext);
                return;
            }
            command.execute(event, getTheme(), permissibleContext);
            return;
        }

        command.permissible().handleUnpermitted(event, this);
    }

    private void dispatchSubcommand(Command parent, SlashCommandEvent event, Permissible.PermissibleContext permissibleContext) {
        var subCommandOpt = getSubCommandByName(parent, event.getSubcommandName());
        if(subCommandOpt.isEmpty()) {
            LOGGER.warn("Received interaction for unknown sub-command {}", event.getCommandPath());
            return;
        }
        var subCommand = subCommandOpt.get();
        var subcommandPermissible = buildPermissible(subCommand.permissible());
        if(subcommandPermissible.permitted(permissibleContext)) {
            subCommand.execute(event, getTheme(), permissibleContext);
            return;
        }

        subCommand.permissible().handleUnpermitted(event, this);
    }

    private Permissible buildPermissible(Permissible base) {
        return base.and(hook.getGlobalPermissible()).or(hook.getBypassPermissible());
    }
}
