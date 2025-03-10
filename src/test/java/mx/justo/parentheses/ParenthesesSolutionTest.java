package mx.justo.parentheses;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParenthesesSolutionTest {

    @ParameterizedTest(name = "Test #{index} - parentheses: {1}")
    @MethodSource("testCases")
    @DisplayName("execute test")
    void validCases(String s, boolean expected) {
        ParenthesesSolution solution = new ParenthesesSolution();
        assertEquals(expected, solution.execute(s));
    }

    private static Stream<Arguments> testCases() {
        return Stream.of(
                Arguments.of(
                        "()", true
                ),
                Arguments.of("()[]{}", true),
                Arguments.of("([])", true),
                Arguments.of(
                        "(]", false
                ),
                Arguments.of("([)]", false)

        );
    }
}