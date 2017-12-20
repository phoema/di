package com.ipph.bio.model;

import org.tempuri.StdInfo;

/**
 * 原始专利号标准专利号对应关系表
 */
public class ExchangePno {
	/***
	 * 原始专利号
	 */	
	public String _id;
	
	/***
	 * 国别
	 */	
	public String country;
	
	/***
	 * 编号
	 */	
	public String docnum;
	
	/***
	 * 类型
	 */	
	public String kind;
	/***
	 * EMBL记录里专利号对应的日期
	 */	
	public String date;
	
	/***
	 * 标准专利号对象
	 */
	public StdInfo StdInfo;
	/***
	 * 标准专利号对象备份
	 */
	public StdInfo StdInfo2;
	/***
	 * 标准专利号
	 */
	public String PNS;
	/***
	 * 异常信息
	 */
	public String exception;
	/***
	 * 数据来源 F:FASTA E:EMBL
	 */
	public String source;
	/***
	 * 数据状态，
	 */
	public int state;
	/***
	 * 记录入库状态， 0 新增 1 embl或者fasta有变化
	 */
	public int status;
	/***
	 * 记录入库时间
	 */
	public long TimeCreate = 0;
	/***
	 * 记录更新时间，增量更新时间戳
	 */
	public long TimeUpdate = 0;
	

}
