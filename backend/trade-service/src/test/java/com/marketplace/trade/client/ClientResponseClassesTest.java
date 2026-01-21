// Generated with assistance from Claude Opus 4.5
// Reviewed and modified by Marcus Wright

package com.marketplace.trade.client;

import static org.junit.jupiter.api.Assertions.*;

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
            ListingServiceClient.ListingResponse response = new ListingServiceClient.ListingResponse();
            response.setListingId(1L);
            assertEquals(1L, response.getListingId());
        }

        @Test
        @DisplayName("Should set and get ownerUserId")
        void listingResponse_OwnerUserId() {
            ListingServiceClient.ListingResponse response = new ListingServiceClient.ListingResponse();
            response.setOwnerUserId(100L);
            assertEquals(100L, response.getOwnerUserId());
        }

        @Test
        @DisplayName("Should set and get cardId")
        void listingResponse_CardId() {
            ListingServiceClient.ListingResponse response = new ListingServiceClient.ListingResponse();
            response.setCardId(50L);
            assertEquals(50L, response.getCardId());
        }

        @Test
        @DisplayName("Should set and get listingStatus")
        void listingResponse_ListingStatus() {
            ListingServiceClient.ListingResponse response = new ListingServiceClient.ListingResponse();
            response.setListingStatus("active");
            assertEquals("active", response.getListingStatus());
        }

        @Test
        @DisplayName("Should handle null values")
        void listingResponse_NullValues() {
            ListingServiceClient.ListingResponse response = new ListingServiceClient.ListingResponse();
            assertNull(response.getListingId());
            assertNull(response.getOwnerUserId());
            assertNull(response.getCardId());
            assertNull(response.getListingStatus());
        }

        @Test
        @DisplayName("Should set all fields correctly")
        void listingResponse_AllFields() {
            ListingServiceClient.ListingResponse response = new ListingServiceClient.ListingResponse();
            response.setListingId(1L);
            response.setOwnerUserId(2L);
            response.setCardId(3L);
            response.setListingStatus("completed");

            assertEquals(1L, response.getListingId());
            assertEquals(2L, response.getOwnerUserId());
            assertEquals(3L, response.getCardId());
            assertEquals("completed", response.getListingStatus());
        }
    }

    @Nested
    @DisplayName("ListingStatusUpdate Tests")
    class ListingStatusUpdateTests {

        @Test
        @DisplayName("Should create with no-args constructor")
        void listingStatusUpdate_NoArgsConstructor() {
            ListingServiceClient.ListingStatusUpdate update = new ListingServiceClient.ListingStatusUpdate();
            assertNull(update.getStatus());
        }

        @Test
        @DisplayName("Should create with status constructor")
        void listingStatusUpdate_StatusConstructor() {
            ListingServiceClient.ListingStatusUpdate update = new ListingServiceClient.ListingStatusUpdate("completed");
            assertEquals("completed", update.getStatus());
        }

        @Test
        @DisplayName("Should set and get status")
        void listingStatusUpdate_SetStatus() {
            ListingServiceClient.ListingStatusUpdate update = new ListingServiceClient.ListingStatusUpdate();
            update.setStatus("active");
            assertEquals("active", update.getStatus());
        }
    }

    @Nested
    @DisplayName("UserResponse Tests")
    class UserResponseTests {

        @Test
        @DisplayName("Should set and get appUserId")
        void userResponse_AppUserId() {
            UserServiceClient.UserResponse response = new UserServiceClient.UserResponse();
            response.setAppUserId(1L);
            assertEquals(1L, response.getAppUserId());
        }

        @Test
        @DisplayName("Should set and get email")
        void userResponse_Email() {
            UserServiceClient.UserResponse response = new UserServiceClient.UserResponse();
            response.setEmail("test@example.com");
            assertEquals("test@example.com", response.getEmail());
        }

        @Test
        @DisplayName("Should set and get username")
        void userResponse_Username() {
            UserServiceClient.UserResponse response = new UserServiceClient.UserResponse();
            response.setUsername("testuser");
            assertEquals("testuser", response.getUsername());
        }

        @Test
        @DisplayName("Should handle null values")
        void userResponse_NullValues() {
            UserServiceClient.UserResponse response = new UserServiceClient.UserResponse();
            assertNull(response.getAppUserId());
            assertNull(response.getEmail());
            assertNull(response.getUsername());
        }

        @Test
        @DisplayName("Should set all fields correctly")
        void userResponse_AllFields() {
            UserServiceClient.UserResponse response = new UserServiceClient.UserResponse();
            response.setAppUserId(100L);
            response.setEmail("user@test.com");
            response.setUsername("user123");

            assertEquals(100L, response.getAppUserId());
            assertEquals("user@test.com", response.getEmail());
            assertEquals("user123", response.getUsername());
        }
    }
}
