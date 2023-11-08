package com.migratorydata;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;

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
                String text = lorem.getWords(30, 60);

                // compress data and encode as base64
                byte[] compressedText = compressAndEncode(text.getBytes());

                System.out.println("Data compressed:" + new String(compressedText));

                // record topic and key points to MigratoryData subject /live/data
                ProducerRecord<String, byte[]> record = new ProducerRecord<>("live",0, "data", compressedText);

                // add necessary metadata to the record
                addMessageMetaData(record, true, QoS.STANDARD, true);

                producer.send(record);

            }
        }, 1000, 5000, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        producer.close();
        liveExecutor.shutdown();
    }

    public static void addMessageMetaData(ProducerRecord<String, byte[]> record, boolean compression, QoS qoS, boolean retained) {
        record.headers().add("qos", new byte[]{(byte) qoS.code()});

        record.headers().add("compression", new byte[] {(byte)(compression ? 1:0) });

        record.headers().add("retained", new byte[] {(byte)(retained ? 1:0) });

        record.headers().add("pushVersion", "6.0.0".getBytes());
    }

    public byte[] compressAndEncode(byte[] content) {
        Deflater compressor = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
        compressor.setInput(content);
        compressor.finish();

        ByteBuffer compressData = ByteBuffer.allocate(8192);
        byte[] compressChunk = new byte[8192];
        do {
            int written = compressor.deflate(compressChunk);
            compressData.put(compressChunk, 0, written);
        } while (!compressor.finished());

        compressor.end();

        compressData.flip();

        return Base64.getEncoder().encode(Arrays.copyOf(compressData.array(), compressData.limit() - compressData.position()));
    }
}