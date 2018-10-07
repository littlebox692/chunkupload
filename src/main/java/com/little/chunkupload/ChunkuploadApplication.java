package com.little.chunkupload;

import com.little.chunkupload.service.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class ChunkuploadApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChunkuploadApplication.class, args);
	}

	@Bean
	CommandLineRunner init(final StorageService storageService) {
		return new CommandLineRunner() {
			@Override
			public void run(String... strings) throws Exception {
				storageService.deleteAll();
				storageService.init();
			}
		};
	}
}
