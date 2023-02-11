package com.gitlab.taucher2003.t2003_utils.common.policy_old;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class PolicyRegistry<C, A> {

    private final Collection<RegistrationEntry<?>> policies = new ArrayList<>();

    public <R> void registerPolicy(Class<R> resourceClass, BasePolicy<R, C, A> policy) {
        policies.add(new RegistrationEntry<>(resourceClass, policy));
    }

    public <R> Optional<BasePolicy<R, C, A>> findPolicyFor(R resource) {
        //noinspection unchecked
        return policies
                .stream()
                .filter(entry -> entry.resourceClass.isInstance(resource))
                .findFirst()
                .map(entry -> (BasePolicy<R, C, A>) entry.policy);
    }

    public <R> boolean can(C context, A ability, R resource) {
        var entryOpt = findPolicyFor(resource);
        if (entryOpt.isEmpty()) {
            return false;
        }

        var policy = entryOpt.get();
        return policy.can(context, ability, resource);
    }

    public <R> Set<A> getEnabledAbilities(C context, R resource) {
        var entryOpt = findPolicyFor(resource);
        if (entryOpt.isEmpty()) {
            return Collections.emptySet();
        }

        var policy = entryOpt.get();
        return policy.getEnabledAbilities(context, resource);
    }

    private final class RegistrationEntry<T> {

        private final Class<T> resourceClass;
        private final BasePolicy<T, C, A> policy;

        private RegistrationEntry(Class<T> resourceClass, BasePolicy<T, C, A> policy) {
            this.resourceClass = resourceClass;
            this.policy = policy;
        }
    }
}
