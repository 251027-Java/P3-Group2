// // Generated with Assistance By Clause Opus 4.5
// // Reviewed and modified by Marcus Wright

// package com.marketplace.listingservice.kafka;

// import lombok.extern.slf4j.Slf4j;
// import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.stereotype.Component;

// /**
//  * Kafka consumer for handling events from other services.
//  * Listens for card-related and user-related events.
//  */
// @Component
// @Slf4j
// public class ListingEventConsumer {

//     /**
//      * Handles card deleted events from the card service.
//      * When a card is deleted, associated listings may need to be updated.
//      *
//      * @param event the card deleted event
//      */
//     @KafkaListener(
//             topics = "${kafka.topics.card-deleted:card-deleted}",
//             groupId = "${spring.kafka.consumer.group-id}",
//             containerFactory = "kafkaListenerContainerFactory")
//     public void handleCardDeletedEvent(String event) {
//         log.info("Received card deleted event: {}", event);
//         // TODO: Implement logic to handle card deletion
//         // This could involve cancelling or marking listings as unavailable
//     }

//     /**
//      * Handles user deleted events from the user service.
//      * When a user is deleted, their listings may need to be cleaned up.
//      *
//      * @param event the user deleted event
//      */
//     @KafkaListener(
//             topics = "${kafka.topics.user-deleted:user-deleted}",
//             groupId = "${spring.kafka.consumer.group-id}",
//             containerFactory = "kafkaListenerContainerFactory")
//     public void handleUserDeletedEvent(String event) {
//         log.info("Received user deleted event: {}", event);
//         // TODO: Implement logic to handle user deletion
//         // This could involve cancelling all listings owned by the user
//     }

//     /**
//      * Handles trade completed events from the trade service.
//      * When a trade is completed, the associated listing should be marked as completed.
//      *
//      * @param event the trade completed event
//      */
//     @KafkaListener(
//             topics = "${kafka.topics.trade-completed:trade-completed}",
//             groupId = "${spring.kafka.consumer.group-id}",
//             containerFactory = "kafkaListenerContainerFactory")
//     public void handleTradeCompletedEvent(String event) {
//         log.info("Received trade completed event: {}", event);
//         // TODO: Implement logic to mark listing as completed when trade is finalized
//     }
// }
