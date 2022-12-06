package com.example.redis.demo;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
@EnableConfigurationProperties(Credentials.class)
public class RedisDemoApplication {


    private static StatefulRedisConnection<String, String> connection;

    private static Credentials credentials;
    private static Logger logger = LoggerFactory.getLogger(RedisDemoApplication.class);


    public RedisDemoApplication(Credentials credentials, StatefulRedisConnection<String, String> connection) {
        this.credentials = credentials;
        this.connection = connection;
    }


    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(RedisDemoApplication.class, args);


        while (true) {
            Thread.sleep(2000);
            try {
                RedisCommands<String, String> syncCommands = connection.sync();
                logger.info("Klucz z bazy nr 1 -> " + syncCommands.get("1"));
            } catch (Exception e) {
                logger.info("connection error");
            }
        }
    }

    @Bean
    @RefreshScope
    StatefulRedisConnection<String, String> redisClient() {
        try {
            String URI = "redis://" + this.credentials.getPassword() + "@localhost:55000/0";
            RedisClient redisClient = RedisClient.create(URI);
            StatefulRedisConnection<String, String> connect = redisClient.connect();
            return connect;
        }
        catch(Exception e) {
            return null;
        }

    }

}
