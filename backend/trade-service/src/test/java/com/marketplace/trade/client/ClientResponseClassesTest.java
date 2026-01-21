// Generated with assistance from Claude Opus 4.5
// Reviewed and modified by Marcus Wright

package com.marketplace.trade.client;

import static org.junit.jupiter.api.Assertions.*;

import com.marketplace.trade.client.dto.ListingResponse;
import com.marketplace.trade.client.dto.ListingStatus;
import com.marketplace.trade.client.dto.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for client response classes
 */
class ClientResponseClassesTest {

    @Nested
    @DisplayName("ListingResponse Tests")
    class ListingResponseTests {

        @Test
        @DisplayName("Should set and get listingId")
        void listingResponse_ListingId() {
            ListingResponse response = new ListingResponse();
            response.setListingId(1L);
            assertEquals(1L, response.getListingId());
        }

        @Test
        @DisplayName("Should set and get ownerUserId")
        void listingResponse_OwnerUserId() {
            ListingResponse response = new ListingResponse();
            response.setOwnerUserId(100L);
            assertEquals(100L, response.getOwnerUserId());
        }

        @Test
        @DisplayName("Should set and get cardId")
        void listingResponse_CardId() {
            ListingResponse response = new ListingResponse();
            response.setCardId(50L);
            assertEquals(50L, response.getCardId());
        }

        @Test
        @DisplayName("Should set and get listingStatus")
        void listingResponse_ListingStatus() {
            ListingResponse response = new ListingResponse();
            response.setListingStatus(ListingStatus.ACTIVE);
            assertEquals("active", response.getListingStatus().getValue());
        }

        @Test
        @DisplayName("Should handle null values")
        void listingResponse_NullValues() {
            ListingResponse response = new ListingResponse();
            assertNull(response.getListingId());
            assertNull(response.getOwnerUserId());
            assertNull(response.getCardId());
            assertNull(response.getListingStatus());
        }

        @Test
        @DisplayName("Should set all fields correctly")
        void listingResponse_AllFields() {
            ListingResponse response = new ListingResponse();
            response.setListingId(1L);
            response.setOwnerUserId(2L);
            response.setCardId(3L);
            response.setListingStatus(ListingStatus.COMPLETED);

            assertEquals(1L, response.getListingId());
            assertEquals(2L, response.getOwnerUserId());
            assertEquals(3L, response.getCardId());
            assertEquals("completed", response.getListingStatus().getValue());
        }
    }

    @Nested
    @DisplayName("UserResponse Tests")
    class UserResponseTests {

        @Test
        @DisplayName("Should set and get appUserId")
        void userResponse_AppUserId() {
            UserResponse response = new UserResponse();
            response.setUserId(1L);
            assertEquals(1L, response.getUserId());
        }

        @Test
        @DisplayName("Should set and get email")
        void userResponse_Email() {
            UserResponse response = new UserResponse();
            response.setEmail("test@example.com");
            assertEquals("test@example.com", response.getEmail());
        }

        @Test
        @DisplayName("Should set and get username")
        void userResponse_Username() {
            UserResponse response = new UserResponse();
            response.setUsername("testuser");
            assertEquals("testuser", response.getUsername());
        }

        @Test
        @DisplayName("Should handle null values")
        void userResponse_NullValues() {
            UserResponse response = new UserResponse();
            assertNull(response.getUserId());
            assertNull(response.getEmail());
            assertNull(response.getUsername());
        }

        @Test
        @DisplayName("Should set all fields correctly")
        void userResponse_AllFields() {
            UserResponse response = new UserResponse();
            response.setUserId(100L);
            response.setEmail("user@test.com");
            response.setUsername("user123");

            assertEquals(100L, response.getUserId());
            assertEquals("user@test.com", response.getEmail());
            assertEquals("user123", response.getUsername());
        }
    }
}
