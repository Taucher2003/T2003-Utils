package com.gitlab.taucher2003.t2003_utils.common.policy.execution;

import com.gitlab.taucher2003.t2003_utils.common.data.Pair;
import com.gitlab.taucher2003.t2003_utils.common.policy.definition.RuleAction;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Runner<RESOURCE, CONTEXT, ABILITY> {

    public static final int FLAG_DISABLE_STEP_OPTIMISATION = 1;

    private final Map<Pair<RESOURCE, CONTEXT>, State> states = new HashMap<>();

    private final List<EvaluationStep<RESOURCE, CONTEXT, ABILITY>> steps;
    private final EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext;

    public Runner(List<EvaluationStep<RESOURCE, CONTEXT, ABILITY>> steps, EvaluationContext<RESOURCE, CONTEXT, ABILITY> evaluationContext) {
        this.steps = steps;
        this.evaluationContext = evaluationContext;
    }

    List<EvaluationStep<RESOURCE, CONTEXT, ABILITY>> getSteps() {
        return steps;
    }

    public double score() {
        return steps.stream().mapToDouble(step -> step.score(evaluationContext)).sum();
    }

    public boolean enabled(RESOURCE resource, CONTEXT context) {
        var state = getState(resource, context, 0);

        return state.passes();
    }

    public State getState(RESOURCE resource, CONTEXT context, int evaluationFlags) {
        var state = states.computeIfAbsent(new Pair<>(resource, context), key -> new State());

        if (!state.evaluated) {
            run(resource, context, state, evaluationFlags);
        }

        return state;
    }

    private void run(RESOURCE resource, CONTEXT context, State state, int evaluationFlags) {
        for (var it = orderedSteps(state, evaluationFlags); it.hasNext(); ) {
            if (state.prevented) {
                break;
            }

            var step = it.next();

            switch (step.getAction()) {
                case ENABLE:
                    if (!state.enabled && step.getCondition().test(resource, context, evaluationContext)) {
                        state.enabled = true;
                    }
                    break;
                case PREVENT:
                    if (step.getCondition().test(resource, context, evaluationContext)) {
                        state.prevented = true;
                    }
                    break;
            }
        }
        state.evaluated = true;
    }

    private Iterator<EvaluationStep<RESOURCE, CONTEXT, ABILITY>> orderedSteps(State state, int evaluationFlags) {
        var steps = flattenedSteps();

        if (steps.size() > 50) {
            return steps.stream().sorted(Comparator.comparingDouble(step -> step.score(evaluationContext))).iterator();
        }

        return new StepIterator(steps, state, evaluationFlags);
    }

    private List<EvaluationStep<RESOURCE, CONTEXT, ABILITY>> flattenedSteps() {
        return steps.stream().flatMap(step -> step.flattened(evaluationContext)).collect(Collectors.toList());
    }

    private final class StepIterator implements Iterator<EvaluationStep<RESOURCE, CONTEXT, ABILITY>> {

        private final Map<RuleAction.Action, List<EvaluationStep<RESOURCE, CONTEXT, ABILITY>>> remainingSteps;
        private final State state;
        private final int evaluationFlags;

        private StepIterator(Collection<EvaluationStep<RESOURCE, CONTEXT, ABILITY>> remainingSteps, State state, int evaluationFlags) {
            this.remainingSteps = remainingSteps.stream().collect(Collectors.groupingBy(EvaluationStep::getAction));
            this.state = state;
            this.evaluationFlags = evaluationFlags;
        }

        @Override
        public boolean hasNext() {
            return !getRemainingSteps().isEmpty();
        }

        @Override
        public EvaluationStep<RESOURCE, CONTEXT, ABILITY> next() {
            var remainingSteps = getRemainingSteps();
            var nextStep = stepWithLowestScore(remainingSteps);
            this.remainingSteps.values().forEach(list -> list.remove(nextStep));
            return nextStep;
        }

        private List<EvaluationStep<RESOURCE, CONTEXT, ABILITY>> getRemainingSteps() {
            if ((evaluationFlags & FLAG_DISABLE_STEP_OPTIMISATION) == FLAG_DISABLE_STEP_OPTIMISATION) {
                return remainingSteps.values().stream().flatMap(List::stream).collect(Collectors.toList());
            }

            if (state.enabled) {
                return remainingSteps.getOrDefault(RuleAction.Action.PREVENT, Collections.emptyList());
            }

            if (remainingSteps.getOrDefault(RuleAction.Action.ENABLE, Collections.emptyList()).isEmpty()) {
                state.prevented = true;
                return Collections.emptyList();
            }
            return remainingSteps.values().stream().flatMap(List::stream).collect(Collectors.toList());
        }

        private EvaluationStep<RESOURCE, CONTEXT, ABILITY> stepWithLowestScore(Iterable<EvaluationStep<RESOURCE, CONTEXT, ABILITY>> remainingSteps) {
            EvaluationStep<RESOURCE, CONTEXT, ABILITY> currentStep = null;

            for (var step : remainingSteps) {
                if (currentStep == null || currentStep.score(evaluationContext) > step.score(evaluationContext)) {
                    currentStep = step;
                }
            }

            return currentStep;
        }
    }

    public static class State {

        private boolean enabled;
        private boolean prevented;
        private boolean evaluated;

        private boolean passes() {
            return !prevented && enabled;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public boolean isPrevented() {
            return prevented;
        }
    }
}
