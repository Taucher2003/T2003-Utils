package com.gitlab.taucher2003.t2003_utils.tjda.commands;

import com.gitlab.taucher2003.t2003_utils.tjda.commands.build.meta.SubCommandGroupMetaBuilder;
import com.gitlab.taucher2003.t2003_utils.tjda.commands.build.meta.SubCommandableMeta;
import com.gitlab.taucher2003.t2003_utils.tjda.theme.Theme;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.function.Function;

public class CommandGroup implements Routable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandGroup.class);

    private final SubCommandableMeta<SubcommandGroupData> meta;

    public CommandGroup(SubCommandableMeta<SubcommandGroupData> meta) {
        this.meta = meta;
    }

    public SubcommandGroupData asJdaObject() {
        var data = new SubcommandGroupData(meta.getName(), meta.getDescription());
        meta.getConfigurator().accept(data);
        return data;
    }

    @Override
    public String name() {
        return meta.getName();
    }

    public SubCommandableMeta<SubcommandGroupData> meta() {
        return meta;
    }

    @Override
    public void doExecuteRouting(CommandInteraction event, Theme theme, Permissible.PermissibleContext permissibleContext,
                                 Function<Permissible, Permissible> permissibleCreator, SlashCommandManagerHook hook) {
        var path = event.getCommandPath().split("/");

        meta.getSubCommands()
                .stream()
                .filter(subCommand -> subCommand.name().equals(path[2]))
                .findFirst()
                .ifPresentOrElse(
                        subCommand -> {
                            var innerPermissible = permissibleCreator.apply(subCommand.meta().getPermissible());
                            if (!innerPermissible.permitted(permissibleContext)) {
                                hook.handleUnpermitted(event, theme);
                                return;
                            }

                            subCommand.execute(event, theme, permissibleContext);
                        },
                        () -> LOGGER.warn("Received interaction for unknown sub-command {}", event.getCommandPath())
                );
    }

    @Override
    public void doAutocompleteRouting(CommandAutoCompleteInteraction event, Permissible.PermissibleContext permissibleContext,
                                      Function<Permissible, Permissible> permissibleCreator) {
        var path = event.getCommandPath().split("/");

        meta.getSubCommands()
                .stream()
                .filter(subCommand -> subCommand.name().equals(path[2]))
                .findFirst()
                .ifPresentOrElse(
                        subCommand -> {
                            var innerPermissible = permissibleCreator.apply(subCommand.meta().getPermissible());
                            if (!innerPermissible.permitted(permissibleContext)) {
                                event.replyChoices(Collections.emptyList()).queue();
                                return;
                            }

                            subCommand.autocomplete(event, permissibleContext);
                        },
                        () -> LOGGER.warn("Received autocomplete for unknown sub-command {}", event.getCommandPath())
                );
    }

    public static SubCommandGroupMetaBuilder createMeta(String name, String description) {
        return new SubCommandGroupMetaBuilder(name, description);
    }
}
