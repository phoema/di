package com.ipph.bio;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix="exchange")
/**
 * 交换文件的配置信息
 * @author jiahh 2015年5月22日
 *
 */
public class ExchangeConfig {

	// 导出交换文件的目标根目录
	public String basedirFasta = "C:\\BIOSEQUENCE\\FASTA";

	// 每个索引文件包含的专利件数
	public int indexPatcnt = 10000;


}
