package com.di.web;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.di.TrsHybaseConfig;
import com.di.service.CKMService;
import com.di.service.HybaseService;
import com.di.util.DEM;
import com.di.util.DEM.CL_cluster;
import com.di.util.DEM.CL_patent;
import com.di.util.DEM.CL_vword;
import com.di.util.Serie;
import com.di.util.Serie2;
import com.di.util.Serie2.SeriePoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.thoughtworks.xstream.XStream;
import com.trs.ckm.clu.TrsPatentCluParam;
import com.trs.ckm.soap.CluTrsResult;
import com.trs.ckm.soap.TrsCkmSoapClient;

/**
 * 本Controller针对原始文件进行初始的入库操作
 * 
 * @author jiahh 2015年5月13日
 *
 */
@RestController
@Slf4j
@RequestMapping("/index")
public class IndexController {

	// yyyymmdd
	private long datetime = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date()));

	@Autowired
	TrsHybaseConfig hybaseConfig;
	@Autowired
	CKMService ckmService;
	@Autowired
	HybaseService hybaseService;
	
	@RequestMapping("/")
	public @ResponseBody String help() {
 		String help = "";
		help += "/IndexController \r\n" + "执行符合条件的记录的文件导出 \r\n</br>";
		return help;
	}

	/**
	 *
	 * 初始化测试数据
	 * http://192.168.13.123:8085/ckmtest/ckm/word?num=5&text=控制模板和管理模块通过无线传感网络连接在一起
	 * @param path
	 * @return  无线传感网络 管理模块 连接
	 * @throws Exception
	 */
	@RequestMapping("/word")
	public String[] word(String text,String num) throws Exception {
		int count = 5;
		if(!Strings.isNullOrEmpty(num)){
			count = Integer.parseInt(num);
		}
		// 发现hybase加入字符和字符就异常
		return ckmService.extractKeywords(text,count);
	}

	
	/**
	 * genClient
	 * 
	 * @param _sHost
	 * @param _sPort
	 * @param _sUser
	 * @param _sPassword
	 * @return
	 */
	private TrsCkmSoapClient genClient() {
		return new TrsCkmSoapClient(hybaseConfig.ckmurl, hybaseConfig.ckmuser, hybaseConfig.ckmpassword);
	}
	
	
	@RequestMapping("/clu")
	public @ResponseBody DEM TRSPatentCluTestCaseNew(String queryexpr){
			if(queryexpr == null){
				queryexpr = "TI=计算机系统 AND PDB=CNA0 AND PD=2014";
			}
	    try {
	        TrsCkmSoapClient _client = this.genClient();
	        TrsPatentCluParam cp = new TrsPatentCluParam();
	        long nowDate = System.currentTimeMillis();
	       cp.setcid(String.valueOf(System.currentTimeMillis()));
	        cp.settrsserverhost("192.168.0.23");
	        cp.settrsserverport("5555");
	        cp.settrsusername("admin");
	        cp.setDbtype("hybase");
	        cp.settrspassword("test2015");
	        cp.settrsbasename("DI_20150817_PAT_201412");
	        // cp.settrsdoccolumn("摘要（简要说明）;说明书;权利要求书");
	        // cp.settrsdoccolumn("摘要（中文）;主权项;权利要求");
	        cp.settrsdoccolumn("FTKO");
	        cp.settrstitlecolumn("TIC");
	        cp.settrsrowidcolumn("PID");
	        cp.settrssortexpr("");
	        cp.settrsmaxcount(5000);
	        cp.settrsmaxcls(10);
	        cp.setTrsmincls(5);
	        cp.settrswherexpr("PDB=CNA0");

			cp.settrsserverhost("10.10.1.16");
			cp.settrsserverport("5555");
			cp.settrsusername("api");
			cp.settrspassword("trsapi2015");
			cp.setDbtype("hybase");
			cp.settrsbasename("DATA_PAT_20150901");
			cp.settrswherexpr(queryexpr);
	        cp.settrsrowidcolumn("PNS");
	        cp.settrstitlecolumn("TI");

			cp.setStopWord("");
	        cp.setSynonymWord("");
	        cp.setKeywordnum(6);
	        //cp.setThemeWord("计算机 100;电脑 1000");
	        CluTrsResult r;
	        r = _client.PatentClusterTrsFile(cp, 0);
	        int i = 0;
	        while (r != null && r.getstatus() > 0) {
//	            Thread.sleep(1000);
	            r = _client.PatentClusterTrsQuery(cp, 0);
	            //System.out.println(i++);
	        }
	        // m_trsCKMClient.PatentClusterTrsDel(cp, 0);
	        if (r.getstatus() < 0) {
	            System.out.println(r.getstatus());
	        }
	        if (r != null && r.getxmlret() != null) {
	            System.out.println(r.getxmlret());
	        }
	        long nowDateEnd = System.currentTimeMillis();
	        System.out.println("end:"+(nowDateEnd-nowDate));
	        
			XStream xstream1 = new XStream();
			xstream1.autodetectAnnotations(true);
			xstream1.processAnnotations(DEM.class);
			xstream1.processAnnotations(CL_cluster.class);
			xstream1.processAnnotations(CL_patent.class);
			xstream1.processAnnotations(CL_vword.class);
			String xml = r.getxmlret().substring(40);
			DEM obj = (DEM)xstream1.fromXML(xml);
			
	        System.out.println(obj);

	        return obj;
	     } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	     }
		return null;
	    

	  }
	/**
	 * 复杂全部聚类分组
	 * @return
	 */
	@RequestMapping("/test3")
	public @ResponseBody  List<Serie2> test3(String queryexpr){
	    	List<Serie2> list = new ArrayList<Serie2>();

			DEM dem = TRSPatentCluTestCaseNew(queryexpr);

			for(int i = 0;i<dem.CL_clusters.size();i++){
				
				CL_cluster clu = dem.CL_clusters.get(i);
				Serie2 serie = new Serie2();
				serie.name = "";
				for(CL_vword word  : clu.CL_vector_table){
					serie.name += word.name + ";";

				}
				int size = clu.CL_patent_table.size();
				serie.name += "("+size +")";
				//int[][] data = new int[size][2];
				SeriePoint[] points = new Serie2.SeriePoint[size];
				for(int j = 0; j < size ;j++){
					CL_patent patent  = clu.CL_patent_table.get(j);

					SeriePoint point2 = new SeriePoint();
					
					point2.x = patent.cx;
					point2.y = patent.cy;
					point2.name = patent.basename;
					point2.id = patent.num;
					points[j] = point2;
				}
				serie.data = points;
				list.add(serie);
			}
	    
			return list;
	  }
	
	/**
	 * 全部聚类分组
	 * @return
	 */
	@RequestMapping("/test")
	public @ResponseBody List<Serie> test(){
	    	List<Serie> list = new ArrayList<Serie>();

			DEM dem = TRSPatentCluTestCaseNew(null);

			for(int i = 0;i<dem.CL_clusters.size();i++){
				
				CL_cluster clu = dem.CL_clusters.get(i);
				Serie serie = new Serie();
				serie.name = "";
				for(CL_vword word  : clu.CL_vector_table){
					serie.name += word.name + ";";

				}
				int size = clu.CL_patent_table.size();
				float[][] data = new float[size][2];
				for(int j = 0; j < size ;j++){
					CL_patent patent  = clu.CL_patent_table.get(j);
					data[j][0] = patent.cx;
					data[j][1] = patent.cy;
				}
				serie.data = data;
				list.add(serie);
			}
	    
			return list;
	  }
	/**
	 * 全部聚类，不分组
	 * @return
	 */
	@RequestMapping("/test2")
	public List<Serie> test2(){
	    	List<Serie> list = new ArrayList<Serie>();

			DEM dem = TRSPatentCluTestCaseNew(null);

			for(int i = 0;i<dem.CL_clusters.size();i++){
				
				CL_cluster clu = dem.CL_clusters.get(i);
				int size = clu.CL_patent_table.size();
				// 获取最大cz
				HashSet<Integer> cz = new HashSet<Integer>();
				for(int j = 0; j < size ;j++){
					CL_patent patent  = clu.CL_patent_table.get(j);
					if(!cz.contains(patent.cz)){
						cz.add(patent.cz);
					}
				}
				for(int czz : cz){
					Serie serie = new Serie();
					serie.name = "";
					for(CL_vword word  : clu.CL_vector_table){
						serie.name += word.name + ";";

					}
					serie.name += String.valueOf(czz);
					float[][] data = new float[size][2];
					int start = 0;
					for(int j = 0; j < size ;j++){
						CL_patent patent  = clu.CL_patent_table.get(j);
						if(czz == patent.cz){
							data[start][0] = patent.cx;
							data[start][1] = patent.cy;
							start++;
						}
					}
					serie.data = data;
					list.add(serie);
				}
			}
	    
			return list;
	  }

	
	/**
	 * 全部聚类分组
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/jsonp")
	public void  test4(HttpServletRequest request,HttpServletResponse response) throws IOException{
	    	
	      response.setContentType("text/plain");  
	        response.setHeader("Pragma", "No-cache");  
	        response.setHeader("Cache-Control", "no-cache");  
	        response.setDateHeader("Expires", 0);  
	        Map<String,String> map = new HashMap<String,String>();   
	        map.put("result", "content");  
	        PrintWriter out = response.getWriter();    
	        //test3();
	        ObjectMapper maper = new ObjectMapper();
	        
	        String query = request.getParameter("queryexpr");
	        //JSONPObject resultJSON = JSONPObject.fromObject(map); //根据需要拼装json  
	        String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数  
	        String value = maper.writeValueAsString(test3(query));
	        out.println(jsonpCallback+"("+value+")");//返回jsonp格式数据  
	        out.flush();  
	        out.close();  

	  }
	/**
	 * 全部聚类分组
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/jsonpclick")
	public void  jsonpclick(HttpServletRequest request,HttpServletResponse response) throws Exception{
	    	
	      response.setContentType("text/plain");  
	        response.setHeader("Pragma", "No-cache");  
	        response.setHeader("Cache-Control", "no-cache");  
	        response.setDateHeader("Expires", 0);  
	        Map<String,String> map = new HashMap<String,String>();   
	        map.put("result", "content");  
	        PrintWriter out = response.getWriter();    
	        //test3();
	        ObjectMapper maper = new ObjectMapper();
	        
	        String query = request.getParameter("pno");
	        //JSONPObject resultJSON = JSONPObject.fromObject(map); //根据需要拼装json  
	        String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数  
	        List<HashMap> hashmap = hybaseService.SearchWithPNO(query, "PNO;TIO;ABSO;FTKO");
	        String value = maper.writeValueAsString(hashmap);
	        out.println(jsonpCallback+"("+value+")");//返回jsonp格式数据  
	        out.flush();  
	        out.close();  

	  }

	
}
