package com.gitlab.taucher2003.t2003_utils.common.policy;

import com.gitlab.taucher2003.t2003_utils.common.data.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public abstract class BasePolicy<R, C, A> {

    enum RuleType {
        NONE(true, true),
        RESOURCE_AND_CONTEXT(false, false),
        RESOURCE_ONLY(false, true),
        CONTEXT_ONLY(true, false);

        private final boolean evaluateForNullResource;
        private final boolean evaluateForNullContext;

        RuleType(boolean evaluateForNullResource, boolean evaluateForNullContext) {
            this.evaluateForNullResource = evaluateForNullResource;
            this.evaluateForNullContext = evaluateForNullContext;
        }
    }

    private final Collection<Pair<Rule<R, C, A>, RuleType>> rules = new ArrayList<>();

    private final ThreadLocal<Set<A>> currentCollector = new ThreadLocal<>();

    protected abstract void definePolicies();

    protected Rule<R, C, A> resourceRule(Predicate<R> resourcePred) {
        return rule((resource, context) -> resourcePred.test(resource), RuleType.RESOURCE_ONLY);
    }

    protected Rule<R, C, A> contextRule(Predicate<C> contextPred) {
        return rule((resource, context) -> contextPred.test(context), RuleType.CONTEXT_ONLY);
    }

    protected Rule<R, C, A> rule(BiPredicate<R, C> resourceContextPred) {
        return rule(resourceContextPred, RuleType.RESOURCE_AND_CONTEXT);
    }

    protected Rule<R, C, A> ifEnabled(A ability) {
        return rule((__, ___) -> currentCollector.get().contains(ability), RuleType.NONE);
    }

    @SafeVarargs
    protected final Rule<R, C, A> ifAnyEnabled(A... abilities) {
        var abilityList = Arrays.asList(abilities);
        return rule((__, ___) -> currentCollector.get().stream().anyMatch(abilityList::contains), RuleType.NONE);
    }

    @SafeVarargs
    protected final Rule<R, C, A> ifAllEnabled(A... abilities) {
        var abilityList = Arrays.asList(abilities);
        return rule((__, ___) -> currentCollector.get().containsAll(abilityList), RuleType.NONE);
    }

    private Rule<R, C, A> rule(BiPredicate<R, C> resourceContextPred, RuleType type) {
        var rule = new Rule<>(this, resourceContextPred);
        rules.add(new Pair<>(rule, type));
        return rule;
    }

    protected <LR extends R> RuleFor<R, LR, C, A> forClass(Class<LR> type) {
        return new RuleFor<>(this, type);
    }

    @SafeVarargs
    protected final void enable(A... abilities) {
        var collector = currentCollector.get();
        collector.addAll(Arrays.asList(abilities));
    }

    @SafeVarargs
    protected final void disable(A... abilities) {
        var collector = currentCollector.get();
        Arrays.asList(abilities).forEach(collector::remove);
    }

    Rule<R, C, A> addRule(Rule<R, C, A> rule, RuleType ruleType) {
        rules.add(new Pair<>(rule, ruleType));
        return rule;
    }

    private void evaluate(R resource, C context) {
        definePolicies();
        rules.forEach(pair -> {
            var rule = pair.first();
            var ruleType = pair.second();

            if (context == null) {
                if (ruleType.evaluateForNullContext) {
                    rule.run(resource, null);
                }
                return;
            }

            if (resource == null) {
                if (ruleType.evaluateForNullResource) {
                    rule.run(null, context);
                }
                return;
            }

            rule.run(resource, context);
        });
    }

    public boolean can(C context, A ability, R resource) {
        return getEnabledAbilities(context, resource).contains(ability);
    }

    public Set<A> getEnabledAbilities(C context, R resource) {
        var collector = new HashSet<A>();

        currentCollector.set(collector);
        evaluate(resource, context);
        currentCollector.set(null);

        return Collections.unmodifiableSet(collector);
    }
}
