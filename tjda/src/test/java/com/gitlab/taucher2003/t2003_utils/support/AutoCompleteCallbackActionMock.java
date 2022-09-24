package com.gitlab.taucher2003.t2003_utils.support;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.interactions.AutoCompleteCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.InteractionCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class AutoCompleteCallbackActionMock implements AutoCompleteCallbackAction {

    private final AtomicInteger executeTimes = new AtomicInteger();

    @NotNull
    @Override
    public OptionType getOptionType() {
        return null;
    }

    @NotNull
    @Override
    public AutoCompleteCallbackAction addChoices(@NotNull Collection<Command.Choice> choices) {
        return null;
    }

    @NotNull
    @Override
    public JDA getJDA() {
        return null;
    }

    @Override
    public AutoCompleteCallbackAction setCheck(@Nullable BooleanSupplier checks) {
        return null;
    }

    @Override
    public void queue(@Nullable Consumer<? super Void> success, @Nullable Consumer<? super Throwable> failure) {
        executeTimes.incrementAndGet();
    }

    @Override
    public Void complete(boolean shouldQueue) {
        executeTimes.incrementAndGet();
        return null;
    }

    @NotNull
    @Override
    public CompletableFuture<Void> submit(boolean shouldQueue) {
        executeTimes.incrementAndGet();
        return null;
    }

    public int executeTimes() {
        return executeTimes.get();
    }

    @NotNull
    @Override
    public InteractionCallbackAction<Void> closeResources() {
        return this;
    }
}
