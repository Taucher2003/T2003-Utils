package com.gitlab.taucher2003.t2003_utils.common.policy.execution;

import com.gitlab.taucher2003.t2003_utils.common.policy.definition.Condition;
import com.gitlab.taucher2003.t2003_utils.common.policy.definition.Conditions;
import com.gitlab.taucher2003.t2003_utils.common.policy.definition.RuleAction;

import java.util.stream.Stream;

public class EvaluationStep<RESOURCE, CONTEXT, ABILITY> {

    private final RuleAction.Action action;
    private final Condition<RESOURCE, CONTEXT, ABILITY> condition;

    public EvaluationStep(RuleAction.Action action, Condition<RESOURCE, CONTEXT, ABILITY> condition) {
        this.action = action;
        this.condition = condition;
    }

    public RuleAction.Action getAction() {
        return action;
    }

    public Condition<RESOURCE, CONTEXT, ABILITY> getCondition() {
        return condition;
    }

    public double score(EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext) {
        if (action == RuleAction.Action.PREVENT) {
            return condition.score(evaluationContext) * (7.0 / 8); // prevent actions can short-circuit, so they are preferred
        }
        return condition.score(evaluationContext);
    }

    public Stream<EvaluationStep<RESOURCE, CONTEXT, ABILITY>> flattened(EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext) {
        if (condition instanceof Conditions.Or) {
            return ((Conditions.Or<RESOURCE, CONTEXT, ABILITY>) condition)
                    .conditions()
                    .flatMap(c -> new EvaluationStep<>(action, c).flattened(evaluationContext));
        }

        if (condition instanceof Conditions.Ability) {
            var ability = ((Conditions.Ability<RESOURCE, CONTEXT, ABILITY>) condition).ability();
            var steps = evaluationContext.runnerForAbility(ability).getSteps();

            if (steps.stream().map(EvaluationStep::getAction).allMatch(RuleAction.Action.ENABLE::equals)) {
                return steps.stream().map(step -> {
                    if (action == RuleAction.Action.PREVENT) {
                        return new EvaluationStep<>(RuleAction.Action.PREVENT, step.condition);
                    }
                    return step;
                }).flatMap(step -> step.flattened(evaluationContext));
            }
        }

        return Stream.of(this);
    }
}
