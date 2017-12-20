package com.izhiliao.restzhiliao;


import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.gwssi.exception.DataBaseException;

import com.izhiliao.App;
import com.izhiliao.TrsHybaseConfig;
import com.izhiliao.service.GwssiService;
import com.izhiliao.web.CKMController;


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
