package com.gitlab.taucher2003.t2003_utils.common.policy.definition.conditions;

import com.gitlab.taucher2003.t2003_utils.common.policy.definition.Condition;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class ForClassConditionBuilder<RESOURCE, LOCAL_RESOURCE extends RESOURCE, CONTEXT, ABILITY> {

    private final Class<LOCAL_RESOURCE> type;

    public ForClassConditionBuilder(Class<LOCAL_RESOURCE> type) {
        this.type = type;
    }

    public Condition<RESOURCE, CONTEXT, ABILITY> resourceCondition(Predicate<LOCAL_RESOURCE> condition) {
        return resourceCondition(condition, Condition.DEFAULT_SCORE);
    }

    public Condition<RESOURCE, CONTEXT, ABILITY> condition(BiPredicate<LOCAL_RESOURCE, CONTEXT> condition) {
        return condition(condition, Condition.DEFAULT_SCORE);
    }

    public Condition<RESOURCE, CONTEXT, ABILITY> resourceCondition(Predicate<LOCAL_RESOURCE> condition, int score) {
        return new SimpleCondition<>(
                (resource, context) -> type.isInstance(resource) && condition.test(type.cast(resource)),
                score,
                true,
                false
        );
    }

    public Condition<RESOURCE, CONTEXT, ABILITY> condition(BiPredicate<LOCAL_RESOURCE, CONTEXT> condition, int score) {
        return new SimpleCondition<>(
                (resource, context) -> type.isInstance(resource) && condition.test(type.cast(resource), context),
                score,
                true,
                true
        );
    }
}
