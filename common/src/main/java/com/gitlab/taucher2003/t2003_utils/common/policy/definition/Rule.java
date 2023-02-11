package com.gitlab.taucher2003.t2003_utils.common.policy.definition;

import java.util.HashSet;
import java.util.Set;

public class Rule<RESOURCE, CONTEXT, ABILITY> {

    protected final PolicyDefinition<RESOURCE, CONTEXT, ABILITY> parent;
    protected final Condition<RESOURCE, CONTEXT, ABILITY> condition;
    protected final Set<RuleAction<ABILITY>> actions = new HashSet<>();
    protected Runnable runner;

    public Rule(PolicyDefinition<RESOURCE, CONTEXT, ABILITY> parent, Condition<RESOURCE, CONTEXT, ABILITY> condition) {
        this.parent = parent;
        this.condition = condition;
    }

    public void policy(Runnable runner) {
        this.runner = runner;
    }

    public void enable(ABILITY ability) {
        actions.add(new RuleAction<>(RuleAction.Action.ENABLE, ability));
    }

    public void prevent(ABILITY ability) {
        actions.add(new RuleAction<>(RuleAction.Action.PREVENT, ability));
    }
}
