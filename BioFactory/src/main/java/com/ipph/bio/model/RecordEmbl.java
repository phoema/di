package com.ipph.bio.model;

import java.util.Hashtable;

/***
 * EMBL文件记录
 * @author lhp
 *
 */
//@Entity
public class RecordEmbl {
	/***
	 * 记录ID，RecID
	 */
	public String _id = "";
	
	/***
	 * 记录操作状态
	 */
	public int state = 0;
	
	/***
	 * 记录入库状态
	 */
	public int status = 0;
	
	/***
	 * 原始记录ID号
	 */
	public String RecID;
	/***
	 * 原始记录ID号
	 */
	public String AC;
		
	/***
	 * 专利号
	 */
	public String PNO;
	
	/***
	 * 专利号对应日期
	 */
	public String PNODT;
	
	/***
	 * 数据格式，用于解析器匹配
	 */
	public String Format ;
	
	/***
	 * 数据内容
	 */
	public String Content ;
	
	/***
	 * 数据内容crc32校验
	 * 用于比较记录是否变化
	 * 原始记录不经过任何处理直接计算
	 */
	public long CRC = 0;
	
	/***
	* 文件路径，相对路径，避免绝对路径造成哈希不一致
	*/
	public String FilePath;
	
	/***
	 * 记录入库时间
	 */
	public long TimeCreate = 0;
	
	/***
	 * 记录内容修改时间
	 */
	public long LastModified = 0;
	
	/***
	 * 记录更新时间，增量更新时间戳
	 */
	public long TimeUpdate = 0;
	
	/***
	 * 记录更新时间，增量更新时间戳
	 */
	public Hashtable<Long,RecordEmbl> OldRecord;
}
