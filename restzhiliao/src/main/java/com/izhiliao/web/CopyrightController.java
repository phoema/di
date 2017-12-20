package com.izhiliao.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.izhiliao.TrsHybaseConfig;
import com.izhiliao.service.GwssiService;
import com.izhiliao.service.HybaseService;
import com.izhiliao.util.TRSResult;

/**
 * 本Controller针对原始文件进行初始的入库操作
 * 
 * @author jiahh 2015年5月13日
 *
 */
@RestController
@Slf4j
@RequestMapping("/copyright")
public class CopyrightController {

	// yyyymmdd
	private long datetime = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date()));

	@Autowired
	TrsHybaseConfig hybaseConfig;

	@Autowired
	HybaseService hybaseService;

	@Autowired
	GwssiService gwssiService;
	

	@RequestMapping("/")
	public String help() {
		String help = "";
		help += "/copyright \r\n" + "执行符合条件的记录的文件导出 \r\n</br>";
		return help;
	}

	/**
	 * 
	 * @param Query
	 * @param Columns
	 * @param Sort
	 * @param start 非必须项 默认0 检索列表从第几条返回 第一条为0
	 * @param recordnum
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/softsearch")
	public TRSResult softsearch(String Query, String Columns, String Sort, String start, String recordcount) throws Exception {
		int startnum = 0;
		int recordnum = 10;
		if (!Strings.isNullOrEmpty(start)) {
			startnum = Integer.parseInt(start);
		}
		if (!Strings.isNullOrEmpty(recordcount)) {
			recordnum = Integer.parseInt(recordcount);
		}
		if (Strings.isNullOrEmpty(Sort)) {
			Sort = null;
		}
		if (Strings.isNullOrEmpty(Columns)) {
			Columns = "SWID;RN;CTN;SWFN;SWSN;SWV;PDF;PYF;SWP;RD;RY;LBNO";
		}

		// 版权检索
		TRSResult result = hybaseService.search("DATA_20170309_RZW",Query, Columns, Sort, startnum, recordnum);

		return  result;
	}
	@RequestMapping("/worksearch")
	public TRSResult worksearch(String Query, String Columns, String Sort, String start, String recordcount) throws Exception {
		int startnum = 0;
		int recordnum = 10;
		if (!Strings.isNullOrEmpty(start)) {
			startnum = Integer.parseInt(start);
		}
		if (!Strings.isNullOrEmpty(recordcount)) {
			recordnum = Integer.parseInt(recordcount);
		}
		if (Strings.isNullOrEmpty(Sort)) {
			Sort = null;
		}
		if (Strings.isNullOrEmpty(Columns)) {
			Columns = "SZID;HID;ANM;RD;RY;TYPE;OWNER;COUNTRY;PROVINCE;CITY;AUTHOR;FD;FPD;RN;PD;PY;FY;FPY;LBNO";
		}

		// 版权检索
		TRSResult result = hybaseService.search("DATA_20170104_RZP",Query, Columns, Sort, startnum, recordnum);

		return result;
	}
	@RequestMapping("/softcategory")
	public Map<String, Map<String, Long>> softcategory(String Query, String key, String categorynum) throws Exception {
		String table = "DATA_20170309_RZW";
		if (Strings.isNullOrEmpty(key)) {
			key = "PYF;RY";
		}
		if (Strings.isNullOrEmpty(categorynum)) {
			categorynum = "5";
		}
		Map<String, Map<String, Long>> result = gwssiService.category(table, Query, key, categorynum);
		return result;
	}
	@RequestMapping("/workcategory")
	public Map<String, Map<String, Long>> workcategory(String Query, String key, String categorynum) throws Exception {
		String table = "DATA_20170104_RZP";
		if (Strings.isNullOrEmpty(key)) {
			key = "FPY;FY";
		}
		if (Strings.isNullOrEmpty(categorynum)) {
			categorynum = "5";
		}
		Map<String, Map<String, Long>> result = gwssiService.category(table, Query, key, categorynum);
		return result;
	}
	
	

}
