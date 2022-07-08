package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {

    private static final String CONFIG_PATH = "src/test/resources/config.properties";
    public static Properties loadDefault() throws IOException {
        return load(CONFIG_PATH);
    }

    public static Properties load(String path) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(path));
        return properties;
    }


}
