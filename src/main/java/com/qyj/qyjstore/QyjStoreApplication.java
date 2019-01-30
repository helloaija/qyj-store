package com.qyj.qyjstore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.qyj.store.dao")
@ComponentScan(basePackages={"com.qyj.store"})
public class QyjStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(QyjStoreApplication.class, args);
	}

}

