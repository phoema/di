package com.izhiliao.service;

/**
 * 国知在线
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.gwssi.itface.DataBaseFactory;
import cn.gwssi.itface.Result;

import com.google.common.base.Strings;
import com.izhiliao.TrsHybaseConfig;
import com.izhiliao.util.TRSResult;
import com.trs.hybase.client.TRSConnection;
import com.trs.hybase.client.TRSDatabase;
import com.trs.hybase.client.TRSDatabaseColumn;
import com.trs.hybase.client.TRSRecord;
import com.trs.hybase.client.TRSResultSet;
import com.trs.hybase.client.params.SearchParams;

@Component("gzhybaseService")
@Slf4j
public class GzHybaseService {
	
	String pid;

	@Autowired
	public TrsHybaseConfig hybaseConfig;
	public GzHybaseService() {
	}

	/**
	 * 调用gwssi的hybase接口
	 * @param strWhere
	 * @param readColumn
	 * @param hybaseConfig
	 * @return
	 * @throws Exception
	 */
	public List<HashMap> search(String strWhere, String readColumn, TrsHybaseConfig hybaseConfig) throws Exception {

		// strWhere = "ABSO=本发明公开了一种基于智能终端的电视节目评论处理方法及系统";
		String sortWhere = "RELEVANCE";
		DataBaseFactory _factory = DataBaseFactory.newInstance("hybase", hybaseConfig.hybasehost,
				hybaseConfig.hybaseport, hybaseConfig.hybaseuser, hybaseConfig.hybasepassword);
		String trsHybaseTable = hybaseConfig.tablename_pat;
		Map<String, String> params = new HashMap<String, String>();
		// params.put("cut_size","2");
		// params.put(DataBaseParamKeys.COLOR_COLUMN,Key);

		System.out.println("strWhere" + strWhere);
		int start = 1;
		int recordNum = 10;
		Result result = _factory.getQuery().select(trsHybaseTable, readColumn, null, strWhere, sortWhere, start,
				recordNum, params);
		ArrayList<TRSRecord> list = result.getNumRecord(start, recordNum);
		if (list == null)
			return null;
		String[] colsarray = readColumn.split(";");
		List<HashMap> resultlist = new ArrayList<HashMap>();
		for (TRSRecord record : list) {
			HashMap table = new HashMap();
			for (int j = 0; j < colsarray.length; j++) {
				String col = colsarray[j];
				System.out.print(record.getString(col));
				System.out.print("\t");
				table.put(col, record.getString(col));
			}
			System.out.println();
			resultlist.add(table);
		}
		return resultlist;
	}
	/**
	 * 封装like语法，调用原始接口
	 * @param Key
	 * @param Value
	 * @param persent
	 * @param Columns
	 * @return
	 * @throws Exception
	 */
	public List<HashMap> patentLikeSearch(String Key, String Value, int persent, String Columns) throws Exception {
		List<TRSRecord> list = new ArrayList<TRSRecord>();
		List<HashMap> resultlist = new ArrayList<HashMap>();
		//System.out.println("-------------01检索词提示（智能检索）---------");
		// 根据说明文档，相似性值应该在20-100之间
		if (persent < 21)
			persent = 21;
		else if (persent > 100)
			persent = 100;
		String trsHybaseTable = hybaseConfig.tablename_pat;
		// String _sStrWhere = "ABSO=like(公开了一种基于视频解码设计实现远程服务器管理的方法,41)";
		// String _sStrWhere = "ABSO:视频解码";

		String _sStrWhere = "ABSO#LIKE:\"本发明公开了一种基于智能终端的电视节目评论处理方法及系统\"~61";
		// ABSO =LIKE(机械去皮 淀粉酶 果浆酶,51)
		_sStrWhere = String.format("%s#LIKE:\"%s\"~%d", Key, Value, persent);

		// Result result =
		// _factory.getQuery().select("DI_PAT_DI20150705_test",null,
		// null,_sStrWhere,
		// "RELEVANCE",0,10,null);
		// System.out.println("----------------------");
		//
		TRSConnection _con = null;
		TRSResultSet _rs = null;
		System.out.println(_sStrWhere);
		SearchParams _searchParams = new SearchParams();
		_searchParams.setSortMethod("RELEVANCE");
		_searchParams.setReadColumns(Columns);
		// Object obj = _factory.getQuery().expressionSelect(trsHybaseTable,
		// null, _sStrWhere, null);
		try {
			// _con = new
			// TRSConnection("http://192.168.0.27:5566","admin","trsadmin",null);
			_con = new TRSConnection("http://" + hybaseConfig.hybasehost + ":" + hybaseConfig.hybaseport,
					hybaseConfig.hybaseuser, hybaseConfig.hybasepassword, null);
			// _con = this.genTRSConnection();
			_rs = _con.executeSelect(trsHybaseTable, _sStrWhere, 0, 100, _searchParams);
			int _iSize = _rs.size();
			_iSize = _iSize > 20 ? 10 : _iSize;

			String[] colsarray = Columns.split(";");
			for (int i = 0; i < _iSize; i++) {
				_rs.moveNext();
				TRSRecord _trsRecord = _rs.get();
				list.add(_trsRecord);
				for (int j = 0; j < colsarray.length; j++) {
					String col = colsarray[j];
					System.out.print(_trsRecord.getString(col));
					System.out.print("\t");
					HashMap table = new HashMap();
					table.put(col, _trsRecord.getString(col));
					resultlist.add(table);
				}
				System.out.println();

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (_con != null)
				_con.close();

		}
		return resultlist;
	}
	/**
	 * 传入号单列表，封装号单检索
	 * @param pno
	 * @param Columns
	 * @return
	 * @throws Exception
	 */
	public List<HashMap> SearchWithPNO(String pno, String Columns) throws Exception {
		return this.patentSearch("PNO=" + pno, Columns);

	}

	/**
	 * 简化参数，调用原始检索
	 * @param Query
	 * @param Columns
	 * @return
	 * @throws Exception
	 */
	public List<HashMap> patentSearch(String Query, String Columns) throws Exception {
		List<TRSRecord> list = new ArrayList<TRSRecord>();
		List<HashMap> resultlist = new ArrayList<HashMap>();
		if (Strings.isNullOrEmpty(Columns)) {
			Columns = "PNO;TIO;FTKO";
		}
		//System.out.println("-------------01检索词提示（普通检索）---------" + Query);
		resultlist = this.patentSearch(Query, Columns, null, 0, 20);

		return resultlist;
	}
	
	
	/**
	 * 通过拓尔思提供底层API创建TRSConnection，执行检索操作
	 * 
	 * @param Query
	 * @param Columns
	 * @param start
	 * @param recordnum
	 * @return
	 * @throws Exception
	 */
	public List<HashMap> patentSearch(String Query, String Columns, String Sort, int start, int recordnum)
			throws Exception {
		List<HashMap> resultlist = new ArrayList<HashMap>();
		if (Strings.isNullOrEmpty(Columns)) {
			Columns = "PNO;TIO;FTKO";
		}
		if (Strings.isNullOrEmpty(Sort)) {
			Sort = "RELEVANCE";
		}
		//System.out.println("-------------01检索词提示（普通检索）---------" + Query);
		// 根据说明文档，相似性值应该在20-100之间
		String trsHybaseTable = hybaseConfig.tablename_pat;
		resultlist = this.simpleSearch(trsHybaseTable,Query,Columns,Sort,start,recordnum);
		return resultlist;
	}
	
	public TRSResult gzPatentSearch(String Query, String Columns, String Sort, int start, int recordnum)
	throws Exception {
		List<HashMap> resultlist = new ArrayList<HashMap>();
		if (Strings.isNullOrEmpty(Columns)) {
			Columns = "PNO;TIO;FTKO";
		}
		if (Strings.isNullOrEmpty(Sort)) {
			Sort = "RELEVANCE";
		}
		//System.out.println("-------------01检索词提示（普通检索）---------" + Query);
		// 根据说明文档，相似性值应该在20-100之间
		String trsHybaseTable = hybaseConfig.tablename_pat;
		TRSResult result = this.gzSimpleSearch(trsHybaseTable, Query, Columns, Sort, start, recordnum);
		return result;
	}
	
	
	/**
	 * 通过拓尔思提供底层API创建TRSConnection，执行检索操作
	 * 
	 * @param Query
	 * @param Columns
	 * @param start
	 * @param recordnum
	 * @return
	 * @throws Exception
	 */
	public TRSResult tradeSearch(String Query, String Columns, String Sort, int start, int recordnum)
			throws Exception {
		List<HashMap> resultlist = new ArrayList<HashMap>();
		if (Strings.isNullOrEmpty(Columns)) {
			Columns = "MN;SN;FD";
		}
		if (Strings.isNullOrEmpty(Sort)) {
			Sort = "RELEVANCE";
		}
		//System.out.println("-------------01检索词提示（普通检索）---------" + Query);
		// 根据说明文档，相似性值应该在20-100之间
		String trsHybaseTable = hybaseConfig.tablename_trade;
		TRSResult result=this.gzSimpleSearch(trsHybaseTable,Query,Columns,Sort,start,recordnum);
		return result;
	}
	/**
	 * 通过拓尔思提供底层API创建TRSConnection，执行检索操作
	 * 
	 * @param Query
	 * @param Columns
	 * @param start
	 * @param recordnum
	 * @return
	 * @throws Exception
	 */
	public List<HashMap> simpleSearch(String Table,String Query, String Columns, String Sort, int start, int recordnum)
			throws Exception {
		TRSResult result = this.search(Table, Query, Columns, Sort, start, recordnum);
		return result.records;
	}
	
	public TRSResult gzSimpleSearch(String Table,String Query, String Columns, String Sort, int start, int recordnum)
			throws Exception {
		TRSResult result = this.search(Table, Query, Columns, Sort, start, recordnum);
		return result;
	}
	/**
	 * 通过拓尔思提供底层API创建TRSConnection，执行检索操作
	 * 
	 * @param Query
	 * @param Columns
	 * @param start
	 * @param recordnum
	 * @return
	 * @throws Exception
	 */
	public TRSResult search(String Table,String Query, String Columns, String Sort, int start, int recordnum)
			throws Exception {
		
		
		
		TRSResult result = new TRSResult();
		
		List<TRSRecord> list = new ArrayList<TRSRecord>();
		List<HashMap> resultlist = new ArrayList<HashMap>();
		if (Strings.isNullOrEmpty(Columns)) {
			Columns = "PNO;TIO;FTKO";
		}
		if (Strings.isNullOrEmpty(Sort)) {
			Sort = "RELEVANCE";
		}
		//System.out.println("-------------01检索词提示（普通检索）---------" + Query);
		// 根据说明文档，相似性值应该在20-100之间
		String trsHybaseTable = Table;
		if(Strings.isNullOrEmpty(Table)){
			trsHybaseTable = hybaseConfig.tablename_pat;
		}
		String _sStrWhere = Query;
		//
		TRSConnection _con = null;
		TRSResultSet _rs = null;
		SearchParams _searchParams = new SearchParams();
		// 设置默认检索语法
		_searchParams.setProperty("search.syntax.name", "trs");
		// 设置默认排序字段
		_searchParams.setSortMethod(Sort);
		// 设置读取值字段
		_searchParams.setReadColumns(Columns);
		//设置高亮
		_searchParams.setColorColumns(Columns);
		try {
			_con = new TRSConnection("http://" + hybaseConfig.hybasehost + ":" + hybaseConfig.hybaseport,
					hybaseConfig.hybaseuser, hybaseConfig.hybasepassword, null);
			_rs = _con.executeSelect(trsHybaseTable, _sStrWhere, start, recordnum, _searchParams);
			// 获取数据库表信息
			TRSDatabase[] dbs =_con.getDatabases(trsHybaseTable);
			TRSDatabase db = dbs[0];
			TRSDatabaseColumn[] columns = db.getAllColumns();
			HashMap<String,Integer> columntypemap = new HashMap<String,Integer>();
			// 获取表信息中字段类型是否是日期类型 
			for(TRSDatabaseColumn column : columns){
				//column.getColType() == column.TYPE_DATE;
				columntypemap.put(column.getName(), column.getColType());
			}
			
			int _iSize = _rs.size();
			_iSize = _iSize > recordnum ? recordnum : _iSize;

			String[] colsarray = Columns.split(";");
			for (int i = 0; i < _iSize; i++) {
				_rs.moveNext();
				TRSRecord _trsRecord = _rs.get();
				list.add(_trsRecord);
				HashMap<String, String> table = new HashMap<String, String>();
				for (int j = 0; j < colsarray.length; j++) {
					String col = colsarray[j];
					
					//增加部分代码
					if("PID".equals(col)){
						pid = _trsRecord.getString(col);
						String str1 = "http://image.zldsj.com/H/";
	    				String str2 = "/THB/THB.GIF";
	    				
	    				//String pid = "PIDCNY020020918000000000025120FCF0AJ3JB014094";
	    				//String bid = "PID/CNY0/2002/0918/00000000002512/0FCF0AJ3JB014094";
	    				String fuhao = "/";
	    				StringBuffer sb = new StringBuffer();
	    				sb.append(str1);
	    				
	    				String a = pid.substring(0,3);
	    				sb.append(a);
	    				sb.append(fuhao);
	    				
	    				String b = pid.substring(3,7);
	    				sb.append(b);
	    				sb.append(fuhao);
	    				
	    				String c = pid.substring(7,11);
	    				sb.append(c);
	    				sb.append(fuhao);
	    				
	    				String d = pid.substring(11,15);
	    				sb.append(d);
	    				sb.append(fuhao);
	    				
	    				String e = pid.substring(15,29);
	    				sb.append(e);
	    				sb.append(fuhao);
	    				
	    				String f = pid.substring(29);
	    				
	    				sb.append(f);
	    				
	    				sb.append(str2);
	    				
	    				String src = sb.toString();
	    				
	    				//HashMap<String, String> table = new HashMap<String, String>();
	    				table.put("src", src);
	    				//resultlist.add(table);
					}else if("UDD".equals(col)){
						
						String str3 = _trsRecord.getString(col);
						
						String str1 = "http://image.zldsj.com/H/";
						String str2 = "/ABS/";
						//String udd = _trsRecord.getString(col);
						String fuhao = "/";
						
	    				StringBuffer sb = new StringBuffer();
	    				sb.append(str1);
	    				
	    				String a = pid.substring(0,3);
	    				sb.append(a);
	    				sb.append(fuhao);
	    				
	    				String b = pid.substring(3,7);
	    				sb.append(b);
	    				sb.append(fuhao);
	    				
	    				String c = pid.substring(7,11);
	    				sb.append(c);
	    				sb.append(fuhao);
	    				
	    				String d = pid.substring(11,15);
	    				sb.append(d);
	    				sb.append(fuhao);
	    				
	    				String e = pid.substring(15,29);
	    				sb.append(e);
	    				sb.append(fuhao);
	    				
	    				String f = pid.substring(29);
	    				
	    				sb.append(f);
	    				
	    				sb.append(str2);
	    				
	    				sb.append(str3);
	    				
	    				String src2 = sb.toString();
						
						table.put("src2", src2);
					}else if("TID".equals(col)){
						String tid = _trsRecord.getString(col);
						String str1="http://image.zldsj.com/N/";
						String fuhao="/";
						String a = "THB.GIF";
						StringBuffer sb = new StringBuffer();
						sb.append(str1);
						String str2 = tid.substring(0, 3);
						sb.append(str2);
						sb.append(fuhao);
						String str3 = tid.substring(3,5);
						sb.append(str3);
						sb.append(fuhao);
						String str4 = tid.substring(5,11);
						sb.append(str4);
						sb.append(fuhao);
						String str5 = tid.substring(11,15);
						sb.append(str5);
						sb.append(fuhao);
						String str6 = tid.substring(15,30);
						sb.append(str6);
						sb.append(fuhao);
						sb.append(a);
						String src3 = sb.toString();
						table.put("src3", src3);
					}
					
					
					String colvalue =  _trsRecord.getString(col);
					// 日期类型格式化
					if(!Strings.isNullOrEmpty(colvalue) && 0 == columntypemap.get(col)){
						colvalue = colvalue.substring(0, 10).replace("/", "");
					}
					table.put(col, colvalue);
				}
				resultlist.add(table);
			}
			// 检索结果数
			result.total = _rs.getNumFound();
			// 指定位置数据库记录
			result.records = resultlist;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (_con != null)
				_con.close();
		}
		return result;
	}
	
	public static void main(String args[]) throws Exception {
		GzHybaseService zhiliao = new GzHybaseService();
		zhiliao.hybaseConfig = new TrsHybaseConfig();
		zhiliao.patentLikeSearch("ABSO", "本发明公开了一种基于智能终端的电视节目评论处理方法及系统", 41, "PNO;ABSO");
	}
}
