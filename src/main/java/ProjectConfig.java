import java.util.Properties;

/**
 * Created by liuzehui on 2018/9/12.
 */
public class ProjectConfig {

    private static Properties properties;

    public synchronized static Properties getProperties() {
        if (null != properties) {
            return properties;
        }
        //获取配置文件config.properties的内容
        Properties  projectProperties = new Properties();
        try {
            projectProperties.load(ProjectConfig.class.getClassLoader().getResourceAsStream("config.properties"));


        } catch (Exception e) {
            //没加载到文件，程序要考虑退出
            e.printStackTrace();
        }
        properties = projectProperties;
        return  projectProperties;
    }
}
