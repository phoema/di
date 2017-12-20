package com.di.web;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.gwssi.itface.Constants;
import cn.gwssi.itface.DataBaseFactory;
import cn.gwssi.itface.Result;

import com.di.TrsHybaseConfig;
import com.di.service.GwssiService;
import com.di.util.Category;
import com.di.util.ExcelUtil;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.trs.hybase.client.TRSRecord;

/**
 * 本Controller针对原始文件进行初始的入库操作
 * 
 * @author jiahh 2015年5月13日
 *
 */
@RestController
@Slf4j
@RequestMapping("/gwssi")
public class GwssiController {

	// yyyymmdd
	private long datetime = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date()));

	@Autowired
	TrsHybaseConfig hybaseConfig;
	@Autowired
	GwssiService gwssiService;
	
	@RequestMapping("/")
	public String help() {
		String help = "";
		help += "/ \r\n" + "执行gwssiAPI相关操作 \r\n</br>";
		help += "/order \r\n" + "执行gwssiAPI排序操作 \r\n</br>";
		help += "/category \r\n" + "执行gwssiAPI统计操作 \r\n</br>";
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
	public List<Category> category(String key,String strWhere,String top) throws Exception {
		//public Map<String, Map<String, Long>> category(String key,String strWhere,String top) throws Exception {
		DataBaseFactory _factory = DataBaseFactory.newInstance(Constants.DB_TYPE_HYBASE, hybaseConfig.hybasehost, hybaseConfig.hybaseport, hybaseConfig.hybaseuser, hybaseConfig.hybasepassword) ;
		if(Strings.isNullOrEmpty(key)){
			key = "PDB";
		}
		String trsHybaseTable = hybaseConfig.tablename;
		//String _sStrWhere = "ABSO#LIKE:\"公开了一种基于视频解码设计实现远程服务器管理的方法\"~41";
		//String strWhere = "AD=2012";
		strWhere = strWhere ==null ? "" : strWhere;
		Map<String, String> params = new HashMap<String, String>();
		if(!Strings.isNullOrEmpty(top)){
			params.put("category_top_num",top);
		}
		
		Map<String, Map<String, Long>>  map = _factory.getQuery().categorySelect(trsHybaseTable, null, key, strWhere, params);
		Map<String, Long> map2 = null;
		//return map;
		List<Category> list = new ArrayList<Category>();
		for(String cat  : map.keySet()){
			map2 = map.get(cat);
			if(map2 == null) continue;
			for(String cat2  : map2.keySet()){
				Category cate = new Category();
				cate.key = cat2;
				cate.value = map2.get(cat2);
				list.add(cate);
//				System.out.print(cat2);
//				System.out.print("\t");
//				System.out.println(map2.get(cat2));
				 
			}

		}
		return list;
	}
	
	@RequestMapping("/category2")
	public Map<String, Map<String, Long>> category2(String key, String strWhere, String top) throws Exception {
		DataBaseFactory _factory = DataBaseFactory.newInstance(Constants.DB_TYPE_HYBASE, hybaseConfig.hybasehost,
				hybaseConfig.hybaseport, hybaseConfig.hybaseuser, hybaseConfig.hybasepassword);
		if (Strings.isNullOrEmpty(key)) {
			key = "PDB";
		}
		String trsHybaseTable = hybaseConfig.tablename;
		// String _sStrWhere = "ABSO#LIKE:\"公开了一种基于视频解码设计实现远程服务器管理的方法\"~41";
		// String strWhere = "AD=2012";
		strWhere = strWhere == null ? "" : strWhere;
		Map<String, String> params = new HashMap<String, String>();
		if (!Strings.isNullOrEmpty(top)) {
			params.put("category_top_num", top);
		}

		Map<String, Map<String, Long>> map = _factory.getQuery().categorySelect(trsHybaseTable, null, key, strWhere,
				params);
		Map<String, Long> map2 = null;
		return map;

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
	public ArrayList<TRSRecord> order(String readColumn,String strWhere,String sortWhere) throws Exception {
		String trsHybaseTable = hybaseConfig.tablename;
		ArrayList<TRSRecord> list = new ArrayList<TRSRecord>();
		//readColumn = readColumn == null? "PNO;TIO;ABSO" : readColumn;
		readColumn = readColumn == null? "PNO;TIO;ABSO" : readColumn;
		strWhere = strWhere == null? "PD=2014" : strWhere;
		sortWhere = sortWhere == null? "RELEVANCE" : sortWhere;
		Result result = gwssiService.search(trsHybaseTable,readColumn, strWhere, sortWhere);
		if(result!=null){
			list = result.getNumRecord(1, 10);
		}
		return list;
	}

	
	/**
	 * 为测试工作准备测试数据 start
	 */
	String[] catarray = {"AD","PDT","IPC","IPCS","IPCC","AY","PY","IN","EPRD"};
	String[] orderarray = {"RELEVANCE","+RELEVANCE","AD","+AD","AD","+AD","PD","+PD","EPRD","+EPRD","INCO","+INCO","IPCSCC","+IPCSCC","CLN","+CLN","DEPC","+DEPC","DC","+DC"};
	String[] query = {
			"PD=2014"
			,"PDT=1"
			,"PDT=2"
			,"PDT=3"
			,"IPCS=A"
			,"IPCS=B"
			,"IPCS=C"
			,"IPCS=D"
			,"IPCS=E"
			,"IPCS=F"
			,"IPCS=G"
			,"IPCS=H"
			,"AD=2013"};
	@RequestMapping("/testdata")
	public String testdata() throws Exception {
		for (String strWhere : query) {
			this.category(strWhere);
		}

		return "OK";
	}
	/**
	 * 生成统计文件
	 * @param strWhere
	 * @throws Exception
	 */
	public void category(String strWhere) throws Exception{
		List<Category> map = null;
		// 判断根目录是否存在
		File rootfile = new File(hybaseConfig.testdatapath);
		if (!rootfile.exists()) {
			Files.createParentDirs(rootfile);
			rootfile.mkdirs();
		}

		// 在F盘创建测试.xls文档，并在该文档中的第一个位置创建名称为第一页的工作表。
		WritableWorkbook book = Workbook.createWorkbook(new File(hybaseConfig.testdatapath+"CategoryResult-"+strWhere.replace("=", "")+".xls"));
		// 第0页作为统计页
		WritableSheet sheet0 = book.createSheet("概览", 0);
		ExcelUtil.createSheetColumn(sheet0, 1, 0, "Query");
		ExcelUtil.createSheetColumn(sheet0, 2, 0, strWhere);
		ExcelUtil.createSheetColumn(sheet0, 3, 0, "Category");
		ExcelUtil.createSheetColumn(sheet0, 4, 0, "Cost(ms)");
		//
		for(int i=0;i<catarray.length;i++){
			long start = System.currentTimeMillis();
			map = this.category(catarray[i],strWhere, "1000");
			long cost = System.currentTimeMillis()-start;

			WritableSheet sheet = book.createSheet(catarray[i], i+1);
			System.out.println(catarray[i]);
			// 统计列及花费时间
			ExcelUtil.createSheetColumn(sheet0, 3, i+1, catarray[i]);
			ExcelUtil.createSheetColumn(sheet0, 4, i+1, String.valueOf(cost));

			for (int j = 0;j < map.size();j ++) {
				Category cat = map.get(j);
				ExcelUtil.createSheetColumn(sheet, 1, j, cat.key);
				ExcelUtil.createSheetColumn(sheet, 2, j, cat.value.toString());
				System.out.println(cat.key + "\t" + cat.value);
			}
		}
		book.write();
		book.close();
	}
	/**
	 * hybaseConfig.querydatapath+"CategoryResult-.xls"
	 * @throws Exception
	 */
	@RequestMapping("/fromexcelSearch")
	public void ExcuteQueryFromExcel() throws Exception{
		
		long start = System.currentTimeMillis();
		// 判断根目录是否存在
		File rootfile = new File(hybaseConfig.querydatapath);
		if (!rootfile.exists()) {
			Files.createParentDirs(rootfile);
			rootfile.mkdirs();
		}

		// 在F盘创建测试.xls文档，并在该文档中的第一个位置创建名称为第一页的工作表。
		Workbook book = Workbook.getWorkbook(new File(hybaseConfig.querydatapath+"CategoryResult-.xls"));
		WritableWorkbook bookCreate = Workbook.createWorkbook(new File(hybaseConfig.querydatapath+"CategoryResult-Modify.xls"),book);
		// 第0页作为统计页
		//WritableSheet sheet1 = bookCreate.createSheet("1000-10000", 1);
		//WritableSheet sheet2 = bookCreate.createSheet("10001-100000", 2);
		for(WritableSheet sheet : bookCreate.getSheets()){
			String tableName = hybaseConfig.tablename;
			// 如果符合模板
			if("TableName".equals(sheet.getCell(0, 0).getContents()) && "Key".equals(sheet.getCell(0, 1).getContents()) && "Query".equals(sheet.getCell(1, 1).getContents()) && "PRECount".equals(sheet.getCell(2, 1).getContents())){
				{
					tableName = sheet.getCell(1, 0).getContents();
					for(int row = 2; row <sheet.getRows();row++){
						Cell cell = sheet.getCell(1, row);
						// 如果有单引号，则转义
						//String strWhere = cell.getContents().replace("'", "\\'");
						Cell key = sheet.getCell(0, row);
						String strWhere = cell.getContents();
						Result result= null; 
						try{
							start = System.currentTimeMillis();
							result = gwssiService.search(tableName, "", strWhere,key.getContents());
							// result
							ExcelUtil.createSheetColumn(sheet, 3, row, String.valueOf(result.getNumFound()));
							// TimeCost
							ExcelUtil.createSheetColumn(sheet, 5, row, String.valueOf(System.currentTimeMillis()-start));

						}catch(Exception ex){
							// exception
							ExcelUtil.createSheetColumn(sheet, 4, row, ex.getMessage());

						}
					}
				}
			}
		}
		bookCreate.write();
		bookCreate.close();

		log.info("cost:" + (System.currentTimeMillis() - start));
	}
	/**
	 * hybaseConfig.querydatapath+"CategoryResult2-.xls"
	 * @throws Exception
	 */
	@RequestMapping("/fromexcelCategory")
	public void ExcuteCategoryFromExcel() throws Exception{
		
		long start = System.currentTimeMillis();
		// 判断根目录是否存在
		File rootfile = new File(hybaseConfig.querydatapath);
		if (!rootfile.exists()) {
			Files.createParentDirs(rootfile);
			rootfile.mkdirs();
		}

		// 在F盘创建测试.xls文档，并在该文档中的第一个位置创建名称为第一页的工作表。
		Workbook book = Workbook.getWorkbook(new File(hybaseConfig.querydatapath+"CategoryResult2-.xls"));
		WritableWorkbook bookCreate = Workbook.createWorkbook(new File(hybaseConfig.querydatapath+"CategoryResult2-CategoryModify.xls"),book);
		//WritableSheet sheet1 = bookCreate.getSheet("1000-10000");
		for(WritableSheet sheet : bookCreate.getSheets()){
			// 如果符合模板
			if("Key".equals(sheet.getCell(0, 0).getContents()) && "Query".equals(sheet.getCell(1, 0).getContents()) && "PRECount".equals(sheet.getCell(2, 0).getContents())){
				for(int row = 1; row <sheet.getRows();row++){
					Cell key = sheet.getCell(0, row);
					Cell cell = sheet.getCell(1, row);
					// 如果有单引号，则转义
					String strWhere = cell.getContents();
					
					Map<String, Map<String, Long>> result= null; 
					try{
						start = System.currentTimeMillis();
						result = gwssiService.category(strWhere,key.getContents(), "5");
						// result
						ExcelUtil.createSheetColumn(sheet, 3, row, result.toString());
						// TimeCost
						ExcelUtil.createSheetColumn(sheet, 5, row, String.valueOf(System.currentTimeMillis()-start));

					}catch(Exception ex){
						// exception
						ExcelUtil.createSheetColumn(sheet, 4, row, ex.getMessage());
					}
				}
			}
		}

		bookCreate.write();
		bookCreate.close();

		log.info("cost:" + (System.currentTimeMillis() - start));
	}

}
