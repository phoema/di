package com.di.divalid;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.di.App;
import com.di.service.GwssiService;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@Slf4j
public class GWssiServiceTest {
	@Autowired
	private GwssiService service;
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public GWssiServiceTest() {
		
	}

	@Test
	public void testCategory() throws Exception {

		String strWhere = "PD=2014";
		System.out.println(strWhere);
		String key = "IPC";
		String categorynum = "6";
		service.category(strWhere, key, categorynum);
		Map<String, Map<String, Long>> map = service.category(strWhere, key, categorynum);;
		Map<String, Long> map2 = null;
		for (String cat : map.keySet()) {
			map2 = map.get(cat);
			if(map2 == null) continue;
			for (String cat2 : map2.keySet()) {
				System.out.print(cat2);
				System.out.print("\t");
				System.out.println(map2.get(cat2));

			}
		}

	}
	
}
