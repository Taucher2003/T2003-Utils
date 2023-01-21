package com.gitlab.taucher2003.t2003_utils.common.policy;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class BasePolicyTest {

    @ParameterizedTest
    @CsvSource({
            "not_resource,resource_ability,false",
            "resource,resource_ability,true",
            ".,block_ability,false",
            "test,block_ability,true",
            "test,disabled_ability,true",
            "disabled_resource,disabled_ability,false",
            "disabled_resource,block_ability,true"
    })
    void resourceRule(String resource, String ability, boolean expected) {
        class TestPolicy extends BasePolicy<String, String, String> {

            @Override
            protected void definePolicies() {
                resourceRule("resource"::equals).enable("resource_ability");

                resourceRule(s -> s.length() > 1).policy(() -> {
                    enable("block_ability");
                    enable("disabled_ability");
                });

                resourceRule("disabled_resource"::equals).prevent("disabled_ability");
            }
        }

        var policy = new TestPolicy();

        assertThat(policy.can(null, ability, resource)).describedAs(
                String.format("'null' has ability '%s' on '%s': %s", ability, resource, expected)
        ).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "not_context,resource_ability,false",
            "context,resource_ability,true",
            ".,block_ability,false",
            "test,block_ability,true",
            "test,disabled_ability,true",
            "disabled_context,disabled_ability,false",
            "disabled_context,block_ability,true"
    })
    void contextRule(String context, String ability, boolean expected) {
        class TestPolicy extends BasePolicy<String, String, String> {

            @Override
            protected void definePolicies() {
                contextRule("context"::equals).enable("resource_ability");

                contextRule(s -> s.length() > 1).policy(() -> {
                    enable("block_ability");
                    enable("disabled_ability");
                });

                contextRule("disabled_context"::equals).prevent("disabled_ability");
            }
        }

        var policy = new TestPolicy();

        assertThat(policy.can(context, ability, null)).describedAs(
                String.format("'%s' has ability '%s' on 'null': %s", context, ability, expected)
        ).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            ".,.,block_ability,false",
            "..,.,block_ability,false",
            ".,..,block_ability,false",
            "..,..,block_ability,true",

            ".,.,disabled_ability,false",
            "..,.,disabled_ability,false",
            ".,..,disabled_ability,false",
            "..,..,disabled_ability,true",

            ".,.,resource_ability,false",
            "resource,.,resource_ability,false",
            ".,context,resource_ability,false",
            "resource,context,resource_ability,true",

            "disabled_resource,..,disabled_ability,false",
            "..,disabled_context,disabled_ability,false",
            "disabled_resource,disabled_context,disabled_ability,false"
    })
    void rule(String resource, String context, String ability, boolean expected) {
        class TestPolicy extends BasePolicy<String, String, String> {

            @Override
            protected void definePolicies() {
                rule((r, c) -> "resource".equals(r) && "context".equals(c)).enable("resource_ability");

                rule((r, c) -> r.length() > 1 && c.length() > 1).policy(() -> {
                    enable("block_ability");
                    enable("disabled_ability");
                });

                rule((r, c) -> "disabled_resource".equals(r) || "disabled_context".equals(c)).prevent("disabled_ability");
            }
        }

        var policy = new TestPolicy();

        assertThat(policy.can(context, ability, resource)).describedAs(
                String.format("'%s' has ability '%s' on '%s': %s", context, ability, resource, expected)
        ).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "ability_1,true",
            "ability_2,false"
    })
    void ifEnabled(String requiredAbility, boolean expected) {
        class TestPolicy extends BasePolicy<String, String, String> {

            @Override
            protected void definePolicies() {
                resourceRule(r -> true).enable("ability_1");

                ifEnabled(requiredAbility).enable("if_enabled");
            }
        }

        var policy = new TestPolicy();
        assertThat(policy.can("context", "if_enabled", "resource")).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "false,false,false",
            "true,false,true",
            "false,true,true",
            "true,true,true"
    })
    void ifAnyEnabled(boolean enableFirst, boolean enableSecond, boolean expected) {
        class TestPolicy extends BasePolicy<String, String, String> {

            @Override
            protected void definePolicies() {
                resourceRule(r -> enableFirst).enable("ability_1");
                resourceRule(r -> enableSecond).enable("ability_2");

                ifAnyEnabled("ability_1", "ability_2").enable("if_any_enabled");
            }
        }

        var policy = new TestPolicy();
        assertThat(policy.can("context", "if_any_enabled", "resource")).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "false,false,false",
            "true,false,false",
            "false,true,false",
            "true,true,true"
    })
    void ifAllEnabled(boolean enableFirst, boolean enableSecond, boolean expected) {
        class TestPolicy extends BasePolicy<String, String, String> {

            @Override
            protected void definePolicies() {
                resourceRule(r -> enableFirst).enable("ability_1");
                resourceRule(r -> enableSecond).enable("ability_2");

                ifAllEnabled("ability_1", "ability_2").enable("if_all_enabled");
            }
        }

        var policy = new TestPolicy();
        assertThat(policy.can("context", "if_all_enabled", "resource")).isEqualTo(expected);
    }

    @Test
    void forClass() {
        class TestPolicy extends BasePolicy<CharSequence, String, String> {

            @Override
            protected void definePolicies() {
                resourceRule(r -> r.charAt(0) == 't').enable("resourceRule");
                forClass(String.class).resourceRule("test"::equalsIgnoreCase).enable("forClassResourceRule");
                forClass(String.class).rule((r, c) -> "test".equalsIgnoreCase(r) && "context".equals(c)).enable("forClassRule");
            }
        }

        var policy = new TestPolicy();
        assertThat(policy.can("context", "resourceRule", "test")).isTrue();
        assertThat(policy.can("context", "forClassResourceRule", "test")).isTrue();
        assertThat(policy.can("context", "forClassResourceRule", "testing")).isFalse();
        assertThat(policy.can("context", "resourceRule", new StringBuilder("test"))).isTrue();
        assertThat(policy.can("context", "forClassResourceRule", new StringBuilder("test"))).isFalse();
        assertThat(policy.can("context", "forClassRule", "test")).isTrue();
        assertThat(policy.can("context", "forClassRule", "testing")).isFalse();
        assertThat(policy.can("Context", "forClassRule", "test")).isFalse();
        assertThat(policy.can("context", "forClassRule", new StringBuilder("test"))).isFalse();
    }

    @ParameterizedTest
    @CsvSource({
            ".,.,",
            "..,.,",
            ".,..,",
            "..,..,block_ability|disabled_ability",

            ".,.,",
            "resource,.,",
            ".,context,",
            "resource,context,resource_ability|block_ability|disabled_ability",

            "disabled_resource,..,block_ability",
            "..,disabled_context,block_ability",
            "disabled_resource,disabled_context,block_ability"
    })
    void getEnabledAbilities(String resource, String context, String expectedAbilities) {
        class TestPolicy extends BasePolicy<String, String, String> {

            @Override
            protected void definePolicies() {
                rule((r, c) -> "resource".equals(r) && "context".equals(c)).enable("resource_ability");

                rule((r, c) -> r.length() > 1 && c.length() > 1).policy(() -> {
                    enable("block_ability");
                    enable("disabled_ability");
                });

                rule((r, c) -> "disabled_resource".equals(r) || "disabled_context".equals(c)).prevent("disabled_ability");
            }
        }

        var policy = new TestPolicy();
        var expectedAbilitiesArr = Optional.ofNullable(expectedAbilities)
                .map(str -> str.split("\\|"))
                .orElseGet(() -> new String[0]);

        assertThat(policy.getEnabledAbilities(context, resource)).containsExactlyInAnyOrder(expectedAbilitiesArr);
    }

    @ParameterizedTest
    @CsvSource({
            ",,",
            "context,,contextRule",
            ",resource,resourceRule",
            "context,resource,contextRule|resourceRule|rule"
    })
    void evaluationsWithNulls(String context, String resource, String expectedAbilities) {
        class TestPolicy extends BasePolicy<String, String, String> {

            @Override
            protected void definePolicies() {
                resourceRule("resource"::equals).enable("resourceRule");
                contextRule("context"::equals).enable("contextRule");
                rule((r, c) -> "resource".equals(r) && "context".equals(c)).enable("rule");
            }
        }

        var policy = new TestPolicy();
        var expectedAbilitiesArr = Optional.ofNullable(expectedAbilities)
                .map(str -> str.split("\\|"))
                .orElseGet(() -> new String[0]);

        assertThat(policy.getEnabledAbilities(context, resource)).containsExactlyInAnyOrder(expectedAbilitiesArr);
    }

    @Test
    void evaluatesOnlyNecessaryPolicies() {
        var rule1 = (Predicate<String>) mock(Predicate.class);
        var rule2 = (Predicate<String>) mock(Predicate.class);

        class TestPolicy extends BasePolicy<String, String, String> {

            @Override
            protected void definePolicies() {
                resourceRule(rule1).policy(() -> {
                    enable("rule1");
                });

                resourceRule(rule2).policy(() -> {
                    enable("rule2");
                });
            }
        }

        var policy = new TestPolicy();

        policy.can(null, "rule1", "");

        verify(rule1).test("");
        verifyNoInteractions(rule2);
    }

    @Test
    void evaluatesAllDependentPolicies() {
        var rule1 = (Predicate<String>) mock(Predicate.class);
        var rule2 = (Predicate<String>) mock(Predicate.class);

        class TestPolicy extends BasePolicy<String, String, String> {

            @Override
            protected void definePolicies() {
                resourceRule(rule1).policy(() -> {
                    enable("rule1");
                });

                resourceRule(rule2).policy(() -> {
                    enable("rule2");
                });

                ifEnabled("rule1").policy(() -> enable("rule3"));
            }
        }

        var policy = new TestPolicy();

        policy.can(null, "rule3", "");

        verify(rule1).test("");
        verifyNoInteractions(rule2);
    }

    @Test
    void definesPoliciesOnlyOnce() {
        class TestPolicy extends BasePolicy<String, String, String> {

            @Override
            protected void definePolicies() {
            }
        }

        var policy = spy(new TestPolicy());

        policy.can("", "", "");
        policy.can("", "", "");

        verify(policy).definePolicies();
    }

    @Test
    void concurrentUsage() throws InterruptedException {
        class TestPolicy extends BasePolicy<String, String, String> {

            @Override
            protected void definePolicies() {
            }
        }

        var policy = spy(new TestPolicy());

        var executor = Executors.newCachedThreadPool();

        for (var i = 0; i < 10; i++) {
            executor.execute(() -> policy.can("", "", ""));
        }

        executor.shutdown();
        executor.awaitTermination(20, TimeUnit.MILLISECONDS);

        verify(policy).definePolicies();
    }

    @Test
    void performance() {
        class TestPolicy extends BasePolicy<String, String, String> {

            @Override
            protected void definePolicies() {
                resourceRule(r -> true).enable("ability_1");
                resourceRule(r -> false).enable("ability_2");

                ifAllEnabled("ability_1", "ability_2").enable("if_all_enabled");
            }
        }

        var policy = new TestPolicy();
        policy.can(null, "if_all_enabled", "");

        for (var i = 0; i < 10000; i++) {
            var start = System.currentTimeMillis();
            policy.can(null, "if_all_enabled", "");
            var end = System.currentTimeMillis();

            assertThat(end - start).describedAs(String.format("Iteration %s", i)).isCloseTo(1, Offset.offset(1L));
        }
    }
}
