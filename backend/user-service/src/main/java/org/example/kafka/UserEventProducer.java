package org.example.kafka;

// Generated with assistance from ChatGPT
// Reviewed and modified by Matt Selle

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * Kafka producer for user-related events.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.user-created:user-created}")
    private String userCreatedTopic;

    @Value("${kafka.topics.user-deleted:user-deleted}")
    private String userDeletedTopic;

    /**
     * Publish user created event.
     */
    public void sendUserCreatedEvent(Long userId, String username, String email) {
        UserEvent event = UserEvent.builder()
                .eventType("USER_CREATED")
                .userId(userId)
                .username(username)
                .email(email)
                .timestamp(LocalDateTime.now())
                .build();

        sendEvent(userCreatedTopic, userId.toString(), event);
    }

    /**
     * Publish user deleted event.
     */
    public void sendUserDeletedEvent(Long userId) {
        UserEvent event = UserEvent.builder()
                .eventType("USER_DELETED")
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .build();

        sendEvent(userDeletedTopic, userId.toString(), event);
    }

    private void sendEvent(String topic, String key, UserEvent event) {
        log.info("Sending user event to topic {}: {}", topic, event);

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info(
                    "User event sent successfully to topic {} with offset {}",
                    topic,
                    result.getRecordMetadata().offset()
                );
            } else {
                log.error(
                    "Failed to send user event to topic {}",
                    topic,
                    ex
                );
            }
        });
    }

    /**
     * User event payload.
     */
    @Data
    @Builder
    public static class UserEvent {
        private String eventType;
        private Long userId;
        private String username;
        private String email;
        private LocalDateTime timestamp;
    }
}

