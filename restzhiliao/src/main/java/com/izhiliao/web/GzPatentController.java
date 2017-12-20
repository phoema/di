package com.izhiliao.web;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.izhiliao.service.GzHybaseService;
import com.izhiliao.util.BIO_CONST;
import com.izhiliao.util.ResultInfo;
import com.izhiliao.util.TRSResult;

/**
 * 适合于国知在线专利检索功能
 * 
 * @author douq
 *
 */
@RestController
@Slf4j
@RequestMapping("/gzpatent")
public class GzPatentController {
	
	// yyyymmdd
	private long datetime = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date()));

	@Autowired
	TrsHybaseConfig hybaseConfig;
	@Autowired
	CKMService ckmService;
	@Autowired
	GzHybaseService gzhybaseService;

	@RequestMapping("/")
	public String help() {
		String help = "";
		help += "/excute \r\n" + "执行符合条件的记录的文件导出 \r\n</br>";
		return help;
	}

	/**
	 * http://localhost:8083/ckmtest/izhiliao/likeSearch?key=ABSO&text=控制模板和管理模块通过无线传感网络连接在一起
	 * http://localhost:8083/patent/likesearch?key=ABSO&text=控制模板和管理模块通过无线传感网络连接在一起
	 * @param key
	 * @param value
	 * @param persent
	 * @param columns
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/gzlikesearch")
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
		
		//strWhere = String.format("%s=LIKE(%s) AND PDB=(CNA0,CNS0,CNY0)", key, text);
		// List<HashMap> list =
		//gzhybaseService.search(strWhere,columns,hybaseConfig);
        List<HashMap> list = gzhybaseService.patentSearch(strWhere, columns, null, 0, 10);
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

		List<HashMap> list = gzhybaseService.patentSearch(query, columns, "RELEVANCE", 0, 10);

		return list;
	}
	
	
	//国知在线检索功能
    @RequestMapping("/gzwordSearch")
    public TRSResult gzwordSearch(String strWhere,Integer page,String columns,String sort) throws Exception{
    	
//    	if(strWhere.contains("名称,摘要和说明")){
//    		String[] strings = strWhere.split("[,]");
//    		int j = strings.length;
//    		String[] strings2 = strWhere.split("-");
////    		String[] strings2 = strWhere.split("[+=]");
//    		StringBuffer sb = new StringBuffer();
//    		
//			String b = strings2[1];
//    		for(int i=0;i<j-1;i++){
//    			
//    			String a = strings[i];
//    			
//    			sb.append(a);
//    			sb.append("=");
//    			sb.append(b);
//    			sb.append(" OR ");
//    		}
//    		String string = sb.toString();
//    		//名称+摘要和说明=计算机
//    		//String substring = string.substring(0, string.length()-8);
//    		//System.out.println(substring);
//    		//名称+摘要和说明+权利要求书=计算机
//    		String substring = string.substring(0, string.length()-4);
//    		//System.out.println(substring);
//    		strWhere = substring;
//    	}
    	
    	
    	
    	if(Strings.isNullOrEmpty(sort)){
    		sort="RELEVANCE";
    	}
    	
    	//摘要
    	if (Strings.isNullOrEmpty(columns)) {
			columns = "ABSO";
		}
    	//专利编码
    	if (!columns.contains("PID")) {
			columns += ";PID";
		}
    	
    	//公布号
//    	if(!columns.contains("PN")){
//    		columns += ";PN";
//    	}
    	//公布号原始
    	if(!columns.contains("PNO")){
    		columns += ";PNO";
    	}
    	
    	//公布号DOCDB
    	if(!columns.contains("PNDB")){
    		columns += ";PNDB";
    	}
    	//公布号EPO
//    	if(!columns.contains("PNE")){
//    		columns += ";PNE";
//    	}
    	//公布号标准
//    	if(!columns.contains("PNS")){
//    		columns += ";PNS";
//    	}
    	//公布号IPPH
//    	if(!columns.contains("PNI")){
//    		columns += ";PNI";
//    	}
    	//名称
    	if(!columns.contains("TIO")){
    		columns += ";TIO";
    	}
    	//申请号
//    	if(!columns.contains("AN")){
//    		columns += ";AN";
//    	}
    	//申请号原始
    	if(!columns.contains("ANO")){
    		columns += ";ANO";
    	}
    	//申请号DOCDB
    	if(!columns.contains("ANDB")){
    		columns += ";ANDB";
    	}
    	//申请号EPO
//    	if(!columns.contains("ANE")){
//    		columns += ";ANE";
//    	}
    	//申请号标准
//    	if(!columns.contains("ANS")){
//    		columns += ";ANS";
//    	}
    	//申请号IPPH
//    	if(!columns.contains("ANI")){
//    		columns += ";ANI";
//    	}
    	//申请日
    	if(!columns.contains("AD")){
    		columns += ";AD";
    	}
    	//公告日(公布日)
    	if(!columns.contains("PD")){
    		columns += ";PD";
    	}
    	//申请人
    	if(!columns.contains("APO")){
    		columns += ";APO";
    	}
    	//IPC
    	if(!columns.contains("IPC")){
    		columns += ";IPC";
    	}
    	//附图
    	if(!columns.contains("UDD")){
    		columns += ";UDD";
    	}
    	
    	//专利类型
    	if(!columns.contains("PDT")){
    		columns += ";PDT";
    	}
    	
    	if(!columns.contains("LSSC")){
    		columns += ";LSSC";
    	}
    	
    	//洛迦诺
    	if(!columns.contains("LC")){
    		columns += ";LC";
    	}
    	
    	//简要说明
    	if(!columns.contains("DEBEO")){
    		columns += ";DEBEO";
    	}
    	
    	int start=0;
    	
    	if(page<1){
    		page=1;
    	}
    	
    	start=(page-1)*10;
    	
    	TRSResult result = gzhybaseService.gzPatentSearch(strWhere, columns, sort, start, 10);
    	
    	
    	long total = result.getTotal();
    	
    	return result;
    	//List<HashMap> list = gzhybaseService.patentSearch(strWhere, columns, "RELEVANCE", start, hybaseConfig.ROWS);
    	//return list;
    }
    
    
    //国知在线查询详情功能，相当于使用PID和PNO进行检索
    @RequestMapping("/gzdetail")
    public List<HashMap> gzDetail(String strWhere,String columns) throws Exception{
    	if (Strings.isNullOrEmpty(columns)) {
			columns = "ABSO";
		}
    	
    	//PID
    	if(!columns.contains("PID")){
    		columns += ";PID";
    	}
    	
    	//申请号
    	if (!columns.contains("ANO")) {
			columns += ";ANO";
		}
    	if (!columns.contains("ANDB")) {
			columns += ";ANDB";
		}
    	
    	
    	//申请日
    	if (!columns.contains("AD")) {
			columns += ";AD";
		}
    	//申请人
    	if(!columns.contains("APO")){
    		columns += ";APO";
    	}
    	//专利权人
    	if(!columns.contains("ASO")){
    		columns += ";ASO";
    	}
    	//申请人地址
    	if(!columns.contains("AP1ADO")){
    		columns += ";AP1ADO";
    	}
    	//申请人区域代码
    	if(!columns.contains("APAC")){
    		columns += ";APAC";
    	}
    	//发明人
    	if(!columns.contains("INO")){
    		columns += ";INO";
    	}
    	//IPC
    	if(!columns.contains("IPC")){
    		columns += ";IPC";
    	}
    	//CPC
    	if(!columns.contains("CPC")){
    		columns += ";CPC";
    	}
    	//UC
    	if(!columns.contains("UC")){
    		columns += ";UC";
    	}
    	//FI
    	if(!columns.contains("FI")){
    		columns += ";FI";
    	}
    	//FTERM
    	if(!columns.contains("FTERM")){
    		columns += ";FTERM";
    	}
    	//优先权PRN
    	if(!columns.contains("PRNO")){
    		columns += ";PRNO";
    	}
    	//优先权PRNDB
    	if(!columns.contains("PRNDB")){
    		columns += ";PRNDB";
    	}
    	//审查员
    	if(!columns.contains("EXO")){
    		columns += ";EXO";
    	}
    	//代理机构
    	if(!columns.contains("CRO")){
    		columns += ";CRO";
    	}
    	//代理人
    	if(!columns.contains("AGO")){
    		columns += ";AGO";
    	}
    	//国际申请
    	if(!columns.contains("PCTA")){
    		columns += ";PCTA";
    	}
    	//国际公布
    	if(!columns.contains("PCTP")){
    		columns += ";PCTP";
    	}
    	//进入国家日期PCTSD
    	if(!columns.contains("PCTSD")){
    		columns += ";PCTSD";
    	}
    	//本国分类
    	if(!columns.contains("NC")){
    		columns += ";NC";
    	}
    	//分案申请
    	if(!columns.contains("DP")){
    		columns += ";DP";
    	}
    	//发明新型附图原图描述
    	if(!columns.contains("UDD")){
    		columns += ";UDD";
    	}
    	//权利要求书
    	if(!columns.contains("CLO")){
    		columns += ";CLO";
    	}
    	//说明书全文
    	if(!columns.contains("FTO")){
    		columns += ";FTO";
    	}
    	//法律状态LSCT
    	if(!columns.contains("LSCT")){
    		columns += ";LSCT";
    	}
    	//公告日(公布日)
    	if(!columns.contains("PD")){
    		columns += ";PD";
    	}
    	
    	if(!columns.contains("PDT")){
    		columns += ";PDT";
    	}
    	
    	if(!columns.contains("PNO")){
    		columns += ";PNO";
    	}
    	
    	//公布号DB
    	if (!columns.contains("PNDB")) {
			columns += ";PNDB";
		}
    	
    	if(!columns.contains("ANO")){
    		columns += ";ANO";
    	}
    	
    	if(!columns.contains("LSSC")){
    		columns += ";LSSC";
    	}
    	
    	if(!columns.contains("TIO")){
    		columns += ";TIO";
    	}
    	//简要说明书
    	if(!columns.contains("DEBEO")){
    		columns += ";DEBEO";
    	}
    	
    	//说明书页数
    	if(!columns.contains("DEPC")){
    		columns += ";DEPC";
    	}
    	
    	//权力要求书数量
    	if(!columns.contains("CLN")){
    		columns += ";CLN";
    	}
    	
    	/*String strWhere = "";
    	strWhere=selectkeys;*/
    	List<HashMap> list = gzhybaseService.patentSearch(strWhere, columns, "RELEVANCE", 0, 1);
    	return list;
    }
    
    
    
    
    
    @RequestMapping("/gznum")
    public List<HashMap> gzNum(String strWhere,String columns) throws Exception{
    	
    	strWhere = new String(strWhere.getBytes("ISO-8859-1"),"UTF-8");
    	
    	if (Strings.isNullOrEmpty(columns)) {
			columns = "ABSO";
		}
    	
    	
    	//说明书页数
//    	if(!columns.contains("DEPC")){
//    		columns += ";DEPC";
//    	}
    	
    	//权力要求书数量
//    	if(!columns.contains("CLN")){
//    		columns += ";CLN";
//    	}
    	
    	/*String strWhere = "";
    	strWhere=selectkeys;*/
    	List<HashMap> list = gzhybaseService.patentSearch(strWhere, columns, "RELEVANCE", 0, 10);
    	return list;
    }
    
	
	/**
	 * http://localhost:8083/patent/cutword?num=5&text=控制模板和管理模块通过无线传感网络连接在一起
	 * 
	 * @param text
	 *            必须项 待拆词文本:控制模板和管理模块通过无线传感网络连接在一起
	 * @param maxword
	 *            非必须项 默认5 最大拆词个数
	 * @return
	 */
	@RequestMapping("/gzcutword")
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
	@RequestMapping("/gzjsonpcutword")
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
	
}
