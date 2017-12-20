package com.ipph.bio.model;

import java.util.Hashtable;

//import javax.persistence.Entity;

/***
 * FASTA文件拆分记录
 * @author lhp
 *
 */
//@Entity
public class RecordFasta {
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
	 * 格式2第二个ID号
	 */
	public String ID2;
	
	/***
	 * 原始专利号
	 */
	public String PNO;
	
	/***
	 * 生物序列ID
	 */
	public String SeqID;
	
	/***
	 * 生物序列,小写，MD5
	 */
	public String MD5;
	
	/***
	 * 记录第一行
	 */
	public String Header;
	
	/***
	* 生物序列，除去回车换行空格等非显示字符，大写
	*/
	public String Seq;
	
	/***
	 * 数据内容crc32校验
	 * 用于比较记录是否变化
	 * 原始记录不经过任何处理直接计算
	 */
	public long CRC = 0;
	
	/***
	 * 数据来源库，用于解析器匹配
	 */
	public String Source;
	
	/***
	 * 数据格式，用于解析器匹配
	 */
	public String Format;
	
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
	 * 记录曾经有关系被删除的Record
	 */
	public Hashtable<Long,RecordFasta> OldRecord;

}
