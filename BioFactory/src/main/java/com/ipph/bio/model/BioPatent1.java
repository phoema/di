package com.ipph.bio.model;

//import javax.persistence.Entity;

/***
 * 生物序列专利
 * @author lhp
 *
 */
//@Entity
public class BioPatent1 {
	/***
	 * 专利文献号，调用接口生成 PK 
	 */
	public String DocID = "";
	
	/***
	 * 记录状态
	 */
	public int state = 0;
	
	/***
	 * 专利号，原始数据
	 */
	public String PatID = "";
	
	/***
	 * 生物序列号,多值
	 */
	public String SeqList = "";
	
	/***
	 * 申请日期
	 */
	//public int ApplDate = 0;
			
	/***
	 * 国家，多值，双分号隔开
	 */
	public String Country = "";
	
	/***
	 * 主管局 如SIPO，WIPO,USPTO等
	 */
	//public String Vendor = "";
	
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
}
