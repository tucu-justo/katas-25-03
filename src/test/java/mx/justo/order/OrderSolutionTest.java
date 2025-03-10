package mx.justo.order;

import mx.justo.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static mx.justo.order.OrderSolution.EMPTY_NAME;
import static org.junit.jupiter.api.Assertions.*;

class OrderSolutionTest {

    private OrderSolution orderSolution;

    @BeforeEach
    void setUp() {
        orderSolution = new OrderSolution();
    }

    @Test
    @DisplayName("Should return empty string when sales map is null")
    void shouldReturnEmptyStringForNullMap() {
        assertEquals("", orderSolution.mostPopularProduct(null));
    }

    @Test
    @DisplayName("Should return empty string when sales map is empty")
    void shouldReturnEmptyStringForEmptyMap() {
        assertEquals("", orderSolution.mostPopularProduct(Collections.emptyMap()));
    }

    @ParameterizedTest(name = "Test #{index} - Most popular product: {1}")
    @MethodSource("provideSalesData")
    @DisplayName("Should return the name of product with highest sales value")
    void shouldReturnMostPopularProduct(Map<Product, Integer> sales, String expectedProductName) {
        assertEquals(expectedProductName, orderSolution.mostPopularProduct(sales));
    }

    @ParameterizedTest(name = "Test #{index} - Most popular between {1} products")
    @MethodSource("provideSalesDataWithTies")
    @DisplayName("Should handle ties correctly")
    void shouldHandleTiesCorrectly(Map<Product, Integer> sales, int productCount, String expectedProductName) {
        assertEquals(expectedProductName, orderSolution.mostPopularProduct(sales));
    }

    @Test
    @DisplayName("Should handle products with null names")
    void shouldHandleProductsWithNullNames() {
        Map<Product, Integer> sales = Map.of(
                new Product("1", null, "Brand A"), 10
        );

        assertDoesNotThrow(() -> orderSolution.mostPopularProduct(sales));
        assertEquals(EMPTY_NAME, orderSolution.mostPopularProduct(sales));
    }


    private static Stream<Arguments> provideSalesData() {
        return Stream.of(
                // Single product
                Arguments.of(
                        Map.of(new Product("1", "Bread", "Brand A"), 10),
                        "Bread"
                ),

                // Multiple products with clear winner
                Arguments.of(
                        Map.of(
                                new Product("1", "Bread", "Brand A"), 10,
                                new Product("2", "Milk", "Brand B"), 5,
                                new Product("3", "Eggs", "Brand C"), 8
                        ),
                        "Bread"
                ),

                // Multiple products with negative sales numbers
                Arguments.of(
                        Map.of(
                                new Product("1", "Bread", "Brand A"), -5,
                                new Product("2", "Milk", "Brand B"), -1,
                                new Product("3", "Eggs", "Brand C"), -10
                        ),
                        "Milk"
                ),

                // Products with zero sales
                Arguments.of(
                        Map.of(
                                new Product("1", "Bread", "Brand A"), 0,
                                new Product("2", "Milk", "Brand B"), 0,
                                new Product("3", "Eggs", "Brand C"), 0
                        ),
                        "Bread" // First entry will be returned in case of tie
                )
        );
    }


    private static Stream<Arguments> provideSalesDataWithTies() {
        // Case 1: Two products with the same sales
        Map<Product, Integer> twoProductsTie = new LinkedHashMap<>();
        twoProductsTie.put(new Product("1", "Bread", "Brand A"), 10);
        twoProductsTie.put(new Product("2", "Milk", "Brand B"), 10);

        // Case 2: Three products with the same sales
        Map<Product, Integer> threeProductsTie = new LinkedHashMap<>();
        threeProductsTie.put(new Product("1", "Bread", "Brand A"), 10);
        threeProductsTie.put(new Product("2", "Milk", "Brand B"), 10);
        threeProductsTie.put(new Product("3", "Eggs", "Brand C"), 10);

        // Case 3: Mixed sales with tie for highest
        Map<Product, Integer> mixedSalesWithTie = new LinkedHashMap<>();
        mixedSalesWithTie.put(new Product("1", "Bread", "Brand A"), 5);
        mixedSalesWithTie.put(new Product("2", "Milk", "Brand B"), 10);
        mixedSalesWithTie.put(new Product("3", "Eggs", "Brand C"), 10);
        mixedSalesWithTie.put(new Product("4", "Cheese", "Brand D"), 8);

        return Stream.of(
                Arguments.of(twoProductsTie, 2, "Bread"),
                Arguments.of(threeProductsTie, 3, "Bread"),
                Arguments.of(mixedSalesWithTie, 4, "Milk")
        );
    }

    @Test
    @DisplayName("Should handle large number of products")
    void shouldHandleLargeNumberOfProducts() {
        // Create a map with a large number of products
        Map<Product, Integer> largeSalesMap = new HashMap<>();
        int maxSales = 0;
        String expectedProduct = "";


        for (int i = 1; i <= 1000; i++) {
            String id = String.valueOf(i);
            String name = "Product " + i;
            int sales = new Random(i).nextInt(10000); // Deterministic but varied values

            largeSalesMap.put(new Product(id, name, "Brand"), sales);

            if (sales > maxSales) {
                maxSales = sales;
                expectedProduct = name;
            }
        }

        assertEquals(expectedProduct, orderSolution.mostPopularProduct(largeSalesMap));
    }
}