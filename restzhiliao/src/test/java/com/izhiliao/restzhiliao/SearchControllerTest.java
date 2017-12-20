package com.izhiliao.restzhiliao;


import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.gwssi.exception.DataBaseException;

import com.izhiliao.App;
import com.izhiliao.web.PatentController;


/**
 * 
 * @author jiahh 2015年5月11日
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@Slf4j
public class SearchControllerTest{
	@Autowired
	PatentController controller;

	public SearchControllerTest() {
		
	}

	@Test
	public void cutword(){
		long start = 0;
		
		for (int i=0;i<10;i++){
			start = System.currentTimeMillis();
			controller.cutword("控制模板和管理模块通过无线传感网络连接在一起", null);
			System.out.println(System.currentTimeMillis() - start);
		}
	}
	@Test
	public void smartsearch(){
		long start = 0;
		
		for (int i=0;i<10;i++){
			start = System.currentTimeMillis();
			controller.smartSearch("TIO", "带一键加速界面的手机", null, null, null, null);
			System.out.println(System.currentTimeMillis() - start);
		}
	}



}
