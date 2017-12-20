package com.izhiliao.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.gwssi.itface.DataBaseFactory;
import cn.gwssi.itface.Result;

import com.izhiliao.TrsHybaseConfig;

@Component("gwssiService")
@Slf4j
public class GwssiService {

	// yyyymmdd
	private long datetime = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date()));

	@Autowired
	TrsHybaseConfig hybaseConfig;
	public Result search(String trsHybaseTable, String readColumn, String strWhere, String sortWhere) throws Exception {
		DataBaseFactory _factory = DataBaseFactory.newInstance("hybase", hybaseConfig.hybasehost,
				hybaseConfig.hybaseport, hybaseConfig.hybaseuser, hybaseConfig.hybasepassword);
		trsHybaseTable = trsHybaseTable == null ? hybaseConfig.tablename_pat : trsHybaseTable;
		Map<String, String> params = new HashMap<String, String>();

		int start = 1;
		int recordNum = 10;
		Result result = _factory.getQuery().select(trsHybaseTable, readColumn, null, strWhere, sortWhere, start,
				recordNum, params);
		return result;
	}

	/**
	 * 统计
	 * 
	 * @param key
	 *            single : column muti:column;column
	 * @param value
	 * @param persent
	 * @param columns
	 * @return
	 * @throws Exception
	 */
	public Map<String, Map<String, Long>> category(String strWhere, String key, String categorynum) throws Exception {

		DataBaseFactory _factory = DataBaseFactory.newInstance("hybase", hybaseConfig.hybasehost,
				hybaseConfig.hybaseport, hybaseConfig.hybaseuser, hybaseConfig.hybasepassword);
		String trsHybaseTable = hybaseConfig.tablename;
		Map<String, String> params = new HashMap<String, String>();
		params.put("category_top_num", categorynum);
		Map<String, Map<String, Long>> map = _factory.getQuery().categorySelect(trsHybaseTable, null, key, strWhere,
				params);
		return map;
	}
	public Map<String, Map<String, Long>> category(String table, String strWhere, String key, String categorynum) throws Exception {

		DataBaseFactory _factory = DataBaseFactory.newInstance("hybase", hybaseConfig.hybasehost,
				hybaseConfig.hybaseport, hybaseConfig.hybaseuser, hybaseConfig.hybasepassword);
		String trsHybaseTable = table;
		Map<String, String> params = new HashMap<String, String>();
		params.put("category_top_num", categorynum);
		Map<String, Map<String, Long>> map = _factory.getQuery().categorySelect(trsHybaseTable, null, key, strWhere,
				params);
		return map;
	}
}
