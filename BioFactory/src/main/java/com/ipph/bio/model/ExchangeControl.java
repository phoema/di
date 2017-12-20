package com.ipph.bio.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;


/**
 * 生物序列控制文件
 * @author jiahh 2015年5月7日
 *
 */
@XStreamAlias("control") 
public class ExchangeControl {
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String type;
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String dowork;

	public List<ExchangeControlFile> filelist;
}
