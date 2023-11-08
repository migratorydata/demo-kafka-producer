package com.migratorydata;

import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9094");

        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");


        DataProducer dataProducer = new DataProducer(config);
        dataProducer.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                dataProducer.stop();
            }
        });
    }
}
