package com.di.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.gwssi.itface.Constants;
import cn.gwssi.itface.DataBaseFactory;
import cn.gwssi.itface.Result;

import com.di.TrsHybaseConfig;
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
@RequestMapping("/test")
public class TestController {

	// yyyymmdd
	private long datetime = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date()));

	@Autowired
	TrsHybaseConfig hybaseConfig;
	@Autowired
	HybaseService hybaseService;
	
	@RequestMapping("/")
	public String help() {
		String help = "";
		help += "/excute \r\n" + "执行符合条件的记录的文件导出 \r\n</br>";
		return help;
	}

	/**
	 * 统计
	 * @param key
	 * @param value
	 * @param persent
	 * @param columns
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/category")
	public Map<String, Long> category(String key,int top) throws Exception {
		DataBaseFactory _factory = DataBaseFactory.newInstance(Constants.DB_TYPE_HYBASE, "192.168.0.27", 5566, "admin", "trsadmin") ;
		if(Strings.isNullOrEmpty(key)){
			key = "IN";
		}
		String trsHybaseTable = "DI_PAT_DI20150705_test";
		//String _sStrWhere = "ABSO#LIKE:\"公开了一种基于视频解码设计实现远程服务器管理的方法\"~41";
		String strWhere = "AD=2012";
		String readColumn= "PNO;TIO;ABSO";
		Map<String, String> params = new HashMap<String, String>();
		Result  result =_factory.getQuery().select(trsHybaseTable, readColumn, null, strWhere,null,1,10,params) ;
		params.put("category_top_num","5");
		Map<String, Map<String, Long>>  map = _factory.getQuery().categorySelect(trsHybaseTable, null, key, strWhere, params);
		Map<String, Long> map2 = null;
		for(String cat  : map.keySet()){
			 map2 = map.get(cat);
				for(String cat2  : map2.keySet()){
					System.out.print(cat2);
					System.out.print("\t");
					System.out.println(map2.get(cat2));
					 
				}

		}
		return map2;
	}
	/**
	 * 统计
	 * @param key
	 * @param value
	 * @param persent
	 * @param columns
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/order")
	public List<HashMap> order(String key,String value,String persent,String columns) throws Exception {
		key = "ABSO";
		// 发现hybase加入字符和字符就异常
		value = "公开了一种 基于 视频解码， 设计实现远程服务器管理的方法";
		key = key == null? "ABSO" : key;
		columns = columns == null? "PNO;TIO;ABSO" : columns;
		int per = 21;
		if(Strings.isNullOrEmpty(persent)){
			per = 21;
		}else{
			per = Integer.parseInt(persent);
		}
		//HybaseService service = new HybaseService(hybaseConfig.host,hybaseConfig.port,hybaseConfig.user,hybaseConfig.password);
		List<HashMap> list = hybaseService.search(key, value, per,columns);
		return list;
	}


}
