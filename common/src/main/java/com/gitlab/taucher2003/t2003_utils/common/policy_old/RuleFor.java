package com.gitlab.taucher2003.t2003_utils.common.policy_old;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class RuleFor<R, LR extends R, C, A> {

    private final BasePolicy<R, C, A> parent;
    private final Class<LR> type;

    public RuleFor(BasePolicy<R, C, A> parent, Class<LR> type) {
        this.parent = parent;
        this.type = type;
    }

    public Rule<R, C, A> resourceRule(Predicate<LR> resourcePred) {
        return parent.addRule(
                new Rule<>(
                        parent,
                        (r, c) -> type.isInstance(r) && resourcePred.test(type.cast(r))
                ),
                BasePolicy.RuleType.RESOURCE_ONLY
        );
    }

    public Rule<R, C, A> rule(BiPredicate<LR, C> resourceContextPred) {
        return parent.addRule(
                new Rule<>(
                        parent,
                        (r, c) -> type.isInstance(r) && resourceContextPred.test(type.cast(r), c)
                ),
                BasePolicy.RuleType.RESOURCE_AND_CONTEXT
        );
    }
}
