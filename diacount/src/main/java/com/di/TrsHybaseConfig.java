package com.di;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix="di.params")
/**
 * 交换文件的配置信息
 * @author jiahh 2015年5月22日
 *
 */
public class TrsHybaseConfig {

	// 数据库IP
	public String hybasehost = "192.168.0.27";
	// 数据库端口
	public int hybaseport = 5566;
	// 用户名
	public String hybaseuser = "admin";
	// 密码
	public String hybasepassword = "trsadmin";

	// 默认测试数据库名称
	public String tablename = "DI_PAT_DI20150705_test";
	// 默认测试数据库名称
	public String tablename_pat = "DATA_PAT_20150901";
	
	// CKM数据库IP
	public String ckmurl = "http://10.10.1.17:8060";
	// CKM数据库IP
	public String ckmhost = "10.10.1.17";
	// CKM数据库IP
	public String ckmport = "8060";
	// CKM用户名
	public String ckmuser = "admin";
	// CKM密码
	public String ckmpassword = "trsadmin";
	// 测试数据地址
	public String testdatapath = "D:\\DI\\HybaseTest\\";
	// 人工检索表达式地址
	public String querydatapath = "D:\\DI\\HybaseQuery\\";
	
	// 人工检索表达式地址
	public String tablename_pat_mongo = "patent";
	
//	_testDI. _sHost = "10.10.1.17"; 
//	_testDI. _sHost = "192.168.0.24"; 


}
