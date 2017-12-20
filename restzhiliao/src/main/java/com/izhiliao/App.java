package com.izhiliao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableConfigurationProperties
@EnableCaching  //配置和使用缓存
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
	@Bean
	public CacheManager getEhCacheManager(){
		CacheManager x =  new EhCacheCacheManager(getEhCacheFactory().getObject());
	    return  x;
	}


	@Bean
	public EhCacheManagerFactoryBean getEhCacheFactory(){
	    EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
	    factoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
	    factoryBean.setShared(true);
	    return factoryBean;
	}

}
