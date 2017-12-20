package com.hp.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.tempuri.ArrayOfStdInfo;
import org.tempuri.DocResult;
import org.tempuri.DocdbnumService;
import org.tempuri.DocdbnumServiceSoap;
import org.tempuri.StdInfo;

import com.google.common.io.Files;
import com.ipph.bio.model.ExchangePno;
import com.ipph.bio.util.BIO_CONST;

public class EMBLUtil {
	// 请求GetStdPublicationInfo未返回值
	private static String MESSAGE_1 = "ArrayOfStdInfo is null";
	// 请求getStdAppPubInfo未返回值
	private static String MESSAGE_2 = "ArrayOfStdInfoisnull2";
	// 请求getStdAppPubInfo返回值
	private static String MESSAGE_3 = "getStdAppPubInfo";
	
	public static Hashtable<String, String> types = new Hashtable<String, String>();
	
	static {
		types.put("protein", "PRT");
		types.put("unassigned RNA", "RNA");
		types.put("unassigned DNA", "DNA");
//		types.put("空", "");
		types.put("genomic DNA", "DNA");
		types.put("mRNA", "RNA");
		types.put("rRNA", "RNA");
		types.put("other DNA", "DNA");
		types.put("other RNA", "RNA");
		types.put("genomic RNA", "RNA");
		types.put("RNA", "RNA");
		types.put("PRT", "PRT");
		types.put("DNA", "DNA");
		types.put("linear PRT", "PRT");
	}
	public static Hashtable<String, String> month = new Hashtable<String, String>();
	static {
		month.put("JAN", "01");
		month.put("FEB", "02");
		month.put("MAR", "03");
    	month.put("APR", "04");
    	month.put("MAY", "05");
    	month.put("JUN", "06");
    	month.put("JUL", "07");
    	month.put("AUG", "08");
    	month.put("SEP", "09");
    	month.put("OCT", "10");
    	month.put("NOV", "11");
    	month.put("DEC", "12");
	}
//	public static StdInfo getDocId(String country, String pno, String type) {
//		StdInfo std = new StdInfo();
//		std.setSTDPUBCOUNTRY(country);
//		std.setSTDPUBNUM(pno);
//		std.setSTDPUBKIND(type);
//		return std;
//
//		/**
//		 * try { DocdbnumService service = new DocdbnumService();
//		 * DocdbnumServiceSoap soap = service.getDocdbnumServiceSoap();
//		 * //DocResult result = soap.getStdAppPubInfo("WO", "2012103865",
//		 * "A2"); DocResult result = soap.getStdAppPubInfo(country, pno,
//		 * type); ArrayOfStdInfo array = result.getStdInfos(); List<StdInfo>
//		 * list = array.getStdInfo(); for (StdInfo std : list) { //return
//		 * std.getSTDPUBCOUNTRY() + std.getSTDPUBNUM() + std.getSTDPUBKIND();
//		 * //return std.getSTDPUBNUM(); return std; } } catch (Exception e) {
//		 * e.printStackTrace(); } return null;
//		 **/
//	}

	/**
	 * 通过webservice获取ExchangePno对象
	 * 乔宗章 2015-07-01 13:24:15
郭锋 2015-07-01 13:22:49 
docdbnum_service.DocResult result3 = docdbnu.GetStdAppPubInfo("US", "7435798", "",1, true); 
郭锋 2015-07-01 13:23:26 
第四个参数是整数类型，1为公开，2为申请，其他为不区分
乔宗章 2015-07-01 13:24:33
我让郭峰改了一下接口，区分是申请还是公开了

	 * @param pno
	 * @return
	 */
	public static ExchangePno getDocId(ExchangePno pno) {

		if("".equals(pno.kind)){
			pno.kind = null;
		}
		if("CA1197480A1".equals(pno._id)){
			pno.kind = "A";
		}
		try {
			DocdbnumService service = new DocdbnumService();
			// wsimport -p org.tempuri -keep http://192.168.6.28/ipphdataservice/docdbnum_service.asmx?wsdl -extension
			DocdbnumServiceSoap soap = service.getDocdbnumServiceSoap();
			DocResult result = null;
//			result = soap.getStdAppPubInfo(pno.country, pno.docnum, pno.kind, 0,true);
//			//result = soap.getStdAppPubInfo(pno.country, pno.docnum, null, 0,true);
//			pno.exception = "excute";
//			if (result.getStdInfos() == null) {
//				pno.exception = "excute-getStdAppPubInfo2";
//				result = soap.getStdAppPubInfo2(pno.country, pno.docnum, pno.kind,pno.date, 0,false);
//			}
			// 使用公开接口请求标准号的特殊id
			HashSet<String> specialPNSet = new HashSet<String>(); 
			specialPNSet.add("KR1020060002012A");
			specialPNSet.add("KR1020060015218A");
			specialPNSet.add("KR1020060015669A");
			if(pno.country.equals("KR")){
				// KR 号码规则过于复杂，首先单独处理
				int num = Integer.parseInt(pno.docnum.substring(2,6));
				String docnum = pno.docnum.substring(2);
				//AN
				if(num < 2007 && !specialPNSet.contains(pno._id)){
					pno.exception = "KR:KR10->KR,AN";
					result = getStd(pno.country, pno.docnum, pno.kind, pno.date ,2,false,soap);
					// 对于2007年之前的韩国文献，去掉文献类型“A”，去掉10，使用申请号作为检索入口进行检索。
					if(result.getStdInfos() == null){
						pno.exception = "KR:CutKind->KR,AN";
						result = getStd(pno.country, pno.docnum, null, pno.date ,2,true,soap);
					}
					if(result.getStdInfos() == null){
						pno.exception = "KR:Cut10GT2007->KR,PN";
						result = getStd(pno.country, docnum, pno.kind, pno.date ,1,false,soap);
					}
				}else{
					pno.exception = "KR:KR10->KR,PN";//3919
					// 2004年之后的数据，去掉文献类型,去掉10，使用公开号作为检索入口进行检索。
					result = getStd(pno.country, pno.docnum, pno.kind, pno.date ,1,false,soap);
					if(result.getStdInfos() == null){
						pno.exception = "KR:Cut10->KR,PN";//8067
						result = getStd(pno.country, docnum, pno.kind, pno.date ,1,false,soap);
					}
				}
				if(result.getStdInfos() == null){
					pno.exception = "excuteANPNDate";
					result = getStd(pno.country, pno.docnum, pno.kind, pno.date ,0,false,soap);
					if(result.getStdInfos() == null || result.getStdInfos().getStdInfo().size() != 1){
						// 2007年前的但是使用公开号的，保存在specialPNSet中，去另一分支
						if(num < 2007 && !specialPNSet.contains(pno._id)){
							// 去掉日期，使用申请号作为检索入口进行检索。
							if(result.getStdInfos() == null){
								pno.exception = "KR:NoDate->KR,AN";
								result = getStd(pno.country, pno.docnum, pno.kind, null ,2,true,soap);
							}
							if(result.getStdInfos() == null){
								pno.exception = "KR:Cut10CutKindGT2007->KR,AN";
								result = getStd(pno.country, docnum, null, pno.date ,2,true,soap);
							}
							if(result.getStdInfos() == null){
								pno.exception = "KR:Cut10CutKindCutDateGT2007->KR,AN";
								result = getStd(pno.country, docnum, null, null ,2,true,soap);
							}

						}else{
							if(result.getStdInfos() == null){
								pno.exception = "KR:CutKind->KR,PN";//0
								result = getStd(pno.country, docnum, null, pno.date ,1,true,soap);
							}
							if(result.getStdInfos() == null){
								pno.exception = "KR:KR10LT2007->KR,AN";
								result = getStd(pno.country, pno.docnum, pno.kind, pno.date ,2,false,soap);
							}
							if(result.getStdInfos() == null){
								pno.exception = "KR:CutKindLT2007->KR,AN";//8
								result = getStd(pno.country, pno.docnum, null, pno.date ,2,true,soap);
							}
							if(result.getStdInfos() == null){
								pno.exception = "KR:CutKindLT2007->KR,AN";
								result = getStd(pno.country, pno.docnum, null, pno.date ,2,true,soap);
							}
							if(result.getStdInfos() == null){
								pno.exception = "KR:NoDate->KR,PN";//1092
								result = getStd(pno.country, docnum, pno.kind, null ,1,false,soap);
							}
							if(result.getStdInfos() == null){
								pno.exception = "KR:NoDateLT2007->KR,AN";//9
								result = getStd(pno.country, pno.docnum, pno.kind, null ,2,false,soap);
							}
						}
					}
				}
			}
			else //if(pno.date == null)
			{
				pno.exception = "excutePN";
				result = getStd(pno.country, pno.docnum, pno.kind, pno.date ,1,false,soap);

				if (result.getStdInfos() == null) {
					pno.exception = "excutePN-cutkind";
					//result = soap.getStdAppPubInfo2(pno.country, pno.docnum, null,pno.date, 0,true);
					result = getStd(pno.country, pno.docnum, null,pno.date, 1,false,soap);
				}
				if (result.getStdInfos() == null) {
					pno.exception = "excutePN-likeKind";
					result = getStd(pno.country, pno.docnum, pno.kind, pno.date ,1,true,soap);
				}
				if (result.getStdInfos() == null) {
					pno.exception = "excuteANPN";
					//result = soap.getStdAppPubInfo2(pno.country, pno.docnum, pno.kind,pno.date, 0,true);
					result = getStd(pno.country, pno.docnum, pno.kind,pno.date, 0,false,soap);
				}
				if (result.getStdInfos() == null) {
					pno.exception = "excuteANPN-cutkind";
					//result = soap.getStdAppPubInfo2(pno.country, pno.docnum, null,pno.date, 0,true);
					result = getStd(pno.country, pno.docnum, null,pno.date, 0,false,soap);
				}
				if(result.getStdInfos() == null && pno.date != null){
					pno.exception = "excutePN-NoDate";
					result = getStd(pno.country, pno.docnum, pno.kind, null ,1,false,soap);
				}
			}
//			else{
//				pno.exception = "excute-getStdAppPubInfo2";
//				//result = soap.getStdAppPubInfo2(pno.country, pno.docnum, pno.kind,pno.date, 0,true);
//				result = getStd(pno.country, pno.docnum, pno.kind,pno.date, 0,false,soap);
//				if (result.getStdInfos() == null) {
//					pno.exception = "excute-getStdAppPubInfo2-cutkind";
//					//result = soap.getStdAppPubInfo2(pno.country, pno.docnum, null,pno.date, 0,true);
//					result = getStd(pno.country, pno.docnum, null,pno.date, 0,false,soap);
//				}
//			}
			// 异常情况
			if (result == null || result.getStdInfos() == null || result.getStdInfos().getStdInfo() == null|| result.getStdInfos().getStdInfo().size() == 0) {
				result.setStdInfos(null);
				if("TEST".equals(pno.country)){
					//nothing
				}
				if("EP".equals(pno.country)){
					//如仍找不到则去掉日期查找
					pno.exception = "EP:NoDate->EP,PN";
					result = getStd(pno.country, pno.docnum, pno.kind,null, 1, false,soap);
					if(result.getStdInfos() == null){
						//如仍找不到则去掉日期去掉Kind查找
						pno.exception = "EP:LikekindNoDate->EP,PN";
						result = getStd(pno.country, pno.docnum,pno.kind, null, 1,true,soap);
					}
					if(result.getStdInfos() == null){
						//如仍找不到则去掉日期去掉Kind查找
						pno.exception = "EP:CutkindNoDate->EP,PN";
						result = getStd(pno.country, pno.docnum,null, null, 1,true,soap);
					}
				}

//				else if("EP".equals(pno.country)){
//					//使用A2替换A
//					if("A".equals(pno.kind)){
//						pno.exception = "EP:A->A2";
//						result = soap.getStdAppPubInfo(pno.country, pno.docnum, "A2", 1,false);
//						if(result.getStdInfos() == null){
//							//如仍找不到则使用A1替换A查找
//							pno.exception = "EP:A->A1";
//							result = soap.getStdAppPubInfo(pno.country, pno.docnum, "A1", 1,false);
//						}
//					}
//					// GOTO result
//				}else if("AU".equals(pno.country)){
//					//使用B2替换B
//					if("B".equals(pno.kind)){
//						pno.exception = "AU:B->B2";
//						result = soap.getStdAppPubInfo(pno.country, pno.docnum, "B2", 1,false);
//						if(result.getStdInfos() == null){
//							//如仍找不到则使用B1替换B查找
//							pno.exception = "AU:B->B1";
//							result = soap.getStdAppPubInfo(pno.country, pno.docnum, "B1", 1,false);
//						}
//					}
//					// TODO result
//				}
				else if("US".equals(pno.country)){
					//如仍找不到则去掉日期查找
					if("A".equals(pno.kind)){
						//如仍找不到则去掉日期去掉Kind查找
						pno.exception = "US:Cutkind->US,PN";
						result = getStd(pno.country, pno.docnum,null, pno.date, 1,true,soap);
						if(result.getStdInfos() == null){
							//如仍找不到则去掉日期去掉Kind查找
							pno.exception = "US:CutkindNoDate->US,PN";
							result = getStd(pno.country, pno.docnum,null, null, 1,true,soap);
						}
						if(result.getStdInfos() == null){
							pno.exception = "US:NoDate->US,PN";
							result = getStd(pno.country, pno.docnum, pno.kind,null, 1, false,soap);
						}
					}

//					if("A".equals(pno.kind)){
//						pno.exception = "US:A->B2";
//						result = soap.getStdAppPubInfo(pno.country, pno.docnum, "B2", 1,false);
//						if(result.getStdInfos() == null){
//							//如仍找不到则使用B1替换A查找
//							pno.exception = "US:A->B1";
//							result = soap.getStdAppPubInfo(pno.country, pno.docnum, "B1", 1,false);
//						}
//					}
					//再版
					if("E".equals(pno.kind)){
						pno.exception = "US:US->USRE";
						result = getStd(pno.country, "RE"+Integer.parseInt(pno.docnum), pno.kind,pno.date, 1,false,soap);
					}
					//再版
					if("H".equals(pno.kind)){
						pno.exception = "US:US->USH";
						result = getStd(pno.country, "H"+Integer.parseInt(pno.docnum), pno.kind,pno.date, 1,false,soap);
					}

				}
				else if("USRE".equals(pno.country)){
					pno.exception = "USRE:REdocnum";
					//本地 USRE33188E 返回 ：US733188E
					result = getStd("US", "RE"+pno.docnum, "E",pno.date, 1,false,soap);
				}
				else if("JP".equals(pno.country)){
					//TODO
					String docnum = pno.docnum;
					int num = Integer.parseInt(pno.docnum.substring(0,4));
					// 
					if(pno.docnum.length()==10 && num<= 1988 && num >1925){
						//昭和年(S)+1925=公元年  昭和年：1925-1988 变换规则，四位年-1925 为 加-，或JP后加S
						//本地号 JP1988294791A 转换  JPS63294791A 返回：JP1988294791A 19881201
						docnum = "S"+String.format("%02d",(num-1925))+Integer.parseInt(pno.docnum.substring(4));
						pno.exception = "JP:YYYY->JPS";
						
						result = getStd(pno.country, docnum, pno.kind, pno.date,1,false,soap);

					}
					if(pno.docnum.length()==10 && num<= 1999 && num >1988){
						//昭和年(S)+1925=公元年  昭和年：1925-1988 变换规则，四位年-1925 为 加-，或JP后加S
						//本地号 JP1994316598A 转换 JPH06316598A 返回：JP1994316598A
						docnum = "H"+String.format("%02d",(num-1988))+Integer.parseInt(pno.docnum.substring(4));
						pno.exception = "JP:YYYY->JPH";
						//result = soap.getStdAppPubInfo(pno.country, docnum, pno.kind, 1,false);
						result = getStd(pno.country, docnum, pno.kind, pno.date,1,false,soap);

					}
					if(result.getStdInfos() == null && num == 1989){
						//1989可能是S64
						docnum = "S64"+Integer.parseInt(pno.docnum.substring(4));
						pno.exception = "JP:S64";
						//result = soap.getStdAppPubInfo(pno.country, docnum, pno.kind, 1,false);
						result = getStd(pno.country, docnum, pno.kind,pno.date, 1,false,soap);
					}
					
				}
				else if("WO".equals(pno.country)){
					String docnum = pno.docnum;
					// 如果2004之前，截取20
					if(pno.docnum.length()==10 && Integer.parseInt(pno.docnum.substring(0,4))< 2004){
						pno.exception = "WO:cut20";
						docnum = pno.docnum.substring(2);
						// 六位流水
						result = getStd(pno.country, docnum, pno.kind,pno.date, 1, true,soap);
						// 5位流水
						if(result.getStdInfos() == null && '0'==pno.docnum.charAt(4)){
							pno.exception = "WO:cut20 and cut 0";
							docnum = pno.docnum.substring(2,4)+pno.docnum.substring(5);
							result = getStd(pno.country, docnum, pno.kind,pno.date, 1, true,soap);
						}
					}
					if(result.getStdInfos() == null){
						//如仍找不到则去掉日期查找
						pno.exception = "WO:NoDate->WO,PN";
						result = getStd(pno.country, pno.docnum, pno.kind,null, 1, false,soap);
					}
					if(result.getStdInfos() == null){
						//如仍找不到则去掉日期去掉Kind查找
						pno.exception = "WO:CutkindNoDate->WO,PN";
						result = getStd(pno.country, pno.docnum,null, null, 1,true,soap);
					}
				}

//				else if("WO".equals(pno.country)){
//					//
//					String docnum = pno.docnum;
//					// 如果2004之前，截取20
//					if(pno.docnum.length()==10 && Integer.parseInt(pno.docnum.substring(0,4))< 2004){
//						docnum = pno.docnum.substring(2);
//					}
//					pno.exception = "WO:cut20";
//					result = soap.getStdAppPubInfo(pno.country, docnum, pno.kind, 1,false);
//					if(result.getStdInfos() == null){
//						//去掉文献类型
//						if("A1".equals(pno.kind)){
//							pno.exception = "WO:A1->A2";
//							result = soap.getStdAppPubInfo(pno.country, docnum, "A2", 1,false);
//						}
//						//去掉文献类型
//						if("A".equals(pno.kind)){
//							pno.exception = "WO:A->A1";
//							result = soap.getStdAppPubInfo(pno.country, docnum, "A1", 1,false);
//							if(result.getStdInfos() == null){
//								//如仍找不到则使用B1替换A查找
//								pno.exception = "WO:A->A2";
//								result = soap.getStdAppPubInfo(pno.country, docnum, "A2", 1,false);
//							}
//						}
//					}
//					
//					// 如果还没有值，且符合【如果2004之前，截取20】多去一个零
//					if(result.getStdInfos() == null && pno.docnum.length()==10 
//							&& Integer.parseInt(pno.docnum.substring(0,4))< 2004
//							&& '0'==pno.docnum.charAt(4)){
//						docnum = pno.docnum.substring(2,4)+pno.docnum.substring(5);
//						
//						pno.exception = "WO:cut20 and cut 0";
//						result = soap.getStdAppPubInfo(pno.country, docnum, pno.kind, 1,false);
//						if(result.getStdInfos() == null){
//							//去掉文献类型
//							if("A1".equals(pno.kind)){
//								pno.exception = "WO:A1->A2Cut0";
//								result = soap.getStdAppPubInfo(pno.country, docnum, "A2", 1,false);
//							}
//							//去掉文献类型
//							if("A".equals(pno.kind)){
//								pno.exception = "WO:A->A1Cut0";
//								result = soap.getStdAppPubInfo(pno.country, docnum, "A1", 1,false);
//								if(result.getStdInfos() == null){
//									//如仍找不到则使用B1替换A查找
//									pno.exception = "WO:A->A2Cut0";
//									result = soap.getStdAppPubInfo(pno.country, docnum, "A2", 1,false);
//								}
//							}
//						}
//					}
//
//				}
				else if("KR".equals(pno.country)){
					if(pno.docnum.startsWith("10") && pno.docnum.length() == 13){
//						int num = Integer.parseInt(pno.docnum.substring(2,6));
//						String docnum = pno.docnum.substring(2);
//						if(num < 2004){
//							pno.exception = "KR:KR10->KR,AN";
//							// 对于2004年之前的韩国文献，去掉文献类型“A”，去掉10，使用申请号作为检索入口进行检索。
//							if(result.getStdInfos() == null){
//								pno.exception = "KR:NoDate->KR,AN";
//								result = getStd(pno.country, pno.docnum, pno.kind, null ,2,false,soap);
//							}
//
//						}else{
//							pno.exception = "KR:KR10->KR,PN";
//							// 2004年之后的数据，去掉文献类型,去掉10，使用公开号作为检索入口进行检索。
//							result = soap.getStdAppPubInfo(pno.country, pno.docnum, pno.kind, 1,false);
//							if(result.getStdInfos() == null){
//								pno.exception = "KR:NoDate->KR,PN";
//								result = getStd(pno.country, pno.docnum, pno.kind, null ,1,false,soap);
//							}
//						}
//						if(result.getStdInfos() == null){
//							pno.exception = "KR:Cut10";
//							if(num < 2004){
//								pno.exception = "KR:KR10->Cut10KR,AN";
//								// 对于2004年之前的韩国文献，去掉文献类型“A”，去掉10，使用申请号作为检索入口进行检索。
//								if(result.getStdInfos() == null){
//									pno.exception = "KR:Cut10NoDate->KR,AN";
//									result = getStd(pno.country, docnum, pno.kind, null ,2,false,soap);
//								}
//
//							}else{
//								pno.exception = "KR:KR10->Cut10KR,PN";
//								// 2004年之后的数据，去掉文献类型,去掉10，使用公开号作为检索入口进行检索。
//								result = getStd(pno.country, docnum, pno.kind, pno.date ,1,false,soap);
//								if(result.getStdInfos() == null){
//									pno.exception = "KR:Cut10NoDate->KR,PN";
//									result = getStd(pno.country, docnum, pno.kind, null ,1,false,soap);
//								}
//							}
//						}
//						
//						pno.exception = "KR:Cut10";
//						result = getStd(pno.country, pno.docnum.substring(2), null, pno.date ,0,true,soap);
//						if(result.getStdInfos() == null){
//							pno.exception = "KR:NoDate->KR,AN";
//							result = getStd(pno.country, pno.docnum, pno.kind, null ,2,false,soap);
//						}
//						if(result.getStdInfos() == null){
//							pno.exception = "KR:NoDateCutKind->KR,AN";
//							result = getStd(pno.country, pno.docnum, null, null ,2,true,soap);
//						}
//						if(result.getStdInfos() == null){
//							pno.exception = "KR:Cut10NoDate->KR,AN";
//							result = getStd(pno.country, pno.docnum.substring(2), pno.kind, null ,2,false,soap);
//						}
//						if(num < 2004){
//							pno.exception = "KR:KR10->KR,AN";
//							// 对于2004年之前的韩国文献，去掉文献类型“A”，去掉10，使用申请号作为检索入口进行检索。
//							result = soap.getStdAppPubInfo(pno.country, pno.docnum.substring(2), pno.kind, 2,false);
//						}else{
//							pno.exception = "KR:KR10->KR,PN";
//							// 2004年之后的数据，去掉文献类型,去掉10，使用公开号作为检索入口进行检索。
//							result = soap.getStdAppPubInfo(pno.country, pno.docnum.substring(2), pno.kind, 1,false);
//						}
//						if(result.getStdInfos() == null){
//							//如仍找不到只去掉文献类型kind
//							pno.exception = "KR:A->Cutkind";
//							result = soap.getStdAppPubInfo(pno.country, pno.docnum, null, 2,false);
//							if(result.getStdInfos() == null && num >=2004){
//								// （还有65条） 如仍找不到只去掉10，保留文献类型kind，使用申请号作为检索入口进行检索。
//								//大于2004但是请求AN,限制2004是因为2004之内的已经调用了此方法
//								pno.exception = "KR:KR10->KR,AN:ButGT2004";
//								result = soap.getStdAppPubInfo(pno.country, pno.docnum.substring(2), pno.kind, 2,false);
//							}
//						}

					}
				}
//					else if("AT".equals(pno.country)){
//					//去掉文献类型
//					if("A".equals(pno.kind)){
//						pno.exception = "AT:A->B";
//						result = soap.getStdAppPubInfo(pno.country, pno.docnum, "B", 1,false);
//					}
//				}
			}
			
			if (result.getStdInfos() != null){
				ArrayOfStdInfo array = result.getStdInfos();
				if (array != null && array.getStdInfo() != null) {
					if (array.getStdInfo().size() > 1) {
						//System.out.println(array.getStdInfo().size());
					}
					for (StdInfo std : array.getStdInfo()) {
						if(pno.date != null && (array.getStdInfo().size() == 1 || pno.date.equals(std.getSTDPUBDATE()))){
							pno.StdInfo = std;
							pno.PNS = std.getSTDPUBCOUNTRY()+ std.getSTDPUBNUM() + std.getSTDPUBKIND();
							break;
						}
						else if("KR1019940700536A".equals(pno._id)){
							// KR1019940700536A KR1019930702844A 出现巧合KR1019940700536A的申请号、申请日期，和KR1019930702844A的公开号、公开日期完全匹配
							if(std.getSTDAPPNUM().equals("101994000700536")){
								pno.StdInfo = std;
								pno.PNS = std.getSTDPUBCOUNTRY()+ std.getSTDPUBNUM() + std.getSTDPUBKIND();
								break;
							}
						}
						else if (pno.StdInfo == null || pno.StdInfo.getSTDPUBNUM() == null || pno.StdInfo.getSTDPUBDATE().compareTo(std.getSTDPUBDATE()) > 0 && pno.StdInfo.getSTDPUBNUM() != null)
						{
							// 取最早日期
							pno.StdInfo = std;
							pno.PNS = std.getSTDPUBCOUNTRY()+ std.getSTDPUBNUM() + std.getSTDPUBKIND();
						}
					}
				}
			}
		} catch (Exception e) {
			pno.exception = e.getMessage();
		}

		return pno;
	}

	private ExchangePno DocToExchangePno(ExchangePno pno,ArrayOfStdInfo array){
		if (array != null && array.getStdInfo() != null) {
			if (array.getStdInfo().size() > 1) {
				System.out.println(array.getStdInfo().size());
			}
			for (StdInfo std : array.getStdInfo()) {
				if (pno.StdInfo == null || pno.StdInfo.getSTDPUBDATE().compareTo(std.getSTDPUBDATE()) > 0) {
					// 取最早日期
					pno.StdInfo = std;
					pno.PNS = std.getSTDPUBCOUNTRY()+ std.getSTDPUBNUM() + std.getSTDPUBKIND();
					pno.exception = "1";
					break;
				}
			}
		}

		return null;
	}
//	/**
//	 * 通过webservice获取ExchangePno对象
//	 * 
//	 * @param pno
//	 * @return
//	 */
//	public static ExchangePno getDocId(ExchangePno pno, MongoTemplate mongoTemplate) {
//
//		try {
//			// 查看库中是否有记录
//			ExchangePno existPno = mongoTemplate.findOne(new Query(Criteria.where("_id").is(pno._id)), ExchangePno.class,
//					BIO_CONST.EXCHANGE_PNO);
//			if (existPno != null){
//				return existPno;
//			} else {
//				// 通过webservice获取
//				DocdbnumService service = new DocdbnumService();
//				// webservice
//				DocdbnumServiceSoap soap = service.getDocdbnumServiceSoap();
//				DocResult result = soap.getStdAppPubInfo(pno.country, pno.docnum, pno.kind, false);
//				// 异常情况
//				if (result.getException() != null) {
//					pno.exception = result.getException();
//				} else {
//					ArrayOfStdInfo array = result.getStdInfos();
//					if (array != null && array.getStdInfo() != null) {
//						if (array.getStdInfo().size() > 1) {
//							System.out.println(array.getStdInfo().size());
//						}
//						for (StdInfo std : array.getStdInfo()) {
//							if (pno.StdInfo == null || pno.StdInfo.getSTDPUBDATE().compareTo(std.getSTDPUBDATE()) > 0) {
//								// 取最早日期
//								pno.StdInfo = std;
//							}
//						}
//						// 正常查出，将结果存入数据库
//						//mongoTemplate.save(pno, BIO_CONST.EXCHANGE_PNO);
//					} else {
//						pno.exception = "ArrayOfStdInfo is null";
//						result = soap.getStdAPPlicationInfo(pno.country, pno.docnum, pno.kind); 
//						if (result.getException() != null) {
//							pno.exception = result.getException();
//						} else {
//							array = result.getStdInfos();
//							if (array != null && array.getStdInfo() != null) {
//								if (array.getStdInfo().size() > 1) {
//									System.out.println(array.getStdInfo().size());
//								}
//								for (StdInfo std : array.getStdInfo()) {
//									if (pno.StdInfo == null || pno.StdInfo.getSTDPUBDATE().compareTo(std.getSTDPUBDATE()) > 0) {
//										// 取最早日期
//										pno.StdInfo = std;
//									}
//								}
//								// 正常查出，将结果存入数据库
//								//mongoTemplate.save(pno, BIO_CONST.EXCHANGE_PNO);
//							} else {
//								pno.exception = "ArrayOfStdInfo is null getStdAPPlicationInfo";
//							}
//						}
//					}
//				}
//				
//			}
//		} catch (Exception e) {
//			pno.exception = e.getMessage();
//		}
//
//		return pno;
//	}

//	/**
//	 * 通过webservice获取ExchangePno对象
//	 * 
//	 * @param pno
//	 * @return
//	 */
//	public static ExchangePno getDocIdWithoutWebService(ExchangePno pno, MongoTemplate mongoTemplate) {
//
//		try {
//			// 查看库中是否有记录
//			ExchangePno existPno = mongoTemplate.findOne(new Query(Criteria.where("_id").is(pno._id)), ExchangePno.class,
//					BIO_CONST.EXCHANGE_PNO);
//			if (existPno != null){
//				return existPno;
//			}
//		} catch (Exception e) {
//			pno.exception = e.getMessage();
//		}
//
//		return pno;
//	}
	
	public static Map<String, ExchangePno> getDocIdsWithoutWebService(MongoTemplate mongoTemplate) {
		Map<String, ExchangePno> map = new HashMap<String, ExchangePno>();
		try {
			// 查看库中是否有记录
			List<ExchangePno> existPno = mongoTemplate.findAll(ExchangePno.class,BIO_CONST.EXCHANGE_PNO);
			for (ExchangePno pno : existPno){
				map.put(pno._id, pno);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}

	public static long getCRC32(String content) {
		CRC32 crc = new CRC32();
		if (content != null) {
			crc.update(content.getBytes());
			// 计算内容的CRC32
			return crc.getValue();
		}
		return 0;
	}

	public static DocResult getStd (String country, String docnum, String kind,String date ,int numType,boolean isLike, DocdbnumServiceSoap soap){
		DocResult result = null;
		if(date == null){
			result = soap.getStdAppPubInfo(country, docnum, kind,numType, isLike);
		}else{
			result = soap.getStdAppPubInfo2(country, docnum, kind,date,numType, isLike);
		}
		if(result.getStdInfos() == null || result.getStdInfos().getStdInfo() == null|| result.getStdInfos().getStdInfo().size() == 0){
			result.setStdInfos(null);
		}
		return result;
	}
	/**
	 * @param args
	 */
	public static void main(String args[]) {
		Pattern reg_splitno = Pattern.compile("^([A-Za-z]{2,5})(\\d+)(\\w*)");
		
		ExchangePno obj = new ExchangePno();
		obj._id = "WO2016177833A1";// WO9949035A2 19990930	WO0130809A WO0130809A1	WO0104328A1 WO0104328A1
		Matcher matcherSplit = reg_splitno.matcher(obj._id);
		obj.date = "20161110";
		if (matcherSplit.find()) {
			obj.country = matcherSplit.group(1);
			obj.docnum = matcherSplit.group(2);
			obj.kind = matcherSplit.group(3);
			System.out.println(obj.docnum);
		}
		// StdInfo info = getDocId("JP", "2008178402", "A");
		// System.out.println(info.getSTDPUBNUM());mongoTemplate
		//KR1020037015298A KR1020040097054A
//		obj._id = "KR1020037015298A";
//		obj.country = "KR";
//		obj.docnum = "1020037015298";
//		obj.kind = "A";
		//String str = "KR1020047016996A".substring(2);
		//obj._id = "WO2006010495A2";
		//obj._id = "KR1020050092069A";
//		obj._id = "WO2015064754A";
//		obj.country = "WO";
//		obj.docnum = "2015064754";
//		obj.kind = "A";
//		obj.date = "20150507";
//		obj._id = "JPH249387A";
//		obj.country = "JP";
//		obj.docnum = "1990097391";
		// JP1988014694A
//		obj._id = "JP1988014694A";
//		obj.country = "JP";
//		obj.docnum = "1988014694";
//		obj.kind = "string.empty";
		DocdbnumService service = new DocdbnumService();
		// wsimport -p org.tempuri -keep http://192.168.6.28/ipphdataservice/docdbnum_service.asmx?wsdl -extension
		DocdbnumServiceSoap soap = service.getDocdbnumServiceSoap();
		DocResult result = null;
		
//		result = getStd(obj.country, obj.docnum, obj.kind, obj.date ,1,false,soap);
//		result = getStd(obj.country, obj.docnum, null, obj.date ,1,true,soap);
//		result = getStd(obj.country, obj.docnum, "", obj.date ,1,true,soap);
//		result = getStd(obj.country, obj.docnum, null, obj.date ,1,false,soap);
//		result = getStd(obj.country, obj.docnum, "", obj.date ,2,false,soap);
//		result = getStd(obj.country, obj.docnum, obj.kind, obj.date ,2,false,soap);
//		result = getStd(obj.country, obj.docnum, null, obj.date ,2,true,soap);
//		result = getStd(obj.country, obj.docnum, "", obj.date ,2,true,soap);
//		result = getStd(obj.country, obj.docnum, null, obj.date ,2,false,soap);
//		result = getStd(obj.country, obj.docnum, "", obj.date ,2,false,soap);
//		result = soap.getStdAppPubInfo(obj.country, obj.docnum, obj.kind,1, false);
//		result = soap.getStdAppPubInfo(obj.country, obj.docnum, obj.kind,2, false);
//		result = soap.getStdAppPubInfo(obj.country, obj.docnum, obj.kind,3, false);
//		result = soap.getStdAppPubInfo(obj.country, obj.docnum.substring(2), obj.kind,1, false);//KR1020050000346Aok 20050000346
//		result = soap.getStdAppPubInfo(obj.country, obj.docnum.substring(2), obj.kind,2, false);//KR1020050000346Aok 100590053
//		//result = soap.getStdAppPubInfo(obj.country, obj.docnum.substring(2), obj.kind,3, false);
//		result = soap.getStdAppPubInfo(obj.country, obj.docnum, null,1, false);
//		result = soap.getStdAppPubInfo(obj.country, obj.docnum, null,2, false);//KR1020050000346Aok 100590053
//		result = soap.getStdAppPubInfo(obj.country, obj.docnum, null,2, true);//KR1020050000346Aok 100590053
//		result = soap.getStdAppPubInfo(obj.country, obj.docnum, null,3, false);
//		result = soap.getStdAppPubInfo2(obj.country, obj.docnum.substring(2), null,obj.date,2, true);//KR1020050000346Aok 20050000346
//		result = soap.getStdAppPubInfo2(obj.country, obj.docnum, obj.kind,obj.date, 0,false);;
//		result = soap.getStdAppPubInfo2(obj.country, obj.docnum, null,obj.date, 0,true);;
//		result = soap.getStdAppPubInfo2(obj.country, obj.docnum, "",obj.date, 0,true);;
//		result = soap.getStdAppPubInfo(obj.country, obj.docnum, "", 0,true);
//		result = soap.getStdAppPubInfo2(obj.country, obj.docnum, obj.kind,obj.date, 1,false);;
//		result = soap.getStdAppPubInfo2(obj.country, obj.docnum, "",obj.date, 0,false);;
//		result = getStd(obj.country, obj.docnum.substring(2), null, null ,2,false,soap);
		ExchangePno pno = getDocId(obj); // KR1020050000346Aok 20050000346
		System.out.println(pno.PNS);
		//String[] arrays = {"KR1020067019039A","KR1020067015813A","KR1020067014916A","KR1020067013685A","KR1020067012152A","KR1020067011699A","KR1020067011358A","KR1020067010908A","KR1020057002657A","KR1020057000869A","KR1020057000495A","KR1020057000077A","KR1020047021459A","KR1020047021388A","KR1020047021273A","KR1020047021061A","KR1020047020345A","KR1020047020311A","KR1020047020138A","KR1020047019564A","KR1020047019541A","KR1020047019438A","KR1020047019418A","KR1020047019194A","KR1020047019191A","KR1020047019092A","KR1020047018958A","KR1020047018629A","KR1020047018599A","KR1020047018595A","KR1020047018592A","KR1020047017776A","KR1020047017741A","KR1020047017714A","KR1020047017669A","KR1020047017532A","KR1020047017339A","KR1020047017099A","KR1020047017047A","KR1020047016863A","KR1020047016573A","KR1020047016096A","KR1020047016042A","KR1020047015773A","KR1020047015589A","KR1020047015322A","KR1020047014352A","KR1020047013080A","KR1020047012256A","KR1020047011906A","KR1020047010362A","KR1020047006771A","KR1020047006766A","KR1020047005259A","KR1020047001914A","KR1020027007921A","KR1020027005079A","KR1020027004776A"};
		//String[] arrays = {"KR1020067019039A","KR1020067015813A","KR1020067014916A","KR1020067013685A","KR1020067012152A","KR1020067011699A","KR1020067011358A","KR1020067010908A","KR1020057002657A","KR1020057000869A","KR1020057000495A","KR1020057000077A","KR1020047021459A","KR1020047021388A","KR1020047021273A","KR1020047021061A","KR1020047020345A","KR1020047020311A","KR1020047020138A","KR1020047019564A","KR1020047019541A","KR1020047019438A","KR1020047019418A","KR1020047019194A","KR1020047019191A","KR1020047019092A","KR1020047018958A","KR1020047018629A","KR1020047018599A","KR1020047018595A","KR1020047018592A","KR1020047017776A","KR1020047017741A","KR1020047017714A","KR1020047017669A","KR1020047017532A","KR1020047017339A","KR1020047017099A","KR1020047017047A","KR1020047016863A","KR1020047016573A","KR1020047016096A","KR1020047016042A","KR1020047015773A","KR1020047015589A","KR1020047015322A","KR1020047014352A","KR1020047013080A","KR1020047012256A","KR1020047011906A","KR1020047010362A","KR1020047006771A","KR1020047006766A","KR1020047005259A","KR1020047001914A","KR1020027007921A","KR1020027005079A","KR1020027004776A"};
		//String[] arrays = {"KR1020020066981A","KR1019997003508A","KR1019990029041A","KR1020017002657A","KR1020000085035A","KR1020037014773A","KR1019997003370A","KR1020047002203A","KR1019970063612A","KR1020010008095A","KR1020010008094A","KR1020010008093A","KR1020017003743A","KR1020047007632A","KR1020047002335A","KR1020047002334A","KR1020047002333A","KR1020047017133A","KR1020047002332A","KR1019997001589A","KR1020010047600A","KR1020050026244A","KR1020050005753A","KR1020067015230A","KR1020030096613A"};
//		String[] arrays = {"KR1020017002657A","KR1019970063612A","KR1020017003743A","KR1020027008944A","KR1020007000526A"};
//		
//		for(String pnostr : arrays){
//			obj = new ExchangePno();
//			obj._id = pnostr;
//			obj.country = "KR";
//			//obj.docnum = pnostr.substring(2,pnostr.length()-1);
//			obj.docnum = pnostr.substring(2,pnostr.length()-1);
//			obj.kind = "A";
//			ExchangePno pno2 = getDocId(obj);
//			result = getStd(obj.country, obj.docnum.substring(2), null, null ,2,false,soap);
//			if(result.getStdInfos() != null){
//				pno2.StdInfo = result.getStdInfos().getStdInfo().get(0);
//			}
//
//			if(pno2 != null && pno2.StdInfo != null){
//				System.out.println(pno2._id + "   " + pno2.StdInfo.getSTDPUBNUM() + "  "+ pno2.exception);
//			}else{
//				System.out.println(pno2._id + "   " +  " getSTDPUBNUM is null ");
//			}
//		}
		
	}
	/**
	 * 获取保证号码和日期都能匹配的申请信息或公开信息
	 * @param pno
	 * @throws IOException 
	 */
	private void testkr(ExchangePno pno) throws IOException{
		DocdbnumService service = new DocdbnumService();
		// wsimport -p org.tempuri -keep http://192.168.6.28/ipphdataservice/docdbnum_service.asmx?wsdl -extension
		DocdbnumServiceSoap soap = service.getDocdbnumServiceSoap();
		DocResult result = null;
		DocResult resultAN = null;
		DocResult resultPN = null;
		if(pno.country.equals("KR")){
			// KR 单独处理
			int num = Integer.parseInt(pno.docnum.substring(2,6));
			String docnum = pno.docnum.substring(2);
			pno.exception = "KR:KR10->KR,AN";
			resultAN = getStd(pno.country, pno.docnum, null, pno.date ,2,true,soap);
			if(resultAN.getStdInfos() == null){
				resultAN = getStd(pno.country, docnum, null, pno.date ,2,true,soap);
			}
			
			resultPN = getStd(pno.country, pno.docnum, null, pno.date ,1,true,soap);
			if(resultPN.getStdInfos() == null){
				resultPN = getStd(pno.country, docnum, null, pno.date ,1,true,soap);
			}

			Files.append("_id \t date \t appnum1 \t pns2", new File("D:\\pnsdouble-KR.txt"), StandardCharsets.UTF_8);
			if(resultAN.getStdInfos() != null && resultPN.getStdInfos() != null ){
				StdInfo stdinfoAN = (resultAN.getStdInfos()).getStdInfo().get(0);
				StdInfo stdinfoPN = (resultPN.getStdInfos()).getStdInfo().get(0);
				String info = pno._id +"\t" + pno.date +"\t" + stdinfoAN.getSTDAPPNUM() +"\t" + stdinfoAN.getSTDPUBNUM();
				info +="\t" + stdinfoPN.getSTDAPPNUM() +"\t" + stdinfoPN.getSTDPUBNUM();
				Files.append(info, new File("D:\\pnsdouble-KR.txt"), StandardCharsets.UTF_8);
				Files.append("\r\n", new File("D:\\pnsdouble-KR.txt"), StandardCharsets.UTF_8);
			}
		}
	}
}
