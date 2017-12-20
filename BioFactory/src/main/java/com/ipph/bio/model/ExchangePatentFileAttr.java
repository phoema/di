package com.ipph.bio.model;

import java.io.File;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;


/**
 * 生物序列专利文件描述
 * @author jiahh 2015年5月7日
 *
 */
@XStreamAlias("file") 
public class ExchangePatentFileAttr {
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String filename;
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String filetype;
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String section;


	public ExchangeSequenceList seqlist;

}
