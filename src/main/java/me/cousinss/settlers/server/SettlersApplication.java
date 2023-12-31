package me.cousinss.settlers.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "me.cousinss.settlers.client")
@ComponentScan(value = "me.cousinss.settlers.server")
public class SettlersApplication {

    public static void main(String[] args) {
        SpringApplication.run(SettlersApplication.class, args);
    }

}
