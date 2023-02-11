package com.gitlab.taucher2003.t2003_utils.common.policy.execution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class PolicyRegistry<CONTEXT, ABILITY> {

    private final Collection<RegistrationEntry<?>> policies = new ArrayList<>();

    public <RESOURCE> void registerPolicy(Class<RESOURCE> resourceClass, Policy<RESOURCE, CONTEXT, ABILITY> policy) {
        policies.add(new RegistrationEntry<>(resourceClass, policy));
    }

    public <RESOURCE> Optional<Policy<RESOURCE, CONTEXT, ABILITY>> findPolicyFor(RESOURCE resource) {
        //noinspection unchecked
        return policies
                .stream()
                .filter(entry -> entry.resourceClass.isInstance(resource))
                .findFirst()
                .map(entry -> (Policy<RESOURCE, CONTEXT, ABILITY>) entry.abilityMap);
    }

    public <RESOURCE> EvaluationContext<RESOURCE, CONTEXT, ABILITY> forContext(CONTEXT context, RESOURCE resource) {
        var entryOpt = findPolicyFor(resource);
        if (entryOpt.isEmpty()) {
            return null;
        }

        var policy = entryOpt.get();
        return policy.forContext(context);
    }

    public <RESOURCE> boolean can(CONTEXT context, RESOURCE resource, ABILITY ability) {
        var entryOpt = findPolicyFor(resource);
        if (entryOpt.isEmpty()) {
            return false;
        }

        var policy = entryOpt.get();
        return policy.forContext(context).can(resource, ability);
    }

    public <RESOURCE> Set<ABILITY> getEnabledAbilities(CONTEXT context, RESOURCE resource) {
        var entryOpt = findPolicyFor(resource);
        if (entryOpt.isEmpty()) {
            return Collections.emptySet();
        }

        var policy = entryOpt.get();
        return policy.forContext(context).getEnabledAbilities(resource);
    }

    private final class RegistrationEntry<RESOURCE> {

        private final Class<RESOURCE> resourceClass;
        private final Policy<RESOURCE, CONTEXT, ABILITY> abilityMap;

        private RegistrationEntry(Class<RESOURCE> resourceClass, Policy<RESOURCE, CONTEXT, ABILITY> abilityMap) {
            this.resourceClass = resourceClass;
            this.abilityMap = abilityMap;
        }
    }
}
