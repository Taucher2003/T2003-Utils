package com.gitlab.taucher2003.t2003_utils.common.policy.definition.conditions;

import com.gitlab.taucher2003.t2003_utils.common.policy.definition.Condition;
import com.gitlab.taucher2003.t2003_utils.common.policy.execution.EvaluationContext;

public class AbilityCondition<RESOURCE, CONTEXT, ABILITY> implements Condition<RESOURCE, CONTEXT, ABILITY> {

    private final ABILITY ability;

    public AbilityCondition(ABILITY ability) {
        this.ability = ability;
    }

    @Override
    public boolean test(RESOURCE resource, CONTEXT context, EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext) {
        return evaluationContext.runnerForAbility(ability).enabled(resource, context);
    }

    @Override
    public Condition<RESOURCE, CONTEXT, ABILITY> simplify() {
        return this;
    }

    @Override
    public double score(EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext) {
        return evaluationContext.runnerForAbility(ability).score();
    }

    public ABILITY ability() {
        return ability;
    }
}
