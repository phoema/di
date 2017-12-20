package com.di.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.gwssi.itface.DataBaseFactory;
import cn.gwssi.itface.Result;

import com.di.TrsHybaseConfig;
import com.google.common.base.Strings;
import com.trs.hybase.client.TRSConnection;
import com.trs.hybase.client.TRSDatabase;
import com.trs.hybase.client.TRSDatabaseColumn;
import com.trs.hybase.client.TRSException;
import com.trs.hybase.client.TRSRecord;
import com.trs.hybase.client.TRSResultSet;
import com.trs.hybase.client.params.SearchParams;

@Component("hybaseService")
@Slf4j
public class HybaseService {
	
	@Autowired
	public TrsHybaseConfig hybaseConfig;
	public HybaseService(){
		
	}

	public List<HashMap> search(String trsHybaseTable,String Key,String Value,int persent,String readColumn,TrsHybaseConfig hybaseConfig) throws Exception{
		
		String strWhere = "ABSO=LIKE(本发明公开了一种基于智能终端的电视节目评论处理方法及系统,61)";
		//ABSO =LIKE(机械去皮 淀粉酶 果浆酶,51)
		strWhere = String.format("%s=LIKE(%s,%d)", Key,Value,persent);
		//strWhere = "ABSO=本发明公开了一种基于智能终端的电视节目评论处理方法及系统";
		String sortWhere = "RELEVANCE";
		DataBaseFactory _factory = DataBaseFactory.newInstance( "hybase", hybaseConfig.hybasehost, hybaseConfig.hybaseport, hybaseConfig.hybaseuser, hybaseConfig.hybasepassword) ;
		trsHybaseTable = trsHybaseTable==null?hybaseConfig.tablename:trsHybaseTable;
		Map<String, String> params = new HashMap<String, String>();
		//params.put("cut_size","2");
		//params.put(DataBaseParamKeys.COLOR_COLUMN,Key);
		
		int start = 1;
		int recordNum = 10;
		Result result = _factory.getQuery().select(trsHybaseTable, readColumn, null, strWhere, sortWhere, start, recordNum, params);
		ArrayList<TRSRecord> list = result.getNumRecord(start, recordNum);
		if(list == null) return null;
		String[] colsarray = readColumn.split(";");
		List<HashMap> resultlist = new ArrayList<HashMap>();
		for (TRSRecord record : list) {
			HashMap table = new HashMap();
			table.put("RELEVANCE", record.getRelevance());
			for(int j = 0; j<colsarray.length;j++){
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
	public List<HashMap> search(String Key,String Value,int persent,String Columns) throws Exception{
		List<TRSRecord> list = new ArrayList<TRSRecord>();
		List<HashMap> resultlist = new ArrayList<HashMap>();
		System.out.println("-------------01检索词提示（智能检索）---------");
		// 根据说明文档，相似性值应该在20-100之间
		if(persent<21) persent = 21;
		else if(persent>100) persent = 100;
		String trsHybaseTable = "DI_PAT_DI20150705_test";
		//String _sStrWhere = "ABSO=like(公开了一种基于视频解码设计实现远程服务器管理的方法,41)";
		//String _sStrWhere = "ABSO:视频解码";
		
		String _sStrWhere = "ABSO#LIKE:\"本发明公开了一种基于智能终端的电视节目评论处理方法及系统\"~61";
		//ABSO =LIKE(机械去皮 淀粉酶 果浆酶,51)
		_sStrWhere = String.format("%s#LIKE:\"%s\"~%d", Key,Value,persent);
		
//		Result result = _factory.getQuery().select("DI_PAT_DI20150705_test",null,
//				null,_sStrWhere,
//				"RELEVANCE",0,10,null);
//		System.out.println("----------------------");
//		
		TRSConnection _con = null;
		TRSResultSet _rs = null;
		System.out.println(_sStrWhere);
		SearchParams _searchParams = new SearchParams();
		_searchParams.setSortMethod("RELEVANCE");
		_searchParams.setReadColumns(Columns);
		//Object obj = _factory.getQuery().expressionSelect(trsHybaseTable, null, _sStrWhere, null);
		try {
			//_con = new TRSConnection("http://192.168.0.27:5566","admin","trsadmin",null);
			_con = new TRSConnection("http://"+hybaseConfig.hybasehost+":"+hybaseConfig.hybaseport,hybaseConfig.hybaseuser,hybaseConfig.hybasepassword,null);			
			//_con = this.genTRSConnection();		
			_rs = _con.executeSelect(trsHybaseTable, _sStrWhere, 0, 100, _searchParams);
			int _iSize = _rs.size();
			_iSize = _iSize > 20 ? 10 : _iSize;
			
			String[] colsarray = Columns.split(";");
			for (int i = 0; i < _iSize; i++) {
				_rs.moveNext();
				TRSRecord _trsRecord = _rs.get();
				list.add(_trsRecord);
				for(int j = 0; j<colsarray.length;j++){
					String col = colsarray[j];
					System.out.print(_trsRecord.getString(col));
					System.out.print("\t");
					HashMap table = new HashMap();
					table.put(col, _trsRecord.getString(col));
					resultlist.add(table);
				}
				System.out.println();

//				System.out.print(_trsRecord.getString("PNO"));
//				System.out.print("\t");
//				System.out.print(_trsRecord.getRelevance());
//				System.out.print("\t");
//				System.out.println(_trsRecord.getString("ABSO"));
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			if(_con != null)
				_con.close();
			
		}
		return resultlist;
	}
	public List<HashMap> SearchWithPNO(String pno ,String Columns) throws Exception{
		return this.simpleSearch("PNO=" + pno, Columns);
		
	}
	
	public List<HashMap> simpleSearch(String Query,String Columns) throws Exception{
		List<TRSRecord> list = new ArrayList<TRSRecord>();
		List<HashMap> resultlist = new ArrayList<HashMap>();
		if(Strings.isNullOrEmpty(Columns) ){
			Columns = "PNO;TIO;FTKO";
		}
		System.out.println("-------------01检索词提示（普通检索）---------" + Query);
		// 根据说明文档，相似性值应该在20-100之间
		String trsHybaseTable = hybaseConfig.tablename;
		String _sStrWhere = Query;
//		
		resultlist = this.simpleSearch(Query, Columns, 0, 20);

		return resultlist;
	}
	
	public List<HashMap> simpleSearch(String Query,String Columns,int start,int recordnum) throws Exception{
		List<TRSRecord> list = new ArrayList<TRSRecord>();
		List<HashMap> resultlist = new ArrayList<HashMap>();
		if(Strings.isNullOrEmpty(Columns) ){
			Columns = "PNO;TIO;FTKO";
		}
		System.out.println("-------------01检索词提示（普通检索）---------" + Query);
		// 根据说明文档，相似性值应该在20-100之间
		String trsHybaseTable = hybaseConfig.tablename_pat;
		String _sStrWhere = Query;
//		
		TRSConnection _con = null;
		TRSResultSet _rs = null;
		System.out.println(_sStrWhere);
		SearchParams _searchParams = new SearchParams();
		//_searchParams.setSortMethod("RELEVANCE");
		_searchParams.setReadColumns(Columns);
		try {
			//_con = new TRSConnection("http://192.168.0.27:5566","admin","trsadmin",null);
			_con = new TRSConnection("http://"+hybaseConfig.hybasehost+":"+hybaseConfig.hybaseport,hybaseConfig.hybaseuser,hybaseConfig.hybasepassword,null);			
			//_con = this.genTRSConnection();		
			_rs = _con.executeSelect(trsHybaseTable, _sStrWhere, start, recordnum, _searchParams);
			int _iSize = _rs.size();
			_iSize = _iSize > recordnum ? recordnum : _iSize;
			
			String[] colsarray = Columns.split(";");
			for (int i = 0; i < _iSize; i++) {
				_rs.moveNext();
				TRSRecord _trsRecord = _rs.get();
				list.add(_trsRecord);
				for(int j = 0; j<colsarray.length;j++){
					String col = colsarray[j];
					//System.out.print(_trsRecord.getString(col));
					//System.out.print("\t");
					HashMap table = new HashMap();
					table.put(col, _trsRecord.getString(col));
					resultlist.add(table);
				}
				//System.out.println();

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			if(_con != null)
				_con.close();
			
		}
		return resultlist;
	}
	
	public boolean create(String tablename) throws Exception{
		boolean success = false;
		TRSConnection conn = null;
		//conn = new TRSConnection("http://"+this._sHost+":"+this._sPort,this._sUser,this._sPassword,null);	
		conn = new TRSConnection("http://"+hybaseConfig.hybasehost+":"+hybaseConfig.hybaseport,hybaseConfig.hybaseuser,hybaseConfig.hybasepassword,null);			
		
        TRSDatabase database = new TRSDatabase("demo_1");
        database.addColumn(new TRSDatabaseColumn("标题", TRSDatabaseColumn.TYPE_PHRASE));
        database.addColumn(new TRSDatabaseColumn("正文", TRSDatabaseColumn.TYPE_DOCUMENT));
        try {
        	success = conn.createDatabase(database);
        } catch (TRSException e) {
            System.out.println("ErrorCode: " + e.getErrorCode());
            System.out.println("ErrorString: " + e.getErrorString());
        } finally {
            conn.close();
        }

		return success;
	}

	public static void main(String args[]) throws Exception {
		HybaseService zhiliao = new HybaseService();
		zhiliao.hybaseConfig = new TrsHybaseConfig();
		zhiliao.search("ABSO", "本发明公开了一种基于智能终端的电视节目评论处理方法及系统", 41,"PNO;ABSO");
	}
}
