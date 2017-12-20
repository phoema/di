package com.ipph.bio.model;

/**
 * 生物序列与生物专利映射表
 * 生物序列关联可以用MD5或SeqID
 */
public class MapSeqPat {
	/** 记录主键随机生成 **/
		
	/***
	 * 生物序列号
	 */	
	public String SeqID;
	
	/***
	 * 生物序列MD5
	 */
	public String MD5;
	
	/***
	 * 专利文献号
	 */
	public String DocID;
	
	/***
	 * 专利原始号
	 */
	public String PNO;
	
	/***
	 * 关联记录号
	 */
	public String AC;
	
	/***
	 * 记录入库时间
	 */
	public long TimeCreate = 0;
	
	/**
	 * 状态
	 */
	public int state = 0;
}
