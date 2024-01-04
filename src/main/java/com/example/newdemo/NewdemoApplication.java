package com.example.newdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"boot.registration"} , exclude = {SecurityAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class})
@EnableJpaRepositories(basePackages = "com.example.newdemo.Repository")
@ComponentScan(basePackages = "com.example.newdemo.Service")
public class NewdemoApplication {

	public static void main(String[] args) {
		System.out.println("PMS");
		SpringApplication.run(NewdemoApplication.class, args);
	}

}
