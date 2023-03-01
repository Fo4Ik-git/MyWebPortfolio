package com.fo4ik.mySite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class MySiteApplication {

	private static final Logger log = LoggerFactory.getLogger(MySiteApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MySiteApplication.class, args);
		log.warn("Start application");
	}

}
