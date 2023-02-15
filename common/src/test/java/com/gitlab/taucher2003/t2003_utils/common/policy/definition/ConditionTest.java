package com.gitlab.taucher2003.t2003_utils.common.policy.definition;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ConditionTest {

    @ParameterizedTest
    @CsvSource({
            "false,false",
            "false,true",
            "true,false",
            "true,true"
    })
    void and(boolean first, boolean second) {
        var firstCondition = createStatic(first);
        var secondCondition = createStatic(second);

        var condition = firstCondition.and(secondCondition);

        assertThat(condition.test(null, null, null)).isEqualTo(first && second);
    }

    @ParameterizedTest
    @MethodSource("fourArguments")
    void and(boolean first, boolean second, boolean third, boolean fourth) {
        var firstCondition = createStatic(first);
        var secondCondition = createStatic(second);
        var thirdCondition = createStatic(third);
        var fourthCondition = createStatic(fourth);

        var condition = firstCondition.and(secondCondition).and(thirdCondition).and(fourthCondition);

        assertThat(condition.test(null, null, null)).isEqualTo(first && second && third && fourth);
    }

    @ParameterizedTest
    @MethodSource("fourArguments")
    void andNested(boolean first, boolean second, boolean third, boolean fourth) {
        var firstCondition = createStatic(first);
        var secondCondition = createStatic(second);
        var thirdCondition = createStatic(third);
        var fourthCondition = createStatic(fourth);

        var condition = firstCondition.and(secondCondition.and(thirdCondition).and(fourthCondition));

        assertThat(condition.test(null, null, null)).isEqualTo(first && second && third && fourth);
    }

    @ParameterizedTest
    @CsvSource({
            "false,false",
            "false,true",
            "true,false",
            "true,true"
    })
    void or(boolean first, boolean second) {
        var firstCondition = createStatic(first);
        var secondCondition = createStatic(second);

        var condition = firstCondition.or(secondCondition);

        assertThat(condition.test(null, null, null)).isEqualTo(first || second);
    }

    @ParameterizedTest
    @MethodSource("fourArguments")
    void or(boolean first, boolean second, boolean third, boolean fourth) {
        var firstCondition = createStatic(first);
        var secondCondition = createStatic(second);
        var thirdCondition = createStatic(third);
        var fourthCondition = createStatic(fourth);

        var condition = firstCondition.or(secondCondition).or(thirdCondition).or(fourthCondition);

        assertThat(condition.test(null, null, null)).isEqualTo(first || second || third || fourth);
    }

    @ParameterizedTest
    @MethodSource("fourArguments")
    void orNested(boolean first, boolean second, boolean third, boolean fourth) {
        var firstCondition = createStatic(first);
        var secondCondition = createStatic(second);
        var thirdCondition = createStatic(third);
        var fourthCondition = createStatic(fourth);

        var condition = firstCondition.or(secondCondition.or(thirdCondition).or(fourthCondition));

        assertThat(condition.test(null, null, null)).isEqualTo(first || second || third || fourth);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void not(boolean value) {
        var condition = createStatic(value);

        assertThat(condition.not().test(null, null, null)).isEqualTo(!value);
    }

    @ParameterizedTest
    @CsvSource({
            "false,false,false,false,false",
            "false,false,false,false,true",
            "false,false,false,true,false",
            "false,false,false,true,true",
            "false,false,true,false,false",
            "false,false,true,false,true",
            "false,false,true,true,false",
            "false,false,true,true,true",
            "false,true,false,false,false",
            "false,true,false,false,true",
            "false,true,false,true,false",
            "false,true,false,true,true",
            "false,true,true,false,false",
            "false,true,true,false,true",
            "false,true,true,true,false",
            "false,true,true,true,true",

            "true,false,false,false,false",
            "true,false,false,false,true",
            "true,false,false,true,false",
            "true,false,false,true,true",
            "true,false,true,false,false",
            "true,false,true,false,true",
            "true,false,true,true,false",
            "true,false,true,true,true",
            "true,true,false,false,false",
            "true,true,false,false,true",
            "true,true,false,true,false",
            "true,true,false,true,true",
            "true,true,true,false,false",
            "true,true,true,false,true",
            "true,true,true,true,false",
            "true,true,true,true,true"
    })
    void complex(boolean first, boolean second, boolean third, boolean fourth, boolean fifth) {
        var firstCondition = createStatic(first);
        var secondCondition = createStatic(second);
        var thirdCondition = createStatic(third);
        var fourthCondition = createStatic(fourth);
        var fifthCondition = createStatic(fifth);

//        !(first && !((second || third) && !fourth) || fifth)
        var condition = firstCondition.and(
                secondCondition.or(thirdCondition).and(
                        fourthCondition.not()
                ).not()
        ).or(fifthCondition).not();

        assertThat(condition.test(null, null, null)).isEqualTo(!(first && !((second || third) && !fourth) || fifth));
    }

    Condition<Object, Object, Object> createStatic(boolean result) {
        return new Conditions.Simple<>((__, ___) -> result, 0, false, false);
    }

    static Stream<Arguments> fourArguments() {
        return Stream.of(
                Arguments.of(false, false, false, false),
                Arguments.of(false, false, false, true),
                Arguments.of(false, false, true, false),
                Arguments.of(false, false, true, true),
                Arguments.of(false, true, false, false),
                Arguments.of(false, true, false, true),
                Arguments.of(false, true, true, false),
                Arguments.of(false, true, true, true),
                Arguments.of(true, false, false, false),
                Arguments.of(true, false, false, true),
                Arguments.of(true, false, true, false),
                Arguments.of(true, false, true, true),
                Arguments.of(true, true, false, false),
                Arguments.of(true, true, false, true),
                Arguments.of(true, true, true, false),
                Arguments.of(true, true, true, true)
        );
    }
}
