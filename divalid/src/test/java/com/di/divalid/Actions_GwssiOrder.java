package com.di.divalid;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import lrapi.lr;

/**
 * 模拟《概览》场景 Actions_GwssiOrder
 */
public class Actions_GwssiOrder {
	private String url =  "http://192.168.13.123:8085/ckmtest/gwssi/order";
	private String[] orderarray = {"RELEVANCE", "+RELEVANCE", "AD", "+AD", "AD", "+AD", "PD", "+PD", "EPRD", "+EPRD", "INCO","+INCO", "IPCSCC", "+IPCSCC", "CLN", "+CLN", "DEPC", "+DEPC", "DC", "+DC"};
	private String[] catarray = {"AD", "PDT", "IPC", "AY", "PY", "IN", "EPRD", "CPC", "LC", "ILSC", "AS"};

	String testdatapath = "D:\\DI\\HybaseTest\\";
	private String readColumn = null;//AD;PNO;TIO
	private int totalExecuteTime = 0;

	public int init() throws Throwable {
		return 0;
	}

	public int end() throws Throwable {
		return 0;
	}// end of end

	public int action() {

		List<String> allQuery = null;
		try {
			allQuery = this.getAllQuery();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//while (true) {

			for (int size = 0; size < orderarray.length; size++) {
				String sortWhere = orderarray[size];
				for (String strWhere : allQuery) {
					String httpurl = String.format(url, strWhere, sortWhere);
					System.out.println(strWhere);
					readLR(strWhere, sortWhere);
				}
			}
		//}
		// while (true) {
		// readLR();
		// }
		 return 0;
	}

	public void readLR(String strWhere,String sortWhere) {
		// lr参数设置
		lrapi.web.set_max_html_param_len("262144");
		lrapi.web.reg_save_param("response", new String[]{"LB=", "RB=", "LAST"});
		lrapi.web.reg_save_param("ResponseBody", new String[]{"LB=", "RB=", "Search=Body", "LAST"});
		// web_set_max_html_param_len("262144"); // 默认最大长度为256
		// web_reg_save_param("ResponseBody", "LB=", "RB=", "Search=Body",
		// LAST);
		// web_save_header(REQUEST, "RequestHeader");//
		// REQUEST为内置变量，保存请求的头信息，需在发送URL请求前注册使用
		//lrapi.web.reg_save_param(RESPONSE, "ResponseHeader");// RESPONSE保存响应的头信息

		// getRequest();
		//String url = "http://192.168.13.123:8082/gwssi/order?readColumn=AD;PNO;TIO;ABSO&&strWhere=%s&&sortWhere=%s";
		lr.start_transaction("Order");
		int stat = -100;
		try {
			stat = lrapi.web.submit_data("", "Action=" + url, new String[]{
					"Method=POST", "RecContentType=text/html;charset=UTF-8", "Snapshot=t7.inf", "Mode=HTML",},
					new String[]{ // ITEM DATA
					"Name=readColumn", "Value="+readColumn, lrapi.web.ENDITEM, 
					"Name=strWhere", "Value=" + strWhere,  lrapi.web.ENDITEM,
					"Name=sortWhere", "Value=" + sortWhere,lrapi.web.ENDITEM, 
					 lrapi.web.LAST});

			totalExecuteTime++;
		} catch (Exception e1) {
			lr.end_transaction("Order", lr.FAIL);
		}
		if (stat == 0) { // sucess
			lr.end_transaction("Order", lr.PASS);
		} else {
			lr.end_transaction("Order", lr.FAIL);
		}
	}
	
	/**
	 * 获取文件中的所有检索式
	 * @return
	 * @throws BiffException
	 * @throws IOException
	 */
	private List<String> getAllQuery() throws Exception{
		ArrayList<String> list = new ArrayList<String>();
		File dict = new File(testdatapath);
		File[] files = dict.listFiles();
		for(File file :files){
			Workbook book = Workbook.getWorkbook(file);
			// 获取概览检索式
			String queryStr = book.getSheet(0).getCell(2, 0).getContents();
			for (String cata : catarray) {
				Sheet sheet = book.getSheet(cata);
				if (sheet == null)
					continue;
				Cell[] cells = sheet.getColumn(1);
				for (int i = 1; i < cells.length; i++) {
					// 如果有单引号，则转义
					String subWhere = cells[i].getContents().replace("'", "\\'");
					String strWhere = queryStr + " and (" + cata + "='" + subWhere + "')";
					list.add(strWhere);
				}
			}
		}
		return list;
	}


}
