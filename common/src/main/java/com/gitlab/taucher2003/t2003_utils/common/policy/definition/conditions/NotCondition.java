package com.gitlab.taucher2003.t2003_utils.common.policy.definition.conditions;

import com.gitlab.taucher2003.t2003_utils.common.policy.definition.Condition;
import com.gitlab.taucher2003.t2003_utils.common.policy.execution.EvaluationContext;

import java.util.stream.Collectors;

public class NotCondition<RESOURCE, CONTEXT, ABILITY> implements Condition<RESOURCE, CONTEXT, ABILITY> {

    private final Condition<RESOURCE, CONTEXT, ABILITY> condition;

    public NotCondition(Condition<RESOURCE, CONTEXT, ABILITY> condition) {
        this.condition = condition;
    }

    @Override
    public boolean test(RESOURCE resource, CONTEXT context, EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext) {
        return !condition.test(resource, context, evaluationContext);
    }

    @Override
    public Condition<RESOURCE, CONTEXT, ABILITY> simplify() {
        if (condition instanceof AndCondition) {
            return new OrCondition<>(((AndCondition<RESOURCE, CONTEXT, ABILITY>) condition).conditions().map(Condition::not).collect(Collectors.toList())).simplify();
        }
        if (condition instanceof OrCondition) {
            return new AndCondition<>(((OrCondition<RESOURCE, CONTEXT, ABILITY>) condition).conditions().map(Condition::not).collect(Collectors.toList())).simplify();
        }
        if (condition instanceof NotCondition) {
            return ((NotCondition<RESOURCE, CONTEXT, ABILITY>) condition).condition.simplify();
        }

        return new NotCondition<>(condition.simplify());
    }

    @Override
    public double score(EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext) {
        return condition.score(evaluationContext);
    }
}
