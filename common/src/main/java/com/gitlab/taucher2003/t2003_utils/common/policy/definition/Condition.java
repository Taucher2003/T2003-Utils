package com.gitlab.taucher2003.t2003_utils.common.policy.definition;

import com.gitlab.taucher2003.t2003_utils.common.policy.definition.conditions.AndCondition;
import com.gitlab.taucher2003.t2003_utils.common.policy.definition.conditions.NotCondition;
import com.gitlab.taucher2003.t2003_utils.common.policy.definition.conditions.OrCondition;
import com.gitlab.taucher2003.t2003_utils.common.policy.execution.EvaluationContext;

import java.util.List;

public interface Condition<RESOURCE, CONTEXT, ABILITY> {

    int DEFAULT_SCORE = 4;

    boolean test(RESOURCE resource, CONTEXT context, EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext);

    Condition<RESOURCE, CONTEXT, ABILITY> simplify();

    double score(EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext);

    default Condition<RESOURCE, CONTEXT, ABILITY> and(Condition<RESOURCE, CONTEXT, ABILITY> other) {
        return new AndCondition<>(List.of(this, other)).simplify();
    }

    default Condition<RESOURCE, CONTEXT, ABILITY> or(Condition<RESOURCE, CONTEXT, ABILITY> other) {
        return new OrCondition<>(List.of(this, other)).simplify();
    }

    default Condition<RESOURCE, CONTEXT, ABILITY> not() {
        return new NotCondition<>(this).simplify();
    }
}
