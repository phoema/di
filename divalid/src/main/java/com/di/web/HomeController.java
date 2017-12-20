package com.di.web;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.gwssi.itface.Constants;
import cn.gwssi.itface.DataBaseFactory;

import com.di.TrsHybaseConfig;
import com.di.service.CKMService;
import com.di.service.HybaseService;
import com.di.util.Category;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.mongodb.DBObject;

/**
 * 本Controller针对原始文件进行初始的入库操作
 * 
 * @author jiahh 2015年5月13日
 *
 */
@RestController
@Slf4j
@RequestMapping("/")
public class HomeController {

	// yyyymmdd
	private long datetime = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date()));

	@Autowired
	TrsHybaseConfig hybaseConfig;
	@Autowired
	CKMService ckmService;
	@Autowired
	HybaseService hybaseService;
	@Autowired
	MongoController mongoController;
	@RequestMapping("/")
	public String help() {
 		String help = "";
		help += "/hybase.html</br>";
		help += "/mongo.html</br>";
		help += "/compare_db.html</br>";
		help += "/valid/compare_pdb</br>";
		help += "/valid/compare_py?country=CNA0</br>";
		help += "/valid/compare_pd?country=CNA0&pubyear=1990</br>";
		help += "/valid/compare_pat?country=CNA0&pd=19900718</br>";

		return help;
	}
	
		@RequestMapping("/group")
		public Map<String, Map<String, Long>> category(String key, String strWhere, String top) throws Exception {
			DataBaseFactory _factory = DataBaseFactory.newInstance(Constants.DB_TYPE_HYBASE, hybaseConfig.hybasehost,
					hybaseConfig.hybaseport, hybaseConfig.hybaseuser, hybaseConfig.hybasepassword);
			if (Strings.isNullOrEmpty(key)) {
				key = "PDB";
			}
			String trsHybaseTable = hybaseConfig.tablename_pat;
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
		 * 全部聚类分组
		 * @return
		 * @throws Exception 
		 */
		@RequestMapping("/jsonpgroup")
		public void  jsonpgroup(HttpServletRequest request,HttpServletResponse response) throws Exception{
		    	
		      response.setContentType("text/plain");  
		        response.setHeader("Pragma", "No-cache");  
		        response.setHeader("Cache-Control", "no-cache");  
		        response.setDateHeader("Expires", 0);  
		        Map<String,String> map = new HashMap<String,String>();   
		        map.put("result", "content");  
		        PrintWriter out = response.getWriter();    
		        //test3();
		        ObjectMapper maper = new ObjectMapper();
		        
		        String key = request.getParameter("key");
		        String strWhere = request.getParameter("strWhere");
		        String top = request.getParameter("top");
		        //JSONPObject resultJSON = JSONPObject.fromObject(map); //根据需要拼装json  
		        String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数  
		        Map<String, Map<String, Long>> hashmap = this.category(key, strWhere, top);
		        String value = maper.writeValueAsString(hashmap);
		        out.println(jsonpCallback+"("+value+")");//返回jsonp格式数据  
		        out.flush();  
		        out.close();  

		  }
		@RequestMapping("/search")
		public List<HashMap> search(String query, String cols, Integer start ,Integer record) throws Exception {
			int start_int = 0;
			int record_int = 10;
			if(start != null) {
				start_int = start.intValue();
			}
			if(record != null) {
				record_int = record.intValue();
			}
			
			List<HashMap> list = hybaseService.simpleSearch(query, cols,start_int,record_int);
			return list;
	}

		/**
		 * 按PDB字段分组统计专利信息
		 * @return
		 * @throws Exception
		 */
		@RequestMapping("/compare_pdb")
		public String compare_pdb() throws Exception {
			
			//country = "USB0";
			//pubyear = "2014";
			String hybaseKey = "PDB";
			String mongoGroupkey = "docInfo.pdb";
			
			return this.compare_common(hybaseKey, null, null);

		}

		/**
		 * 按公开年PY字段分组统计专利信息，通过专利国别过滤
		 * @param country
		 * @return
		 * @throws Exception
		 */
		@RequestMapping("/compare_py")
		public String compare_py(String country) throws Exception {
			
//			country = "USB0";
			//pubyear = "2014";
			String hybaseKey = "PY";
			String mongoGroupkey = "docInfo.py";
			
			return this.compare_common(hybaseKey, country, null);

		}
		/**
		 * 按公开日PD字段分组统计专利信息，通过专利国别、公开年过滤
		 * @param country
		 * @param pubyear
		 * @return
		 * @throws Exception
		 */
		@RequestMapping("/compare_pd")
		public String compare_pd(String country ,String pubyear) throws Exception {
//searchkey=docInfo.pdb;docInfo.pd&searchvalue=CNB0;20001101			
//			country = "USB0";
//			pubyear = "2014";
			String hybaseKey = "PD";
//			String strWhere = "PDB="+country+" AND PY="+pubyear;
//			String mongoGroupkey = "docInfo.pd";
//			String mongoSearchkey = "docInfo.pdb;docInfo.py";
//			String mongoSearchvalue = country + ";" + pubyear;
//			StringBuilder build = new StringBuilder();

			return this.compare_common(hybaseKey, country, pubyear);

	}
		/**
		 * country=CNB0&pd=20001101
		 * 计算指定国家、指定公开日中hybase和mongo的专利差异
		 * @param country
		 * @param pubyear
		 * @return
		 * @throws Exception
		 */
		@RequestMapping("/compare_pat")
		public List<String> compare_pat(String country ,String pd) throws Exception {
			//searchkey=docInfo.pdb;docInfo.pd&searchvalue=CNB0;20001101			
//			country = "USB0";
//			pubyear = "2014";
			String hybaseKey = "PD";
//			String strWhere = "PDB="+country+" AND PY="+pubyear;
//			String mongoGroupkey = "docInfo.pd";
//			String mongoSearchkey = "docInfo.pdb;docInfo.py";
//			String mongoSearchvalue = country + ";" + pubyear;
//			StringBuilder build = new StringBuilder();
			String searchkey="docInfo.pdb;docInfo.pd";
			String searchvalue = country + ";" + pd;
			List<DBObject> mongoList = mongoController.search(searchkey, searchvalue);
			
			String query = "PDB="+country+" AND PD="+pd;
			List<HashMap> hybaseList = hybaseService.simpleSearch(query, "PID",0,mongoList.size());
			
			HashSet<String> hybaseset = new HashSet<String>();
			for(HashMap map : hybaseList){
				hybaseset.add(map.get("PID").toString());
			}

			List<String> result = new ArrayList<String>();
			if(mongoList.size() == hybaseList.size())
				return result;
			for(DBObject dbobj : mongoList){
				if(!hybaseset.contains((String)dbobj.get("_id"))){
					result.add((String)dbobj.get("_id"));
				}
				
			}
			return result;

	}
		public String compare_common(String hybaseKey,String country ,String pubyear) throws Exception {
			
//			country = "USB0";
//			pubyear = "2014";
			//hybaseKey = "PD";
			String mongoGroupkey = "docInfo.pd";
			
			String strWhere = "PDB="+country+" AND PY="+pubyear;
			String mongoSearchkey = "docInfo.pdb;docInfo.py";
			String mongoSearchvalue = country + ";" + pubyear;
			if("PD".equals(hybaseKey)){
				// 要求指定国家、公开年
				strWhere = "PDB="+country+" AND PY="+pubyear;
				mongoGroupkey = "docInfo.pd";
				mongoSearchkey = "docInfo.pdb;docInfo.py";
				mongoSearchvalue = country + ";" + pubyear;

			}else if("PY".equals(hybaseKey)){
				// 要求指定国家
				strWhere = "PDB="+country;
				mongoGroupkey = "docInfo.py";
				mongoSearchkey = "docInfo.pdb";
				mongoSearchvalue = country;
			}else if("PDB".equals(hybaseKey)){
				strWhere = null;
				mongoGroupkey = "docInfo.pdb";
				mongoSearchkey = null;
				mongoSearchvalue = null;
			}
			StringBuilder build = new StringBuilder();
			Map<String, Map<String, Long>> hybaseResult = this.category(hybaseKey, strWhere, null);
			//{PD={2014/08/12=6609, 2014/08/26=6601
			Map<String, Long> hybaseMap = hybaseResult.get(hybaseKey);
			// 全量分库统计
			//List<HashMap> mongoResult = mongoController.report("docInfo.pdb", null, null);
			// 指定年指定库docInfo.pdb;docInfo.py统计公开日
			List<HashMap> mongoResult = mongoController.report(mongoGroupkey, mongoSearchkey, mongoSearchvalue);
			System.out.println("compare|"+hybaseKey + "\t mongocount \t \t haybasecount \t \t compare");
			//如果用“.”作为分隔的话，必须是如下写法：String.split("\\.")
			String[] arra = mongoGroupkey.split("\\.");
			
			for (HashMap map : mongoResult) {
				String keyvalue = "";//{total=62883, docInfo={pdb=USS0}}
				Long total = Long.parseLong(map.get("total").toString());
				Map docInfo = (HashMap)map.get(arra[0]);
				String mongovalue = docInfo.get(arra[1]).toString();
				build.append(mongovalue + "\t mongovalue:\t " + total);
				for(String key : hybaseMap.keySet()){
					Long hybasevalue = hybaseMap.get(key);
				//System.out.print(key + "hybasevalue:" + hybasevalue );
				// {total=6184, docInfo={pd=20141230}}
					// 日期格式的差异，将key进行格式化
					String keyformat = key;
					if("PY".equals(hybaseKey)){
						keyformat = key.substring(0, 4);
					}else if("PD".equals(hybaseKey)){
						keyformat = key.replace("/", "");
					}
					if(keyformat.equals(mongovalue)){
						build.append("\t hybasevalue:\t " + hybasevalue );
						if(!total.equals(hybasevalue)){
							build.append("\t compare:false");
						}
						break;
					}
				}
				build.append("\r\n");
			}

			System.out.println(build);
			return build.toString();
		}

		
		/*******************TradeMark Start****************************/
		@RequestMapping("/group_trade")
		public Map<String, Map<String, Long>> category_trade(String key, String strWhere, String top) throws Exception {
			DataBaseFactory _factory = DataBaseFactory.newInstance(Constants.DB_TYPE_HYBASE, hybaseConfig.hybasehost,
					hybaseConfig.hybaseport, hybaseConfig.hybaseuser, hybaseConfig.hybasepassword);
			if (Strings.isNullOrEmpty(key)) {
				key = "TMDB";
			}
			
			String trsHybaseTable = "DATA_TRA_20150901";
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
		
		/*******************TradeMark End****************************/

}
