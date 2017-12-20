package com.izhiliao.web;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.izhiliao.TrsHybaseConfig;
import com.izhiliao.service.CKMService;
import com.izhiliao.service.HybaseService;
import com.izhiliao.service.TradeService;
import com.izhiliao.util.ResultInfo;

/**
 * 本Controller针对原始文件进行初始的入库操作
 * 
 * @author jiahh 2015年5月13日
 *
 */
@RestController
@Slf4j
@RequestMapping("/trade")
public class TradeController {

	// yyyymmdd
	private long datetime = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date()));

	@Autowired
	TrsHybaseConfig hybaseConfig;
	@Autowired
	CKMService ckmService;
	@Autowired
	HybaseService hybaseService;
	@Autowired
	TradeService tradeService;
	

	@RequestMapping("/")
	public String help() {
		String help = "";
		help += "/excute \r\n" + "执行符合条件的记录的文件导出 \r\n</br>";
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
	@RequestMapping("/search")
	public List<HashMap> search(String Query, String Columns, String Sort, String start, String recordcount) throws Exception {
		int startnum = 0;
		int recordnum = 10;
		if (!Strings.isNullOrEmpty(start)) {
			startnum = Integer.parseInt(start);
		}
		if (!Strings.isNullOrEmpty(recordcount)) {
			recordnum = Integer.parseInt(recordcount);
		}
		if (Strings.isNullOrEmpty(Sort)) {
			Sort = "-FD";
		}
		if (Strings.isNullOrEmpty(Columns)) {
			Columns = "MNC;SN;FD;HNO";
		}

		// 商标检索
		List<HashMap> simpleList = hybaseService.tradeSearch(Query, Columns, Sort, startnum, recordnum);

		return simpleList;
	}

	public ResultInfo search(){
		
		
		return null;
		
	}
	@RequestMapping("/getnum")
	public void getnum() throws Exception{
		long rec = 0;
		String Query = "HN=\"瀚逸酒店管理（上海）有限公司\"";
		File file2 = new File("C:\\15内部资料.txt");
		File file = new File("C:\\15内部资料.xls");
		Workbook book = Workbook.getWorkbook(file);
		Sheet sheet = book.getSheet(0);
		Cell[] cells = sheet.getColumn(0);

		for(Cell cell : cells){
			String content = cell.getContents().replace("(", "\\(").replace(")", "\\)").replace("[", "\\[").replace("]", "\\]");
			Query = "HN=\""+ content +"\"";
			rec = tradeService.getRecordNum(Query);
			System.out.println(cell.getContents() + "\t" + rec);
			Files.append(cell.getContents() + "\t" + rec +"\r\n", file2,StandardCharsets.UTF_8);
		}
	}


}
