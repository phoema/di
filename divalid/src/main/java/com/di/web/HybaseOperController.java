package com.di.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.di.TrsHybaseConfig;
import com.di.service.HybaseService;
import com.google.common.base.Strings;
import com.trs.hybase.client.TRSConnection;
import com.trs.hybase.client.TRSDatabase;
import com.trs.hybase.client.TRSDatabaseColumn;
import com.trs.hybase.client.TRSException;

/**
 * 本Controller针对原始文件进行初始的入库操作
 * 
 * @author jiahh 2015年5月13日
 *
 */
@RestController
@Slf4j
@RequestMapping("/hybase")
public class HybaseOperController {

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
	 *
	 * 初始化测试数据
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/search")
	public List<HashMap> search(String key,String value,String persent,String columns) throws Exception {
		key = "ABSO";
		// 发现hybase加入字符和字符就异常
		value = "公开了一种 基于 视频解码 设计实现远程服务器管理的方法";
		columns = "PNO;TIO;ABSO";
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
	@RequestMapping("/test")
	public TRSDatabase test() throws Exception {

		boolean success = false;
		TRSConnection conn = null;
		conn = new TRSConnection("http://"+hybaseConfig.hybasehost+":"+hybaseConfig.hybaseport,hybaseConfig.hybaseuser,hybaseConfig.hybasepassword,null);	
		
        TRSDatabase database = new TRSDatabase("demo_1");
        database.addColumn(new TRSDatabaseColumn("标题", TRSDatabaseColumn.TYPE_PHRASE));
        database.addColumn(new TRSDatabaseColumn("正文", TRSDatabaseColumn.TYPE_DOCUMENT));
        try {
        	success = conn.createDatabase(database);
        } catch (TRSException e) {
            System.out.println("ErrorCode: " + e.getErrorCode());
            System.out.println("ErrorString: " + e.getErrorString());
        } finally {
            conn.close();
        }
        return database;
	}

}
