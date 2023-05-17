package com.gitlab.taucher2003.t2003_utils.common.policy.definition;

import com.gitlab.taucher2003.t2003_utils.common.data.Triple;
import com.gitlab.taucher2003.t2003_utils.common.policy.definition.conditions.ContextCondition;
import com.gitlab.taucher2003.t2003_utils.common.policy.execution.EvaluationContext;
import com.gitlab.taucher2003.t2003_utils.common.policy.execution.Runner;

import java.util.Optional;
import java.util.function.Function;

public class Delegate<RESOURCE, DELEGATE_RESOURCE, CONTEXT, ABILITY> {

    private final PolicyDefinition<RESOURCE, CONTEXT, ABILITY> policy;
    private final Function<RESOURCE, DELEGATE_RESOURCE> mapper;

    public Delegate(PolicyDefinition<RESOURCE, CONTEXT, ABILITY> policy, Function<RESOURCE, DELEGATE_RESOURCE> mapper) {
        this.policy = policy;
        this.mapper = mapper;
    }

    public void forAbilities(ABILITY... abilities) {
        for (var ability : abilities) {
            policy.rule(
                    new ContextCondition<>(
                            triple -> getState(triple, ability)
                                    .map(Runner.State::isEnabled)
                                    .orElse(false)
                    )
            ).enable(ability);

            policy.rule(
                    new ContextCondition<>(
                            triple -> getState(triple, ability)
                                    .map(Runner.State::isPrevented)
                                    .orElse(false)
                    )
            ).prevent(ability);
        }
    }

    private Optional<Runner.State> getState(Triple<RESOURCE, CONTEXT, EvaluationContext<RESOURCE, CONTEXT, ABILITY>> triple, ABILITY ability) {
        var resource = triple.first();
        var context = triple.second();
        var evaluationContext = triple.third();
        var registry = evaluationContext.getRegistry();
        if (resource == null) {
            return Optional.empty();
        }

        if (registry == null) {
            throw new IllegalStateException("Delegate can't be used in Policy without PolicyRegistry");
        }

        var delegateResource = mapper.apply(resource);
        if (delegateResource == null) {
            return Optional.empty();
        }

        var resourceContext =
                (EvaluationContext<DELEGATE_RESOURCE, CONTEXT, ABILITY>) registry.forContext(context).forResourceClass(delegateResource.getClass());
        if (resourceContext == null) {
            return Optional.empty();
        }

        return Optional.of(resourceContext.runnerForAbility(ability).getState(delegateResource, context, Runner.FLAG_DISABLE_STEP_OPTIMISATION));
    }
}
