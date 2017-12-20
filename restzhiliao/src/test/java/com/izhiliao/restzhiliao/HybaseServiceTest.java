package com.izhiliao.restzhiliao;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.izhiliao.App;
import com.izhiliao.service.GwssiService;
import com.izhiliao.service.HybaseService;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@Slf4j
public class HybaseServiceTest {
	@Autowired
	private HybaseService service;
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public HybaseServiceTest() {
		
	}

	/**
	 * 根据检索式导出专利信息，拼接成压力测试用的检索式
	 * @throws Exception
	 */
	@Test
	public void testSearch() throws Exception {

		StringBuilder queryBuilder = new StringBuilder();
		String queryTemp = "";
		int start = 1;
		int recordnum = 5000;
		String columns = "PNO;IPC;APO;FTKO";
		String[] columnArray = columns.split(";");
		//String query1 = "PD=2015 AND PDB=CNA0";
		String query2 = "PD=2014 AND PDB=USB0";
		String query3 = "PD=2013 AND PDB=CNA0";
		String query4 = "PD=2012 AND PDB=CNA0";
		String query5 = "PD=2011 AND PDB=CNA0";
		String[] queryArray = {
				"PD=2015 AND PDB=CNA0 "
				,"PD=2014 AND PDB=CNA0 "
				,"PD=2013 AND PDB=CNA0 "
				,"PD=2012 AND PDB=CNA0 "
				,"PD=2011 AND PDB=CNA0 "
				,"PD=2015 AND PDB=USB0 "
				,"PD=2014 AND PDB=USB0 "
				,"PD=2013 AND PDB=USB0 "
				,"PD=2012 AND PDB=USB0 "
				,"PD=2011 AND PDB=USB0 "};
		final File newFile = new File("/soft/test/query.txt");


		for(String query : queryArray){
			
			List<HashMap> patentList = service.patentSearch(query, columns,null, start, recordnum);
			String colvalue = "";
			String[] valueArray;
			
			for(int i = 0; i<patentList.size();i++){
				HashMap map = patentList.get(i);
				queryTemp = "";
				for(String column : columnArray){
					if(map.get(column) == null){
						break;
					}
					colvalue = map.get(column).toString();
					// 	由于测试环境限制，字符存在逗号的就不要了
					if(colvalue.contains(",")){
						break;
					}
					if(!Strings.isNullOrEmpty(queryTemp)){
						queryTemp += " OR ";
					}

					//colvalue = colvalue.replace(";", "','").replace("/", "\\/");//.replace(" ", "");
					colvalue = colvalue.replace("'","\\'").replace(";", "' OR '").replace("/", "\\/");//.replace(" ", "");
					queryTemp +=column + "=('" + colvalue + "')";
				}
				if(!Strings.isNullOrEmpty(queryTemp)){
					Files.append(query + "AND (" + queryTemp + ")\r\n", newFile,StandardCharsets.UTF_8);
				}
			}
			
		}


	}
	
}
