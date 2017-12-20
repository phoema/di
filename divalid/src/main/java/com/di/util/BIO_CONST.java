package com.di.util;

public class BIO_CONST {

	// 回车换行
	public final static String CHAR_ENTER = "\r\n";

	// FASTA的格式定义
	public final static String FASTA_FORMAT01 = "FASTA01";
	public final static String FASTA_FORMAT02 = "FASTA02";
	
	// EMBL的格式定义
	public final static String EMBL_FORMAT01 = "EMBL01";
	public final static String EMBL_FORMAT02 = "EMBL02";
	// 两个源数据库集合的定义
	public final static String SOURCE_FASTA = "sourceFASTA";
	public final static String SOURCE_EMBL = "sourceEMBL";

	// 两个源数据库集合的定义
	public final static String ROOT_FASTA_FORMAT01 = "D:\\MyWork\\didoc\\生物序列\\生物序列样例数据-0506\\FASTA";
	public final static String ROOT_EMBL_FORMAT01 = "sourceEMBL";

	//4个中间库集合的定义
	
	public final static String BIO_SEQUENCE = "BioSequence";
	public final static String BIO_SEQUENCE_AC = "BioSequenceAC";
	public final static String BIO_PATENT = "BioPatent";
//	public final static String BIO_SEQUENCE = "BioSequence";
//	public final static String BIO_SEQUENCE_AC = "BioSequenceAC";
//	public final static String BIO_FEATURE = "BioFeature";
//	public final static String BIO_PATENT = "BioPatent";
	public final static String MAP_SEQ_PAT = "MapSeqPat";


	// 原始专利号标准专利号对应关系表
	public final static String EXCHANGE_PNO = "ExchangePNO";
	
	public final static int STATE_0 = 0; // 新增或更新
	public final static int STATE_1 = 1; // 导出成功
	//public final static int STATE_2 = 2; // 缺专利号
	public final static int STATE_3 = 3; //embl记录超过300000的所有embl记录
	public final static int STATE_4 = 4; // embl没有fasta记录
	//public final static int STATE_5 = 5; // 处理异常
	//public final static int STATE_6 = 6; // embl跟fasta记录对应不上，有一方缺失
	//public final static int STATE_7 = 7; // 记录重复
	//public final static int STATE_8 = 8; // embl多值
	//public final static int STATE_9 = 9; // PN缺失
	//public final static int STATE_10 = 10; // fasta跟embl中PN不匹配
	public final static int STATE_11 = 11; // embl无法解析出type
	public final static int STATE_12 = 12; // embl没有fasta记录，但是切出了序列内容

	
	public final static int PNO_STATE_1 = 1; // 导出完成
	public final static int PNO_STATE_2 = 2; // 此专利不存在embl记录，处理下一个专利
	public final static int PNO_STATE_3 = 3; // embl记录超过10000，处理下一个专利
	public final static int PNO_STATE_4 = 4; // 此专利的所有embl记录都没有匹配上fasta记录 处理下一个专利
	public final static int PNO_STATE_5 = 5; // 导出到BioPatent失败
	public final static int PNO_STATE_6 = 6; // 此专利的存在embl记录都没有匹配上fasta记录情况 处理下一个专利
	
	// fasta embl exhcnagepno原始库的入库状态
	public final static int STATUS_0 = 0;//新增
	public final static int STATUS_1 = 1;//更新
	// fasta embl原始库的入库状态
	public final static int STATUS_2 = 2;//整理Pno入库，Source库专用

	//bioPatent.state 状态定义
	public final static int PATENT_STATE_0 = 0; // 新增
	public final static int PATENT_STATE_1 = 1; // 更新
	public final static int PATENT_STATE_9 = 9; // 文件导出成功

	//生成xml文件的文件头
	public final static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	// 数据主题
	public final static String TOPIC = "fOREIGN_SEQ";
	// 数据控制文件定义:数据资源类型 BIOLOGY：生物序列数据
	public final static String EXCHANGE_CONTROLTYPE = "BIOLOGY";

}
