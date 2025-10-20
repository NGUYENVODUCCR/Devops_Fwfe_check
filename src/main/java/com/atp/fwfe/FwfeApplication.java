package com.atp.fwfe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FwfeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FwfeApplication.class, args);
	}

}
