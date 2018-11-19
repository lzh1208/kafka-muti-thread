import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liuzehui on 2018/9/12.
 */
public class advanceConsumer {
    private static Logger logger = Logger.getLogger(advanceConsumer.class);
    private static final Properties properties= ProjectConfig.getProperties();
    private static ConsumerConnector consumer;
    private final static  String TOPIC=properties.getProperty("kafka.topic");
    private final int threadsNum=Integer.parseInt(properties.getProperty("kafka.consumer.thread"));
    private Properties props;

    private advanceConsumer() throws Exception{
        props=new Properties();
        //zookeeper
        props.put("zookeeper.connect",properties.getProperty("kafka.zookeeper.connect"));
        //topic
        props.put("group.id",properties.getProperty("kafka.group.id"));

        //Zookeeper 超时
        props.put("zookeeper.session.timeout.ms", "4000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "smallest");


        props.put("serializer.class", "kafka.serializer.StringEncoder");
        ConsumerConfig config=new ConsumerConfig(props);

        consumer= kafka.consumer.Consumer.createJavaConsumerConnector(config);


    }


    public  void consume() throws Exception{
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(TOPIC, new Integer(threadsNum));

        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
        StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());

        Map<String, List<KafkaStream<String, String>>> consumerMap =
                consumer.createMessageStreams(topicCountMap,keyDecoder,valueDecoder);
        List<KafkaStream<String, String>> streams = consumerMap.get(TOPIC);
        ExecutorService executor = Executors.newFixedThreadPool(threadsNum);
        for (final KafkaStream stream : streams) {
            executor.submit(new KafkaConsumerThread(stream));
        }

    }





    public static void main(String[] args) throws Exception {
        logger.info(properties);
        new advanceConsumer().consume();
    }
}
