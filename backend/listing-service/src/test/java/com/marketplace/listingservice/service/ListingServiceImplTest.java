// Generated with Assistance By Clause Opus 4.5
// Reviewed and modified by Marcus Wright 

package com.marketplace.listingservice.service;

import com.marketplace.listingservice.client.CardServiceClient;
import com.marketplace.listingservice.client.UserServiceClient;
import com.marketplace.listingservice.client.dto.CardResponse;
import com.marketplace.listingservice.dto.CreateListingRequest;
import com.marketplace.listingservice.dto.ListingResponse;
import com.marketplace.listingservice.dto.UpdateListingRequest;
import com.marketplace.listingservice.entity.Listing;
import com.marketplace.listingservice.entity.ListingStatus;
import com.marketplace.listingservice.exception.CardNotFoundException;
import com.marketplace.listingservice.exception.InvalidListingOperationException;
import com.marketplace.listingservice.exception.ListingNotFoundException;
import com.marketplace.listingservice.exception.UserNotFoundException;
import com.marketplace.listingservice.kafka.ListingEventProducer;
import com.marketplace.listingservice.repository.ListingRepository;
import com.marketplace.listingservice.service.impl.ListingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


/**
 * Unit tests for ListingServiceImpl.
 * Uses Mockito to mock the repository layer.
 */
@ExtendWith(MockitoExtension.class)
class ListingServiceImplTest {

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private ListingEventProducer listingEventProducer;

    @Mock
    private CardServiceClient cardServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private ListingServiceImpl listingService;

    private Listing testListing;
    private CreateListingRequest createRequest;
    private UpdateListingRequest updateRequest;

    @BeforeEach
    void setUp() {
        testListing = Listing.builder()
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

        updateRequest = UpdateListingRequest.builder()
                .conditionRating(9)
                .build();
    }

    @Nested
    @DisplayName("Create Listing Tests")
    class CreateListingTests {

        @Test
        @DisplayName("Should create listing successfully")
        void createListing_Success() {
            CardResponse cardResponse = CardResponse.builder().cardId(200L).name("Test Card").build();
            when(userServiceClient.userExists(100L)).thenReturn(true);
            when(cardServiceClient.getCardById(200L)).thenReturn(cardResponse);
            when(listingRepository.save(any(Listing.class))).thenReturn(testListing);
            doNothing().when(listingEventProducer).sendListingCreatedEvent(any(Listing.class));

            ListingResponse response = listingService.createListing(createRequest);

            assertThat(response).isNotNull();
            assertThat(response.getListingId()).isEqualTo(1L);
            assertThat(response.getOwnerUserId()).isEqualTo(100L);
            assertThat(response.getCardId()).isEqualTo(200L);
            assertThat(response.getConditionRating()).isEqualTo(8);
            assertThat(response.getListingStatus()).isEqualTo(ListingStatus.ACTIVE);

            verify(userServiceClient, times(1)).userExists(100L);
            verify(cardServiceClient, times(1)).getCardById(200L);
            verify(listingRepository, times(1)).save(any(Listing.class));
            verify(listingEventProducer, times(1)).sendListingCreatedEvent(any(Listing.class));
        }

        @Test
        @DisplayName("Should create listing with custom status")
        void createListing_WithCustomStatus() {
            createRequest.setListingStatus(ListingStatus.ACTIVE);
            CardResponse cardResponse = CardResponse.builder().cardId(200L).name("Test Card").build();
            when(userServiceClient.userExists(100L)).thenReturn(true);
            when(cardServiceClient.getCardById(200L)).thenReturn(cardResponse);
            when(listingRepository.save(any(Listing.class))).thenReturn(testListing);
            doNothing().when(listingEventProducer).sendListingCreatedEvent(any(Listing.class));

            ListingResponse response = listingService.createListing(createRequest);

            assertThat(response).isNotNull();
            assertThat(response.getListingStatus()).isEqualTo(ListingStatus.ACTIVE);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void createListing_UserNotFound() {
            when(userServiceClient.userExists(100L)).thenReturn(false);

            assertThatThrownBy(() -> listingService.createListing(createRequest))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("100");

            verify(userServiceClient, times(1)).userExists(100L);
            verify(cardServiceClient, never()).getCardById(anyLong());
            verify(listingRepository, never()).save(any(Listing.class));
        }

        @Test
        @DisplayName("Should throw CardNotFoundException when card does not exist")
        void createListing_CardNotFound() {
            when(userServiceClient.userExists(100L)).thenReturn(true);
            when(cardServiceClient.getCardById(200L)).thenReturn(null);

            assertThatThrownBy(() -> listingService.createListing(createRequest))
                    .isInstanceOf(CardNotFoundException.class)
                    .hasMessageContaining("200");

            verify(userServiceClient, times(1)).userExists(100L);
            verify(cardServiceClient, times(1)).getCardById(200L);
            verify(listingRepository, never()).save(any(Listing.class));
        }
    }

    @Nested
    @DisplayName("Get Listing Tests")
    class GetListingTests {

        @Test
        @DisplayName("Should get listing by ID successfully")
        void getListingById_Success() {
            when(listingRepository.findById(1L)).thenReturn(Optional.of(testListing));

            ListingResponse response = listingService.getListingById(1L);

            assertThat(response).isNotNull();
            assertThat(response.getListingId()).isEqualTo(1L);
            verify(listingRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when listing not found")
        void getListingById_NotFound() {
            when(listingRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> listingService.getListingById(999L))
                    .isInstanceOf(ListingNotFoundException.class)
                    .hasMessageContaining("999");
        }

        @Test
        @DisplayName("Should get all listings")
        void getAllListings_Success() {
            Listing listing2 = Listing.builder()
                    .listingId(2L)
                    .ownerUserId(101L)
                    .cardId(201L)
                    .conditionRating(7)
                    .listingStatus(ListingStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(listingRepository.findAll()).thenReturn(Arrays.asList(testListing, listing2));

            List<ListingResponse> responses = listingService.getAllListings();

            assertThat(responses).hasSize(2);
            verify(listingRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no listings exist")
        void getAllListings_Empty() {
            when(listingRepository.findAll()).thenReturn(Collections.emptyList());

            List<ListingResponse> responses = listingService.getAllListings();

            assertThat(responses).isEmpty();
        }

        @Test
        @DisplayName("Should get listings by owner user ID")
        void getListingsByOwnerUserId_Success() {
            when(listingRepository.findByOwnerUserId(100L)).thenReturn(List.of(testListing));

            List<ListingResponse> responses = listingService.getListingsByOwnerUserId(100L);

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getOwnerUserId()).isEqualTo(100L);
        }

        @Test
        @DisplayName("Should get listings by card ID")
        void getListingsByCardId_Success() {
            when(listingRepository.findByCardId(200L)).thenReturn(List.of(testListing));

            List<ListingResponse> responses = listingService.getListingsByCardId(200L);

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getCardId()).isEqualTo(200L);
        }

        @Test
        @DisplayName("Should get listings by status")
        void getListingsByStatus_Success() {
            when(listingRepository.findByListingStatus(ListingStatus.ACTIVE))
                    .thenReturn(List.of(testListing));

            List<ListingResponse> responses = listingService.getListingsByStatus(ListingStatus.ACTIVE);

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getListingStatus()).isEqualTo(ListingStatus.ACTIVE);
        }

        @Test
        @DisplayName("Should get active listings")
        void getActiveListings_Success() {
            when(listingRepository.findByListingStatus(ListingStatus.ACTIVE))
                    .thenReturn(List.of(testListing));

            List<ListingResponse> responses = listingService.getActiveListings();

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getListingStatus()).isEqualTo(ListingStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("Update Listing Tests")
    class UpdateListingTests {

        @Test
        @DisplayName("Should update listing successfully")
        void updateListing_Success() {
            Listing updatedListing = Listing.builder()
                    .listingId(1L)
                    .ownerUserId(100L)
                    .cardId(200L)
                    .conditionRating(9)
                    .listingStatus(ListingStatus.ACTIVE)
                    .createdAt(testListing.getCreatedAt())
                    .build();

            when(listingRepository.findById(1L)).thenReturn(Optional.of(testListing));
            when(listingRepository.save(any(Listing.class))).thenReturn(updatedListing);
            doNothing().when(listingEventProducer).sendListingUpdatedEvent(any(Listing.class));

            ListingResponse response = listingService.updateListing(1L, updateRequest);

            assertThat(response).isNotNull();
            assertThat(response.getConditionRating()).isEqualTo(9);
            verify(listingRepository, times(1)).save(any(Listing.class));
            verify(listingEventProducer, times(1)).sendListingUpdatedEvent(any(Listing.class));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent listing")
        void updateListing_NotFound() {
            when(listingRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> listingService.updateListing(999L, updateRequest))
                    .isInstanceOf(ListingNotFoundException.class);
        }

        @Test
        @DisplayName("Should throw exception when updating non-active listing")
        void updateListing_NotActive() {
            testListing.setListingStatus(ListingStatus.COMPLETED);
            when(listingRepository.findById(1L)).thenReturn(Optional.of(testListing));

            assertThatThrownBy(() -> listingService.updateListing(1L, updateRequest))
                    .isInstanceOf(InvalidListingOperationException.class)
                    .hasMessageContaining("not active");
        }

        @Test
        @DisplayName("Should update listing status")
        void updateListing_UpdateStatus() {
            UpdateListingRequest statusUpdate = UpdateListingRequest.builder()
                    .listingStatus(ListingStatus.CANCELLED)
                    .build();

            Listing updatedListing = Listing.builder()
                    .listingId(1L)
                    .ownerUserId(100L)
                    .cardId(200L)
                    .conditionRating(8)
                    .listingStatus(ListingStatus.CANCELLED)
                    .createdAt(testListing.getCreatedAt())
                    .build();

            when(listingRepository.findById(1L)).thenReturn(Optional.of(testListing));
            when(listingRepository.save(any(Listing.class))).thenReturn(updatedListing);
            doNothing().when(listingEventProducer).sendListingUpdatedEvent(any(Listing.class));

            ListingResponse response = listingService.updateListing(1L, statusUpdate);

            assertThat(response.getListingStatus()).isEqualTo(ListingStatus.CANCELLED);
        }
    }

    @Nested
    @DisplayName("Cancel Listing Tests")
    class CancelListingTests {

        @Test
        @DisplayName("Should cancel listing successfully")
        void cancelListing_Success() {
            Listing cancelledListing = Listing.builder()
                    .listingId(1L)
                    .ownerUserId(100L)
                    .cardId(200L)
                    .conditionRating(8)
                    .listingStatus(ListingStatus.CANCELLED)
                    .createdAt(testListing.getCreatedAt())
                    .build();

            when(listingRepository.findById(1L)).thenReturn(Optional.of(testListing));
            when(listingRepository.save(any(Listing.class))).thenReturn(cancelledListing);
            doNothing().when(listingEventProducer).sendListingStatusChangedEvent(any(Listing.class), anyString());

            ListingResponse response = listingService.cancelListing(1L);

            assertThat(response.getListingStatus()).isEqualTo(ListingStatus.CANCELLED);
            verify(listingEventProducer, times(1))
                    .sendListingStatusChangedEvent(any(Listing.class), eq("CANCELLED"));
        }

        @Test
        @DisplayName("Should throw exception when cancelling non-existent listing")
        void cancelListing_NotFound() {
            when(listingRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> listingService.cancelListing(999L))
                    .isInstanceOf(ListingNotFoundException.class);
        }

        @Test
        @DisplayName("Should throw exception when cancelling non-active listing")
        void cancelListing_NotActive() {
            testListing.setListingStatus(ListingStatus.COMPLETED);
            when(listingRepository.findById(1L)).thenReturn(Optional.of(testListing));

            assertThatThrownBy(() -> listingService.cancelListing(1L))
                    .isInstanceOf(InvalidListingOperationException.class)
                    .hasMessageContaining("not active");
        }
    }

    @Nested
    @DisplayName("Complete Listing Tests")
    class CompleteListingTests {

        @Test
        @DisplayName("Should complete listing successfully")
        void completeListing_Success() {
            Listing completedListing = Listing.builder()
                    .listingId(1L)
                    .ownerUserId(100L)
                    .cardId(200L)
                    .conditionRating(8)
                    .listingStatus(ListingStatus.COMPLETED)
                    .createdAt(testListing.getCreatedAt())
                    .build();

            when(listingRepository.findById(1L)).thenReturn(Optional.of(testListing));
            when(listingRepository.save(any(Listing.class))).thenReturn(completedListing);
            doNothing().when(listingEventProducer).sendListingStatusChangedEvent(any(Listing.class), anyString());

            ListingResponse response = listingService.completeListing(1L);

            assertThat(response.getListingStatus()).isEqualTo(ListingStatus.COMPLETED);
            verify(listingEventProducer, times(1))
                    .sendListingStatusChangedEvent(any(Listing.class), eq("COMPLETED"));
        }

        @Test
        @DisplayName("Should throw exception when completing non-existent listing")
        void completeListing_NotFound() {
            when(listingRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> listingService.completeListing(999L))
                    .isInstanceOf(ListingNotFoundException.class);
        }

        @Test
        @DisplayName("Should throw exception when completing non-active listing")
        void completeListing_NotActive() {
            testListing.setListingStatus(ListingStatus.CANCELLED);
            when(listingRepository.findById(1L)).thenReturn(Optional.of(testListing));

            assertThatThrownBy(() -> listingService.completeListing(1L))
                    .isInstanceOf(InvalidListingOperationException.class)
                    .hasMessageContaining("not active");
        }
    }

    @Nested
    @DisplayName("Delete Listing Tests")
    class DeleteListingTests {

        @Test
        @DisplayName("Should delete listing successfully")
        void deleteListing_Success() {
            when(listingRepository.findById(1L)).thenReturn(Optional.of(testListing));
            doNothing().when(listingRepository).delete(any(Listing.class));
            doNothing().when(listingEventProducer).sendListingDeletedEvent(anyLong());

            listingService.deleteListing(1L);

            verify(listingRepository, times(1)).delete(testListing);
            verify(listingEventProducer, times(1)).sendListingDeletedEvent(1L);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent listing")
        void deleteListing_NotFound() {
            when(listingRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> listingService.deleteListing(999L))
                    .isInstanceOf(ListingNotFoundException.class);
        }
    }
}
