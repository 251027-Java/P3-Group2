// Generated with Assistance By Clause Opus 4.5
// Reviewed and modified by Marcus Wright 

package com.marketplace.listingservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {"listing-created", "listing-updated", "listing-status-changed", "listing-deleted"})
@SpringBootTest
class ListingServiceApplicationTests {

    // @Test
    // void contextLoads() {
    //     // Verifies that the application context loads successfully
    // }
}
