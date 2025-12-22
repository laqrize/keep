package pl.ros.keep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "pl.ros.keep.core.jpa")
@EnableMongoRepositories(basePackages = "pl.ros.keep.core.mongo")
public class KeepApplication {

    public static void main(String[] args) {
        SpringApplication.run(KeepApplication.class, args);
    }

}
