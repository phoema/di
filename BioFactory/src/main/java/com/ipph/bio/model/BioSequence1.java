package com.ipph.bio.model;

import java.util.*;
//import javax.persistence.Entity;

/***
 * 生物序列 xml=seq
 * @author lhp
 *
 */
//@Entity
public class BioSequence1 {
	/***
	 * 生物序列ID，主键
	 * 算法：MD5哈希，取中间8个字节换64位整数
	 * 算法：遇到重复号，加随机数，直到没有重复
	 * 说明：生物序列去除回车换行，小写转换后哈希
	 */
	public long SeqID = 0;
	
	/***
	 * 记录状态
	 */
	public int state = 0;
	
	/***
	 * 身份号 ID，多值
	 */
	public String ID = "";
	
	/***
	 * 记录号 AC，多值
	 */
	public String AC = "";
	
	/***
	 * 基因名称 GN，xml=gn
	 * 可能存在特殊字符
	 */
	public String GN = "";
	
	/***
	 * 类型 OC,xml=type
	 * DNA,PRT(Protein) etc
	 */
	public String Type = "";
	
	/***
	 * 细胞器 OG xml=og
	 */
	public String OG = "";
	
	/***
	 * 物种OS， xml=organism 
	 */
	public String Organism = "";
	
	/***
	 * 来源数据库，多值
	 */
	public String Source = "";
	
	/***
	 * 数据资源类型，如PAT等，多值
	 */
	public String DataType = "";
	
	/***
	 * 拓扑结构类型
	 */
	public String TopType = "";	
	
	/***
	 * 关键词 KW，多值
	 */
	public String Keywords = "";
	
	/***
	 * 描述信息 DE
	 */
	public String Desc = "";
	
	/***
	 * 序列长度 xml=length
	 */
	public int Length = 0;
	
	/***
	 * 生物序列MD5，小写
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
}
