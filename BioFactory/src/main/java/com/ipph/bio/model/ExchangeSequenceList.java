package com.ipph.bio.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;


/**
 * 生物序列列表信息
 * @author jiahh 2015年5月7日
 *
 */
@XStreamAlias("seqlist") 
public class ExchangeSequenceList {
	/***
	 * 序列数量
	 */
	@XStreamAsAttribute
	public String amount;
	/***
	 * 序列列表
	 */
	 @XStreamImplicit
	public List<BioSequenceAC> seqlist;


}
