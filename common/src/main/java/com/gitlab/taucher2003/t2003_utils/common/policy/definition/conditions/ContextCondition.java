package com.gitlab.taucher2003.t2003_utils.common.policy.definition.conditions;

import com.gitlab.taucher2003.t2003_utils.common.data.Triple;
import com.gitlab.taucher2003.t2003_utils.common.policy.definition.Condition;
import com.gitlab.taucher2003.t2003_utils.common.policy.execution.EvaluationContext;

import java.util.function.Predicate;

public class ContextCondition<RESOURCE, CONTEXT, ABIILITY> implements Condition<RESOURCE, CONTEXT, ABIILITY> {

    private final Predicate<Triple<RESOURCE, CONTEXT, EvaluationContext<RESOURCE, CONTEXT, ABIILITY>>> predicate;

    public ContextCondition(Predicate<Triple<RESOURCE, CONTEXT, EvaluationContext<RESOURCE, CONTEXT, ABIILITY>>> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean test(RESOURCE resource, CONTEXT context, EvaluationContext<RESOURCE, CONTEXT, ABIILITY> evaluationContext) {
        return predicate.test(new Triple<>(resource, context, evaluationContext));
    }

    @Override
    public Condition<RESOURCE, CONTEXT, ABIILITY> simplify() {
        return this;
    }

    @Override
    public double score(EvaluationContext<RESOURCE, CONTEXT, ABIILITY> evaluationContext) {
        return 0;
    }
}
