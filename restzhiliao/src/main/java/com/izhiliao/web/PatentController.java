package com.izhiliao.web;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.izhiliao.TrsHybaseConfig;
import com.izhiliao.service.CKMService;
import com.izhiliao.service.HybaseService;
import com.izhiliao.util.BIO_CONST;
import com.izhiliao.util.ResultInfo;

/**
 * 本Controller针对原始文件进行初始的入库操作
 * 
 * @author jiahh 2015年5月13日
 *
 */
@RestController
@Slf4j
@RequestMapping("/patent")
public class PatentController  extends CommonController{

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
	 * http://localhost:8083/patent/likesearch?key=ABSO&text=控制模板和管理模块通过无线传感网络连接在一起
	 * 
	 * @param key
	 * @param value
	 * @param persent
	 * @param columns
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/likesearch")
	public List<HashMap> likeSearch(String key, String text, String persent, String columns) throws Exception {
		// 发现hybase加入字符和字符就异常
		key = key == null ? "ABSO" : key;
		columns = columns == null ? "PNO;TIO;ABSO" : columns;
		int per = 61;
		if (Strings.isNullOrEmpty(persent)) {
			per = 61;
		} else {
			per = Integer.parseInt(persent);
		}
		String strWhere = "ABSO=LIKE(本发明公开了一种基于智能终端的电视节目评论处理方法及系统,61)";
		// 固定在中国库内检索
		strWhere = String.format("%s=LIKE(%s,%d) AND PDB=(CNA0,CNS0,CNY0)", key, text, per);

		// List<HashMap> list =
		// hybaseService.search(strWhere,columns,hybaseConfig);
		List<HashMap> list = hybaseService.patentSearch(strWhere, columns, null, 0, 10);
		return list;
	}
	/**
	 * 普通拆词检索 http://localhost:8083/patent/wordSearch?key=ABSO&text=一种餐饮智慧云无线传感网络通信基站
	 * text=控制模板和管理模块通过无线传感网络连接在一起 等效
	 * localhost:8083/patent/word?num=8&text=控制模板和管理模块通过无线传感网络连接在一起
	 * localhost:8083/patent/search?key=ABSO&persent=21&value=
	 * 无线传感网络 管理模块 连接
	 * 
	 * @param key
	 *            待检索字段：如果为空 取ABSO
	 *            名称:TIO;技术领域:TFO;背景技术:TBO；发明内容:ISO；具体实施方式:SEO；附图说明
	 *            :DDO；权利要求:CLO；摘要:ABSO;
	 * @param text
	 *            待检文本
	 * @param persent
	 *            相似程度 21-99(超过50，空结果概率较大) 如果为空 取21
	 * @param columns
	 *            返回列 PNO ANO PD AD TIO ABSO CLO FTO 如果为空 取PNO;TIO;ABSO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/wordSearch")
	public List<HashMap> wordSearch(String key, String text,String columns) throws Exception {
		String[] words = ckmService.extractKeywords(text, 5);
		String value = "";
		
		if (Strings.isNullOrEmpty(columns)) {
			columns = key;
		}
		// 为读取字段添加ANO 根据业务，页面以申请号显示，那么相同申请号就固定显示一个就行
		if (!columns.contains("ANO")) {
			columns += ";ANO";
		}

		
		for (String word : words) {
			value += word + " ";
		}
		// 切词拼检索式进行普通检索
		String query = key + "=(";

		for (String word : words) {
			query += word + ",";
		}
		query = query.substring(0, query.length() - 1) + ") AND PDB=(CNA0,CNS0,CNY0)";

		List<HashMap> simpleList = hybaseService.patentSearch(query, columns, "RELEVANCE", 0, 10);

		return simpleList;
	}
	/**
	 * http://localhost:8083/patent/cutword?num=5&text=控制模板和管理模块通过无线传感网络连接在一起
	 * 自助撰写调试 http://localhost:5287/SelfWrite/default.aspx?jsoncallback=?&Action=smartsearch&key=TIO&text=专利名称就是根据要申请专利的技术内容给专利起个名字&columns=PD;ANO;TIO;PDT;PDB
	 * http://10.10.1.7:8085/restzhiliao/patent/cutword?num=5&text=控制模板和管理模块通过无线传感网络连接在一起
	 * @param text
	 *            必须项 待拆词文本:控制模板和管理模块通过无线传感网络连接在一起
	 * @param maxword
	 *            非必须项 默认5 最大拆词个数
	 * @return
	 */
	@RequestMapping("/cutword")
	public ResultInfo cutword(String text, String maxword) {
		ResultInfo info = new ResultInfo();
		// 如果未传入切词数，默认取前5
		int count = 5;
		String[] words = null;
		try {
			if (!Strings.isNullOrEmpty(maxword)) {
				count = Integer.parseInt(maxword);
			}
			// 向CKM传入文本进行切词
			words = ckmService.extractKeywords(text, count);
		} catch (Exception e) {
			info.ReturnValue = BIO_CONST.RETURN_FAIL;
			info.ErrorInfo = e.getMessage();
			e.printStackTrace();
		}
		info.Option = words;
		return info;
	}
	/**
	 * 全部聚类分组
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/jsonpcutword")
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
	        
	        String text = request.getParameter("text");
	        String maxword = request.getParameter("maxword");
	        //JSONPObject resultJSON = JSONPObject.fromObject(map); //根据需要拼装json  
	        String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数  
	        ResultInfo result = this.cutword(text, maxword);
	        String value = maper.writeValueAsString(result);
	        out.println(jsonpCallback+"("+value+")");//返回jsonp格式数据  
	        out.flush();  
	        out.close();  

	  }
	/**
	 * 智能检索 http://localhost:8083/patent/smartsearch?key=ABSO&text=一种餐饮智慧云无线传感网络通信基站
	 * 
	 * @param key
	 *            必须项 在哪个字段内检索
	 * @param text
	 *            必须项 待检文本
	 * @param columns
	 *            非必须项，返回哪些列， 默认为待检字段key及ANO申请号，指定格式 单值TIO，多值以分号隔开 TIO;ABSO
	 * @param wordcount
	 *            非必须项 默认5 最大拆词个数
	 * @param start
	 *            非必须项 默认0 检索列表从第几条返回 第一条为0
	 * @param recordcount
	 *            非必须项 默认5 检索列表返回多少条 key待检字段
	 *            名称:TIO;技术领域:TFO;背景技术:TBO；发明内容:ISO；具体实施方式
	 *            :SEO；附图说明:DDO；权利要求:CLO；摘要:ABSO; columns 显示字段，指定格式
	 *            单值TIO，多值以分号隔开 TIO;ABSO
	 * @return ResultInfo
	 */
	@RequestMapping("/smartsearch")
	public ResultInfo smartSearch(String key, String text, String columns, String wordcount, String start,
			String recordcount) {

		ResultInfo info = new ResultInfo();
		int startnum = 0;
		int recordnum = 5;
		// Like检索的相关百分比
		/*
		 * 如果是2到【修订115】 19【#】 之间的数字，则表示检索结果限定在“忽略二
		 * 元索引，并且在限定范围内出现的词数不能少于该参数所对应的数目”， 如果 是【修订116】 20【#】
		 * 到100之间的数值，则表示检索结果限定在“忽略二元索引，并 且在限定范围内出现的词数与第一个参数所含总词数的百分比不能少于该参
		 * 数所对应的数值”。
		 */
		String persent = "61";
		// 获取切词数据
		int count = 5;
		String[] words = null;
		List<HashMap> returnList = new ArrayList<HashMap>();
		// catch异常封装成info.Error发给前台
		try {

			if (!Strings.isNullOrEmpty(wordcount)) {
				count = Integer.parseInt(wordcount);
			}
			if (!Strings.isNullOrEmpty(start)) {
				startnum = Integer.parseInt(start);
			}
			if (!Strings.isNullOrEmpty(recordcount)) {
				recordnum = Integer.parseInt(recordcount);
			}

			words = ckmService.extractKeywords(text, count);
			info.Option1 = words;

			if (Strings.isNullOrEmpty(columns)) {
				columns = key;
			}
			// 为读取字段添加ANO 根据业务，页面以申请号显示，那么相同申请号就固定显示一个就行
			if (!columns.contains("ANO")) {
				columns += ";ANO";
			}

			// 切词拼检索式进行普通检索
			String query = key + "=(";

			for (String word : words) {
				query += word + ",";
			}
			query = query.substring(0, query.length() - 1) + ") AND PDB=(CNA0,CNS0,CNY0)";

			List<HashMap> simpleList = hybaseService.patentSearch(query, columns, "RELEVANCE", startnum, recordnum);
			// 如果不是第一页 直接返回
			if (startnum != 0) {
				returnList = simpleList;
			} else {
				// 如果是第一页，补充LIKE检索的值
				// 获取LIKE检索
				List<HashMap> likeList = this.likeSearch(key, text, persent, columns);
				if (likeList == null || likeList.size() == 0)
					returnList = simpleList;
				else {
					for (int i = 0; i < likeList.size(); i++) {
						// 取两个
						if (i == 2)
							break;
						returnList.add(likeList.get(i));
					}
					for (HashMap simplemap : simpleList) {
						boolean isHave = false;
						for (HashMap returnmap : returnList) {
							if (simplemap.get("ANO").toString().equals(returnmap.get("ANO").toString())) {
								isHave = true;
								break;
							}
						}
						if (!isHave) {
							returnList.add(simplemap);
						}
						if(returnList.size()>=recordnum){
							break;
						}
					}

				}
			}
			info.Option = returnList;

		} catch (Exception e) {
			info.ReturnValue = BIO_CONST.RETURN_FAIL;
			info.ErrorInfo = e.getMessage();
			e.printStackTrace();
		}

		return info;
	}
	@RequestMapping("/search")
	public List<HashMap>  search(String query, String columns,int start,int recordcount) throws Exception{
		
		List<HashMap> simpleList = hybaseService.patentSearch(query, columns, "+PD", start, recordcount);
		return simpleList;
	}
	/**
	 * 全部聚类分组
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/jsonpsmartsearch")
	public void  jsonpsmartSearch(HttpServletRequest request,HttpServletResponse response) throws Exception{
	    	
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
			String text = request.getParameter("text");
			String columns = request.getParameter("columns");
			String wordcount = request.getParameter("wordcount");
			String start = request.getParameter("start");
			String recordcount = request.getParameter("recordcount");
	        //JSONPObject resultJSON = JSONPObject.fromObject(map); //根据需要拼装json  
	        String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数  
	        ResultInfo result = this.smartSearch(key, text, columns, wordcount, start, recordcount);
	        String value = maper.writeValueAsString(result);
	        out.println(jsonpCallback+"("+value+")");//返回jsonp格式数据  
	        out.flush();  
	        out.close();  

	  }

}
