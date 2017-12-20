package com.ipph.bio.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;


/**
 * 生物序列索引文件
 * @author jiahh 2015年5月7日
 *
 */
@XStreamAlias("content") 
public class ExchangeIndex {
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String file;
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String dateExchange;
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String dateProduced;
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String patcnt;
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String filecnt;
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String size;
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String md5;
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String status;

	 @XStreamImplicit
	 public List<BioPatent> doclist;
}
