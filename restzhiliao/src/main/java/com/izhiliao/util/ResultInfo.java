package com.izhiliao.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value=Include.NON_EMPTY)
public class ResultInfo {

	/**
	 * 返回值 0成功 -1失败
	 */
	public int ReturnValue;
	/**
	 * 错误信息
	 */
	public String ErrorInfo;
	/**
	 * 返回结果集
	 */
	public Object Option;
	/**
	 * 返回结果集1
	 */
	public Object Option1;

}
