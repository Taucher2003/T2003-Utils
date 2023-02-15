package com.gitlab.taucher2003.t2003_utils.common.policy.definition;

import java.util.Objects;

public class RuleAction<ABILITY> {

    protected final Action action;
    protected final ABILITY ability;

    public RuleAction(Action action, ABILITY ability) {
        this.action = action;
        this.ability = ability;
    }

    @Override
    public int hashCode() {
        return Objects.hash(action, ability);
    }

    public enum Action {
        ENABLE,
        PREVENT
    }
}
