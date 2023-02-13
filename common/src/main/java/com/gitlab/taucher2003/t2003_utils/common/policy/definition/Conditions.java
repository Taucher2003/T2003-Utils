package com.gitlab.taucher2003.t2003_utils.common.policy.definition;

import com.gitlab.taucher2003.t2003_utils.common.policy.execution.EvaluationContext;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Conditions {

    public static final int DEFAULT_SCORE = 4;

    private Conditions() {
    }

    public static class Simple<RESOURCE, CONTEXT, ABILITY> implements Condition<RESOURCE, CONTEXT, ABILITY> {

        private final BiPredicate<RESOURCE, CONTEXT> delegate;
        private final int score;
        private final boolean usesResource;
        private final boolean usesContext;

        public Simple(BiPredicate<RESOURCE, CONTEXT> delegate, int score, boolean usesResource, boolean usesContext) {
            this.delegate = delegate;
            this.score = score;
            this.usesResource = usesResource;
            this.usesContext = usesContext;
        }

        @Override
        public boolean test(RESOURCE resource, CONTEXT context, EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext) {
            if (resource == null && usesResource) {
                return false;
            }
            if (context == null && usesContext) {
                return false;
            }

            return delegate.test(resource, context);
        }

        @Override
        public Condition<RESOURCE, CONTEXT, ABILITY> simplify() {
            return this;
        }

        @Override
        public double score(EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext) {
            return score;
        }
    }

    public static class Ability<RESOURCE, CONTEXT, ABILITY> implements Condition<RESOURCE, CONTEXT, ABILITY> {

        private final ABILITY ability;

        public Ability(ABILITY ability) {
            this.ability = ability;
        }

        @Override
        public boolean test(RESOURCE resource, CONTEXT context, EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext) {
            return evaluationContext.runnerForAbility(ability).enabled(resource, context);
        }

        @Override
        public Condition<RESOURCE, CONTEXT, ABILITY> simplify() {
            return this;
        }

        @Override
        public double score(EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext) {
            return evaluationContext.runnerForAbility(ability).score();
        }

        public ABILITY ability() {
            return ability;
        }
    }

    public static class And<RESOURCE, CONTEXT, ABILITY> implements Condition<RESOURCE, CONTEXT, ABILITY> {

        private final List<Condition<RESOURCE, CONTEXT, ABILITY>> conditions;

        public And(List<Condition<RESOURCE, CONTEXT, ABILITY>> conditions) {
            this.conditions = conditions;
        }

        @Override
        public boolean test(RESOURCE resource, CONTEXT context, EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext) {
            // we want to evaluate conditions with low score first
            //noinspection RedundantStreamOptionalCall
            return conditions()
                    .sorted(Comparator.comparingDouble(condition -> condition.score(evaluationContext)))
                    .allMatch(condition -> condition.test(resource, context, evaluationContext));
        }

        @Override
        public Condition<RESOURCE, CONTEXT, ABILITY> simplify() {
            var simplifiedConditions = conditions().flatMap(condition -> {
                var simplified = condition.simplify();
                if (And.class.equals(simplified.getClass())) {
                    return ((And<RESOURCE, CONTEXT, ABILITY>) simplified).conditions();
                }
                return Stream.of(simplified);
            }).collect(Collectors.toList());

            return new And<>(simplifiedConditions);
        }

        @Override
        public double score(EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext) {
            return conditions().mapToDouble(condition -> condition.score(evaluationContext)).sum();
        }

        public Stream<Condition<RESOURCE, CONTEXT, ABILITY>> conditions() {
            return conditions.stream();
        }
    }

    public static class Or<RESOURCE, CONTEXT, ABILITY> implements Condition<RESOURCE, CONTEXT, ABILITY> {

        private final List<Condition<RESOURCE, CONTEXT, ABILITY>> conditions;

        public Or(List<Condition<RESOURCE, CONTEXT, ABILITY>> conditions) {
            this.conditions = conditions;
        }

        @Override
        public boolean test(RESOURCE resource, CONTEXT context, EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext) {
            // we want to evaluate conditions with low score first
            //noinspection RedundantStreamOptionalCall
            return conditions()
                    .sorted(Comparator.comparingDouble(condition -> condition.score(evaluationContext)))
                    .anyMatch(condition -> condition.test(resource, context, evaluationContext));
        }

        @Override
        public Condition<RESOURCE, CONTEXT, ABILITY> simplify() {
            var simplifiedConditions = conditions().flatMap(condition -> {
                var simplified = condition.simplify();
                if (Or.class.equals(simplified.getClass())) {
                    return ((Or<RESOURCE, CONTEXT, ABILITY>) simplified).conditions();
                }
                return Stream.of(simplified);
            }).collect(Collectors.toList());

            return new Or<>(simplifiedConditions);
        }

        @Override
        public double score(EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext) {
            return conditions().mapToDouble(condition -> condition.score(evaluationContext)).sum();
        }

        public Stream<Condition<RESOURCE, CONTEXT, ABILITY>> conditions() {
            return conditions.stream();
        }
    }

    public static class Not<RESOURCE, CONTEXT, ABILITY> implements Condition<RESOURCE, CONTEXT, ABILITY> {

        private final Condition<RESOURCE, CONTEXT, ABILITY> condition;

        public Not(Condition<RESOURCE, CONTEXT, ABILITY> condition) {
            this.condition = condition;
        }

        @Override
        public boolean test(RESOURCE resource, CONTEXT context, EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext) {
            return !condition.test(resource, context, evaluationContext);
        }

        @Override
        public Condition<RESOURCE, CONTEXT, ABILITY> simplify() {
            if (condition instanceof And) {
                return new Or<>(((And<RESOURCE, CONTEXT, ABILITY>) condition).conditions().map(Condition::not).collect(Collectors.toList())).simplify();
            }
            if (condition instanceof Or) {
                return new And<>(((Or<RESOURCE, CONTEXT, ABILITY>) condition).conditions().map(Condition::not).collect(Collectors.toList())).simplify();
            }
            if (condition instanceof Not) {
                return ((Not<RESOURCE, CONTEXT, ABILITY>) condition).condition.simplify();
            }

            return new Not<>(condition.simplify());
        }

        @Override
        public double score(EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext) {
            return condition.score(evaluationContext);
        }
    }

    public static class ForClass<RESOURCE, LOCAL_RESOURCE extends RESOURCE, CONTEXT, ABILITY> {

        private final Class<LOCAL_RESOURCE> type;

        ForClass(Class<LOCAL_RESOURCE> type) {
            this.type = type;
        }

        public Condition<RESOURCE, CONTEXT, ABILITY> resourceCondition(Predicate<LOCAL_RESOURCE> condition) {
            return resourceCondition(condition, DEFAULT_SCORE);
        }

        public Condition<RESOURCE, CONTEXT, ABILITY> condition(BiPredicate<LOCAL_RESOURCE, CONTEXT> condition) {
            return condition(condition, DEFAULT_SCORE);
        }

        public Condition<RESOURCE, CONTEXT, ABILITY> resourceCondition(Predicate<LOCAL_RESOURCE> condition, int score) {
            return new Simple<>(
                    (resource, context) -> type.isInstance(resource) && condition.test(type.cast(resource)),
                    score,
                    true,
                    false
            );
        }

        public Condition<RESOURCE, CONTEXT, ABILITY> condition(BiPredicate<LOCAL_RESOURCE, CONTEXT> condition, int score) {
            return new Simple<>(
                    (resource, context) -> type.isInstance(resource) && condition.test(type.cast(resource), context),
                    score,
                    true,
                    true
            );
        }
    }
}
