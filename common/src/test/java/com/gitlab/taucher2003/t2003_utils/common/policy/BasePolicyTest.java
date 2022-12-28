package com.gitlab.taucher2003.t2003_utils.common.policy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

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
    void worksWithResourceRule(String resource, String ability, boolean expected) {
        class TestPolicy extends BasePolicy<String, String, String> {

            @Override
            protected void definePolicies() {
                resourceRule("resource"::equals).enable("resource_ability");

                resourceRule(s -> s.length() > 1).policy(() -> {
                    enable("block_ability");
                    enable("disabled_ability");
                });

                resourceRule("disabled_resource"::equals).disable("disabled_ability");
            }
        }

        var policy = new TestPolicy();

        assertThat(policy.can(null, ability, resource)).isEqualTo(expected);
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
    void worksWithContextRule(String context, String ability, boolean expected) {
        class TestPolicy extends BasePolicy<String, String, String> {

            @Override
            protected void definePolicies() {
                contextRule("context"::equals).enable("resource_ability");

                contextRule(s -> s.length() > 1).policy(() -> {
                    enable("block_ability");
                    enable("disabled_ability");
                });

                contextRule("disabled_context"::equals).disable("disabled_ability");
            }
        }

        var policy = new TestPolicy();

        assertThat(policy.can(context, ability, null)).isEqualTo(expected);
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
    void worksWithRule(String resource, String context, String ability, boolean expected) {
        class TestPolicy extends BasePolicy<String, String, String> {

            @Override
            protected void definePolicies() {
                rule((r, c) -> "resource".equals(r) && "context".equals(c)).enable("resource_ability");

                rule((r, c) -> r.length() > 1 && c.length() > 1).policy(() -> {
                    enable("block_ability");
                    enable("disabled_ability");
                });

                rule((r, c) -> "disabled_resource".equals(r) || "disabled_context".equals(c)).disable("disabled_ability");
            }
        }

        var policy = new TestPolicy();

        assertThat(policy.can(context, ability, resource)).isEqualTo(expected);
    }
}
