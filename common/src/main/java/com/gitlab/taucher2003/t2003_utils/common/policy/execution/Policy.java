package com.gitlab.taucher2003.t2003_utils.common.policy.execution;

public class Policy<RESOURCE, CONTEXT, ABILITY> {

    private final AbilityMap<RESOURCE, CONTEXT, ABILITY> abilityMap;
    private PolicyRegistry<CONTEXT, ABILITY> registry;

    public Policy(AbilityMap<RESOURCE, CONTEXT, ABILITY> abilityMap) {
        this.abilityMap = abilityMap;
    }

    public EvaluationContext<RESOURCE, CONTEXT, ABILITY> forContext(CONTEXT context) {
        return new EvaluationContext<>(abilityMap, context, registry);
    }

    void setRegistry(PolicyRegistry<CONTEXT, ABILITY> registry) {
        this.registry = registry;
    }
}
