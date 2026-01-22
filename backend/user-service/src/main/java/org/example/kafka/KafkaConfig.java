// // Generated with assistance from ChatGPT 4.0
// // Reviewed and modified by Matt Selle
// package org.example.kafka;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
// import org.apache.kafka.clients.consumer.ConsumerConfig;
// import org.apache.kafka.clients.producer.ProducerConfig;
// import org.apache.kafka.common.serialization.StringDeserializer;
// import org.apache.kafka.common.serialization.StringSerializer;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.kafka.annotation.EnableKafka;
// import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
// import org.springframework.kafka.core.ConsumerFactory;
// import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
// import org.springframework.kafka.core.DefaultKafkaProducerFactory;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.kafka.core.ProducerFactory;
// import org.springframework.kafka.support.serializer.JsonDeserializer;
// import org.springframework.kafka.support.serializer.JsonSerializer;

// import java.util.HashMap;
// import java.util.Map;

// /**
//  * Kafka configuration for producer
//  */
// @Configuration
// @EnableKafka
// public class KafkaConfig {

//     @Value("${spring.kafka.bootstrap-servers}")
//     private String bootstrapServers;

//     /* ---------- ObjectMapper ---------- */

//     @Bean
//     public ObjectMapper objectMapper() {
//         ObjectMapper mapper = new ObjectMapper();
//         mapper.registerModule(new JavaTimeModule());
//         return mapper;
//     }

//     /* ---------- PRODUCER ---------- */

//     @Bean
//     public ProducerFactory<String, Object> producerFactory(ObjectMapper objectMapper) {
//         Map<String, Object> props = new HashMap<>();
//         props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//         props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//         props.put(ProducerConfig.ACKS_CONFIG, "all");
//         props.put(ProducerConfig.RETRIES_CONFIG, 3);

//         DefaultKafkaProducerFactory<String, Object> factory = new DefaultKafkaProducerFactory<>(props);
//         factory.setValueSerializer(new JsonSerializer<>(objectMapper));
//         return factory;
//     }

//     @Bean
//     public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
//         return new KafkaTemplate<>(producerFactory);
//     }

//     /* ---------- CONSUMER ---------- */

//     @Bean
//     public ConsumerFactory<String, Object> consumerFactory(ObjectMapper objectMapper) {
//         Map<String, Object> props = new HashMap<>();
//         props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//         props.put(ConsumerConfig.GROUP_ID_CONFIG, "trade-service-group");
//         props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//         props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

//         JsonDeserializer<Object> valueDeserializer = new JsonDeserializer<>(Object.class, objectMapper);
//         valueDeserializer.addTrustedPackages("*");

//         return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer);
//     }

//     @Bean
//     public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
//             ConsumerFactory<String, Object> consumerFactory) {

//         ConcurrentKafkaListenerContainerFactory<String, Object> factory =
//                 new ConcurrentKafkaListenerContainerFactory<>();
//         factory.setConsumerFactory(consumerFactory);
//         return factory;
//     }
// }
