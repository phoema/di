package com.izhiliao.web;

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

import cn.gwssi.exception.DataBaseException;
import cn.gwssi.exception.ExpressionException;
import cn.gwssi.itface.DataBaseFactory;
import cn.gwssi.itface.Result;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.izhiliao.TrsHybaseConfig;
import com.izhiliao.service.CKMService;
import com.izhiliao.service.HybaseService;
import com.izhiliao.util.DEM;
import com.izhiliao.util.Serie;
import com.izhiliao.util.Serie2;
import com.izhiliao.util.DEM.CL_cluster;
import com.izhiliao.util.DEM.CL_patent;
import com.izhiliao.util.DEM.CL_vword;
import com.izhiliao.util.Serie2.SeriePoint;
import com.thoughtworks.xstream.XStream;
import com.trs.ckm.clu.TrsPatentCluParam;
import com.trs.ckm.soap.CluTrsResult;
import com.trs.ckm.soap.TrsCkmSoapClient;
import com.trs.hybase.client.TRSRecord;

/**
 * 本Controller针对原始文件进行初始的入库操作
 * 
 * @author jiahh 2015年5月13日
 *
 */
@RestController
@Slf4j
@RequestMapping("/ckm")
public class CKMController {

	// yyyymmdd
	private long datetime = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date()));

	@Autowired
	TrsHybaseConfig hybaseConfig;
	@Autowired
	CKMService ckmService;
	@Autowired
	HybaseService hybaseService;

	@RequestMapping(value = "/", produces = "application/json; charset=utf-8")
	public @ResponseBody String help() {
		String help = "";
		help += "/excute \r\n" + "执行符合条件的记录的文件导出 \r\n</br>";
		return help;
	}

	/**
	 *
	 * 初始化测试数据 http://192.168.13.123:8085/ckmtest/ckm/cutword?num=5&text=控制模板和管理模块通过无线传感网络连接在一起
	 * http://10.10.1.7:8085/restzhiliao/ckm/cutword?num=5&text=控制模板和管理模块通过无线传感网络连接在一起
	 * @param path
	 * @return 无线传感网络 管理模块 连接
	 * @throws Exception
	 */
	@RequestMapping("/cutword")
	public String[] cutword(String text, String num) throws Exception {
		int count = 5;
		if (!Strings.isNullOrEmpty(num)) {
			count = Integer.parseInt(num);
		}
		// 发现hybase加入字符和字符就异常
		return ckmService.extractKeywords(text, count);
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
	/**
	 * 
	 * @param queryexpr
	 * @return
	 */
	@RequestMapping("/clu")
	public @ResponseBody DEM TRSPatentCluTestCaseNew(String queryexpr,String doccolumn) {

		return TRSPatentCluTestCase(queryexpr, 5, 5, "", 5,doccolumn);
	}
	/**
	 * 
	 * @param queryexpr
	 * @return
	 */
	public @ResponseBody DEM TRSPatentCluTestCaseNew(String queryexpr) {

		return TRSPatentCluTestCase(queryexpr, 5, 5, "", 5,"FTKO");
	}

	public @ResponseBody DEM TRSPatentCluTestCase(String queryexpr, int mincls, int maxcls, String StopWord,
			int Keywordnum,String doccolumn) {
		if(Strings.isNullOrEmpty(doccolumn)){
			doccolumn = "FTKO";
		}

		System.out.println("Start:TRSPatentCluTestCase");
		int maxcount = 5000;
		if (queryexpr == null) {
			queryexpr = "TI=计算机系统 AND PDB=CNA0 AND PD=2014";
		}
		if (maxcls <= 0 || maxcls > 10) {
			maxcls = 5;
		}
		if (mincls <= 0 || mincls > 10) {
			mincls = 5;
		}

		if (StopWord == null)
			StopWord = "";
		try {

			TrsCkmSoapClient _client = this.genClient();
			TrsPatentCluParam cp = new TrsPatentCluParam();
			long nowDate = System.currentTimeMillis();
			// cp.setcid(String.valueOf(System.currentTimeMillis()));
			// cp.settrsserverhost("192.168.0.23");
			// cp.settrsserverport("5555");
			// cp.settrsusername("admin");
			// cp.setDbtype("hybase");
			// cp.settrspassword("test2015");
			// cp.settrsbasename("DI_20150817_PAT_201412");
			// cp.settrsdoccolumn("摘要（简要说明）;说明书;权利要求书");
			// cp.settrsdoccolumn("摘要（中文）;主权项;权利要求");

			// cp.settrsserverhost("10.10.1.16");
			// cp.settrsserverport("5555");
			// cp.settrsusername("api");
			// cp.settrspassword("trsapi2015");
			// cp.setDbtype("hybase");
			// cp.settrsbasename("DATA_PAT_20150901");

			cp.setcid(String.valueOf(System.currentTimeMillis()));
			cp.settrsserverhost("10.10.1.17");
			cp.settrsserverport("5555");
			cp.settrsusername("diuser");
			cp.setDbtype("hybase");
			cp.settrspassword("gwssi123");
			cp.settrsbasename(hybaseConfig.tablename_pat);
			// cp.settrsdoccolumn("摘要（简要说明）;说明书;权利要求书");
			// cp.settrsdoccolumn("摘要（中文）;主权项;权利要求");
			// 摘要关键词CLKWO、权利要求关键词ABKWO、全文关键词FTKO
			cp.settrsdoccolumn(doccolumn);
			// cp.settrsdoccolumn("CLKWO 0.3;ABKWO 0.5;FTKO 0.2");
			cp.settrstitlecolumn("TIC");
			cp.settrsrowidcolumn("PID");
			cp.settrssortexpr("");
			cp.settrsmaxcount(maxcount);
			cp.settrsmaxcls(maxcls);
			cp.setTrsmincls(mincls);
			cp.settrswherexpr(queryexpr);
			cp.setStopWord(StopWord);
			cp.setSynonymWord("");
			cp.setKeywordnum(Keywordnum);
			// cp.setThemeWord("计算机 100;电脑 1000");
			CluTrsResult r;
			r = _client.PatentClusterTrsFile(cp, 0);
			int i = 0;
			while (r != null && r.getstatus() > 0) {
				// Thread.sleep(1000);
				r = _client.PatentClusterTrsQuery(cp, 0);
				// System.out.println(i++);
			}
			// m_trsCKMClient.PatentClusterTrsDel(cp, 0);
			if (r.getstatus() < 0) {
				System.out.println(r.getstatus());
			}
			if (r != null && r.getxmlret() != null) {
				System.out.println(r.getxmlret());
			}
			long nowDateEnd = System.currentTimeMillis();
			System.out.println("end:" + (nowDateEnd - nowDate));

			XStream xstream1 = new XStream();
			xstream1.autodetectAnnotations(true);
			xstream1.processAnnotations(DEM.class);
			xstream1.processAnnotations(CL_cluster.class);
			xstream1.processAnnotations(CL_patent.class);
			xstream1.processAnnotations(CL_vword.class);
			String xml = r.getxmlret().substring(40);
			DEM obj = (DEM) xstream1.fromXML(xml);

			String querynos = "PID=(";
			for (CL_cluster clu : obj.CL_clusters) {
				int size = clu.CL_patent_table.size();
				for (int j = 0; j < size; j++) {
					CL_patent patent = clu.CL_patent_table.get(j);
					querynos += patent.num + ",";
				}
			}
			querynos = querynos.substring(0, querynos.length() - 1) + ")";
			System.out.println("querynos:" + querynos);
			/********** Hybase Search Start ***********/
			nowDate = nowDateEnd;
			nowDateEnd = System.currentTimeMillis();
			System.out.println("hybaseStart:" + (nowDateEnd - nowDate));
			DataBaseFactory _factory = DataBaseFactory.newInstance("hybase", hybaseConfig.hybasehost,
					hybaseConfig.hybaseport, hybaseConfig.hybaseuser, hybaseConfig.hybasepassword);
			Map<String, String> params = new HashMap<String, String>();

			int start = 1;
			int recordNum = maxcount + 100;// 发现PID重复问题，暂时+100回避
			String readColumn = "PID;ANO;PNO;TIO;IPC;APO;INO;LSSCN";
			Result result = _factory.getQuery().select(hybaseConfig.tablename_pat, readColumn, null, queryexpr, null,
					start, recordNum, params);
			int _iSize = result.size();

			String[] colsarray = readColumn.split(";");
			Map<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();
			for (int k = 0; k < _iSize; k++) {
				result.next();
				TRSRecord _trsRecord = result.getRecord(k);
				HashMap<String, String> table = new HashMap<String, String>();
				for (int x = 0; x < colsarray.length; x++) {
					String col = colsarray[x];
					// System.out.print(_trsRecord.getString(col));
					// System.out.print("\t");
					table.put(col, _trsRecord.getString(col));
				}
				// System.out.print(table.get("PID").toString() + "\r\n");
				map.put(table.get("PID").toString(), table);

			}
			nowDate = nowDateEnd;
			nowDateEnd = System.currentTimeMillis();
			System.out.println("hybaseEnd:" + (nowDateEnd - nowDate));
			/********** Hybase Search End ***********/

			for (CL_cluster clu : obj.CL_clusters) {
				clu.name = "";
				for (CL_vword vword : clu.CL_vector_table) {
					clu.name += vword.name + ";";
				}
				int size = clu.CL_patent_table.size();
				float[][] data = new float[size][3];
				for (int j = 0; j < size; j++) {
					CL_patent patent = clu.CL_patent_table.get(j);
					data[j][0] = patent.cx;
					data[j][1] = patent.cy;
					data[j][2] = patent.similarity;
					HashMap<String, String> mappat = map.get(patent.num);
					if (mappat == null) {
						System.out.println(patent);
					}
					patent.ano = mappat.get("ANO");
					patent.pno = mappat.get("PNO");
					patent.tio = mappat.get("TIO");
					patent.ipc = mappat.get("IPC");
					patent.apo = mappat.get("APO");
					patent.ino = mappat.get("INO");
					patent.lsscn = mappat.get("LSSCN");
				}
				clu.data = data;
			}

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
	 * 
	 * @return
	 * @throws DataBaseException
	 * @throws ExpressionException
	 */
	@RequestMapping("/test3")
	public @ResponseBody List<Serie2> test3(String queryexpr) {
		List<Serie2> list = new ArrayList<Serie2>();

		DEM dem = TRSPatentCluTestCaseNew(queryexpr);

		for (int i = 0; i < dem.CL_clusters.size(); i++) {

			CL_cluster clu = dem.CL_clusters.get(i);
			Serie2 serie = new Serie2();
			serie.name = "";
			for (CL_vword word : clu.CL_vector_table) {
				serie.name += word.name + ";";

			}
			int size = clu.CL_patent_table.size();
			serie.name += "(" + size + ")";
			// int[][] data = new int[size][2];
			SeriePoint[] points = new Serie2.SeriePoint[size];
			for (int j = 0; j < size; j++) {
				CL_patent patent = clu.CL_patent_table.get(j);

				SeriePoint point2 = new SeriePoint();

				point2.x = patent.cx;
				point2.y = patent.cy;
				point2.z = patent.cz;
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
	 * 
	 * @return
	 */
	@RequestMapping("/test")
	public @ResponseBody List<Serie> test() {
		List<Serie> list = new ArrayList<Serie>();

		DEM dem = TRSPatentCluTestCaseNew(null);

		for (int i = 0; i < dem.CL_clusters.size(); i++) {

			CL_cluster clu = dem.CL_clusters.get(i);
			Serie serie = new Serie();
			serie.name = "";
			for (CL_vword word : clu.CL_vector_table) {
				serie.name += word.name + ";";

			}
			int size = clu.CL_patent_table.size();
			float[][] data = new float[size][2];
			for (int j = 0; j < size; j++) {
				CL_patent patent = clu.CL_patent_table.get(j);
				data[j][0] = patent.cx;
				data[j][1] = patent.cy;
			}
			serie.data = data;
			list.add(serie);
		}

		return list;
	}
	/**
	 * 全部聚类分组
	 * 
	 * @return
	 */
	@RequestMapping("/testxyz")
	public @ResponseBody List<Serie> testxyz(String query) {
		List<Serie> list = new ArrayList<Serie>();

		DEM dem = TRSPatentCluTestCaseNew(query);

		for (int i = 0; i < dem.CL_clusters.size(); i++) {

			CL_cluster clu = dem.CL_clusters.get(i);
			Serie serie = new Serie();
			serie.name = "";
			for (CL_vword word : clu.CL_vector_table) {
				serie.name += word.name + ";";

			}
			serie.center = new float[3];
			serie.center[0] = clu.cx;
			serie.center[1] = clu.cy;
			serie.center[2] = clu.cz;
			int size = clu.CL_patent_table.size();
			float[][] data = new float[size][3];
			for (int j = 0; j < size; j++) {
				CL_patent patent = clu.CL_patent_table.get(j);
				data[j][0] = patent.cx;
				data[j][1] = patent.cy;
				// data[j][2] = (int)(patent.similarity*100);
				data[j][2] = patent.similarity;
			}
			serie.data = data;
			list.add(serie);
		}

		return list;
	}
	/**
	 * 全部聚类分组
	 * 
	 * @return
	 */
	@RequestMapping("/testxyz2")
	public @ResponseBody List<Serie2> testxyz2(String query) {
		List<Serie2> list = new ArrayList<Serie2>();

		DEM dem = TRSPatentCluTestCaseNew(query);

		for (int i = 0; i < dem.CL_clusters.size(); i++) {

			CL_cluster clu = dem.CL_clusters.get(i);
			Serie2 serie = new Serie2();
			serie.name = "";
			for (CL_vword word : clu.CL_vector_table) {
				serie.name += word.name + ";";

			}
			serie.center = new float[3];
			serie.center[0] = clu.cx;
			serie.center[1] = clu.cy;
			serie.center[2] = clu.cz;
			int size = clu.CL_patent_table.size();
			SeriePoint[] data = new SeriePoint[size];
			for (int j = 0; j < size; j++) {
				CL_patent patent = clu.CL_patent_table.get(j);
				SeriePoint point = new SeriePoint();
				point.id = patent.num;
				point.x = patent.cx;
				point.y = patent.cy;
				point.z = patent.similarity;
				// data[j][2] = (int)(patent.similarity*100);
			}
			serie.data = data;
			list.add(serie);
		}

		return list;
	}
	/**
	 * 全部聚类，不分组
	 * 
	 * @return
	 */
	@RequestMapping("/test2")
	public List<Serie> test2() {
		List<Serie> list = new ArrayList<Serie>();

		DEM dem = TRSPatentCluTestCaseNew(null);

		for (int i = 0; i < dem.CL_clusters.size(); i++) {

			CL_cluster clu = dem.CL_clusters.get(i);
			int size = clu.CL_patent_table.size();
			// 获取最大cz
			HashSet<Integer> cz = new HashSet<Integer>();
			for (int j = 0; j < size; j++) {
				CL_patent patent = clu.CL_patent_table.get(j);
				if (!cz.contains(patent.cz)) {
					cz.add(patent.cz);
				}
			}
			for (int czz : cz) {
				Serie serie = new Serie();
				serie.name = "";
				for (CL_vword word : clu.CL_vector_table) {
					serie.name += word.name + ";";

				}
				serie.name += String.valueOf(czz);
				float[][] data = new float[size][2];
				int start = 0;
				for (int j = 0; j < size; j++) {
					CL_patent patent = clu.CL_patent_table.get(j);
					if (czz == patent.cz) {
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
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/jsonp")
	public void test4(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("text/plain");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		Map<String, String> map = new HashMap<String, String>();
		map.put("result", "content");
		PrintWriter out = response.getWriter();
		// test3();
		ObjectMapper maper = new ObjectMapper();

		String query = request.getParameter("queryexpr");
		// JSONPObject resultJSON = JSONPObject.fromObject(map); //根据需要拼装json
		String jsonpCallback = request.getParameter("jsonpCallback");// 客户端请求参数
		String value = maper.writeValueAsString(test3(query));
		out.println(jsonpCallback + "(" + value + ")");// 返回jsonp格式数据
		out.flush();
		out.close();

	}
	/**
	 * 全部聚类分组
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/jsonpxyz")
	public void jsonpxyz(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("text/plain");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		Map<String, String> map = new HashMap<String, String>();
		map.put("result", "content");
		PrintWriter out = response.getWriter();
		// test3();
		ObjectMapper maper = new ObjectMapper();

		String query = request.getParameter("queryexpr");
		// JSONPObject resultJSON = JSONPObject.fromObject(map); //根据需要拼装json
		String jsonpCallback = request.getParameter("jsonpCallback");// 客户端请求参数
		String value = maper.writeValueAsString(testxyz(query));
		out.println(jsonpCallback + "(" + value + ")");// 返回jsonp格式数据
		out.flush();
		out.close();

	}
	/**
	 * 全部聚类分组，返回原始数据
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/jsonpstd")
	public void jsonpstd(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("text/plain");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		Map<String, String> map = new HashMap<String, String>();
		map.put("result", "content");
		PrintWriter out = response.getWriter();
		// test3();
		ObjectMapper maper = new ObjectMapper();

		String query = request.getParameter("queryexpr");
		// JSONPObject resultJSON = JSONPObject.fromObject(map); //根据需要拼装json
		String jsonpCallback = request.getParameter("jsonpCallback");// 客户端请求参数
		String value = "";
		if (request.getParameter("mincls") != null) {
			int mincls = Integer.parseInt(request.getParameter("mincls"));
			int maxcls = Integer.parseInt(request.getParameter("maxcls"));
			String StopWord = request.getParameter("StopWord");
			int Keywordnum = Integer.parseInt(request.getParameter("Keywordnum"));

			value = maper.writeValueAsString(TRSPatentCluTestCase(query, mincls, maxcls, StopWord, Keywordnum,null));
		} else {
			value = maper.writeValueAsString(TRSPatentCluTestCaseNew(query));
		}
		out.println(jsonpCallback + "(" + value + ")");// 返回jsonp格式数据
		out.flush();
		out.close();

	}
	/**
	 * 全部聚类分组
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/jsonpclick")
	public void jsonpclick(HttpServletRequest request, HttpServletResponse response) throws Exception {

		response.setContentType("text/plain");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		Map<String, String> map = new HashMap<String, String>();
		map.put("result", "content");
		PrintWriter out = response.getWriter();
		// test3();
		ObjectMapper maper = new ObjectMapper();

		String query = request.getParameter("pno");
		// JSONPObject resultJSON = JSONPObject.fromObject(map); //根据需要拼装json
		String jsonpCallback = request.getParameter("jsonpCallback");// 客户端请求参数
		List<HashMap> hashmap = hybaseService.SearchWithPNO(query, "PNO;TIO;ABSO;FTKO");
		String value = maper.writeValueAsString(hashmap);
		out.println(jsonpCallback + "(" + value + ")");// 返回jsonp格式数据
		out.flush();
		out.close();

	}

}
