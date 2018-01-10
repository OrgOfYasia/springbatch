package com.yasia.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication(scanBasePackages="com.yasia.batch")
public class BatchDemo1Application {

	public static void main(String[] args) {
		System.out.println("");
		SpringApplication.run(BatchDemo1Application.class, args);

	}

}
