package com.di.util;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Series 维度集合
 * @author jiahh 2015年10月16日
 *
 */
@XStreamAlias("DEM") 
public class DEM {
	
	@XStreamImplicit(itemFieldName="CL_cluster")
	public List<CL_cluster> CL_clusters;
	
	@XStreamAlias("CL_cluster") 
	public class CL_cluster {

		@XStreamAsAttribute
		public int cnum;
		/*** 非xml字段 聚类名称 table.iteretor  */
		@XStreamOmitField
		public String name;
		@XStreamAsAttribute
		public float cx;
		@XStreamAsAttribute
		public float cy;
		@XStreamAsAttribute
		public float cz;
		@XStreamAlias("CL_vector_table") 
		public List<CL_vword> CL_vector_table;
		@XStreamAlias("CL_patent_table") 
		public List<CL_patent> CL_patent_table;
		/*** 非xml字段 聚类名称 patent_table.xyz  */
		@XStreamOmitField
		public float[][] data;

	}
	@XStreamAlias("CL_vword") 
	public class CL_vword {

		@XStreamAsAttribute
		public String name;
		@XStreamAsAttribute
		public float weight;
		@XStreamAsAttribute
		public String group;
	}
	@XStreamAlias("CL_patent") 
	public class CL_patent {

		@XStreamAsAttribute
		public String num;
		@XStreamAsAttribute
		public String basename;
		@XStreamAsAttribute
		public float similarity;
		@XStreamAsAttribute
		public int cx;
		@XStreamAsAttribute
		public int cy;
		@XStreamAsAttribute
		public int cz;
		/*** 非xml字段 聚类名称 table.PNS;PID;ANS;TIO;IPC;APO;INO;LSBCN  */
		@XStreamOmitField
		public String tio;
		@XStreamOmitField
		public String ano;
		@XStreamOmitField
		public String pno;
		@XStreamOmitField
		public String ipc;
		@XStreamOmitField
		public String apo;
		@XStreamOmitField
		public String ino;
		@XStreamOmitField
		public String lsscn;

	}

}
