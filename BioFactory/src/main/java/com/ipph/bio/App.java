package com.ipph.bio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Hello world!
 *
 */
/**
 * springboot快速启动项
 * 可以使用其他方式启动，通过maven可以进行控制
 * @author jiahh
 *
 */
@SpringBootApplication
@EnableConfigurationProperties
public class App {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(App.class, args);
	}
}
