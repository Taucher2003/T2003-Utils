package com.gitlab.taucher2003.t2003_utils.common.policy.execution;

import com.gitlab.taucher2003.t2003_utils.common.policy.definition.Condition;
import com.gitlab.taucher2003.t2003_utils.common.policy.definition.PolicyDefinition;
import com.gitlab.taucher2003.t2003_utils.common.policy.definition.conditions.AbilityCondition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PolicyTest {

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
        class TestPolicy extends PolicyDefinition<String, String, String> {

            @Override
            protected void definePolicies() {
                rule(resourceCondition("resource"::equals)).enable("resource_ability");

                rule(resourceCondition(s -> s.length() > 1)).policy(() -> {
                    enable("block_ability");
                    enable("disabled_ability");
                });

                rule(resourceCondition("disabled_resource"::equals)).prevent("disabled_ability");
            }
        }

        var definition = new TestPolicy();
        var policy = definition.initialize();


        assertThat(policy.forContext(null).can(resource, ability)).describedAs(
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
        class TestPolicy extends PolicyDefinition<String, String, String> {

            @Override
            protected void definePolicies() {
                rule(contextCondition("context"::equals)).enable("resource_ability");

                rule(contextCondition(s -> s.length() > 1)).policy(() -> {
                    enable("block_ability");
                    enable("disabled_ability");
                });

                rule(contextCondition("disabled_context"::equals)).prevent("disabled_ability");
            }
        }

        var definition = new TestPolicy();
        var policy = definition.initialize();

        assertThat(policy.forContext(context).can(null, ability)).describedAs(
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
        class TestPolicy extends PolicyDefinition<String, String, String> {

            @Override
            protected void definePolicies() {
                rule(condition((r, c) -> "resource".equals(r) && "context".equals(c))).enable("resource_ability");

                rule(condition((r, c) -> r.length() > 1 && c.length() > 1)).policy(() -> {
                    enable("block_ability");
                    enable("disabled_ability");
                });

                rule(condition((r, c) -> "disabled_resource".equals(r) || "disabled_context".equals(c))).prevent("disabled_ability");
            }
        }

        var definition = new TestPolicy();
        var policy = definition.initialize();

        assertThat(policy.forContext(context).can(resource, ability)).describedAs(
                String.format("'%s' has ability '%s' on '%s': %s", context, ability, resource, expected)
        ).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "ability_1,true",
            "ability_2,false"
    })
    void ifEnabled(String requiredAbility, boolean expected) {
        class TestPolicy extends PolicyDefinition<String, String, String> {

            @Override
            protected void definePolicies() {
                rule(resourceCondition(r -> true)).enable("ability_1");

                rule(ifEnabled(requiredAbility)).enable("if_enabled");
            }
        }

        var definition = new TestPolicy();
        var policy = definition.initialize();
        assertThat(policy.forContext("context").can("resource", "if_enabled")).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "false,false,false",
            "true,false,true",
            "false,true,true",
            "true,true,true"
    })
    void ifAnyEnabled(boolean enableFirst, boolean enableSecond, boolean expected) {
        class TestPolicy extends PolicyDefinition<String, String, String> {

            @Override
            protected void definePolicies() {
                rule(resourceCondition(r -> enableFirst)).enable("ability_1");
                rule(resourceCondition(r -> enableSecond)).enable("ability_2");

                rule(ifAnyEnabled("ability_1", "ability_2")).enable("if_any_enabled");
            }
        }

        var definition = new TestPolicy();
        var policy = definition.initialize();
        assertThat(policy.forContext("context").can("resource", "if_any_enabled")).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "false,false,false,false,false",
            "false,false,false,true,false",
            "false,false,true,false,false",
            "false,false,true,true,false",
            "false,true,false,false,false",
            "false,true,false,true,false",
            "false,true,true,false,false",
            "false,true,true,true,false",
            "true,false,false,false,false",
            "true,false,false,true,false",
            "true,false,true,true,false",
            "true,true,false,false,true",
            "true,true,false,true,true",
            "true,true,true,false,false",
            "true,true,true,true,false"
    })
    void ifAllEnabled(boolean enableFirst, boolean enableSecond, boolean preventSecond, boolean enableThird, boolean expected) {
        class TestPolicy extends PolicyDefinition<String, String, String> {

            @Override
            protected void definePolicies() {
                rule(resourceCondition(r -> enableFirst)).enable("ability_1");
                rule(resourceCondition(r -> enableSecond)).enable("ability_2");
                rule(resourceCondition(r -> preventSecond)).prevent("ability_2");
                rule(resourceCondition(r -> enableThird)).prevent("unrelated");

                rule(ifAllEnabled("ability_1", "ability_2")).enable("if_all_enabled");
            }
        }

        var definition = new TestPolicy();
        var policy = definition.initialize();
        assertThat(policy.forContext("context").can("resource", "if_all_enabled")).isEqualTo(expected);
    }

    @Test
    void ifAnyAllEnabledWithSingleAbility() {
        class TestPolicy extends PolicyDefinition<String, String, String> {

            @Override
            protected void definePolicies() {
                rule(condition((__, ___) -> true)).enable("ability_1");

                rule(ifAnyEnabled("ability_1")).enable("if_any_enabled");
                rule(ifAllEnabled("ability_1")).enable("if_all_enabled");
            }
        }

        var definition = new TestPolicy();
        var policy = definition.initialize();
        assertThat(policy.forContext("context").can("resource", "if_any_enabled")).isTrue();
        assertThat(policy.forContext("context").can("resource", "if_all_enabled")).isTrue();
    }

    @Test
    void forClass() {
        class TestPolicy extends PolicyDefinition<CharSequence, String, String> {

            @Override
            protected void definePolicies() {
                rule(resourceCondition(r -> r.charAt(0) == 't')).enable("resourceRule");
                rule(forClass(String.class).resourceCondition("test"::equalsIgnoreCase)).enable("forClassResourceRule");
                rule(forClass(String.class).condition((r, c) -> "test".equalsIgnoreCase(r) && "context".equals(c))).enable("forClassRule");
            }
        }

        var definition = new TestPolicy();
        var policy = definition.initialize();
        var evaluationContext = policy.forContext("context");
        assertThat(evaluationContext.can("test", "resourceRule")).isTrue();
        assertThat(evaluationContext.can("test", "forClassResourceRule")).isTrue();
        assertThat(evaluationContext.can("testing", "forClassResourceRule")).isFalse();
        assertThat(evaluationContext.can(new StringBuilder("test"), "resourceRule")).isTrue();
        assertThat(evaluationContext.can(new StringBuilder("test"), "forClassResourceRule")).isFalse();
        assertThat(evaluationContext.can("test", "forClassRule")).isTrue();
        assertThat(evaluationContext.can("testing", "forClassRule")).isFalse();
        assertThat(policy.forContext("Context").can("test", "forClassRule")).isFalse();
        assertThat(evaluationContext.can(new StringBuilder("test"), "forClassRule")).isFalse();
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
        class TestPolicy extends PolicyDefinition<String, String, String> {

            @Override
            protected void definePolicies() {
                rule(condition((r, c) -> "resource".equals(r) && "context".equals(c))).enable("resource_ability");

                rule(condition((r, c) -> r.length() > 1 && c.length() > 1)).policy(() -> {
                    enable("block_ability");
                    enable("disabled_ability");
                });

                rule(condition((r, c) -> "disabled_resource".equals(r) || "disabled_context".equals(c))).prevent("disabled_ability");
            }
        }

        var definition = new TestPolicy();
        var policy = definition.initialize();
        var expectedAbilitiesArr = Optional.ofNullable(expectedAbilities)
                .map(str -> str.split("\\|"))
                .orElseGet(() -> new String[0]);

        assertThat(policy.forContext(context).getEnabledAbilities(resource)).containsExactlyInAnyOrder(expectedAbilitiesArr);
    }

    @ParameterizedTest
    @CsvSource({
            ",,",
            "context,,contextRule|contextRuleNull",
            ",resource,resourceRule|resourceRuleNull",
            "context,resource,contextRule|resourceRule|rule|contextRuleNull|resourceRuleNull|ruleNull"
    })
    void evaluationsWithNulls(String context, String resource, String expectedAbilities) {
        class TestPolicy extends PolicyDefinition<String, String, String> {

            @SuppressWarnings("LiteralAsArgToStringEquals")
            @Override
            protected void definePolicies() {
                rule(resourceCondition("resource"::equals)).enable("resourceRule");
                rule(contextCondition("context"::equals)).enable("contextRule");
                rule(condition((r, c) -> "resource".equals(r) && "context".equals(c))).enable("rule");

                // these rules will fail when used with null
                rule(resourceCondition(r -> r.equals("resource"))).enable("resourceRuleNull");
                rule(contextCondition(c -> c.equals("context"))).enable("contextRuleNull");
                rule(condition((r, c) -> r.equals("resource") && c.equals("context"))).enable("ruleNull");
            }
        }

        var definition = new TestPolicy();
        var policy = definition.initialize();
        var expectedAbilitiesArr = Optional.ofNullable(expectedAbilities)
                .map(str -> str.split("\\|"))
                .orElseGet(() -> new String[0]);

        assertThat(policy.forContext(context).getEnabledAbilities(resource)).containsExactlyInAnyOrder(expectedAbilitiesArr);
    }

    @Test
    void shortCircuitsForEnable() {
        var condition1 = (Condition<String, String, String>) mock(Condition.class);
        var condition2 = (Condition<String, String, String>) mock(Condition.class);
        when(condition1.test(eq(""), eq(""), any(EvaluationContext.class))).thenReturn(true);
        when(condition1.score(any(EvaluationContext.class))).thenReturn(0D);

        class TestPolicy extends PolicyDefinition<String, String, String> {

            @Override
            protected void definePolicies() {
                rule(condition1).enable("ability");
                rule(condition2).enable("ability");
            }
        }

        var definition = new TestPolicy();
        var policy = definition.initialize();

        policy.forContext("").can("", "ability");

        verify(condition1).test(eq(""), eq(""), any(EvaluationContext.class));
        verify(condition2, times(0)).test(any(), any(), any());
    }

    @Test
    void shortCircuitsIfEnabledWithPrevents() {
        var condition1 = (Condition<String, String, String>) mock(Condition.class);
        var condition2 = (Condition<String, String, String>) mock(Condition.class);
        var condition3 = (Condition<String, String, String>) mock(Condition.class);
        var ifEnabledCondition = new AbilityCondition<String, String, String>("ability");
        var abilitySpy = spy(ifEnabledCondition);
        when(condition2.test(eq(""), eq(""), any(EvaluationContext.class))).thenReturn(true);

        class TestPolicy extends PolicyDefinition<String, String, String> {

            @Override
            protected void definePolicies() {
                rule(condition1).enable("ability");
                rule(condition2).enable("ability");
                rule(condition3).enable("ability");

                rule(ifEnabledCondition).enable("ability_2");
            }
        }

        var definition = new TestPolicy();
        var policy = definition.initialize();
        var evaluationContext = policy.forContext("");

        assertThat(evaluationContext.can("", "ability_2")).isTrue();

        verify(condition2).test(eq(""), eq(""), any(EvaluationContext.class));
        verify(condition3, times(0)).test(any(), any(), any());
        verify(abilitySpy, times((0))).test(any(), any(), any());
    }

    @Test
    void executesLowerScoreFirst() {
        var condition1 = (Condition<String, String, String>) mock(Condition.class);
        var condition2 = (Condition<String, String, String>) mock(Condition.class);
        when(condition1.score(any(EvaluationContext.class))).thenReturn(1D);
        when(condition2.score(any(EvaluationContext.class))).thenReturn(0D);

        class TestPolicy extends PolicyDefinition<String, String, String> {

            @Override
            protected void definePolicies() {
                rule(condition1).enable("ability");
                rule(condition2).enable("ability");
            }
        }

        var definition = new TestPolicy();
        var policy = definition.initialize();

        assertThat(policy.forContext("").can("", "ability")).isFalse();

        var inOrder = Mockito.inOrder(condition2, condition1);
        inOrder.verify(condition2).test(eq(""), eq(""), any(EvaluationContext.class));
        inOrder.verify(condition1).test(eq(""), eq(""), any(EvaluationContext.class));
    }

    @Test
    void evaluatesConditionsOnlyOnce() {
        var condition = (Condition<String, String, String>) mock(Condition.class);

        class TestPolicy extends PolicyDefinition<String, String, String> {

            @Override
            protected void definePolicies() {
                rule(condition).enable("ability");
            }
        }

        var definition = new TestPolicy();
        var policy = definition.initialize();

        var evaluationContext = policy.forContext("");
        evaluationContext.can("", "ability");
        evaluationContext.can("", "ability");

        verify(condition, times(1)).test(eq(""), eq(""), any(EvaluationContext.class));
    }

    @Test
    void evaluatesRulesInEfficientOrder() {
        var condition1 = (Condition<String, String, String>) mock(Condition.class);
        var condition2 = (Condition<String, String, String>) mock(Condition.class);
        var condition3 = (Condition<String, String, String>) mock(Condition.class);
        var condition4 = (Condition<String, String, String>) mock(Condition.class);
        when(condition1.score(any(EvaluationContext.class))).thenReturn(2D);
        when(condition2.score(any(EvaluationContext.class))).thenReturn(1.1D);
        when(condition3.score(any(EvaluationContext.class))).thenReturn(1D);
        when(condition4.score(any(EvaluationContext.class))).thenReturn(0D);

        class TestPolicy extends PolicyDefinition<String, String, String> {

            @Override
            protected void definePolicies() {
                rule(condition1).enable("ability");
                rule(condition2).prevent("ability");
                rule(condition3).enable("ability");
                rule(condition4).enable("ability");
            }
        }

        var definition = new TestPolicy();
        var policy = definition.initialize();
        var evaluationContext = policy.forContext("");
        evaluationContext.can("", "ability");

        var inOrder = Mockito.inOrder(
                condition4,
                condition2, // it has a higher score than 3, but as a prevent rule it can short-circuit the evaluation
                condition3,
                condition1
        );
        inOrder.verify(condition4).test(eq(""), eq(""), any(EvaluationContext.class));
        inOrder.verify(condition2).test(eq(""), eq(""), any(EvaluationContext.class));
        inOrder.verify(condition3).test(eq(""), eq(""), any(EvaluationContext.class));
        inOrder.verify(condition1).test(eq(""), eq(""), any(EvaluationContext.class));
    }

    @Test
    void evaluatesRulesWithPolicyBlocksInEfficientOrder() {
        var condition1 = (Condition<String, String, String>) mock(Condition.class);
        var condition2 = (Condition<String, String, String>) mock(Condition.class);
        var condition3 = (Condition<String, String, String>) mock(Condition.class);
        var condition4 = (Condition<String, String, String>) mock(Condition.class);
        when(condition1.score(any(EvaluationContext.class))).thenReturn(2D);
        when(condition2.score(any(EvaluationContext.class))).thenReturn(1.1D);
        when(condition3.score(any(EvaluationContext.class))).thenReturn(1D);
        when(condition4.score(any(EvaluationContext.class))).thenReturn(0D);

        class TestPolicy extends PolicyDefinition<String, String, String> {

            @Override
            protected void definePolicies() {
                rule(condition1).policy(() -> enable("ability"));
                rule(condition2).policy(() -> prevent("ability"));
                rule(condition3).policy(() -> enable("ability"));
                rule(condition4).policy(() -> enable("ability"));
            }
        }

        var definition = new TestPolicy();
        var policy = definition.initialize();
        var evaluationContext = policy.forContext("");
        evaluationContext.can("", "ability");

        var inOrder = Mockito.inOrder(
                condition4,
                condition2, // it has a higher score than 3, but as a prevent rule it can short-circuit the evaluation
                condition3,
                condition1
        );
        inOrder.verify(condition4).test(eq(""), eq(""), any(EvaluationContext.class));
        inOrder.verify(condition2).test(eq(""), eq(""), any(EvaluationContext.class));
        inOrder.verify(condition3).test(eq(""), eq(""), any(EvaluationContext.class));
        inOrder.verify(condition1).test(eq(""), eq(""), any(EvaluationContext.class));
    }

    @Test
    void stopsAtFirstEnableConditionForBigPolicies() {
        var iterationsUntilTrue = 40;

        @SuppressWarnings("unchecked")
        var conditions = (Condition<String, String, String>[]) IntStream.range(0, 52).mapToObj(iteration -> {
            var condition = (Condition<String, String, String>) mock(Condition.class);
            when(condition.score(any())).thenReturn(Double.valueOf(iteration));
            when(condition.test(eq(""), eq(""), any(EvaluationContext.class))).thenReturn(iteration >= iterationsUntilTrue);
            return condition;
        }).toArray(Condition[]::new);

        var inOrder = Mockito.inOrder((Object[]) conditions);
        var conditionList = Arrays.asList(Arrays.copyOf(conditions, conditions.length));
        Collections.shuffle(conditionList);

        class TestPolicy extends PolicyDefinition<String, String, String> {

            @Override
            protected void definePolicies() {
                for (var condition : conditionList) {
                    rule(condition).enable("ability");
                }
            }
        }

        var definition = new TestPolicy();
        var policy = definition.initialize();
        var evaluationContext = policy.forContext("");
        evaluationContext.can("", "ability");

        for (var i = 0; i <= iterationsUntilTrue; i++) {
            var condition = conditions[i];
            inOrder.verify(condition).test(eq(""), eq(""), any(EvaluationContext.class));
        }
        for (var i = iterationsUntilTrue + 1; i < conditions.length; i++) {
            var condition = conditions[i];
            verify(condition, times(0)).test(any(), any(), any());
        }
    }

    @Test
    void evaluatesOnlyNecessaryPolicies() {
        var rule1 = (Condition<String, String, String>) mock(Condition.class);
        var rule2 = (Condition<String, String, String>) mock(Condition.class);

        class TestPolicy extends PolicyDefinition<String, String, String> {

            @Override
            protected void definePolicies() {
                rule(rule1).policy(() -> {
                    enable("rule1");
                });

                rule(rule2).policy(() -> {
                    enable("rule2");
                });
            }
        }

        var definition = new TestPolicy();
        var policy = definition.initialize();

        policy.forContext("").can(null, "rule1");

        verify(rule1).test(isNull(), eq(""), any(EvaluationContext.class));
        verifyNoInteractions(rule2);
    }

    @Test
    void evaluatesAllDependentPolicies() {
        var rule1 = (Condition<String, String, String>) mock(Condition.class);
        var rule2 = (Condition<String, String, String>) mock(Condition.class);

        class TestPolicy extends PolicyDefinition<String, String, String> {

            @Override
            protected void definePolicies() {
                rule(rule1).policy(() -> {
                    enable("rule1");
                });

                rule(rule2).policy(() -> {
                    enable("rule2");
                });

                rule(ifEnabled("rule1")).policy(() -> enable("rule3"));
            }
        }

        var definition = new TestPolicy();
        var policy = definition.initialize();

        policy.forContext("").can(null, "rule3");

        verify(rule1).test(isNull(), eq(""), any(EvaluationContext.class));
        verifyNoInteractions(rule2);
    }
}
