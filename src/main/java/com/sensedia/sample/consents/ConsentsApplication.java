package com.sensedia.sample.consents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@SpringBootApplication
@EnableReactiveMongoAuditing
public class ConsentsApplication {

  public static void main(String[] args) {
    SpringApplication.run(ConsentsApplication.class, args);
  }
}
