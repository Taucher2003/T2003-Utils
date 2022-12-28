package com.gitlab.taucher2003.t2003_utils.common.policy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public abstract class BasePolicy<R, C, A> {

    private final Collection<Rule<R, C, A>> rules = new ArrayList<>();

    private final ThreadLocal<List<A>> currentCollector = new ThreadLocal<>();

    protected abstract void definePolicies();

    protected Rule<R, C, A> resourceRule(Predicate<R> resourcePred) {
        return rule((resource, context) -> resourcePred.test(resource));
    }

    protected Rule<R, C, A> contextRule(Predicate<C> contextPred) {
        return rule((resource, context) -> contextPred.test(context));
    }

    protected Rule<R, C, A> rule(BiPredicate<R, C> resourceContextPred) {
        var rule = new Rule<>(this, resourceContextPred);
        rules.add(rule);
        return rule;
    }

    @SafeVarargs
    protected final void enable(A... abilities) {
        var collector = currentCollector.get();
        collector.addAll(Arrays.asList(abilities));
    }

    @SafeVarargs
    protected final void disable(A... abilities) {
        var collector = currentCollector.get();
        collector.removeAll(Arrays.asList(abilities));
    }

    private void evaluate(R resource, C context) {
        definePolicies();
        rules.forEach(rule -> rule.run(resource, context));
    }

    public boolean can(C context, A ability, R resource) {
        var collector = new ArrayList<A>();

        currentCollector.set(collector);
        evaluate(resource, context);
        currentCollector.set(null);

        return collector.contains(ability);
    }
}
