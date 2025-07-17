package com.sensedia.sample.consents;

import org.springframework.boot.SpringApplication;

import com.sensedia.sample.consents.config.TestConfig;

public class TestConsentsApplication {

    public static void main(String[] args) {
        SpringApplication.from(ConsentsApplication::main)
                .with(TestConfig.class)
                .run(args);
    }
}