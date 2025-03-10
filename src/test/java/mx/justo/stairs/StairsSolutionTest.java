package mx.justo.stairs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StairsSolutionTest {

    private final StairsSolution solution = new StairsSolution();

    @DisplayName("Testing climbStairs with various step counts")
    @ParameterizedTest(name = "Test #{index}: n={0}, expected={1}")
    @MethodSource("provideTestCases")
    void climbStairsTest(int steps, int expectedWays) {
        assertEquals(expectedWays, solution.climbStairs(steps));
    }

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                // Base cases
                Arguments.of(1, 1),  // 1 way to climb 1 stair: [1]
                Arguments.of(2, 2),  // 2 ways to climb 2 stairs: [1,1], [2]
                Arguments.of(3, 3),  // 3 ways to climb 3 stairs: [1,1,1], [1,2], [2,1]

                // Additional test cases
                Arguments.of(4, 5),  // 5 ways: [1,1,1,1], [1,1,2], [1,2,1], [2,1,1], [2,2]
                Arguments.of(5, 8),  // 8 ways
                Arguments.of(6, 13), // 13 ways

                // Edge cases
                Arguments.of(0, 1),  // Base case handling

                // Larger case to verify performance
                Arguments.of(10, 89) // 89 ways
        );
    }
}