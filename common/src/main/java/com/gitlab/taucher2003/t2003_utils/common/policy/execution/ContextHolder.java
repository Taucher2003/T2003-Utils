package com.gitlab.taucher2003.t2003_utils.common.policy.execution;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class ContextHolder<CONTEXT, ABILITY> {

    private final Map<Class<?>, EvaluationContext<?, CONTEXT, ABILITY>> evaluationContexts = new HashMap<>();

    private final CONTEXT context;
    private final PolicyRegistry<CONTEXT, ABILITY> registry;

    public ContextHolder(CONTEXT context, PolicyRegistry<CONTEXT, ABILITY> registry) {
        this.context = context;
        this.registry = registry;
    }

    public <RESOURCE> EvaluationContext<RESOURCE, CONTEXT, ABILITY> forResourceClass(Class<RESOURCE> resourceClass) {
        return (EvaluationContext<RESOURCE, CONTEXT, ABILITY>) evaluationContexts.computeIfAbsent(
                resourceClass,
                clazz -> registry.findPolicyForClass(resourceClass)
                        .map(policy -> policy.forContext(context))
                        .orElseThrow(() -> new NoSuchElementException("No policy for class " + resourceClass.getName() + " found"))
        );
    }

    public <RESOURCE> ResourceEvaluationContext<RESOURCE, CONTEXT, ABILITY> forResource(RESOURCE resource) {
        if (resource == null) {
            return null;
        }

        return new ResourceEvaluationContext<>(
                resource,
                (EvaluationContext<RESOURCE, CONTEXT, ABILITY>) forResourceClass(resource.getClass())
        );
    }

    public static class ResourceEvaluationContext<RESOURCE, CONTEXT, ABILITY> {

        private final RESOURCE resource;
        private final EvaluationContext<RESOURCE, CONTEXT, ABILITY> delegate;

        public ResourceEvaluationContext(RESOURCE resource, EvaluationContext<RESOURCE, CONTEXT, ABILITY> delegate) {
            this.resource = resource;
            this.delegate = delegate;
        }

        public boolean can(ABILITY ability) {
            return delegate.can(resource, ability);
        }

        public Set<ABILITY> getEnabledAbilities() {
            return delegate.getEnabledAbilities(resource);
        }
    }
}
