package com.di.util;

import java.net.UnknownHostException;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.Mongo;
import com.mongodb.MongoClientOptions;

@Configuration
//@EnableConfigurationProperties(MongoProperties.class)
/**
 *  * jiahh:spring关于mongo的安全认证在mongo3.0以后有一个bug，默认使用SCRAM-SHA-1且硬编码不允许使用MONGODB-CR
 * 所以得重写spring代码，要么反编译要么替代，本方法采用替代形式替换MongoAutoConfiguration、MongoProperties两个类
 * @author jiahh 2015年11月17日
 *
 */
public class MongoAutoConfiguration {
  @Autowired
  private MongoProperties properties;
  @Autowired(required = false)
  private MongoClientOptions options;
  private Mongo mongo;
  @PreDestroy
  public void close() {
    if (this.mongo != null) {
      this.mongo.close();
    }
  }
  @Bean
  public Mongo mongo() throws UnknownHostException {
    this.mongo = this.properties.createMongoClient(this.options);
    return this.mongo;
  }
}