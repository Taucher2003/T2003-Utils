package com.gitlab.taucher2003.t2003_utils.common.policy.definition;

import com.gitlab.taucher2003.t2003_utils.common.policy.definition.conditions.AbilityCondition;
import com.gitlab.taucher2003.t2003_utils.common.policy.definition.conditions.ForClassConditionBuilder;
import com.gitlab.taucher2003.t2003_utils.common.policy.definition.conditions.SimpleCondition;
import com.gitlab.taucher2003.t2003_utils.common.policy.execution.AbilityMap;
import com.gitlab.taucher2003.t2003_utils.common.policy.execution.EvaluationStep;
import com.gitlab.taucher2003.t2003_utils.common.policy.execution.Policy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public abstract class PolicyDefinition<RESOURCE, CONTEXT, ABILITY> {

    private final Collection<Rule<RESOURCE, CONTEXT, ABILITY>> rules = new ArrayList<>();
    private final ThreadLocal<Rule<RESOURCE, CONTEXT, ABILITY>> currentRule = new ThreadLocal<>();

    protected abstract void definePolicies();

    protected Condition<RESOURCE, CONTEXT, ABILITY> resourceCondition(Predicate<RESOURCE> condition) {
        return resourceCondition(condition, Condition.DEFAULT_SCORE);
    }

    protected Condition<RESOURCE, CONTEXT, ABILITY> contextCondition(Predicate<CONTEXT> condition) {
        return contextCondition(condition, Condition.DEFAULT_SCORE);
    }

    protected Condition<RESOURCE, CONTEXT, ABILITY> condition(BiPredicate<RESOURCE, CONTEXT> condition) {
        return condition(condition, Condition.DEFAULT_SCORE);
    }

    protected Condition<RESOURCE, CONTEXT, ABILITY> resourceCondition(Predicate<RESOURCE> condition, int score) {
        return new SimpleCondition<>((resource, context) -> condition.test(resource), score, true, false);
    }

    protected Condition<RESOURCE, CONTEXT, ABILITY> contextCondition(Predicate<CONTEXT> condition, int score) {
        return new SimpleCondition<>(((resource, context) -> condition.test(context)), score, false, true);
    }

    protected Condition<RESOURCE, CONTEXT, ABILITY> condition(BiPredicate<RESOURCE, CONTEXT> condition, int score) {
        return new SimpleCondition<>(condition, score, true, true);
    }

    protected <LOCAL_RESOURCE extends RESOURCE> ForClassConditionBuilder<RESOURCE, LOCAL_RESOURCE, CONTEXT, ABILITY> forClass(Class<LOCAL_RESOURCE> type) {
        return new ForClassConditionBuilder<>(type);
    }

    protected Condition<RESOURCE, CONTEXT, ABILITY> ifEnabled(ABILITY ability) {
        return new AbilityCondition<>(ability);
    }

    @SafeVarargs
    protected final Condition<RESOURCE, CONTEXT, ABILITY> ifAnyEnabled(ABILITY ability, ABILITY... additionalAbilities) {
        var condition = ifEnabled(ability);
        return Arrays.stream(additionalAbilities)
                .reduce(condition, (cond, innerAbility) -> cond.or(ifEnabled(innerAbility)), Condition::or);
    }

    @SafeVarargs
    protected final Condition<RESOURCE, CONTEXT, ABILITY> ifAllEnabled(ABILITY ability, ABILITY... additionalAbilities) {
        var condition = ifEnabled(ability);
        return Arrays.stream(additionalAbilities)
                .reduce(condition, (cond, innerAbility) -> cond.and(ifEnabled(innerAbility)), Condition::and);
    }

    protected Rule<RESOURCE, CONTEXT, ABILITY> rule(Condition<RESOURCE, CONTEXT, ABILITY> condition) {
        var rule = new Rule<>(this, condition);
        rules.add(rule);
        return rule;
    }

    protected void enable(ABILITY ability) {
        currentRule.get().actions.add(new RuleAction<>(RuleAction.Action.ENABLE, ability));
    }

    protected void prevent(ABILITY ability) {
        currentRule.get().actions.add(new RuleAction<>(RuleAction.Action.PREVENT, ability));
    }

    public Policy<RESOURCE, CONTEXT, ABILITY> initialize() {
        definePolicies();
        var abilityMap = new AbilityMap<RESOURCE, CONTEXT, ABILITY>();
        for (var rule : rules) {
            currentRule.set(rule);
            if (rule.runner != null) {
                rule.runner.run();
            }
            currentRule.remove();
            buildAbilityMap(abilityMap, rule);
        }
        return new Policy<>(abilityMap);
    }

    private void buildAbilityMap(AbilityMap<RESOURCE, CONTEXT, ABILITY> abilityMap, Rule<RESOURCE, CONTEXT, ABILITY> rule) {
        for (var action : rule.actions) {
            abilityMap.addStep(action.ability, new EvaluationStep<>(action.action, rule.condition));
        }
    }
}
