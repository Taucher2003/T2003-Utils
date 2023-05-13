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
        policy.setRegistry(this);
    }

    public <RESOURCE> Optional<Policy<RESOURCE, CONTEXT, ABILITY>> findPolicyForClass(Class<RESOURCE> resourceClass) {
        //noinspection unchecked
        return policies
                .stream()
                .filter(entry -> entry.resourceClass.isAssignableFrom(resourceClass))
                .findFirst()
                .map(entry -> (Policy<RESOURCE, CONTEXT, ABILITY>) entry.policy);
    }

    public <RESOURCE> Optional<Policy<RESOURCE, CONTEXT, ABILITY>> findPolicyFor(RESOURCE resource) {
        if (resource == null) {
            return Optional.empty();
        }

        //noinspection unchecked
        return findPolicyForClass((Class<RESOURCE>) resource.getClass());
    }

    public ContextHolder<CONTEXT, ABILITY> forContext(CONTEXT context) {
        return new ContextHolder<>(context, this);
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
        private final Policy<RESOURCE, CONTEXT, ABILITY> policy;

        private RegistrationEntry(Class<RESOURCE> resourceClass, Policy<RESOURCE, CONTEXT, ABILITY> policy) {
            this.resourceClass = resourceClass;
            this.policy = policy;
        }
    }
}
