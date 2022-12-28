package com.gitlab.taucher2003.t2003_utils.common.policy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PolicyRegistryTest {

    @Test
    void canWithoutPolicies() {
        var registry = new PolicyRegistry<Object, String>();

        assertThat(registry.can(new Object(), "test_ability", new Object())).isFalse();
    }

    @Test
    void canWithSinglePolicy() {
        var ability = "test_ability";
        var resource = new Object();
        var context = new Object();

        var policy = (BasePolicy<Object, Object, String>) mock(BasePolicy.class);
        when(policy.can(context, ability, resource)).thenReturn(true);

        var registry = new PolicyRegistry<Object, String>();
        registry.registerPolicy(Object.class, policy);

        assertThat(registry.can(context, ability, resource)).isTrue();
    }

    @Test
    void canWithMultiplePolicies() {
        var ability = "test_ability";
        var resource = new Object();
        var context = new Object();

        var policy = (BasePolicy<Object, Object, String>) mock(BasePolicy.class);
        when(policy.can(context, ability, resource)).thenReturn(true);

        var unrelatedPolicy1 = (BasePolicy<String, Object, String>) mock(BasePolicy.class);
        when(unrelatedPolicy1.can(any(), any(), any())).thenReturn(false);

        var unrelatedPolicy2 = (BasePolicy<Integer, Object, String>) mock(BasePolicy.class);
        when(unrelatedPolicy2.can(any(), any(), any())).thenReturn(false);

        var registry = new PolicyRegistry<Object, String>();
        registry.registerPolicy(String.class, unrelatedPolicy1);
        registry.registerPolicy(Integer.class, unrelatedPolicy2);
        registry.registerPolicy(Object.class, policy);

        assertThat(registry.can(context, ability, resource)).isTrue();
        verifyNoInteractions(unrelatedPolicy1);
        verifyNoInteractions(unrelatedPolicy2);
        verify(policy).can(context, ability, resource);
    }
}
