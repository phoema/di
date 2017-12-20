package com.ipph.bio.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * 生物序列控制文件内索引文件对象
 * @author jiahh 2015年5月7日
 *
 */
@XStreamAlias("file") 
public class ExchangeControlFile {
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String filename;
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String patcnt;
	
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String status;
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String section;
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String sequence;
	/***
	 * 
	 */
	@XStreamAsAttribute
	public String md5;

}
