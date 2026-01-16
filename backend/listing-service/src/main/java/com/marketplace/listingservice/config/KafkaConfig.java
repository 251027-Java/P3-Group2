// Generated with Assistance By Clause Opus 4.5
// Reviewed and modified by Marcus Wright 

package com.marketplace.listingservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka configuration for creating topics and configuring producers/consumers.
 */
@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.listing-created:listing-created}")
    private String listingCreatedTopic;

    @Value("${kafka.topics.listing-updated:listing-updated}")
    private String listingUpdatedTopic;

    @Value("${kafka.topics.listing-status-changed:listing-status-changed}")
    private String listingStatusChangedTopic;

    @Value("${kafka.topics.listing-deleted:listing-deleted}")
    private String listingDeletedTopic;

    @Bean
    public NewTopic listingCreatedTopic() {
        return TopicBuilder.name(listingCreatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic listingUpdatedTopic() {
        return TopicBuilder.name(listingUpdatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic listingStatusChangedTopic() {
        return TopicBuilder.name(listingStatusChangedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic listingDeletedTopic() {
        return TopicBuilder.name(listingDeletedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
