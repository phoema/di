package com.ipph.bio.model;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * 生物序列专利信息
 * 
 * @author jiahh 2015年5月7日
 * 
 */
@XStreamAlias("doclist")
public class BioPatent {
	/*** 非xml字段 start */
	@XStreamOmitField
	public String _id = null;

	/***
	 * 专利文献号，调用接口生成 PK
	 */
	@XStreamOmitField
	public String DocID = "";

	/***
	 * 专利号，原始数据
	 */
	@XStreamOmitField
	public String PatID;
	/***
	 * 记录状态
	 */
	@XStreamOmitField
	public int state = 0;

	/***
	 * 生物序列号,多值
	 */
	@XStreamOmitField
	public Hashtable<String, String> SeqList = new Hashtable<String, String>();
	/***
	 * 序列数量
	 */
	@XStreamOmitField
	public int count = 0;
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
	 * 记录生成交换文件的时间 yyyyMMdd
	 */
	@XStreamOmitField
	public long TimeExchange = 0;

	/*** 非xml字段 end */

	/***
	 * 
	 */
	@XStreamAsAttribute
	public String topic;
	/***
	 * 国别，符合国别标准代码
	 */
	@XStreamAsAttribute
	public String country;
	/***
	 * 标准格式的案卷编号 对于专利著录项目数据包，“国别+案卷编号+文献类型”三字段关联是标准格式的文献号；
	 */
	@XStreamAsAttribute
	public String docNumber;
	/***
	 * 文献类型
	 */
	@XStreamAsAttribute
	public String kind;
	/***
	 * 原始格式的文献号
	 */
	@XStreamAsAttribute
	public String PNO;
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String PNS;
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String datePublication;
	/***
	 * 生物序列文件的格式，FASTA：FASTA生物序列文件规范
	 */
	@XStreamAsAttribute
	public String Format;
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String path;
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String status;
	/***
	 * 
	 */

	public ExchangePatentFileAttr file;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((DocID == null) ? 0 : DocID.hashCode());
		result = prime * result + ((Format == null) ? 0 : Format.hashCode());
		result = prime * result + ((PNO == null) ? 0 : PNO.hashCode());
		result = prime * result + ((PNS == null) ? 0 : PNS.hashCode());
		result = prime * result + ((PatID == null) ? 0 : PatID.hashCode());
		result = prime * result + ((SeqList == null) ? 0 : SeqList.hashCode());
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		result = prime * result + count;
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((datePublication == null) ? 0 : datePublication.hashCode());
		result = prime * result + ((docNumber == null) ? 0 : docNumber.hashCode());
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((topic == null) ? 0 : topic.hashCode());
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
		BioPatent other = (BioPatent) obj;
		if (DocID == null) {
			if (other.DocID != null)
				return false;
		} else if (!DocID.equals(other.DocID))
			return false;
		if (Format == null) {
			if (other.Format != null)
				return false;
		} else if (!Format.equals(other.Format))
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
		if (PatID == null) {
			if (other.PatID != null)
				return false;
		} else if (!PatID.equals(other.PatID))
			return false;
		if (SeqList == null) {
			if (other.SeqList != null)
				return false;
		} else if (!SeqList.equals(other.SeqList))
			return false;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.equals(other._id))
			return false;
		if (count != other.count)
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (datePublication == null) {
			if (other.datePublication != null)
				return false;
		} else if (!datePublication.equals(other.datePublication))
			return false;
		if (docNumber == null) {
			if (other.docNumber != null)
				return false;
		} else if (!docNumber.equals(other.docNumber))
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		if (kind == null) {
			if (other.kind != null)
				return false;
		} else if (!kind.equals(other.kind))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (topic == null) {
			if (other.topic != null)
				return false;
		} else if (!topic.equals(other.topic))
			return false;
		return true;
	}

}
