package com.netology.cloud;

import com.netology.cloud.config.CorsProperties;
import com.netology.cloud.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({StorageProperties.class, CorsProperties.class})
public class CloudServiceBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudServiceBackendApplication.class, args);
	}

}
