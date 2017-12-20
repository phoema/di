package com.izhiliao.restzhiliao;

import lrapi.lr;

/**
 * 模拟《概览》场景 Actions_GwssiOrder
 */
public class CopyOfActions_GwssiOrder {
	private String url =  "http://192.168.13.123:8082/gwssi/order";
	private String[] array = {"RELEVANCE", "+RELEVANCE", "AD", "+AD", "AD", "+AD", "PD", "+PD", "EPRD", "+EPRD", "INCO","+INCO", "IPCSCC", "+IPCSCC", "CLN", "+CLN", "DEPC", "+DEPC", "DC", "+DC"};
	private String[] catarray = {"AD", "PDT", "IPC", "AY", "PY", "IN", "EPRY", "CPC", "LC", "ILSC", "AS"};

	private int insertNum = 1;// 万的倍数 测试插入数据大小 100 = 100w
	private static int fieldNum = 10;
	private int totalExecuteTime = 0;

	public int init() throws Throwable {
		return 0;
	}

	public int end() throws Throwable {
		return 0;
	}// end of end

	public int action() {
		while (true) {
			readLR();
		}
		// return 0;
	}

	public void readLR() {
		// lr参数设置
		lrapi.web.set_max_html_param_len("262144");
		lrapi.web.reg_save_param("response", new String[]{"LB=", "RB=", "LAST"});
		lrapi.web.reg_save_param("ResponseBody", new String[]{"LB=", "RB=", "Search=Body", "LAST"});
		// web_set_max_html_param_len("262144"); // 默认最大长度为256
		// web_reg_save_param("ResponseBody", "LB=", "RB=", "Search=Body",
		// LAST);
		// web_save_header(REQUEST, "RequestHeader");//
		// REQUEST为内置变量，保存请求的头信息，需在发送URL请求前注册使用
		//lrapi.web.reg_save_param(RESPONSE, "ResponseHeader");// RESPONSE保存响应的头信息

		// getRequest();
		//String url = "http://192.168.13.123:8082/gwssi/order?readColumn=AD;PNO;TIO;ABSO&&strWhere=%s&&sortWhere=%s";
		lr.start_transaction("Order");
		int stat = -100;
		try {
			stat = lrapi.web.submit_data("", "Action=" + url, new String[]{
					"Method=POST", "RecContentType=text/html;charset=UTF-8", "Snapshot=t7.inf", "Mode=HTML",},
					new String[]{ // ITEM DATA
					"Name=readColumn", "Value=AD;PNO;TIO", lrapi.web.ENDITEM, "Name=strWhere", "Value=" + "PD=2014",
							lrapi.web.ENDITEM,"Name=sortWhere", "Value=" + "+RELEVANCE",
							lrapi.web.ENDITEM, lrapi.web.LAST});
			totalExecuteTime++;
		} catch (Exception e1) {
			lr.end_transaction("Order", lr.FAIL);
		}
		String record = lr.eval_string("<ResponseBody>");
		// System.out.println(record);
		// System.out.println("record" + record);
		if (stat == 0) { // sucess
			lr.end_transaction("Order", lr.PASS);
		} else {
			lr.end_transaction("Order", lr.FAIL);
		}
	}

}
