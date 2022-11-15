package com.gitlab.taucher2003.t2003_utils.tjda.commands;

import com.gitlab.taucher2003.t2003_utils.tjda.commands.build.meta.CommandMeta;
import com.gitlab.taucher2003.t2003_utils.tjda.commands.build.meta.SubCommandMetaBuilder;
import com.gitlab.taucher2003.t2003_utils.tjda.theme.Theme;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Arrays;

public abstract class SubCommand implements CommandMixin {

    private final CommandMeta<SubcommandData> meta;

    @Deprecated
    protected SubCommand(String name, String description) {
        this(name, description, new CommandArgument[0]);
    }

    @Deprecated
    protected SubCommand(String name, String description, CommandArgument[] arguments) {
        this(name, description, arguments, Command.UNRESTRICTED);
    }

    @Deprecated
    protected SubCommand(String name, String description, Permissible permissible) {
        this(name, description, new CommandArgument[0], permissible);
    }

    @Deprecated
    protected SubCommand(String name, String description, CommandArgument[] arguments, Permissible permissible) {
        this(
                new SubCommandMetaBuilder(name, description)
                        .setArguments(Arrays.asList(arguments))
                        .setPermissible(permissible)
                        .build()
        );
    }

    protected SubCommand(CommandMeta<SubcommandData> meta) {
        this.meta = meta;
    }

    public SubcommandData asJdaObject() {
        var data = new SubcommandData(meta.getName(), meta.getDescription());
        meta.getConfigurator().accept(data);
        return data;
    }

    public String name() {
        return meta.getName();
    }

    public CommandMeta<SubcommandData> meta() {
        return meta;
    }

    public abstract void execute(CommandInteraction event, Theme theme, Permissible.PermissibleContext permissibleContext);

    // can be overridden
    public void autocomplete(CommandAutoCompleteInteraction event, Permissible.PermissibleContext permissibleContext) {
    }
}
