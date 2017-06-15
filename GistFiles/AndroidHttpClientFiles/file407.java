package com.xxx.sample.spring.cloud.service.cas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by Orrin on 2017/1/20.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
public class ProducerCasApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProducerCasApplication.class, args);
	}
}