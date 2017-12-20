package com.izhiliao.web;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.izhiliao.TrsHybaseConfig;
import com.izhiliao.service.CKMService;
import com.izhiliao.service.HybaseService;

/**
 * 本Controller针对原始文件进行初始的入库操作
 * 
 * @author jiahh 2015年5月13日
 *
 */
@RestController
@Slf4j
@RequestMapping("/")
public class HomeController {

	// yyyymmdd
	private long datetime = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date()));

	@Autowired
	TrsHybaseConfig hybaseConfig;
	@Autowired
	CKMService ckmService;
	@Autowired
	HybaseService hybaseService;
	@RequestMapping("/")
	public String help() {
		String help = "";
		help += "/search/cutword?maxword=5&text=控制模板和管理模块通过无线传感网络连接在一起</br>";
		help += "text:必须项 待拆词文本:控制模板和管理模块通过无线传感网络连接在一起</br>";
		help += "maxword:非必须项 默认5 最大拆词个数</br>";
		help += "/search/smartsearch?key=ABSO&text=一种餐饮智慧云无线传感网络通信基站</br>";
		help += "key:必须项 在哪个字段内检索</br>";
		help += "key集合:名称:TIO;技术领域:TFO;背景技术:TBO；发明内容:ISO；具体实施方式 :SEO；附图说明:DDO；权利要求:CLO；摘要:ABSO;</br>";
		help += "text:必须项 待检文本</br>";
		help += "columns:非必须项，返回哪些列， 默认为待检字段key及ANO申请号，指定格式 单值TIO，多值以分号隔开 TIO;ABSO</br>";
		help += "wordcount:非必须项 默认5 最大拆词个数</br>";
		help += "start:非必须项 默认0 检索列表从第几条返回 第一条为0</br>";
		help += "patcount:非必须项 默认10 检索列表返回多少条 key待检字段</br>";
		help += "start:非必须项 默认0 检索列表从第几条返回 第一条为0</br>";

		return help;
	}

}
