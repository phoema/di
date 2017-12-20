package com.ipph.bio.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * 生物序列信息
 * 
 * @author jiahh 2015年5月7日
 * 
 */
@XStreamAlias("seq")
public class BioSequenceAC {

	/*** 非xml字段 start */
	/***
	 * 生物序列ID，主键 算法：MD5哈希，取中间8个字节换64位整数 算法：遇到重复号，加随机数，直到没有重复
	 * 说明：生物序列去除回车换行，小写转换后哈希
	 */
	@XStreamAsAttribute
	@XStreamAlias("id")
	public String _id = "";

	/***
	 * 记录状态
	 */
	@XStreamOmitField
	public int state = 0;

	/***
	 * 记录号 AC，多值
	 */
	@XStreamOmitField
	public String AC;
	/***
	 * 原始专利号
	 */
	@XStreamOmitField
	public String PNO;
	/***
	 * 标准专利号
	 */
	@XStreamOmitField
	public String PNS;
	/***
	 * 来源数据库，多值
	 */
	@XStreamOmitField
	public String Source;

	/***
	 * 数据资源类型，如PAT等，多值
	 */
	@XStreamOmitField
	public String DataType;

	/***
	 * 拓扑结构类型
	 */
	@XStreamOmitField
	public String TopType;
	/***
	 * 关键词 KW，多值
	 */
	@XStreamOmitField
	public String Keywords;

	/***
	 * 描述信息 DE
	 */
	@XStreamOmitField
	public String Desc;
	/***
	 * 生物序列MD5，小写
	 */
	@XStreamOmitField
	public String MD5 ;

	/***
	 * 生物序列
	 */
	@XStreamOmitField
	public String Seq ;

	/***
	 * 记录入库时间
	 */
	@XStreamOmitField
	public long TimeCreate = 0;

	/***
	 * 记录内容修改时间
	 */
	@XStreamOmitField
	public long LastModified = 0;

	/***
	 * 记录更新时间，增量更新控制时间戳
	 */
	@XStreamOmitField
	public long TimeUpdate = 0;

	/*** 非xml字段 end */

	/***
	 * 序列ID
	 */
	@XStreamOmitField
	public String SeqID;

	/***
	 * 序列长度 xml=length
	 */
	public int length;
	/***
	 * 序列类型 DNA,PRT(Protein) etc
	 */

	public String type;
	
	/***
	 * 序列类型 DNA,PRT(Protein) etc，原始类型
	 */
	@XStreamOmitField
	public String srcType;
	/***
	 * 基因名称
	 */

	public String gn;
	/***
	 * 亚细胞定位 细胞器 OG
	 */

	public String og;
	/***
	 * 来源生物体
	 */

	public String organism;

	public List<BioFeature> features = new ArrayList<BioFeature>();

	/***
	 * 备注
	 */
	public String comments;

	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((AC == null) ? 0 : AC.hashCode());
		result = prime * result + ((DataType == null) ? 0 : DataType.hashCode());
		result = prime * result + ((Desc == null) ? 0 : Desc.hashCode());
		result = prime * result + ((Keywords == null) ? 0 : Keywords.hashCode());
//		result = prime * result + (int) (LastModified ^ (LastModified >>> 32));
		result = prime * result + ((MD5 == null) ? 0 : MD5.hashCode());
		result = prime * result + ((PNO == null) ? 0 : PNO.hashCode());
		result = prime * result + ((PNS == null) ? 0 : PNS.hashCode());
		result = prime * result + ((Seq == null) ? 0 : Seq.hashCode());
		result = prime * result + ((SeqID == null) ? 0 : SeqID.hashCode());
		result = prime * result + ((Source == null) ? 0 : Source.hashCode());
//		result = prime * result + (int) (TimeCreate ^ (TimeCreate >>> 32));
//		result = prime * result + (int) (TimeUpdate ^ (TimeUpdate >>> 32));
		result = prime * result + ((TopType == null) ? 0 : TopType.hashCode());
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		result = prime * result + ((features == null) ? 0 : features.hashCode());
		result = prime * result + ((gn == null) ? 0 : gn.hashCode());
		result = prime * result + length;
		result = prime * result + ((og == null) ? 0 : og.hashCode());
		result = prime * result + ((organism == null) ? 0 : organism.hashCode());
		result = prime * result + ((srcType == null) ? 0 : srcType.hashCode());
//		result = prime * result + state;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BioSequenceAC other = (BioSequenceAC) obj;
		if (AC == null) {
			if (other.AC != null)
				return false;
		} else if (!AC.equals(other.AC))
			return false;
		if (DataType == null) {
			if (other.DataType != null)
				return false;
		} else if (!DataType.equals(other.DataType))
			return false;
		if (Desc == null) {
			if (other.Desc != null)
				return false;
		} else if (!Desc.equals(other.Desc))
			return false;
		if (Keywords == null) {
			if (other.Keywords != null)
				return false;
		} else if (!Keywords.equals(other.Keywords))
			return false;
//		if (LastModified != other.LastModified)
//			return false;
		if (MD5 == null) {
			if (other.MD5 != null)
				return false;
		} else if (!MD5.equals(other.MD5))
			return false;
		if (PNO == null) {
			if (other.PNO != null)
				return false;
		} else if (!PNO.equals(other.PNO))
			return false;
		if (PNS == null) {
			if (other.PNS != null)
				return false;
		} else if (!PNS.equals(other.PNS))
			return false;
		if (Seq == null) {
			if (other.Seq != null)
				return false;
		} else if (!Seq.equals(other.Seq))
			return false;
		if (SeqID == null) {
			if (other.SeqID != null)
				return false;
		} else if (!SeqID.equals(other.SeqID))
			return false;
		if (Source == null) {
			if (other.Source != null)
				return false;
		} else if (!Source.equals(other.Source))
			return false;
//		if (TimeCreate != other.TimeCreate)
//			return false;
//		if (TimeUpdate != other.TimeUpdate)
//			return false;
		if (TopType == null) {
			if (other.TopType != null)
				return false;
		} else if (!TopType.equals(other.TopType))
			return false;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.equals(other._id))
			return false;
		if (features == null) {
			if (other.features != null)
				return false;
		} else if (!features.equals(other.features))
			return false;
		if (gn == null) {
			if (other.gn != null)
				return false;
		} else if (!gn.equals(other.gn))
			return false;
		if (length != other.length)
			return false;
		if (og == null) {
			if (other.og != null)
				return false;
		} else if (!og.equals(other.og))
			return false;
		if (organism == null) {
			if (other.organism != null)
				return false;
		} else if (!organism.equals(other.organism))
			return false;
		if (srcType == null) {
			if (other.srcType != null)
				return false;
		} else if (!srcType.equals(other.srcType))
			return false;
//		if (state != other.state)
//			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	
}
