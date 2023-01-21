package com.gitlab.taucher2003.t2003_utils.common.policy;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.BiPredicate;

public class Rule<R, C, A> {

    private final Collection<A> seenAbilities = new HashSet<>();
    private final Collection<A> abilityDependencies = new HashSet<>();
    private final BasePolicy<R, C, A> parent;
    private final BiPredicate<R, C> predicate;

    private Runnable executor;

    Rule(BasePolicy<R, C, A> parent, BiPredicate<R, C> predicate) {
        this.parent = parent;
        this.predicate = predicate;
    }

    public void policy(Runnable executor) {
        this.executor = executor;
        this.seenAbilities.clear();
        this.seenAbilities.addAll(parent.trackAbilities(this));
    }

    @SafeVarargs
    public final void enable(A... ability) {
        policy(() -> parent.enable(ability));
    }

    @SafeVarargs
    public final void prevent(A... ability) {
        policy(() -> parent.prevent(ability));
    }

    void run(R resource, C context) {
        if (predicate.test(resource, context)) {
            executor.run();
        }
    }

    void runUnchecked() {
        executor.run();
    }

    Collection<A> getSeenAbilities() {
        return seenAbilities;
    }

    Collection<A> getAbilityDependencies() {
        return abilityDependencies;
    }

    @SafeVarargs
    final Rule<R, C, A> addDependencies(A... ability) {
        abilityDependencies.addAll(Arrays.asList(ability));
        return this;
    }
}
