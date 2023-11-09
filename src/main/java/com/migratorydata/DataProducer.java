package com.migratorydata;

import com.migratorydata.client.MigratoryDataKafkaUtils;
import com.migratorydata.client.MigratoryDataMessage;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataProducer {

    private final ScheduledExecutorService liveExecutor = Executors.newSingleThreadScheduledExecutor();

    private final KafkaProducer<String, byte[]> producer;

    private final Lorem lorem;

    public DataProducer(Properties config) {
        this.producer = new KafkaProducer<>(config);

        this.lorem = LoremIpsum.getInstance();
    }

    public void start() {
        liveExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // generate random text
                String text = lorem.getWords(1024, 2048);

                // record topic and key points to MigratoryData subject /live/data
                ProducerRecord<String, byte[]> record = MigratoryDataKafkaUtils.createRecord("live", "data", text.getBytes(), true, MigratoryDataMessage.QoS.STANDARD, true);

                System.out.println("Before compression size:" + (text.getBytes().length));
                System.out.println("After compression size:" + (record.value().length));
                System.out.println("Compression ratio:" + text.getBytes().length / (float)record.value().length);

                producer.send(record);
            }
        }, 1000, 5000, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        producer.close();
        liveExecutor.shutdown();
    }
}