package com.gitlab.taucher2003.t2003_utils.tjda.commands.build.meta;

import com.gitlab.taucher2003.t2003_utils.tjda.commands.Permissible;

import java.util.function.Consumer;

public class CommandMeta<T> {

    private final String name;
    private final String description;
    private final Permissible permissible;
    private final Consumer<T> configurator;

    public CommandMeta(String name, String description, Permissible permissible, Consumer<T> configurator) {
        this.name = name;
        this.description = description;
        this.permissible = permissible;
        this.configurator = configurator;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Permissible getPermissible() {
        return permissible;
    }

    public Consumer<T> getConfigurator() {
        return configurator;
    }
}
