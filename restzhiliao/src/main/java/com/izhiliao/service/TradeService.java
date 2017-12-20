package com.izhiliao.service;

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
import com.trs.hybase.client.TRSConnection;
import com.trs.hybase.client.TRSDatabase;
import com.trs.hybase.client.TRSDatabaseColumn;
import com.trs.hybase.client.TRSException;
import com.trs.hybase.client.TRSRecord;
import com.trs.hybase.client.TRSResultSet;
import com.trs.hybase.client.params.SearchParams;

@Component("tradeService")
@Slf4j
public class TradeService {

	@Autowired
	public TrsHybaseConfig hybaseConfig;
	public TradeService() {

	}

	public List<HashMap> simpleSearch(String Query, String Columns) throws Exception {
		List<TRSRecord> list = new ArrayList<TRSRecord>();
		List<HashMap> resultlist = new ArrayList<HashMap>();
		if (Strings.isNullOrEmpty(Columns)) {
			Columns = "PNO;TIO;FTKO";
		}
		//System.out.println("-------------01检索词提示（普通检索）---------" + Query);
		resultlist = this.simpleSearch(Query, Columns, null, 0, 20);

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
	public List<HashMap> simpleSearch(String Query, String Columns, String Sort, int start, int recordnum)
			throws Exception {
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
		String trsHybaseTable = hybaseConfig.tablename_pat;
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
		try {
			_con = new TRSConnection("http://" + hybaseConfig.hybasehost + ":" + hybaseConfig.hybaseport,
					hybaseConfig.hybaseuser, hybaseConfig.hybasepassword, null);
			_rs = _con.executeSelect(trsHybaseTable, _sStrWhere, start, recordnum, _searchParams);
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
					table.put(col, _trsRecord.getString(col));
				}
				resultlist.add(table);
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

	public long getRecordNum(String Query) throws Exception{
		
		long  ret = 0;
		String trsHybaseTable = hybaseConfig.tablename_trade;
		String _sStrWhere = Query;
		String Columns = "ID";
		String Sort = "RELEVANCE";
		
		TRSConnection _con = null;
		TRSResultSet _rs = null;
		SearchParams _searchParams = new SearchParams();
		// 设置默认检索语法
		_searchParams.setProperty("search.syntax.name", "trs");
		// 设置默认排序字段
		_searchParams.setSortMethod(Sort);
		// 设置读取值字段
		_searchParams.setReadColumns(Columns);
		try {
			_con = new TRSConnection("http://" + hybaseConfig.hybasehost + ":" + hybaseConfig.hybaseport,
					hybaseConfig.hybaseuser, hybaseConfig.hybasepassword, null);
			_rs = _con.executeSelect(trsHybaseTable, _sStrWhere, 0, 10, _searchParams);
			ret = _rs.getNumFound();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (_con != null)
				_con.close();

		}
		return ret;
		
	}
	public static void main(String args[]) throws Exception {
		TradeService zhiliao = new TradeService();
		zhiliao.hybaseConfig = new TrsHybaseConfig();
	}
}
