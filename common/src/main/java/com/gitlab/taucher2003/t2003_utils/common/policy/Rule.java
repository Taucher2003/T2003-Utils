package com.gitlab.taucher2003.t2003_utils.common.policy;

import java.util.function.BiPredicate;

public class Rule<R, C, A> {

    private final BasePolicy<R, C, A> parent;
    private final BiPredicate<R, C> predicate;

    private Runnable executor;

    Rule(BasePolicy<R, C, A> parent, BiPredicate<R, C> predicate) {
        this.parent = parent;
        this.predicate = predicate;
    }

    public void policy(Runnable executor) {
        this.executor = executor;
    }

    @SafeVarargs
    public final void enable(A... ability) {
        executor = () -> parent.enable(ability);
    }

    @SafeVarargs
    public final void disable(A... ability) {
        executor = () -> parent.disable(ability);
    }

    void run(R resource, C context) {
        if (predicate.test(resource, context)) {
            executor.run();
        }
    }
}
