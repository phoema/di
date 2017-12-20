package com.di.divalid;


import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.di.App;
import com.di.service.GwssiService;


/**
 * 
 * @author jiahh 2015年5月11日
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@Slf4j
public class WebTest{
	@Autowired
	GwssiService gwssiService;

	public WebTest() {
		
	}

	@Test
    public void testSelect() throws Exception
    {
		System.out.println("TIO");
    }

}
