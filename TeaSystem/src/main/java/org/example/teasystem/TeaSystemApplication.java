package org.example.teasystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;

@SpringBootApplication(exclude = {
        ElasticsearchRepositoriesAutoConfiguration.class,
        RedisRepositoriesAutoConfiguration.class
})
public class TeaSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeaSystemApplication.class, args);
    }

}
