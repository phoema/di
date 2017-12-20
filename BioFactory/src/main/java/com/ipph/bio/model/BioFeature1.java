package com.ipph.bio.model;

//import javax.persistence.Entity;

/***
 * 生物序列特征，1个生物序列有多个特征
 * @author lhp
 *
 */
//@Entity
public class BioFeature1 {
	public BioFeature1()
	{
	}
	
	public BioFeature1(String loc, String words, String desc)
	{
		this.Location = loc;
		this.Keywords = words;
		this.Desc = desc;
	}
	
	/***
	 * 特征序列ID，生成算法与生物序列ID一致
	 */
	public long FeatID = 0;
	
	/***
	 * 记录状态
	 */
	public int state = 0;
	
	/***
	 * 生物序列ID
	 */
	public long SeqID = 0;
	
	/***
	 * 位置信息,如(3)...(224)
	 */
	public String Location = "";
	
	/***
	 * 位置信息的开始与结束
	 * 从location中提取
	 */
	public int beg_pos = -1;
	public int end_pos = -1;
	
	/***
	 * 来源生物体，多值
	 */
	public String Organism = "";
	
	/***
	 * 特征关键词，多值
	 */
	public String Keywords = "";
	
	/***
	 * 描述信息
	 */
	public String Desc = "";
	
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
