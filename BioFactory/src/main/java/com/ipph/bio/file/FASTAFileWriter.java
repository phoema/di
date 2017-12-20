package com.ipph.bio.file;

import java.io.*;
import java.util.*;

/***
 * FASTA格式记录文件保存对象
 * @author lhp
 * FASTA文件格式
 * 每一条记录由‘〉’字符开始
 * 一条记录由信息+生物序列两个部分
 * 生物信息：保存在一行，不同字段由分隔串隔开，一般用|隔开
 * 生物序列：一行或多行，多行按80个字符换行
 */
public class FASTAFileWriter {
	private BufferedWriter _bwr = null;
	private FileOutputStream _fos = null;
	private OutputStreamWriter _osw = null;
	
//	/***
//	 * 是否保留生物序列的回车符号
//	 */
//	private boolean _keepReturn = false;
//	
//	/***
//	 * 第一行字段分割串
//	 * 格式样例：gi|3991100|gb|AAC84527.1|AR000950 Sequence 10 from patent US 5736377
//	 */
//	private String _sepstr = "| ";
//	
//	/***
//	 * 第一行每个字段的名字与值分割符
//	 * 一般字段名采用2字符或4字符固定长度代码
//	 * 格式样例：NRNL1:NRN_DJ170560 PN:WO2006024951 A2
//	 */
//	private String _sepNameVal = ":";
//	
//	public FASTAFileWriter()
//	{
//		
//	}
//	
//	/***
//	 * 功能：关闭FASTA格式文件写对象
//	 * @throws IOException
//	 */
//	public void close()
//	{
//		try {
//			if(_bwr != null)
//			{
//				_bwr.close();
//				_bwr = null;
//			}
//			if(_osw != null)
//			{
//				_osw.close();
//				_osw = null;
//			}
//			if(_fos != null)
//			{
//				_fos.close();
//				_fos = null;
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//	}
//	
//	/***
//	 * 功能打开FASTA文件写对象
//	 * @param sFilePath 文件路径
//	 * @param sEncoding 文本编码
//	 * @param keepReturn 是否保持生物序列的回车
//	 * @return 成功返回 true
//	 * @throws IOException
//	 */
//	public boolean open(String sFilePath, String sEncoding, boolean keepReturn)
//	{
//		try {
//			File f = new File(sFilePath);
//			if(!f.exists() && !f.isDirectory())
//			{
//				return false;
//			}
//			_keepReturn = keepReturn;
//			
//			_fos = new FileOutputStream(sFilePath);
//			
//			_osw = new OutputStreamWriter(_fos, sEncoding);
//			
//			_bwr = new BufferedWriter(_osw);
//			return true;
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		return false;
//	}
//	
//	/***
//	 * 功能：设置第一行字段分割串，设置字段名与字段值分割串
//	 * @param sepstr 字段分割串， null/空串，不修改
//	 * @param sepNameVal 字段名与字段值分割串 ，null/空串，不修改
//	 */
//	public void setSepStr(String sepstr, String sepNameVal)
//	{
//		if(sepstr!=null && !sepstr.isEmpty())
//		{
//			this._sepstr = sepstr;
//		}
//		if(sepNameVal!=null && !sepNameVal.isEmpty())
//		{
//			this._sepNameVal = sepNameVal;
//		}
//	}
//	
//	/***
//	 * 功能：保存一条FASTA格式记录
//	 * @param sFirstLine 第一行文本
//	 * @param sBioSeq 生物序列
//	 * @return 成功返回true
//	 */
//	public boolean write(String sFirstLine, String sBioSeq)
//	{
//		try {
//			//FASTA记录，以〉字符开头
//			_bwr.write(">");
//			_bwr.write(sFirstLine);
//			if(!sFirstLine.endsWith("\r\n"))
//			{
//				_bwr.write("\r\n");
//			}
//			
//			writeBioSeq(sBioSeq);
//			return true;
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		return false;
//	}
//	
//	/***
//	 * 功能：保存一条FASTA记录
//	 * @param lstField 字段值列表
//	 * @param sBioSeq 生物序列
//	 * @return 成功返回true
//	 */
//	public boolean write(List<String> lstField, String sBioSeq)
//	{
//		try {
//			_bwr.write(">");
//			
//			int count = lstField.size();
//			for(int i=0; i<count; i++)
//			{
//				if(i>0)
//				{
//					_bwr.write(_sepstr);
//				}
//				_bwr.write(lstField.get(i));
//			}
//			_bwr.write("\r\n");
//			
//			writeBioSeq(sBioSeq);
//			return true;
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		return false;
//	}
//	
//	/***
//	 * 功能：保存一条FASTA格式记录
//	 * @param lstName 字段名列表
//	 * @param lstValue 字段值列表
//	 * @param sBioSeq 生物序列
//	 * @return 成功返回true
//	 */
//	public boolean write(List<String> lstName, List<String> lstValue, String sBioSeq)
//	{
//		try {
//			if(lstName.size() != lstValue.size())
//			{
//				return false;
//			}
//			
//			_bwr.write(">");
//			
//			int count = lstName.size();
//			for(int i=0; i<count; i++)
//			{
//				if(i>0)
//				{
//					_bwr.write(_sepstr);
//				}
//				_bwr.write(lstName.get(i));
//				_bwr.write(_sepNameVal);
//				_bwr.write(lstValue.get(i));
//			}
//			_bwr.write("\r\n");
//			
//			writeBioSeq(sBioSeq);
//			return true;
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		return false;
//	}
//	
//	/***
//	 * 功能：保存生物序列，分行按80个字符切割
//	 * @param sBioSeq 生物序列
//	 * @throws Exception
//	 */
//	private void writeBioSeq(String sBioSeq) throws Exception
//	{
//		if( !_keepReturn )
//		{
//			//去除回车换行符
//			if(sBioSeq.indexOf("\r\n")>0)
//			{
//				sBioSeq = sBioSeq.replace("\r\n", "");
//			}
//			if(sBioSeq.indexOf('\r')>0)
//			{
//				sBioSeq = sBioSeq.replace("'\r'", "");
//			}
//			if(sBioSeq.indexOf('\n')>0)
//			{
//				sBioSeq = sBioSeq.replace("'\n'", "");
//			}
//			_bwr.write(sBioSeq);
//			_bwr.write("\r\n");
//		}
//		else 
//		{
//			int length = sBioSeq.length();
//			if(length < 80)
//			{
//				_bwr.write(sBioSeq);
//				_bwr.write("\r\n");
//			}
//			else 
//			{
//				//按80个字符分行存储
//				int start = 0, len = 0;
//				do {
//					len = length - start;
//					if(len > 80)
//					{
//						len = 80;
//					}
//					_bwr.write(sBioSeq.substring(start, len));
//					_bwr.write("\r\n");
//					start += len;
//				} while (start < length);
//			}
//		}
//	}
}
