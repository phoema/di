package com.ipph.bio;


import java.util.HashSet;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.base.Strings;
import com.hp.util.EMBLUtil;
import com.ipph.bio.model.ExchangePno;
import com.ipph.bio.util.BIO_CONST;
import com.ipph.bio.web.RecordController;
import com.ipph.bio.web.RecordTestController;
import com.mongodb.WriteResult;


/**
 * 
 * @author jiahh 2015年5月11日
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@Slf4j
public class RecordTestControllerTest{
	@Autowired
	RecordTestController recordTestController;
	@Autowired
	RecordController recordController;

	String categorytop = "5";
	//String[] catarray = {"AD","PDT","IPC","AY","PY","IN","EPRY","CPC","LC","ILSC","AS"};
	String[] catarray = {"AD","PDT","IPC","IPCS","IPCC","AY","PY","IN","EPRD"};
	String[] orderarray = {"RELEVANCE","+RELEVANCE","AD","+AD","AD","+AD","PD","+PD","EPRD","+EPRD","INCO","+INCO","IPCSCC","+IPCSCC","CLN","+CLN","DEPC","+DEPC","DC","+DC"};
	public RecordTestControllerTest() {
		
	}

	@Test
	public void multiCategory(){

		//recordTestController.reexcutefastahasnoembl();
		System.out.println("");
	}
	@Test
	public void testreport() throws Exception{

		//recordTestController.report();
		System.out.println("");
	}
	@Test
	public void testpnsdiff(){
		log.info("Start:");
		//recordTestController.testpnsdiff();
	}

}
