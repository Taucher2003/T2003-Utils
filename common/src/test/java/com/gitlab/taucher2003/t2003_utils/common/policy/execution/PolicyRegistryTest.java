package com.gitlab.taucher2003.t2003_utils.common.policy.execution;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PolicyRegistryTest {

    @Test
    void returnsCorrectEvaluationContext() {
        var resource = new Object();
        var context = new Object();

        var policy = (Policy<Object, Object, String>) mock(Policy.class);
        var evaluationContext = (EvaluationContext<Object, Object, String>) mock(EvaluationContext.class);
        when(policy.forContext(context)).thenReturn(evaluationContext);

        var registry = new PolicyRegistry<Object, String>();
        registry.registerPolicy(Object.class, policy);

        assertThat(registry.forContext(context, resource)).isEqualTo(evaluationContext);
    }

    @Test
    void canWithoutPolicies() {
        var registry = new PolicyRegistry<Object, String>();

        assertThat(registry.can(new Object(), new Object(), "test_ability")).isFalse();
    }

    @Test
    void canWithSinglePolicy() {
        var ability = "test_ability";
        var resource = new Object();
        var context = new Object();

        var policy = (Policy<Object, Object, String>) mock(Policy.class);
        var evaluationContext = (EvaluationContext<Object, Object, String>) mock(EvaluationContext.class);
        when(policy.forContext(context)).thenReturn(evaluationContext);
        when(evaluationContext.can(resource, ability)).thenReturn(true);

        var registry = new PolicyRegistry<Object, String>();
        registry.registerPolicy(Object.class, policy);

        assertThat(registry.can(context, resource, ability)).isTrue();
    }

    @Test
    void canWithMultiplePolicies() {
        var ability = "test_ability";
        var resource = new Object();
        var context = new Object();

        var policy = (Policy<Object, Object, String>) mock(Policy.class);
        var evaluationContext = (EvaluationContext<Object, Object, String>) mock(EvaluationContext.class);
        when(policy.forContext(context)).thenReturn(evaluationContext);
        when(evaluationContext.can(resource, ability)).thenReturn(true);

        var unrelatedPolicy1 = (Policy<String, Object, String>) mock(Policy.class);

        var unrelatedPolicy2 = (Policy<Integer, Object, String>) mock(Policy.class);

        var registry = new PolicyRegistry<Object, String>();
        registry.registerPolicy(String.class, unrelatedPolicy1);
        registry.registerPolicy(Integer.class, unrelatedPolicy2);
        registry.registerPolicy(Object.class, policy);

        assertThat(registry.can(context, resource, ability)).isTrue();
        verifyNoInteractions(unrelatedPolicy1);
        verifyNoInteractions(unrelatedPolicy2);
        verify(policy).forContext(context);
        verify(evaluationContext).can(resource, ability);
    }

    @Test
    void getEnabledAbilitiesWithoutPolicies() {
        var registry = new PolicyRegistry<Object, String>();

        assertThat(registry.getEnabledAbilities(new Object(), new Object())).isEmpty();
    }

    @Test
    void getEnabledAbilitiesWithSinglePolicy() {
        var resource = new Object();
        var context = new Object();
        var enabledAbilities = Set.of("ability_1", "ability_2");

        var policy = (Policy<Object, Object, String>) mock(Policy.class);
        var evaluationContext = (EvaluationContext<Object, Object, String>) mock(EvaluationContext.class);
        when(policy.forContext(context)).thenReturn(evaluationContext);
        when(evaluationContext.getEnabledAbilities(resource)).thenReturn(enabledAbilities);

        var registry = new PolicyRegistry<Object, String>();
        registry.registerPolicy(Object.class, policy);

        assertThat(registry.getEnabledAbilities(context, resource)).containsExactlyElementsOf(enabledAbilities);
    }

    @Test
    void getEnabledAbilitiesWithMultiplePolicies() {
        var resource = new Object();
        var context = new Object();
        var enabledAbilities = Set.of("ability_1", "ability_2");

        var policy = (Policy<Object, Object, String>) mock(Policy.class);
        var evaluationContext = (EvaluationContext<Object, Object, String>) mock(EvaluationContext.class);
        when(policy.forContext(context)).thenReturn(evaluationContext);
        when(evaluationContext.getEnabledAbilities(resource)).thenReturn(enabledAbilities);

        var unrelatedPolicy1 = (Policy<String, Object, String>) mock(Policy.class);

        var unrelatedPolicy2 = (Policy<Integer, Object, String>) mock(Policy.class);

        var registry = new PolicyRegistry<Object, String>();
        registry.registerPolicy(String.class, unrelatedPolicy1);
        registry.registerPolicy(Integer.class, unrelatedPolicy2);
        registry.registerPolicy(Object.class, policy);

        assertThat(registry.getEnabledAbilities(context, resource)).containsExactlyElementsOf(enabledAbilities);
        verifyNoInteractions(unrelatedPolicy1);
        verifyNoInteractions(unrelatedPolicy2);
        verify(evaluationContext).getEnabledAbilities(resource);
    }
}
