#!/bin/bash
export PATH=$PATH:/usr/bin # Not strictly needed if we use full path or if it's in path

KAFKA_BROKER="localhost:9092"

create_topic() {
  TOPIC=$1
  PARTITIONS=$2
  REPLICATION=$3

  kafka-topics \
    --bootstrap-server $KAFKA_BROKER \
    --create \
    --if-not-exists \
    --topic $TOPIC \
    --partitions $PARTITIONS \
    --replication-factor $REPLICATION
}

# Wait for Kafka to be ready?/ when is this run? i don't see it being called anywhere? 

echo "Creating topics for Card Marketplace..."
create_topic listings 3 1
create_topic trades 3 1

echo "Successfully created topics:"
kafka-topics --bootstrap-server $KAFKA_BROKER --list
