package com.gitlab.taucher2003.t2003_utils.support;

import com.gitlab.taucher2003.t2003_utils.tjda.commands.SlashCommandManagerHook;
import com.gitlab.taucher2003.t2003_utils.tjda.theme.Theme;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

import java.util.concurrent.atomic.AtomicInteger;

public class SlashCommandManagerHookMock implements SlashCommandManagerHook {

    private final AtomicInteger unpermittedTimes = new AtomicInteger();

    @Override
    public void handleUnpermitted(IReplyCallback interaction, Theme theme) {
        unpermittedTimes.incrementAndGet();
    }

    public int unpermittedTimes() {
        return unpermittedTimes.get();
    }

    public void reset() {
        unpermittedTimes.set(0);
    }
}
