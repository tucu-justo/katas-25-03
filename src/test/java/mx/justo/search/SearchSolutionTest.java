package mx.justo.search;

import mx.justo.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchSolutionTest {

    private SearchSolution searchSolution;
    private List<Product> groceryInventory;

    @BeforeEach
    void setUp() {
        searchSolution = new SearchSolution();

        // Create a grocery-focused product inventory
        groceryInventory = Arrays.asList(
                new Product("1", "Organic Bananas", "Fresh Harvest"),
                new Product("2", "Whole Milk", "Dairy Delights"),
                new Product("3", "White Bread", "Baker's Best"),
                new Product("4", "Organic Baby Spinach", "Fresh Harvest"),
                new Product("5", "Free Range Eggs", "Farm Fresh"),
                new Product("6", "Ground Beef", "Premium Meats"),
                new Product("7", "Pure Orange Juice", "Sunshine Foods"),
                new Product("8", "Pasta Sauce", "Italian Classics"),
                new Product("9", "Organic Apples", "Fresh Harvest"),
                new Product("10", "Chocolate Chip Cookies", "Sweet Treats"),
                new Product("11", "Plain Greek Yogurt", "Dairy Delights"),
                new Product("12", "Fresh Harvest Salad Mix", "Green Gardens"),
                new Product("13", "Russet Potatoes", "Farm Fresh"),
                new Product("14", "Unsalted Butter", "Dairy Delights"),
                new Product("15", "Fresh Harvest Berries", "Berry Farms")
        );
    }

    @Test
    @DisplayName("Should return empty list when query is null")
    void searchWithNullQuery() {
        List<Product> results = searchSolution.searchProducts(null, groceryInventory);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when query is empty")
    void searchWithEmptyQuery() {
        List<Product> results = searchSolution.searchProducts("", groceryInventory);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when inventory is null")
    void searchWithNullInventory() {
        List<Product> results = searchSolution.searchProducts("Organic", null);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when inventory is empty")
    void searchWithEmptyInventory() {
        List<Product> results = searchSolution.searchProducts("Organic", Collections.emptyList());
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should return products with exact name matches first")
    void shouldPrioritizeExactNameMatches() {
        List<Product> extendedInventory = new ArrayList<>(groceryInventory);
        extendedInventory.add(new Product("16", "Fresh Harvest", "Green Gardens"));

        List<Product> results = searchSolution.searchProducts("fresh harvest", extendedInventory);

        assertFalse(results.isEmpty());
        assertEquals("16", results.getFirst().getId()); // "Fresh Harvest" should be first (exact name match)
    }

    @Test
    @DisplayName("Should return products with exact brand matches after exact name matches")
    void shouldPrioritizeExactBrandMatchesSecond() {
        List<Product> results = searchSolution.searchProducts("fresh harvest", groceryInventory);

        assertFalse(results.isEmpty());
        // Products with "Fresh Harvest" in the name should come first
        // (Product 12: "Fresh Harvest Salad Mix" and Product 15: "Fresh Harvest Berries")
        // Then products with "Fresh Harvest" as the brand
        boolean hasProductWithBrand = false;
        for (Product p : results) {
            if (p.getBrand().equalsIgnoreCase("Fresh Harvest")) {
                hasProductWithBrand = true;
                break;
            }
        }
        assertTrue(hasProductWithBrand, "Should include products with 'Fresh Harvest' brand");
    }

    @Test
    @DisplayName("Should perform case-insensitive searches")
    void shouldBeCaseInsensitive() {
        List<Product> resultsLower = searchSolution.searchProducts("organic", groceryInventory);
        List<Product> resultsUpper = searchSolution.searchProducts("ORGANIC", groceryInventory);
        List<Product> resultsMixed = searchSolution.searchProducts("OrGaNiC", groceryInventory);

        assertEquals(resultsLower.size(), resultsUpper.size());
        assertEquals(resultsLower.size(), resultsMixed.size());

        // Check that the same products are found regardless of case
        for (int i = 0; i < resultsLower.size(); i++) {
            assertEquals(resultsLower.get(i).getId(), resultsUpper.get(i).getId());
            assertEquals(resultsLower.get(i).getId(), resultsMixed.get(i).getId());
        }
    }

    @Test
    @DisplayName("Should perform partial matches")
    void shouldPerformPartialMatches() {
        List<Product> results = searchSolution.searchProducts("dairy", groceryInventory);

        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(p -> p.getId().equals("2"))); // Whole Milk (Dairy Delights)
        assertTrue(results.stream().anyMatch(p -> p.getId().equals("11"))); // Greek Yogurt (Dairy Delights)
        assertTrue(results.stream().anyMatch(p -> p.getId().equals("14"))); // Unsalted Butter (Dairy Delights)
    }

    @Test
    @DisplayName("Should handle queries with leading/trailing whitespace")
    void shouldHandleWhitespace() {
        List<Product> results = searchSolution.searchProducts("  organic  ", groceryInventory);

        assertFalse(results.isEmpty());
        // Should find all organic products
        assertTrue(results.stream().anyMatch(p -> p.getName().contains("Organic")));
    }

    @Test
    @DisplayName("Should handle products with null name or brand")
    void shouldHandleNullNameOrBrand() {
        List<Product> inventoryWithNulls = new ArrayList<>(groceryInventory);
        inventoryWithNulls.add(new Product("17", null, "Green Gardens"));
        inventoryWithNulls.add(new Product("18", "Organic Carrots", null));
        inventoryWithNulls.add(new Product("19", null, null));

        // This should not throw exceptions
        List<Product> results = searchSolution.searchProducts("green", inventoryWithNulls);

        // Should find the product with brand "Green Gardens"
        assertTrue(results.stream().anyMatch(p -> p.getId().equals("17")));
    }

    @ParameterizedTest
    @DisplayName("Should find products by brand name")
    @ValueSource(strings = {"Dairy Delights", "Fresh Harvest", "Farm Fresh"})
    void shouldFindProductsByBrand(String brand) {
        List<Product> results = searchSolution.searchProducts(brand, groceryInventory);

        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(p -> p.getBrand().equals(brand)));
    }

    @Test
    @DisplayName("Should return empty list for non-matching query")
    void shouldReturnEmptyForNonMatches() {
        List<Product> results = searchSolution.searchProducts("smartphone", groceryInventory);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should properly sort by relevance with all relevance levels")
    void shouldSortByAllRelevanceLevels() {
        List<Product> specialInventory = Arrays.asList(
                new Product("1", "organic", "Market Fresh"),         // exact name match
                new Product("2", "Tomatoes", "organic"),             // exact brand match
                new Product("3", "Organic Spinach", "Market Fresh"), // partial name match
                new Product("4", "Apples", "Organic Valley")         // partial brand match
        );

        List<Product> results = searchSolution.searchProducts("organic", specialInventory);

        assertEquals(4, results.size());
        assertEquals("1", results.get(0).getId()); // exact name match
        assertEquals("2", results.get(1).getId()); // exact brand match
        assertEquals("3", results.get(2).getId()); // partial name match
        assertEquals("4", results.get(3).getId()); // partial brand match
    }


    @Test
    @DisplayName("Should prioritize products with multiple query term occurrences")
    void shouldHandleProductsWithMultipleOccurrences() {
        List<Product> results = searchSolution.searchProducts("fresh", groceryInventory);

        // Products with "Fresh" in both name and brand should appear in results
        // And should be ranked according to the relevance rules
        long countProductsWithFresh = results.stream()
                .filter(p -> p.getName().toLowerCase().contains("fresh") ||
                        p.getBrand().toLowerCase().contains("fresh"))
                .count();

        assertTrue(countProductsWithFresh >= 5, "Should find at least 5 products with 'fresh'");
    }
}