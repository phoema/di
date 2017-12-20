package com.izhiliao.web;

/**
 * 适合国知在线的商标检索功能
 */
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.izhiliao.TrsHybaseConfig;
import com.izhiliao.service.CKMService;
import com.izhiliao.service.GzHybaseService;
import com.izhiliao.service.HybaseService;
import com.izhiliao.util.ResultInfo;
import com.izhiliao.util.TRSResult;

@RestController
@Slf4j
@RequestMapping("/gztrade")
public class GzTradeController {
	// yyyymmdd
		private long datetime = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date()));

		@Autowired
		TrsHybaseConfig hybaseConfig;
		@Autowired
		CKMService ckmService;
		@Autowired
		GzHybaseService gzhybaseService;

		@RequestMapping("/")
		public String help() {
			String help = "";
			help += "/excute \r\n" + "执行符合条件的记录的文件导出 \r\n</br>";
			return help;
		}

		/**
		 * 
		 * @param Query
		 * @param Columns
		 * @param Sort
		 * @param start 非必须项 默认0 检索列表从第几条返回 第一条为0
		 * @param recordnum
		 * @return
		 * @throws Exception
		 */
		@RequestMapping("/search")
		public TRSResult search(String strWhere, Integer page,String columns, String sort) throws Exception {
//			int startnum = 0;
//			int recordnum = 10;
			//商标名称原始
			if (Strings.isNullOrEmpty(columns)) {
				columns = "MNO";
			}
			
			if(Strings.isNullOrEmpty(sort)){
	    		sort="RELEVANCE";
	    	}
			
	    	//尼斯号
	    	if (!columns.contains("NC")) {
				columns += ";NC";
			}
	    	//注册号
	    	if(!columns.contains("RN")){
	    		columns += ";RN";
	    	}
	    	//注册日期
	    	if(!columns.contains("RD")){
	    		columns += ";RD";
	    	}
	    	//申请号
	    	if(!columns.contains("SN")){
	    		columns += ";SN";
	    	}
	    	//申请日期
	    	if(!columns.contains("FD")){
	    		columns += ";FD";
	    	}
	    	//申请人名称
	    	if(!columns.contains("HNO")){
	    		columns += ";HNO";
	    	}
	    	//代理人名称
	    	if(!columns.contains("ARO")){
	    		columns += ";ARO";
	    	}
	    	
	    	//序号扩展(逻辑主键)
	    	if(!columns.contains("TID")){
	    		columns += ";TID";
	    	}
	    	
	    	//当前权利状态
	    	if(!columns.contains("CS")){
	    		columns += ";CS";
	    	}
	    	
	    	//商标类型
	    	if(!columns.contains("MK")){
	    		columns += ";MK";
	    	}
	    	
	    	int start=0;
	    	
	    	if(page<1){
	    		page=1;
	    	}
	    	
	    	start=(page-1)*10;
	    	
			// 商标检索
	    	TRSResult result = gzhybaseService.tradeSearch(strWhere, columns, sort, start, 10);
			
			return result;
		}
		
		
		@RequestMapping("/getDetail")
		public TRSResult getDetail(String strWhere,String columns, String sort) throws Exception {
//			int startnum = 0;
//			int recordnum = 10;
			//注册号
			if (Strings.isNullOrEmpty(columns)) {
				columns = "RN";
			}
			//排序
			if(Strings.isNullOrEmpty(sort)){
	    		sort="RELEVANCE";
	    	}
			//申请号
			if (!columns.contains("SN")) {
				columns += ";SN";
			}
			
			//注册日期
			if (!columns.contains("RD")) {
				columns += ";RD";
			}
			
			//申请日期
			if (!columns.contains("FD")) {
				columns += ";FD";
			}
			
	    	//尼斯分类
	    	if (!columns.contains("NC")) {
				columns += ";NC";
			}
	    	
	    	//当前权利状态统计
	    	if(!columns.contains("CS")){
	    		columns += ";CS";
	    	}
	    	
	    	//类似群号
	    	if(!columns.contains("NCS")){
	    		columns += ";NCS";
	    	}
	    	
	    	//申请人名称
	    	if(!columns.contains("HNO")){
	    		columns += ";HNO";
	    	}
	    	
	    	//申请人区域代码
	    	if(!columns.contains("HNAC")){
	    		columns += ";HNAC";
	    	}
	    	
	    	//申请人地址
	    	if(!columns.contains("HNADO")){
	    		columns += ";HNADO";
	    	}
	    	
	    	//代理人名称
	    	if(!columns.contains("ARO")){
	    		columns += ";ARO";
	    	}
	    	//商品服务列表
	    	if(!columns.contains("PHRASE")){
	    		columns += ";PHRASE";
	    	}
	    	//初审公告期号
	    	if(!columns.contains("RAI")){
	    		columns += ";RAI";
	    	}
	    	//初审公告日期
	    	if(!columns.contains("FAD")){
	    		columns += ";FAD";
	    	}
	    	//初审公告页码(库里没有没找到)
	    	//国际注册日期
	    	if(!columns.contains("IRD")){
	    		columns += ";IRD";
	    	}
	    	//注册公告期号
	    	if(!columns.contains("RAI")){
	    		columns += ";RAI";
	    	}
	    	
	    	//优先权日期
	    	if(!columns.contains("MPRD")){
	    		columns += ";MPRD";
	    	}
	    	
	    	//专用权期限开始日期
	    	if(!columns.contains("SRSD")){
	    		columns += ";SRSD";
	    	}
	    	
	    	//专用权期限截止日期
	    	if(!columns.contains("SRED")){
	    		columns += ";SRED";
	    	}
	    	
	    	//异议截止日期
	    	if(!columns.contains("OED")){
	    		columns += ";OED";
	    	}
	    	
	    	//后期指定日期
	    	if(!columns.contains("LSD")){
	    		columns += ";LSD";
	    	}
	    	
	    	//指定颜色
	    	if(!columns.contains("MSC")){
	    		columns += ";MSC";
	    	}
	    	//共有商标（没有）
	    	//商标公告状态
	    	
	    	//认证编号
	    	if(!columns.contains("WKCN")){
	    		columns += ";WKCN";
	    	}
	    	
	    	//所在地区
	    	if(!columns.contains("WKA")){
	    		columns += ";WKA";
	    	}
	    	
	    	//认定机关
	    	if(!columns.contains("WKCA")){
	    		columns += ";WKCA";
	    	}
	    	
	    	//认定方式
	    	if(!columns.contains("WKCM")){
	    		columns += ";WKCM";
	    	}
	    	
	    	//认定批次
	    	if(!columns.contains("WKCB")){
	    		columns += ";WKCB";
	    	}
	    	
	    	//认定公布时间
	    	if(!columns.contains("WKCD")){
	    		columns += ";WKCD";
	    	}
	    	
	    	//商标类型
	    	if(!columns.contains("MK")){
	    		columns += ";MK";
	    	}
	    	
	    	
	    	
	    	//名称
	    	if(!columns.contains("MNO")){
	    		columns += ";MNO";
	    	}
	    	if(!columns.contains("TID")){
	    		columns += ";TID";
	    	}
	    	
	    	
	    	//企业官方网站（没有）
	    	
	    	/*int start=0;
	    	
	    	if(page<1){
	    		page=1;
	    	}
	    	
	    	start=(page-1)*hybaseConfig.ROWS+1;*/
	    	
			// 商标检索
	    	TRSResult result = gzhybaseService.tradeSearch(strWhere, columns, sort, 0,1);
			return result;
		}
		public ResultInfo search(){
			return null;
		}
	}
