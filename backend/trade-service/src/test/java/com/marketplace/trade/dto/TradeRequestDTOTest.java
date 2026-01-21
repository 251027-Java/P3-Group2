package com.marketplace.trade.dto;
// Generated with assistance from ChatGPT 4.0
// Reviewed and modified by Matt Selle

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class TradeRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidTradeRequestDTO() {
        TradeRequestDTO dto = new TradeRequestDTO(1L, 2L, List.of(10L, 11L));

        Set<ConstraintViolation<TradeRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Valid DTO should have no constraint violations");
    }

    @Test
    void testListingIdNull() {
        TradeRequestDTO dto = new TradeRequestDTO(null, 2L, List.of(10L));

        Set<ConstraintViolation<TradeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("listingId")
                        && v.getMessage().equals("Listing ID is required")));
    }

    @Test
    void testRequestingUserIdNull() {
        TradeRequestDTO dto = new TradeRequestDTO(1L, null, List.of(10L));

        Set<ConstraintViolation<TradeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("requestingUserId")
                        && v.getMessage().equals("Requesting user ID is required")));
    }

    @Test
    void testOfferedCardIdsEmpty() {
        TradeRequestDTO dto = new TradeRequestDTO(1L, 2L, new ArrayList<>());

        Set<ConstraintViolation<TradeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("offeredCardIds")
                        && v.getMessage().equals("At least one card must be offered")));
    }

    @Test
    void testOfferedCardIdsNull() {
        TradeRequestDTO dto = new TradeRequestDTO(1L, 2L, null);

        Set<ConstraintViolation<TradeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("offeredCardIds")
                        && v.getMessage().equals("At least one card must be offered")));
    }
}
