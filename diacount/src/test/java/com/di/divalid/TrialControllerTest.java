package com.di.divalid;


import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.di.App;
import com.di.web.TrialController;


/**
 * 
 * @author jiahh 2015年5月11日
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@Slf4j
public class TrialControllerTest{
	@Autowired
	TrialController trialController;

	public TrialControllerTest() {
		
	}

	@Test
	public void multiCategory() throws Exception{
		// String name, String mail, String company, String phone, String note
		trialController.mail("jiahh","贾辉辉","男", "a@a.com", "出版社" , "158888","","");
	}



}
