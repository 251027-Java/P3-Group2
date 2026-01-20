// Generated with Assistance By Clause Opus 4.5
// Reviewed and modified by Marcus Wright

package com.marketplace.listingservice.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.listingservice.dto.CreateListingRequest;
import com.marketplace.listingservice.dto.ListingResponse;
import com.marketplace.listingservice.dto.UpdateListingRequest;
import com.marketplace.listingservice.entity.ListingStatus;
import com.marketplace.listingservice.exception.InvalidListingOperationException;
import com.marketplace.listingservice.exception.ListingNotFoundException;
import com.marketplace.listingservice.service.ListingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Unit tests for ListingController.
 * Uses MockMvc for testing REST endpoints.
 */
@WebMvcTest(ListingController.class)
class ListingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ListingService listingService;

    private ListingResponse testListingResponse;
    private CreateListingRequest createRequest;
    private UpdateListingRequest updateRequest;

    @BeforeEach
    void setUp() {
        testListingResponse = ListingResponse.builder()
                .listingId(1L)
                .ownerUserId(100L)
                .cardId(200L)
                .conditionRating(8)
                .listingStatus(ListingStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        createRequest = CreateListingRequest.builder()
                .ownerUserId(100L)
                .cardId(200L)
                .conditionRating(8)
                .build();

        updateRequest = UpdateListingRequest.builder().conditionRating(9).build();
    }

    @Nested
    @DisplayName("Create Listing Endpoint Tests")
    class CreateListingEndpointTests {

        @Test
        @DisplayName("POST /api/listings - Success")
        void createListing_Success() throws Exception {
            when(listingService.createListing(any(CreateListingRequest.class))).thenReturn(testListingResponse);

            mockMvc.perform(post("/api/listings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.listingId", is(1)))
                    .andExpect(jsonPath("$.ownerUserId", is(100)))
                    .andExpect(jsonPath("$.cardId", is(200)))
                    .andExpect(jsonPath("$.conditionRating", is(8)))
                    .andExpect(jsonPath("$.listingStatus", is("ACTIVE")));

            verify(listingService, times(1)).createListing(any(CreateListingRequest.class));
        }

        @Test
        @DisplayName("POST /api/listings - Validation Error")
        void createListing_ValidationError() throws Exception {
            CreateListingRequest invalidRequest = CreateListingRequest.builder()
                    .conditionRating(15) // Invalid: exceeds max of 10
                    .build();

            mockMvc.perform(post("/api/listings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Get Listing Endpoint Tests")
    class GetListingEndpointTests {

        @Test
        @DisplayName("GET /api/listings/{id} - Success")
        void getListingById_Success() throws Exception {
            when(listingService.getListingById(1L)).thenReturn(testListingResponse);

            mockMvc.perform(get("/api/listings/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.listingId", is(1)))
                    .andExpect(jsonPath("$.ownerUserId", is(100)));

            verify(listingService, times(1)).getListingById(1L);
        }

        @Test
        @DisplayName("GET /api/listings/{id} - Not Found")
        void getListingById_NotFound() throws Exception {
            when(listingService.getListingById(999L)).thenThrow(new ListingNotFoundException(999L));

            mockMvc.perform(get("/api/listings/999")).andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /api/listings - Success")
        void getAllListings_Success() throws Exception {
            ListingResponse listing2 = ListingResponse.builder()
                    .listingId(2L)
                    .ownerUserId(101L)
                    .cardId(201L)
                    .conditionRating(7)
                    .listingStatus(ListingStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(listingService.getAllListings()).thenReturn(Arrays.asList(testListingResponse, listing2));

            mockMvc.perform(get("/api/listings"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].listingId", is(1)))
                    .andExpect(jsonPath("$[1].listingId", is(2)));
        }

        @Test
        @DisplayName("GET /api/listings - Empty")
        void getAllListings_Empty() throws Exception {
            when(listingService.getAllListings()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/listings")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("GET /api/listings/active - Success")
        void getActiveListings_Success() throws Exception {
            when(listingService.getActiveListings()).thenReturn(List.of(testListingResponse));

            mockMvc.perform(get("/api/listings/active"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].listingStatus", is("ACTIVE")));
        }

        @Test
        @DisplayName("GET /api/listings/user/{userId} - Success")
        void getListingsByOwnerUserId_Success() throws Exception {
            when(listingService.getListingsByOwnerUserId(100L)).thenReturn(List.of(testListingResponse));

            mockMvc.perform(get("/api/listings/user/100"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].ownerUserId", is(100)));
        }

        @Test
        @DisplayName("GET /api/listings/card/{cardId} - Success")
        void getListingsByCardId_Success() throws Exception {
            when(listingService.getListingsByCardId(200L)).thenReturn(List.of(testListingResponse));

            mockMvc.perform(get("/api/listings/card/200"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].cardId", is(200)));
        }

        @Test
        @DisplayName("GET /api/listings/status/{status} - Success")
        void getListingsByStatus_Success() throws Exception {
            when(listingService.getListingsByStatus(ListingStatus.ACTIVE)).thenReturn(List.of(testListingResponse));

            mockMvc.perform(get("/api/listings/status/ACTIVE"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].listingStatus", is("ACTIVE")));
        }
    }

    @Nested
    @DisplayName("Update Listing Endpoint Tests")
    class UpdateListingEndpointTests {

        @Test
        @DisplayName("PUT /api/listings/{id} - Success")
        void updateListing_Success() throws Exception {
            ListingResponse updatedResponse = ListingResponse.builder()
                    .listingId(1L)
                    .ownerUserId(100L)
                    .cardId(200L)
                    .conditionRating(9)
                    .listingStatus(ListingStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(listingService.updateListing(eq(1L), any(UpdateListingRequest.class)))
                    .thenReturn(updatedResponse);

            mockMvc.perform(put("/api/listings/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.conditionRating", is(9)));
        }

        @Test
        @DisplayName("PUT /api/listings/{id} - Not Found")
        void updateListing_NotFound() throws Exception {
            when(listingService.updateListing(eq(999L), any(UpdateListingRequest.class)))
                    .thenThrow(new ListingNotFoundException(999L));

            mockMvc.perform(put("/api/listings/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("PUT /api/listings/{id} - Invalid Operation")
        void updateListing_InvalidOperation() throws Exception {
            when(listingService.updateListing(eq(1L), any(UpdateListingRequest.class)))
                    .thenThrow(new InvalidListingOperationException("Cannot update a listing that is not active"));

            mockMvc.perform(put("/api/listings/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Cancel Listing Endpoint Tests")
    class CancelListingEndpointTests {

        @Test
        @DisplayName("POST /api/listings/{id}/cancel - Success")
        void cancelListing_Success() throws Exception {
            ListingResponse cancelledResponse = ListingResponse.builder()
                    .listingId(1L)
                    .ownerUserId(100L)
                    .cardId(200L)
                    .conditionRating(8)
                    .listingStatus(ListingStatus.CANCELLED)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(listingService.cancelListing(1L)).thenReturn(cancelledResponse);

            mockMvc.perform(post("/api/listings/1/cancel"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.listingStatus", is("CANCELLED")));
        }

        @Test
        @DisplayName("POST /api/listings/{id}/cancel - Not Found")
        void cancelListing_NotFound() throws Exception {
            when(listingService.cancelListing(999L)).thenThrow(new ListingNotFoundException(999L));

            mockMvc.perform(post("/api/listings/999/cancel")).andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Complete Listing Endpoint Tests")
    class CompleteListingEndpointTests {

        @Test
        @DisplayName("POST /api/listings/{id}/complete - Success")
        void completeListing_Success() throws Exception {
            ListingResponse completedResponse = ListingResponse.builder()
                    .listingId(1L)
                    .ownerUserId(100L)
                    .cardId(200L)
                    .conditionRating(8)
                    .listingStatus(ListingStatus.COMPLETED)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(listingService.completeListing(1L)).thenReturn(completedResponse);

            mockMvc.perform(post("/api/listings/1/complete"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.listingStatus", is("COMPLETED")));
        }

        @Test
        @DisplayName("POST /api/listings/{id}/complete - Not Found")
        void completeListing_NotFound() throws Exception {
            when(listingService.completeListing(999L)).thenThrow(new ListingNotFoundException(999L));

            mockMvc.perform(post("/api/listings/999/complete")).andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Delete Listing Endpoint Tests")
    class DeleteListingEndpointTests {

        @Test
        @DisplayName("DELETE /api/listings/{id} - Success")
        void deleteListing_Success() throws Exception {
            doNothing().when(listingService).deleteListing(1L);

            mockMvc.perform(delete("/api/listings/1")).andExpect(status().isNoContent());

            verify(listingService, times(1)).deleteListing(1L);
        }

        @Test
        @DisplayName("DELETE /api/listings/{id} - Not Found")
        void deleteListing_NotFound() throws Exception {
            doThrow(new ListingNotFoundException(999L)).when(listingService).deleteListing(999L);

            mockMvc.perform(delete("/api/listings/999")).andExpect(status().isNotFound());
        }
    }
}
