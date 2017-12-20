package com.di.divalid;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import lombok.extern.slf4j.Slf4j;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import cn.gwssi.exception.DataBaseException;
import cn.gwssi.itface.Constants;
import cn.gwssi.itface.DataBaseFactory;

import com.di.App;
import com.di.TrsHybaseConfig;
import com.di.service.GwssiService;
import com.di.util.Category;
import com.di.util.ExcelUtil;
import com.di.web.GwssiController;
import com.google.common.io.Files;
import com.trs.hybase.client.TRSRecord;


/**
 * 
 * @author jiahh 2015年5月11日
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@Slf4j
public class GwssiControllerTest{
	@Autowired
	GwssiController gwssiController;
	@Autowired
	TrsHybaseConfig trsHybaseConfig;
	@Autowired
	GwssiService gwssiService;

	String categorytop = "5";
	//String[] catarray = {"AD","PDT","IPC","AY","PY","IN","EPRY","CPC","LC","ILSC","AS"};
	String[] catarray = {"AD","PDT","IPC","IPCS","IPCC","AY","PY","IN","EPRD"};
	String[] orderarray = {"RELEVANCE","+RELEVANCE","AD","+AD","AD","+AD","PD","+PD","EPRD","+EPRD","INCO","+INCO","IPCSCC","+IPCSCC","CLN","+CLN","DEPC","+DEPC","DC","+DC"};
	public GwssiControllerTest() {
		
	}

	@Test
	public void multiCategory() throws DataBaseException{
		String key = "AY;IN";
		key = "AD;PDT;IPC;IPCS;IPCC;AY;PY;IN;EPRD";
		key = "PN;PNO;PNDB;PNE;PNS;PNI;AN;ANO;ANDB;ANE;ANS;ANI";
		DataBaseFactory _factory = DataBaseFactory.newInstance(Constants.DB_TYPE_HYBASE, "192.168.0.23", 5555, "admin", "trsadmin") ;
		String trsHybaseTable = "DI_PAT_DI20150701_test";
		//String _sStrWhere = "ABSO#LIKE:\"公开了一种基于视频解码设计实现远程服务器管理的方法\"~41";
		//String strWhere = "AD=2012";
		String strWhere = "PD=2014";
		Map<String, String> params = new HashMap<String, String>();
		params.put("category_top_num","5");
		for(String one :key.split(";")){
			try{
			Map<String, Map<String, Long>>  map = _factory.getQuery().categorySelect(trsHybaseTable, null, key, "", params);

			System.out.println(one);
			System.out.println(map);
			}catch(Exception ex){
				System.out.println(one);
				System.out.println(ex);
			}
		}
	}
	@Test
	// 固定检索式分组，为自动测试排序和分组抽取数据
    public void testCategory() throws Exception
    {
		String strWhere = "PD=2014";
//		strWhere = "PDT=1";
//		strWhere = "PD=2";
//		strWhere = "PD=3";
//		strWhere = "AY=2013";
		gwssiController.category(strWhere);

    }
	@Test
	// 固定检索式分组，为自动测试排序和分组抽取数据
    public void testCategoryAll() throws Exception
    {
		gwssiController.testdata();			
    }
	@Test
    public void testOrderOnce() throws Exception
    {
		String readColumn = "IN;AD;TIO";
		String where = "Chen Xing'ai".replace("'", "\\'");
		String strWhere = "IPCS=A  AND ( IN ='"+where+"')";
		String sortWhere = "IN;AD";
		ArrayList<TRSRecord> result = gwssiController.order(readColumn, strWhere, sortWhere);
		for(int i = 0;i< result.size();i++){
			TRSRecord record = result.get(i);
			System.out.println(record.getString("AD"));
		}
    }

	@Test
    public void testOrder() throws Exception
    {
		String readColumn = "TIO";
		String strWhere = null;
		String sortWhere = null;
		ArrayList<TRSRecord> result = null;
		
		File dict = new File(trsHybaseConfig.testdatapath);
		File[] files = dict.listFiles();
		for(File file :files){
			Workbook book = Workbook.getWorkbook(file);
			// 获取概览检索式
			String queryStr = book.getSheet(0).getCell(2, 0).getContents();
			for (int size = 0; size < orderarray.length; size++) {
				sortWhere = orderarray[size];
				for (String cata : catarray) {
					Sheet sheet = book.getSheet(cata);
					Cell[] cells = sheet.getColumn(1);
					for (int i = 1; i < cells.length; i++) {
						// 如果有单引号，则转义
						String subWhere = cells[i].getContents().replace("'", "\\'");
						strWhere = queryStr + " and (" + cata + "='" + subWhere + "')";
						result = gwssiController.order(readColumn, strWhere, sortWhere);
					}
				}
			}
		}

    }
	@Test
	public void testHttpOrder() throws Exception {
		// 线程数
		int threadnum = 1;
		List<HttpThreadOrder> list = new ArrayList<HttpThreadOrder>();
		for (int i = 0; i < threadnum; i++) {
			HttpThreadOrder myThread = new HttpThreadOrder();
			Thread t1 = new Thread(myThread);
			t1.start();
			list.add(myThread);
		}
		// 持续时间
		Thread.sleep(1000 * 60 * 20);
		for (int i = 0; i < list.size(); i++) {
			list.get(i).close();
		}
		log.info("testend:" + count);


	}

	// 受保护的计数器
	private volatile int count = 0;
	/**
	 * 多线程网络访问测试Mysql
	 * @author jiahh 2015年5月8日
	 *
	 */
	class HttpThreadOrder extends Thread {
		private volatile boolean isRun = true;

		public void close() {
			this.isRun = false;
		}
		// int i=0;//1
		public void run() {		
			//
			//url = "http://192.168.0.75/txnPatentImgTextListRecord.ajax?select-key:thesaurus=&select-key:cross=&select-key:buttonItem=&select-key:expressCN2=&attribute-node:patent_start-row=1&attribute-node:patent_page-row=10&select-key:expressCN=%s&attribute-node:patent_sort-column=%s";
			//
			while (true) {
				try {
					httporder();
				}catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void httporder() throws Exception{
		//String url = "http://192.168.0.82:8085/ckmtest/gwssi/order?readColumn=AD;PNO;TIO;ABSO&&strWhere=%s&&sortWhere=%s" ;	
		String url = "http://192.168.0.82:8085/ckmtest/gwssi/order?readColumn=AD;PNO;TIO;ABSO&&strWhere=%s&&sortWhere=%s" ;	
		RestTemplate  template = new RestTemplate (); 
	
		List<String> allQuery = this.getAllQuery();
		for (int size = 0; size < orderarray.length; size++) {
			String sortWhere = orderarray[size];
			for(String strWhere : allQuery){
				String httpurl = String.format(url, strWhere, sortWhere);
				ArrayList<TRSRecord> obj = template.getForObject(httpurl, ArrayList.class);
				log.info("obj:" + obj.size());
			}
		}

	}
	
	/**
	 * 获取文件中的所有检索式
	 * @return
	 * @throws BiffException
	 * @throws IOException
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	@Test
	public void getAllQueryTest() throws Exception{
	
		long start = System.currentTimeMillis();
		List<String> list = this.getAllQuery();
		log.info("list.size()" + list.size());
	}
	/**
	 * 获取文件中的所有检索式
	 * @return
	 * @throws BiffException
	 * @throws IOException
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	@Test
	public void getAllQueryToExcel() throws Exception{
	
		long start = System.currentTimeMillis();
		// 判断根目录是否存在
		File rootfile = new File(trsHybaseConfig.querydatapath);
		if (!rootfile.exists()) {
			Files.createParentDirs(rootfile);
			rootfile.mkdirs();
		}

		// 在F盘创建测试.xls文档，并在该文档中的第一个位置创建名称为第一页的工作表。
		WritableWorkbook bookCreate = Workbook.createWorkbook(new File(trsHybaseConfig.querydatapath+"CategoryResult-.xls"));
		// 第0页作为统计页
		WritableSheet sheet1 = bookCreate.createSheet("1000-10000", 1);
		WritableSheet sheet2 = bookCreate.createSheet("10001-100000", 2);
		ExcelUtil.createSheetColumn(sheet1, 0, 0, "Key");
		ExcelUtil.createSheetColumn(sheet1, 1, 0, "Query");
		ExcelUtil.createSheetColumn(sheet1, 2, 0, "PRECount");
		ExcelUtil.createSheetColumn(sheet1, 3, 0, "Result");
		ExcelUtil.createSheetColumn(sheet1, 4, 0, "Exception");
		ExcelUtil.createSheetColumn(sheet1, 5, 0, "TimeCost");

		ExcelUtil.createSheetColumn(sheet2, 0, 0, "Key");
		ExcelUtil.createSheetColumn(sheet2, 1, 0, "Query");
		ExcelUtil.createSheetColumn(sheet2, 2, 0, "PRECount");
		ExcelUtil.createSheetColumn(sheet2, 3, 0, "Result");
		ExcelUtil.createSheetColumn(sheet2, 4, 0, "Exception");
		ExcelUtil.createSheetColumn(sheet2, 5, 0, "TimeCost");

		int sheet1num = 1;
		int sheet2num = 1;
		File dict = new File(trsHybaseConfig.testdatapath);
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
				Cell[] cells2 = sheet.getColumn(2);
				for (int i = 0; i < cells.length; i++) {
					int count = Integer.parseInt(cells2[i].getContents());
					// 如果有单引号，则转义
					String subWhere = cells[i].getContents().replace("'", "\\'");
					String strWhere = queryStr + " and (" + cata + "='" + subWhere + "')";
					//1000 - 10000
					if(1000< count && count<=10000){
						ExcelUtil.createSheetColumn(sheet1, 0, sheet1num, "AY;PDT");
						ExcelUtil.createSheetColumn(sheet1, 1, sheet1num, strWhere);
						ExcelUtil.createSheetColumn(sheet1, 2, sheet1num, String.valueOf(count));
						sheet1num++;
					}else if(10000< count && count<=100000){
						ExcelUtil.createSheetColumn(sheet2, 0, sheet2num, "AY;PDT");
						ExcelUtil.createSheetColumn(sheet2, 1, sheet2num, strWhere);
						ExcelUtil.createSheetColumn(sheet2, 2, sheet2num, String.valueOf(count));
						sheet2num++;
					}


				}
			}
		}
		bookCreate.write();
		bookCreate.close();

		// 做两份，一份检索用，一份统计用
		Files.copy(new File(trsHybaseConfig.querydatapath+"CategoryResult-.xls"), new File(trsHybaseConfig.querydatapath+"CategoryResult2-.xls"));
//		Workbook book = Workbook.getWorkbook(new File(trsHybaseConfig.querydatapath+"CategoryResult-.xls"));
//		WritableWorkbook bookCreate2 = Workbook.createWorkbook(new File(trsHybaseConfig.querydatapath+"CategoryResult2-.xls"),book);
//		bookCreate2.write();
//		bookCreate2.close();

		log.info("cost:" + (System.currentTimeMillis() - start));
	}
	/**
	 * 获取文件中的所有检索式,并执行检索结果，调整为controller
	 * @return
	 * @throws BiffException
	 * @throws IOException
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	@Test
	public void ExcuteQueryFromExcel() throws Exception{
	
		gwssiController.ExcuteQueryFromExcel();
	}
	@Test
	public void ExcuteCategoryFromExcel() throws Exception{
	
		gwssiController.ExcuteCategoryFromExcel();
	}
	/**
	 * 获取文件中的所有检索式
	 * @return
	 * @throws BiffException
	 * @throws IOException
	 */
	private List<String> getAllQuery() throws BiffException, IOException{
		ArrayList<String> list = new ArrayList<String>();
		File dict = new File(trsHybaseConfig.testdatapath);
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
	class HttpThreadDI27 extends Thread {
		private volatile boolean isRun = true;

		public void close() {
			this.isRun = false;
		}
		// int i=0;//1
		public void run() {
			
	        

			RestTemplate  template = new RestTemplate (); 
			//template.postForLocation("http://192.168.0.75//txn999999.do?username=admin&password=gwssiadmin", null); 
			//template.getForObject("http://192.168.0.75//txn999999.do?username=admin&password=gwssiadmin", Object.class); 
			
			String url = "http://192.168.0.82:8085/ckmtest/gwssi/order?readColumn=AD;PNO;TIO;ABSO&&strWhere=%s&&sortWhere=%s" ;	
			//String url = "http://192.168.0.82:8085/ckmtest/gwssi/order?readColumn=AD;PNO;TIO;ABSO&&strWhere=%s&&sortWhere=%s" ;	
			//HttpClient  httpclient = new DefaultHttpClient(new ThreadSafeClientConnManager());
			//
			url = "http://192.168.0.75/txnPatentImgTextListRecord.ajax?select-key:thesaurus=&select-key:cross=&select-key:buttonItem=&select-key:expressCN2=&attribute-node:patent_start-row=1&attribute-node:patent_page-row=10&select-key:expressCN=%s&attribute-node:patent_sort-column=%s";
			// 创建HttpClient实例     
	        HttpClient httpclient = new DefaultHttpClient();  
	        HttpClient httpclient2 = new DefaultHttpClient();  
	        // 创建Get方法实例     
	        HttpGet httpgets = new HttpGet(URI.create("http://192.168.0.75//txn999999.do?username=admin&password=gwssiadmin"));    
	        HttpGet httpgets2 = new HttpGet();    

			//
			Workbook book;
			while (true) {
			try {
				book = Workbook.getWorkbook(new File("D:\\DI\\HybaseTest\\CategoryResult-.xls"));
				//String[] catarray = {"CPC","LC","ILSC","AS"};
				for (int size = 0;size <orderarray.length;size++){
					for(String cata : catarray){
						Sheet sheet = book.getSheet(cata);
						if(sheet == null) continue;
						Cell[] cells = sheet.getColumn(1);
						for(int i=1;i<cells.length;i++){
							
					        // 创建Get方法实例     
					        httpgets.setURI(URI.create("http://192.168.0.75//txn999999.do?username=admin&password=gwssiadmin"));    
					        
					        HttpResponse response = null;
							try {
								response = httpclient.execute(httpgets);
								response.getAllHeaders();
								httpclient.getConnectionManager().closeIdleConnections(0,TimeUnit.MILLISECONDS);
							}  catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}    

							String strWhere = cata + "='" + cells[i].getContents() + "'";
							log.info("strWhere:" + strWhere);
								String sortWhere = orderarray[size];
									log.info(strWhere+"--" + sortWhere);
									//ArrayList<TRSRecord> list = gwssiController.order("AD;PNO;TIO;ABSO", strWhere, sortWhere);
									String httpurl = String.format(url, strWhere,sortWhere);
									// 创建HttpClient实例     
							        // 创建Get方法实例     
							        httpgets2.setURI(URI.create(httpurl));
							        for(Header header :response.getAllHeaders()){
								        httpgets2.addHeader(header);;
							        	
							        }
							        try {
							        	response = httpclient2.execute(httpgets2);
							        }  catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
							        
									response.getAllHeaders();
									response.getEntity();
									try {
										//ArrayList<TRSRecord> obj = template.getForObject(httpurl, ArrayList.class); 
									}  catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									// 持续时间
									Thread.sleep(100);

	
							}
						}
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		}
	}
	/**
	 * 保留cookie，再次请求网站
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	@Test
	public void testHttpCookie() throws ClientProtocolException, IOException {

		RestTemplate template = new RestTemplate();
		
		
		// template.postForLocation("http://192.168.0.75//txn999999.do?username=admin&password=gwssiadmin",
		// null);
		// template.getForObject("http://192.168.0.75//txn999999.do?username=admin&password=gwssiadmin",
		// Object.class);

		String url = "http://192.168.0.82:8085/ckmtest/gwssi/order?readColumn=AD;PNO;TIO;ABSO&&strWhere=%s&&sortWhere=%s";
		// 替换为DI公共接口
		url = "http://192.168.0.75/txnPatentImgTextListRecord.ajax?select-key:thesaurus=&select-key:cross=&select-key:buttonItem=&select-key:expressCN2=&attribute-node:patent_start-row=1&attribute-node:patent_page-row=10&attribute-node:patent_sort-column=-PD&select-key:expressCN=";
		url = url + URLEncoder.encode("(((专利权人='专利') OR (标题='专利') OR (申请人='专利') OR (摘要='专利') OR (发明人='专利')))");
		
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
		String cookie = this.getUserCookie(httpclient);
		httpgets.setURI(URI.create(url));
		httpclient2.setCookieStore(httpclient.getCookieStore());
		response = httpclient2.execute(httpgets);
		response.getAllHeaders();
		String responseString = EntityUtils.toString(response.getEntity());
		
		httpgets.releaseConnection();
		httpclient.getConnectionManager().closeIdleConnections(0, TimeUnit.MILLISECONDS);
		httpclient2.getConnectionManager().closeIdleConnections(0, TimeUnit.MILLISECONDS);
	}
    public String getUserCookie(HttpClient httpclient){
        List<Cookie> cookies = ((AbstractHttpClient)httpclient).getCookieStore().getCookies();
        String userCookie = "";
        for(Cookie cookie: cookies){
            String name = cookie.getName();
            String value = cookie.getValue();

            System.out.print(name+":" + value);
        }
        return userCookie;
    }
	@Test
	public void testHttpCategory() throws Exception {
		// 线程数
		int threadnum = 1;
		List<HttpThreadCategory> list = new ArrayList<HttpThreadCategory>();
		for (int i = 0; i < threadnum; i++) {
			HttpThreadCategory myThread = new HttpThreadCategory();
			Thread t1 = new Thread(myThread);
			t1.start();
			list.add(myThread);
		}
		// 持续时间
		Thread.sleep(1000 * 60 * 10);
		for (int i = 0; i < list.size(); i++) {
			list.get(i).close();
		}
		log.info("testend:" + count);
	}

	// 受保护的计数器
	private volatile int countcategory = 0;
	/**
	 * 多线程网络访问测试Mysql
	 * @author jiahh 2015年5月8日
	 *
	 */
	class HttpThreadCategory extends Thread {
		private volatile boolean isRun = true;

		private String top = "5";
		public void close() {
			this.isRun = false;
		}
		// int i=0;//1
		public void run() {
			while (true) {
				try {
					httpcategory();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		}
	}
	private void httpcategory() throws Exception{
		RestTemplate template = new RestTemplate();
		String url = "http://192.168.0.82:8085/ckmtest/gwssi/category?key=%s&&strWhere=%s&&top=%s";
		
		List<String> allQuery = this.getAllQuery();
		for (int size = 0; size < catarray.length; size++) {
			String key = catarray[size];
			for(String strWhere : allQuery){
				String httpurl = String.format(url, key, strWhere, categorytop);
				List<Category> obj = template.getForObject(httpurl, List.class);
				System.out.println(obj);
			}

		}
	}

//	@Test
//    public void testCategoryDI75() throws Exception
//    {
//		String strWhere = "PD=1985 TO 2014";
//		long total = 0;
//		List<Category> map = null;
//		long start = System.currentTimeMillis();
//		// 
//		//map = gwssiController.category("AD",strWhere, "100");
//		System.out.println(System.currentTimeMillis()-start);
//		total +=System.currentTimeMillis()-start;
//
//		// 在F盘创建测试.xls文档，并在该文档中的第一个位置创建名称为第一页的工作表。
//		WritableWorkbook book = Workbook.createWorkbook(new File("D:\\DI\\HybaseTest\\CategoryResult.xls"));
//		//String[] catarray = {"PDT","IPC","AY","PY","IN","EPRY","CPC","LC","ILSC","AS"};
//		String[] catarray = {"API","AD","PDT","IPC","AY","PY","IN","EPRY","CPC","LC","ILSC","AS"};
////		String[] orderarray = {"RELEVANCE","+RELEVANCE","AD","+AD","AD","+AD","PD","+PD","EPRD","+EPRD","INCO","+INCO","IPCSCC","+IPCSCC","CLN","+CLN","DEPC","+DEPC","DC","+DC"};
//		//String[] catarray = {"API"};
//		
//		for(int i=0;i<catarray.length;i++){
//			
//			RestTemplate  template = new RestTemplate (); 
//			
//			String url = "http://192.168.0.82:8085/ckmtest/gwssi/category?key=%s&&strWhere=%s&&top=%s" ;	
//			url = "http://192.168.0.75/txnPatentCQ.ajax?select-key:buttonItem=&select-key:expressCN2=&select-key:expressCN=( 标题 = '小' OR 标题 = '软件' ) ";
//			String httpurl = String.format(url, key,strWhere,top);
//			List<Category> obj = template.getForObject(httpurl, List.class); 
//
//			
//			map = gwssiController.category(catarray[i],strWhere, "1000");
//			WritableSheet sheet = book.createSheet(catarray[i], i);
//			System.out.println(catarray[i]);
//
//			for (int j = 0;j < map.size();j ++) {
//				Category cat = map.get(j);
//				ExcelUtil.createSheetColumn(sheet, 1, j, cat.key);
//				ExcelUtil.createSheetColumn(sheet, 2, j, cat.value.toString());
//				System.out.println(cat.key + "\t" + cat.value);
//			}
//		}
//		book.write();
//		book.close();
//
//    }
// 


}
