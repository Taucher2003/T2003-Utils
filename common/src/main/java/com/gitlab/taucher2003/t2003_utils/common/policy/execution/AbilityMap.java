package com.gitlab.taucher2003.t2003_utils.common.policy.execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AbilityMap<RESOURCE, CONTEXT, ABILITY> {

    private final Map<ABILITY, List<EvaluationStep<RESOURCE, CONTEXT, ABILITY>>> map = new HashMap<>();

    public void addStep(ABILITY ability, EvaluationStep<RESOURCE, CONTEXT, ABILITY> step) {
        map.compute(ability, (__, steps) -> {
            if (steps == null) {
                steps = new ArrayList<>();
            }
            steps.add(step);
            return steps;
        });
    }

    public List<EvaluationStep<RESOURCE, CONTEXT, ABILITY>> getSteps(ABILITY ability) {
        return map.getOrDefault(ability, Collections.emptyList());
    }

    public Set<ABILITY> getContainedAbilities() {
        return map.keySet();
    }
}
