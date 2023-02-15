package com.gitlab.taucher2003.t2003_utils.common.policy.definition;

import com.gitlab.taucher2003.t2003_utils.common.policy.execution.EvaluationContext;

import java.util.List;

public interface Condition<RESOURCE, CONTEXT, ABILITY> {

    boolean test(RESOURCE resource, CONTEXT context, EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext);

    Condition<RESOURCE, CONTEXT, ABILITY> simplify();

    double score(EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext);

    default Condition<RESOURCE, CONTEXT, ABILITY> and(Condition<RESOURCE, CONTEXT, ABILITY> other) {
        return new Conditions.And<>(List.of(this, other)).simplify();
    }

    default Condition<RESOURCE, CONTEXT, ABILITY> or(Condition<RESOURCE, CONTEXT, ABILITY> other) {
        return new Conditions.Or<>(List.of(this, other)).simplify();
    }

    default Condition<RESOURCE, CONTEXT, ABILITY> not() {
        return new Conditions.Not<>(this).simplify();
    }
}
