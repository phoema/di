package com.izhiliao.util;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value=Include.NON_EMPTY)
public class TRSResult {

	/**
	 * 总记录数
	 */
	public long total;
	/**
	 * 数据列表
	 */
	public List<HashMap> records;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public List<HashMap> getRecords() {
		return records;
	}
	public void setRecords(List<HashMap> records) {
		this.records = records;
	}

}
