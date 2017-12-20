package com.ipph.bio.file;

import java.io.IOException;
import java.util.Properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

@Data
@ConfigurationProperties(prefix="mongodb.datasource")
@Slf4j
public class MongoProp {

	public String mongosIP;
	public String mongosPort;
	public String DB;
	public String Collection;
	public int poolSize;
	public int blockSize;
	
	public MongoProp(){
		Properties properties = new Properties();
		Resource resource = new ClassPathResource("config.properties");
		try {
			properties = PropertiesLoaderUtils.loadProperties(resource);
			mongosIP = properties.getProperty("mongosIP");
			log.info("mongosIP2" + mongosIP);
			mongosPort = properties.getProperty("mongosPort");
			DB = properties.getProperty("DB");
			Collection = properties.getProperty("Collection");
			poolSize = Integer.parseInt(properties.getProperty("poolSize"));
			String block = properties.getProperty("blockSize");
			blockSize = Integer.parseInt(properties
					.getProperty("blockSize"));
			log.error("poolSize:" + poolSize + "-blockSize:" + blockSize);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
