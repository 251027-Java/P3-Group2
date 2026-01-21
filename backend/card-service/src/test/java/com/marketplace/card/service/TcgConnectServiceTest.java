package com.marketplace.card.service;

import static org.junit.jupiter.api.Assertions.*;

import com.marketplace.card.dto.TcgProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * Unit tests for TcgConnectService.
 * Focuses on the isCard() logic for filtering single cards from sealed products.
 */
class TcgConnectServiceTest {

    private TcgConnectService tcgConnectService;

    @BeforeEach
    void setUp() {
        tcgConnectService = new TcgConnectService();
    }

    @Nested
    @DisplayName("isCard() Tests - Extended Data Heuristics")
    class ExtendedDataHeuristicTests {

        @Test
        @DisplayName("Should return true for product with 'Number' in extendedData")
        void isCard_ProductWithNumberInExtendedData_ReturnsTrue() {
            // Arrange
            TcgProductDto product = new TcgProductDto();
            product.setName("Pikachu");
            TcgProductDto.ExtendedDataDto extendedData = new TcgProductDto.ExtendedDataDto();
            extendedData.setName("Number");
            extendedData.setValue("25");
            product.setExtendedData(Arrays.asList(extendedData));

            // Act
            boolean result = tcgConnectService.isCard(product);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return true for product with 'NUMBER' in extendedData (case insensitive)")
        void isCard_ProductWithNumberUpperCase_ReturnsTrue() {
            // Arrange
            TcgProductDto product = new TcgProductDto();
            product.setName("Charizard");
            TcgProductDto.ExtendedDataDto extendedData = new TcgProductDto.ExtendedDataDto();
            extendedData.setName("NUMBER");
            extendedData.setValue("4");
            product.setExtendedData(Arrays.asList(extendedData));

            // Act
            boolean result = tcgConnectService.isCard(product);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("Should check other extended data fields if 'Number' not present")
        void isCard_ProductWithOtherExtendedData_DefaultsToNameCheck() {
            // Arrange
            TcgProductDto product = new TcgProductDto();
            product.setName("Mewtwo");
            TcgProductDto.ExtendedDataDto extendedData = new TcgProductDto.ExtendedDataDto();
            extendedData.setName("Rarity");
            extendedData.setValue("Rare");
            product.setExtendedData(Arrays.asList(extendedData));

            // Act
            boolean result = tcgConnectService.isCard(product);

            // Assert - should default to true since no exclusion keywords
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("isCard() Tests - Name Blacklist")
    class NameBlacklistTests {

        @Test
        @DisplayName("Should return false for product with 'booster' in name")
        void isCard_BoosterProduct_ReturnsFalse() {
            // Arrange
            TcgProductDto product = new TcgProductDto();
            product.setName("Scarlet & Violet Booster Box");

            // Act
            boolean result = tcgConnectService.isCard(product);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for product with 'box' in name")
        void isCard_BoxProduct_ReturnsFalse() {
            // Arrange
            TcgProductDto product = new TcgProductDto();
            product.setName("Elite Trainer Box");

            // Act
            boolean result = tcgConnectService.isCard(product);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for product with 'pack' in name")
        void isCard_PackProduct_ReturnsFalse() {
            // Arrange
            TcgProductDto product = new TcgProductDto();
            product.setName("Booster Pack");

            // Act
            boolean result = tcgConnectService.isCard(product);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for product with 'tin' in name")
        void isCard_TinProduct_ReturnsFalse() {
            // Arrange
            TcgProductDto product = new TcgProductDto();
            product.setName("Collector's Tin");

            // Act
            boolean result = tcgConnectService.isCard(product);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for product with 'case' in name")
        void isCard_CaseProduct_ReturnsFalse() {
            // Arrange
            TcgProductDto product = new TcgProductDto();
            product.setName("Display Case");

            // Act
            boolean result = tcgConnectService.isCard(product);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for product with 'collection' in name")
        void isCard_CollectionProduct_ReturnsFalse() {
            // Arrange
            TcgProductDto product = new TcgProductDto();
            product.setName("Premium Collection");

            // Act
            boolean result = tcgConnectService.isCard(product);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for product with 'deck' in name")
        void isCard_DeckProduct_ReturnsFalse() {
            // Arrange
            TcgProductDto product = new TcgProductDto();
            product.setName("Battle Deck");

            // Act
            boolean result = tcgConnectService.isCard(product);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for product with 'bundle' in name")
        void isCard_BundleProduct_ReturnsFalse() {
            // Arrange
            TcgProductDto product = new TcgProductDto();
            product.setName("Holiday Bundle");

            // Act
            boolean result = tcgConnectService.isCard(product);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for product with 'display' in name")
        void isCard_DisplayProduct_ReturnsFalse() {
            // Arrange
            TcgProductDto product = new TcgProductDto();
            product.setName("Counter Display");

            // Act
            boolean result = tcgConnectService.isCard(product);

            // Assert
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("isCard() Tests - Default Behavior")
    class DefaultBehaviorTests {

        @Test
        @DisplayName("Should return true for simple card name without exclusion keywords")
        void isCard_SimpleCardName_ReturnsTrue() {
            // Arrange
            TcgProductDto product = new TcgProductDto();
            product.setName("Pikachu");

            // Act
            boolean result = tcgConnectService.isCard(product);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return true for card name with no extendedData")
        void isCard_NullExtendedData_ReturnsTrue() {
            // Arrange
            TcgProductDto product = new TcgProductDto();
            product.setName("Charizard VMAX");
            product.setExtendedData(null);

            // Act
            boolean result = tcgConnectService.isCard(product);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return true for card name with empty extendedData")
        void isCard_EmptyExtendedData_ReturnsTrue() {
            // Arrange
            TcgProductDto product = new TcgProductDto();
            product.setName("Mewtwo GX");
            product.setExtendedData(Collections.emptyList());

            // Act
            boolean result = tcgConnectService.isCard(product);

            // Assert
            assertTrue(result);
        }
    }
}
