package com.springcloudconfig.client;

import com.springcloudconfig.client.properties.DataSourceConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import javax.annotation.PostConstruct;

@SpringBootApplication
@Slf4j
public class UserApplication {
	@Value("${spring.jpa.hibernate.ddl-auto}")
	private String hbmToDLL;
	@Autowired
	private DataSourceConfigProperties dataSourceProperties;
	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

	@PostConstruct
	public void afterInit(){
		log.info("DataSourceConfigProperties : {}", dataSourceProperties);
		log.info("hbmToDLL : {}", hbmToDLL);
	}

}
