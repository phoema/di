package com.di.web;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.di.TrsHybaseConfig;
import com.di.service.CKMService;
import com.di.service.HybaseService;
import com.google.common.base.Strings;

/**
 * 本Controller针对原始文件进行初始的入库操作
 * 
 * @author jiahh 2015年5月13日
 *
 */
@RestController
@Slf4j
@RequestMapping("/izhiliao")
public class iZhiLiaoController {

	// yyyymmdd
	private long datetime = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date()));

	@Autowired
	TrsHybaseConfig hybaseConfig;
	@Autowired
	CKMService ckmService;
	@Autowired
	HybaseService hybaseService;
	
	@RequestMapping("/")
	public String help() {
 		String help = "";
		help += "/excute \r\n" + "执行符合条件的记录的文件导出 \r\n</br>";
		return help;
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @param persent
	 * @param columns
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/search")
	public List<HashMap> search(String key,String value,String persent,String columns) throws Exception {
		// 发现hybase加入字符和字符就异常
		key = key == null? "ABSO" : key;
		value = value == null? "公开了一种 基于 视频解码 设计实现远程服务器管理的方法" : value;
		columns = columns == null? "PNO;TIO;ABSO" : columns;
		int per = 21;
		if(Strings.isNullOrEmpty(persent)){
			per = 21;
		}else{
			per = Integer.parseInt(persent);
		}
		List<HashMap> list = hybaseService.search(hybaseConfig.tablename,key, value, per,columns,hybaseConfig);
		return list;
	}
	/**
	 * http://192.168.13.123:8085/ckmtest/izhiliao/wordsearch?key=ABSO&persent=21&text=控制模板和管理模块通过无线传感网络连接在一起
	 * 等效
	 * http://192.168.13.123:8085/ckmtest/ckm/word?num=8&text=控制模板和管理模块通过无线传感网络连接在一起
	 * http://192.168.13.123:8085/ckmtest/izhiliao/search?key=ABSO&persent=21&value=无线传感网络 管理模块 连接
	 * @param key 待检索字段：如果为空 取ABSO
	 * 名称:TIO;技术领域:TFO;背景技术:TBO；发明内容:ISO；具体实施方式:SEO；附图说明:DDO；权利要求:CLO；摘要:ABSO;
	 * @param text 待检文本
	 * @param persent 相似程度 21-99(超过50，空结果概率较大) 如果为空 取21
	 * @param columns 返回列 PNO ANO PD AD TIO ABSO CLO FTO 如果为空 取PNO;TIO;ABSO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/wordsearch")
	public List<HashMap> wordsearch(String key,String text,String persent,String columns) throws Exception {
		String[] array = ckmService.extractKeywords(text,8);
		String value = "";
		for(String word : array){
			value +=word + " ";
		}
		return this.search(key, value, persent, columns);
	}

	/**
	 * 通过DI项目公共API进行专利检索请求
	 * @param query
	 * @param extField TFO,TBO,ISO,SEO,DDO,CLO的子集
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	@RequestMapping("/searchcookie")
	public String searchcookie(String query,String extField) throws ClientProtocolException, IOException{
		String webhost = "http://192.168.0.75/";
		// TODO test
		query = "(((专利权人='专利') OR (标题='专利') OR (申请人='专利') OR (摘要='专利') OR (发明人='专利')))";
		if(Strings.isNullOrEmpty(extField)){
			extField ="&select-key:extField=TFO,TBO,ISO,SEO,DDO,CLO";
		}else{
			extField = "";
		}
		//
		String url = "http://192.168.0.82:8085/ckmtest/gwssi/order?readColumn=AD;PNO;TIO;ABSO&&strWhere=%s&&sortWhere=%s";
		// 替换为DI公共接口
		url = "txnPatentImgTextListRecord.ajax?select-key:thesaurus=&select-key:cross=&select-key:buttonItem=&select-key:expressCN2=&attribute-node:patent_start-row=1&attribute-node:patent_page-row=10&attribute-node:patent_sort-column=-PD&select-key:expressCN=";
		url = webhost + url + URLEncoder.encode(query) + extField;
		
		// 创建HttpClient实例
		AbstractHttpClient httpclient = new DefaultHttpClient();
		AbstractHttpClient httpclient2 = new DefaultHttpClient();
		// 创建Get方法实例
		HttpGet httpgets = new HttpGet(
				URI.create("http://192.168.0.75//txn999999.do?username=admin&password=gwssiadmin"));
		HttpGet httpgets2 = new HttpGet();

		// 创建Get方法实例
		httpgets.setURI(URI.create("http://192.168.0.75//txn999999.do?username=admin&password=gwssiadmin"));
		
		
		HttpResponse response = null;
		response = httpclient.execute(httpgets);
		httpgets.setURI(URI.create(url));
		httpclient2.setCookieStore(httpclient.getCookieStore());
		response = httpclient2.execute(httpgets);
		response.getAllHeaders();
		String responseString = EntityUtils.toString(response.getEntity());
		httpgets.releaseConnection();
		httpclient.getConnectionManager().closeIdleConnections(0, TimeUnit.MILLISECONDS);
		httpclient2.getConnectionManager().closeIdleConnections(0, TimeUnit.MILLISECONDS);

		return responseString;
	}
}
