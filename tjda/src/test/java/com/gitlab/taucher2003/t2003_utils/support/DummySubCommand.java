package com.gitlab.taucher2003.t2003_utils.support;

import com.gitlab.taucher2003.t2003_utils.tjda.commands.Permissible;
import com.gitlab.taucher2003.t2003_utils.tjda.commands.SubCommand;
import com.gitlab.taucher2003.t2003_utils.tjda.theme.Theme;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.util.concurrent.atomic.AtomicInteger;

public class DummySubCommand extends SubCommand {

    private final AtomicInteger executedTimes = new AtomicInteger();
    private final AtomicInteger autocompleteTimes = new AtomicInteger();

    public DummySubCommand(String name) {
        super(name, "");
    }

    public DummySubCommand(String name, Permissible permissible) {
        super(name, "", permissible);
    }

    @Override
    public void execute(CommandInteraction event, Theme theme, Permissible.PermissibleContext permissibleContext) {
        executedTimes.incrementAndGet();
    }

    @Override
    public void autocomplete(CommandAutoCompleteInteraction event, Permissible.PermissibleContext permissibleContext) {
        autocompleteTimes.incrementAndGet();
    }

    public int executedTimes() {
        return executedTimes.get();
    }

    public int autocompleteTimes() {
        return autocompleteTimes.get();
    }

    public void reset() {
        executedTimes.set(0);
        autocompleteTimes.set(0);
    }
}
