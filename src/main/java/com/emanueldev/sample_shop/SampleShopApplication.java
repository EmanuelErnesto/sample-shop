package com.emanueldev.sample_shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SampleShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(SampleShopApplication.class, args);
	}

}
