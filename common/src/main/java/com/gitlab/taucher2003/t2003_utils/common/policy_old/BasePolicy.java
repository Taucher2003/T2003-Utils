package com.gitlab.taucher2003.t2003_utils.common.policy_old;

import com.gitlab.taucher2003.t2003_utils.common.data.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    private final AtomicReference<Collection<Pair<Rule<R, C, A>, RuleType>>> rules = new AtomicReference<>();
    private final ThreadLocal<Collector> currentCollector = new ThreadLocal<>();

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
        return rule((__, ___) -> currentCollector.get().enabled(ability), RuleType.NONE).addDependencies(ability);
    }

    @SafeVarargs
    protected final Rule<R, C, A> ifAnyEnabled(A... abilities) {
        var abilityList = Arrays.asList(abilities);
        return rule((__, ___) -> currentCollector.get().anyEnabled(abilityList), RuleType.NONE).addDependencies(abilities);
    }

    @SafeVarargs
    protected final Rule<R, C, A> ifAllEnabled(A... abilities) {
        var abilityList = Arrays.asList(abilities);
        return rule((__, ___) -> currentCollector.get().allEnabled(abilityList), RuleType.NONE).addDependencies(abilities);
    }

    private Rule<R, C, A> rule(BiPredicate<R, C> resourceContextPred, RuleType type) {
        var rule = new Rule<>(this, resourceContextPred);
        rules.get().add(new Pair<>(rule, type));
        return rule;
    }

    protected <LR extends R> RuleFor<R, LR, C, A> forClass(Class<LR> type) {
        return new RuleFor<>(this, type);
    }

    @SafeVarargs
    protected final void enable(A... abilities) {
        var collector = currentCollector.get();
        collector.enabledAbilities.addAll(Arrays.asList(abilities));
    }

    @SafeVarargs
    protected final void prevent(A... abilities) {
        var collector = currentCollector.get();
        collector.preventedAbilities.addAll(Arrays.asList(abilities));
    }

    Rule<R, C, A> addRule(Rule<R, C, A> rule, RuleType ruleType) {
        rules.get().add(new Pair<>(rule, ruleType));
        return rule;
    }

    private void ensurePolicies() {
        if (rules.compareAndSet(null, new ArrayList<>())) {
            definePolicies();
            enableDependencies();
        }
    }

    private void evaluateAbility(R resource, C context, A ability) {
        rules.get().forEach(pair -> {
            var rule = pair.first();
            if (!rule.getSeenAbilities().contains(ability)) {
                return;
            }

            var ruleType = pair.second();

            evaluateRule(rule, ruleType, resource, context);
        });
    }

    private void evaluate(R resource, C context) {
        rules.get().forEach(pair -> {
            var rule = pair.first();
            var ruleType = pair.second();
            evaluateRule(rule, ruleType, resource, context);
        });
    }

    private void evaluateRule(Rule<R, C, A> rule, RuleType ruleType, R resource, C context) {
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
    }

    public boolean can(C context, A ability, R resource) {
        ensurePolicies();
        return withEvaluationContext(() -> evaluateAbility(resource, context, ability)).enabled(ability);
    }

    public Set<A> getEnabledAbilities(C context, R resource) {
        ensurePolicies();
        return Collections.unmodifiableSet(
                withEvaluationContext(() -> evaluate(resource, context)).getEnabledAbilities()
        );
    }

    Collection<A> trackAbilities(Rule<R, C, A> rule) {
        return Collections.unmodifiableSet(withEvaluationContext(rule::runUnchecked).getSeenAbilities());
    }

    void enableDependencies() {
        var rules = this.rules.get().stream().map(Pair::first).collect(Collectors.toList());
        rules.forEach(rule -> {
            var dependencies = rule.getAbilityDependencies();
            dependencies.forEach(dependency -> rules.stream()
                    .filter(r -> r.getSeenAbilities().contains(dependency))
                    .forEach(r -> r.getSeenAbilities().addAll(rule.getSeenAbilities())));
        });
    }

    private Collector withEvaluationContext(Runnable execution) {
        var collector = new Collector();

        currentCollector.set(collector);
        execution.run();
        currentCollector.remove();

        return collector;
    }

    class Collector {

        private final Collection<A> enabledAbilities = new HashSet<>();
        private final Collection<A> preventedAbilities = new HashSet<>();

        public boolean enabled(A ability) {
            return enabledAbilities.contains(ability) && !preventedAbilities.contains(ability);
        }

        public boolean allEnabled(Collection<A> abilities) {
            return enabledAbilities.containsAll(abilities) && preventedAbilities.stream().noneMatch(abilities::contains);
        }

        public boolean anyEnabled(Iterable<A> abilities) {
            for (var ability : abilities) {
                if (enabled(ability)) {
                    return true;
                }
            }
            return false;
        }

        public Set<A> getEnabledAbilities() {
            var abilities = new HashSet<>(enabledAbilities);
            abilities.removeAll(preventedAbilities);
            return abilities;
        }

        public Set<A> getSeenAbilities() {
            var abilities = new HashSet<>(enabledAbilities);
            abilities.addAll(preventedAbilities);
            return abilities;
        }
    }
}
