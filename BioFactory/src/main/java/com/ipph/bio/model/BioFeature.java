package com.ipph.bio.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * 生物序列feature文件
 * 
 * @author jiahh 2015年5月7日 说明：一条记录由一组FH与FT构成，多个FH与多个FT，直到XX字段或其他字段出现为一条记录
 *         控制：数据更新时，用AC与SeqID查询所有Feature记录集，取出结果记录的_id、CRC字段
 *         控制：文本内容CRC相同的，标记为有效记录，内容CRC不同的，形成新记录、标记为新增状态
 *         控制：处理过程中，需要增加的或是没有变化的记录，对每条记录记录有效标记 控制:
 *         执行记录插入操作，没有打上处理标记的，使用_id字段删除原库记录
 */
@XStreamAlias("feature")
public class BioFeature {
	/***
	 * 记录号，SeqID__Location__AC_CRC,全小写，md5小写
	 */
	@XStreamOmitField
	public String _id;

	/***
	 * 记录状态
	 */
	@XStreamOmitField
	public int state = 0;
	/***
	 * 记录状态
	 */
	@XStreamOmitField
	public Boolean tag = null;

	/*** 非xml字段 start */

	/***
	 * 特征序列ID，生成算法与生物序列ID一致 根据location信息，取出生物序列片段，再计算
	 */
	@XStreamOmitField
	public long FeatID = 0;

	/***
	 * 生物序列ID
	 */
	@XStreamOmitField
	public String SeqID = "";
	/***
	 * 位置信息的开始与结束 从location中提取
	 */
	@XStreamOmitField
	public int beg_pos = -1;
	@XStreamOmitField
	public int end_pos = -1;
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
	 * 记录更新时间，增量更新时间戳
	 */
	@XStreamOmitField
	public long TimeUpdate = 0;

	/***
	 * mol_type,可能多值 样例mol_type="genomic DNA" 取等号后，双引号内文本，冒号替换成下划线
	 */
	@XStreamOmitField
	public String mol_type;

	/***
	 * db_xref,可能多值 样例db_xref="taxon:32644" 取等号后，双引号内文本，冒号替换成下划线
	 */
	@XStreamOmitField
	public String db_xref;

	/***
	 * 序列AC号
	 */
	@XStreamOmitField
	public String AC = "";

	/***
	 * 数据内容crc32校验 用于比较记录是否变化 原始记录不经过任何处理直接计算
	 */
	@XStreamOmitField
	public long crc = 0;

	/***
	 * EMBL记录FT字段原始文本, other字段使用
	 */
	@XStreamOmitField
	public String content;

	/*** 非xml字段 end */

	/***
	 * 特征序号
	 */
	@XStreamAsAttribute
	public String sequence;

	/***
	 * 序列起始、终止位置
	 */
	public String location;

	/***
	 * 名称或关键词
	 */
	public String keywords;
	/***
	 * 其他信息
	 */
	public String other;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((AC == null) ? 0 : AC.hashCode());
		result = prime * result + (int) (FeatID ^ (FeatID >>> 32));
		result = prime * result + (int) (LastModified ^ (LastModified >>> 32));
		result = prime * result + ((SeqID == null) ? 0 : SeqID.hashCode());
		result = prime * result + (int) (TimeCreate ^ (TimeCreate >>> 32));
		result = prime * result + (int) (TimeUpdate ^ (TimeUpdate >>> 32));
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		result = prime * result + beg_pos;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + (int) (crc ^ (crc >>> 32));
		result = prime * result + ((db_xref == null) ? 0 : db_xref.hashCode());
		result = prime * result + end_pos;
		result = prime * result + ((keywords == null) ? 0 : keywords.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((mol_type == null) ? 0 : mol_type.hashCode());
		result = prime * result + ((other == null) ? 0 : other.hashCode());
		result = prime * result + ((sequence == null) ? 0 : sequence.hashCode());
		result = prime * result + state;
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
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
		BioFeature other = (BioFeature) obj;
		if (AC == null) {
			if (other.AC != null)
				return false;
		} else if (!AC.equals(other.AC))
			return false;
		if (FeatID != other.FeatID)
			return false;
//		if (LastModified != other.LastModified)
//			return false;
		if (SeqID == null) {
			if (other.SeqID != null)
				return false;
		} else if (!SeqID.equals(other.SeqID))
			return false;
//		if (TimeCreate != other.TimeCreate)
//			return false;
//		if (TimeUpdate != other.TimeUpdate)
//			return false;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.equals(other._id))
			return false;
		if (beg_pos != other.beg_pos)
			return false;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (crc != other.crc)
			return false;
		if (db_xref == null) {
			if (other.db_xref != null)
				return false;
		} else if (!db_xref.equals(other.db_xref))
			return false;
		if (end_pos != other.end_pos)
			return false;
		if (keywords == null) {
			if (other.keywords != null)
				return false;
		} else if (!keywords.equals(other.keywords))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (mol_type == null) {
			if (other.mol_type != null)
				return false;
		} else if (!mol_type.equals(other.mol_type))
			return false;
		if (this.other == null) {
			if (other.other != null)
				return false;
		} else if (!this.other.equals(other.other))
			return false;
		if (sequence == null) {
			if (other.sequence != null)
				return false;
		} else if (!sequence.equals(other.sequence))
			return false;
//		if (state != other.state)
//			return false;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		return true;
	}

}
