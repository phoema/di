package com.ipph.bio.model;

/**
 * 生物序列信息
 * 
 * @author linl 2015年6月4日
 * 
 */
public class BioSequence {

	public String _id = "";

	/***
	 * 生物序列MD5
	 */
	public String MD5 = "";

	/***
	 * 生物序列
	 */
	public String Seq = "";

	/***
	 * 记录入库时间
	 */
	public long TimeCreate = 0;

	/***
	 * 记录内容修改时间
	 */
	public long LastModified = 0;

	/***
	 * 记录更新时间，增量更新控制时间戳
	 */
	public long TimeUpdate = 0;

	/*** 非xml字段 end */

	/***
	 * 序列长度 xml=length
	 */
	public int length;

}
