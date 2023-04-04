package com.gitlab.taucher2003.t2003_utils.common.policy.definition.conditions;

import com.gitlab.taucher2003.t2003_utils.common.policy.definition.Condition;
import com.gitlab.taucher2003.t2003_utils.common.policy.execution.EvaluationContext;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AndCondition<RESOURCE, CONTEXT, ABILITY> implements Condition<RESOURCE, CONTEXT, ABILITY> {

    private final List<Condition<RESOURCE, CONTEXT, ABILITY>> conditions;

    public AndCondition(List<Condition<RESOURCE, CONTEXT, ABILITY>> conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean test(RESOURCE resource, CONTEXT context, EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext) {
        // we want to evaluate conditions with low score first
        //noinspection RedundantStreamOptionalCall
        return conditions()
                .sorted(Comparator.comparingDouble(condition -> condition.score(evaluationContext)))
                .allMatch(condition -> condition.test(resource, context, evaluationContext));
    }

    @Override
    public Condition<RESOURCE, CONTEXT, ABILITY> simplify() {
        var simplifiedConditions = conditions().flatMap(condition -> {
            var simplified = condition.simplify();
            if (AndCondition.class.equals(simplified.getClass())) {
                return ((AndCondition<RESOURCE, CONTEXT, ABILITY>) simplified).conditions();
            }
            return Stream.of(simplified);
        }).collect(Collectors.toList());

        return new AndCondition<>(simplifiedConditions);
    }

    @Override
    public double score(EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext) {
        return conditions().mapToDouble(condition -> condition.score(evaluationContext)).sum();
    }

    public Stream<Condition<RESOURCE, CONTEXT, ABILITY>> conditions() {
        return conditions.stream();
    }
}
