package com.gitlab.taucher2003.t2003_utils.common.policy.definition.conditions;

import com.gitlab.taucher2003.t2003_utils.common.policy.definition.Condition;
import com.gitlab.taucher2003.t2003_utils.common.policy.execution.EvaluationContext;

import java.util.function.BiPredicate;

public class SimpleCondition<RESOURCE, CONTEXT, ABILITY> implements Condition<RESOURCE, CONTEXT, ABILITY> {

    private final BiPredicate<RESOURCE, CONTEXT> delegate;
    private final int score;
    private final boolean usesResource;
    private final boolean usesContext;

    public SimpleCondition(BiPredicate<RESOURCE, CONTEXT> delegate, int score, boolean usesResource, boolean usesContext) {
        this.delegate = delegate;
        this.score = score;
        this.usesResource = usesResource;
        this.usesContext = usesContext;
    }

    @Override
    public boolean test(RESOURCE resource, CONTEXT context, EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext) {
        if (resource == null && usesResource) {
            return false;
        }
        if (context == null && usesContext) {
            return false;
        }

        return delegate.test(resource, context);
    }

    @Override
    public Condition<RESOURCE, CONTEXT, ABILITY> simplify() {
        return this;
    }

    @Override
    public double score(EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext) {
        return score;
    }
}
