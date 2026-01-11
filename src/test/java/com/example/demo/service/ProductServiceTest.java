package com.example.demo.service;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product laptop;
    private Product mouse;
    private Product keyboard;
    private Product inactiveProduct;

    @BeforeEach
    void setUp() {
        laptop = createProduct(1L, "Laptop Pro", new BigDecimal("999.99"), 10, true);
        mouse = createProduct(2L, "Wireless Mouse", new BigDecimal("29.99"), 50, true);
        keyboard = createProduct(3L, "Mechanical Keyboard", new BigDecimal("149.99"), 25, true);
        inactiveProduct = createProduct(4L, "Old Product", new BigDecimal("49.99"), 5, false);
    }

    private Product createProduct(Long id, String name, BigDecimal price, int stock, boolean active) {
        Product product = new Product(name, "Description", "Electronics", price, stock);
        product.setId(id);
        product.setActive(active);
        product.setCreatedDate(LocalDate.now());
        return product;
    }

    @Nested
    @DisplayName("getProductsByCategorySortedByPrice")
    class GetProductsByCategorySortedByPriceTests {

        @Test
        @DisplayName("Should return products sorted by price ascending")
        void shouldReturnProductsSortedByPriceAscending() {
            // given
            when(productRepository.findByCategory("Electronics"))
                    .thenReturn(Arrays.asList(laptop, mouse, keyboard));

            // when
            List<Product> result = productService.getProductsByCategorySortedByPrice("Electronics");

            // then
            assertEquals(3, result.size());
            assertEquals(mouse.getName(), result.get(0).getName());
            assertEquals(keyboard.getName(), result.get(1).getName());
            assertEquals(laptop.getName(), result.get(2).getName());

            verify(productRepository).findByCategory("Electronics");
        }

        @Test
        @DisplayName("Should return empty list when no products in category")
        void shouldReturnEmptyListWhenNoProductsInCategory() {
            // given
            when(productRepository.findByCategory("NonExistent"))
                    .thenReturn(Collections.emptyList());

            // when
            List<Product> result = productService.getProductsByCategorySortedByPrice("NonExistent");

            // then
            assertTrue(result.isEmpty());
            verify(productRepository).findByCategory("NonExistent");
        }
    }

    @Nested
    @DisplayName("getAvailableProductsWithinBudget")
    class GetAvailableProductsWithinBudgetTests {

        @Test
        @DisplayName("Should filter products within budget that are active and in stock")
        void shouldFilterProductsWithinBudgetActiveAndInStock() {
            // given
            when(productRepository.findInStock())
                    .thenReturn(Arrays.asList(laptop, mouse, keyboard, inactiveProduct));

            // when
            List<Product> result = productService.getAvailableProductsWithinBudget(new BigDecimal("200.00"));

            // then
            assertEquals(2, result.size());
            assertEquals(keyboard.getName(), result.get(0).getName());
            assertEquals(mouse.getName(), result.get(1).getName());
            assertFalse(result.stream().anyMatch(p -> p.getName().equals(inactiveProduct.getName())));
        }

        @Test
        @DisplayName("Should return products sorted by price descending within budget")
        void shouldReturnProductsSortedByPriceDescending() {
            // given
            when(productRepository.findInStock())
                    .thenReturn(Arrays.asList(mouse, keyboard));

            // when
            List<Product> result = productService.getAvailableProductsWithinBudget(new BigDecimal("500.00"));

            // then
            assertEquals(2, result.size());
            assertEquals(keyboard.getName(), result.get(0).getName());
            assertEquals(mouse.getName(), result.get(1).getName());
        }

        @Test
        @DisplayName("Should return empty list when no products within budget")
        void shouldReturnEmptyListWhenNoProductsWithinBudget() {
            // given
            when(productRepository.findInStock())
                    .thenReturn(Arrays.asList(laptop, keyboard));

            // when
            List<Product> result = productService.getAvailableProductsWithinBudget(new BigDecimal("10.00"));

            // then
            assertTrue(result.isEmpty());
        }

        @ParameterizedTest(name = "Should throw exception for invalid budget: {0}")
        @MethodSource("com.example.demo.service.ProductServiceTest#invalidBudgets")
        void shouldThrowExceptionForInvalidBudget(BigDecimal budget) {
            // when
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> productService.getAvailableProductsWithinBudget(budget)
            );

            // then
            assertEquals("Budget must be positive", exception.getMessage());
            verifyNoInteractions(productRepository);
        }
    }

    static Stream<BigDecimal> invalidBudgets() {
        return Stream.of(
                null,
                BigDecimal.ZERO,
                new BigDecimal("-10.00")
        );
    }
}
