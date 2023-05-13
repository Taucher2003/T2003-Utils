package com.gitlab.taucher2003.t2003_utils.common.policy.execution;

import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EvaluationContext<RESOURCE, CONTEXT, ABILITY> {

    private final Map<ABILITY, Runner<RESOURCE, CONTEXT, ABILITY>> runners = new HashMap<>();

    private final AbilityMap<RESOURCE, CONTEXT, ABILITY> abilityMap;
    private final CONTEXT context;
    private final PolicyRegistry<CONTEXT, ABILITY> registry;

    public EvaluationContext(AbilityMap<RESOURCE, CONTEXT, ABILITY> abilityMap, CONTEXT context, PolicyRegistry<CONTEXT, ABILITY> registry) {
        this.abilityMap = abilityMap;
        this.context = context;
        this.registry = registry;
    }

    public boolean can(RESOURCE resource, ABILITY ability) {
        return runnerForAbility(ability).enabled(resource, context);
    }

    public Set<ABILITY> getEnabledAbilities(RESOURCE resource) {
        return abilityMap.getContainedAbilities()
                .stream()
                .reduce(new HashSet<>(), (a, b) -> {
                    if (runnerForAbility(b).enabled(resource, context)) {
                        a.add(b);
                    }
                    return a;
                }, (a, b) -> {
                    a.addAll(b);
                    return a;
                });
    }

    public PolicyRegistry<CONTEXT, ABILITY> getRegistry() {
        return registry;
    }

    @ApiStatus.Internal
    public Runner<RESOURCE, CONTEXT, ABILITY> runnerForAbility(ABILITY ability) {
        return runners.computeIfAbsent(ability, a -> new Runner<>(abilityMap.getSteps(a), this));
    }
}
