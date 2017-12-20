package com.di.divalid;


import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.gwssi.exception.DataBaseException;

import com.di.App;
import com.di.TrsHybaseConfig;
import com.di.service.GwssiService;
import com.di.web.CKMController;


/**
 * 
 * @author jiahh 2015年5月11日
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@Slf4j
public class CKMControllerTest{
	@Autowired
	CKMController ckmController;
	@Autowired
	TrsHybaseConfig trsHybaseConfig;
	@Autowired
	GwssiService gwssiService;

	public CKMControllerTest() {
		
	}

	@Test
	public void multiCategory() throws DataBaseException{
		ckmController.TRSPatentCluTestCaseNew(null);
	}



}
