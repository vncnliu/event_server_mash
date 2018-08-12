package top.vncnliu.event.server.mash.sample.store.kafka;

import kafka.admin.ConsumerGroupCommand;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

/**
 * User: liuyq
 * Date: 2018/7/26
 * Description:
 */
public class TestEventConsumer {

    @Test
    void main(){
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.1.156:9092");
        props.put("group.id", "test-group");
        props.put("client.id", "test-client");
        props.put("enable.auto.commit", "false");
        props.put("auto.offset.reset", "earliest");
        //一次最大拉取的条数
        props.put("max.poll.records", 1);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Arrays.asList("server_mash_store"));

        boolean running = true;
        while (running) {
            System.out.println("poll");
            ConsumerRecords<String, String> records = consumer.poll(1000);
            if(records.count()!=0){
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("key:%s:%s\n",record.key(),record.value());
                    /*if("2".equals(record.value())){
                        consumer.commitSync();
                    }*/
                }
            }
        }
    }
}
