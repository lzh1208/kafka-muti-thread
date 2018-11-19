import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSONObject;
import java.util.Properties;

/**
 * Created by liuzehui on 2018/9/12.
 */
public class KafkaConsumerThread implements Runnable{

    private static Logger logger = Logger.getLogger(KafkaConsumerThread.class);
    private static final Properties properties= ProjectConfig.getProperties();
    private KafkaStream<String, String> stream;
    private BlockChain blockclient;

    public KafkaConsumerThread(KafkaStream<String, String> stream) throws Exception {
        this.stream = stream;
        this.blockclient = new BlockChain(properties.getProperty("blockchain.host"),
                Integer.parseInt(properties.getProperty("blockchain.port")),properties.getProperty("blockchain.pem"));
    }


    @Override
    public void run()  {
        ConsumerIterator<String, String> it = stream.iterator();
        while (it.hasNext()) {
            String message=it.next().message();
            logger.info(message);
            update2Chain(blockclient,message);

        }
    }

    public static String update2Chain(BlockChain blockclient,String JsonString) {
        JSONObject object=JSONObject.parseObject(JsonString);

        String id=object.get("id").toString();
        String name=object.get("name").toString();
        String content=object.get("content").toString();
        String hash=object.get("hash").toString();
        String result="";
        if("".equals(blockclient.AssetOf(id))){
            result=blockclient.CreateAsset(id,name,content,hash);
            logger.info("create success!");
        }else{
            result=blockclient.UpdateAsset(id,name,content,hash);
            logger.info("update success!");
        }
        //logger.info(result);

        return result;

    }
}
