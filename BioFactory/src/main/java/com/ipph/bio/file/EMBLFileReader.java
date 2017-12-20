package com.ipph.bio.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.hp.util.MD5FileUtil;
import com.ipph.bio.model.RecordEmbl;
import com.ipph.bio.util.BIO_CONST;

/***
 * 功能：EBI生物序列文件EMBL格式读取器
 * @author lhp
 * EMBL文件格式
 * 文件格式：一条记录由多行组成，每条记录有多个字段，每条记录由ID字段开始
 * 数  据  行：字段名+四个空格+字段内容，同一个字段可能出现多次
 * 字段格式：如ID   NRN_DJ170560; DNA; NR1; 5 SQ
 * 不同数据节用XX\r\n隔开
 * 使用说明：ID字段的代码，在第一个分号前，需要在FASTA文件中找生物序列
 * 使用说明：先把FASTA文件入库到MongoDB
 */
@Slf4j
public class EMBLFileReader {
	
	private BufferedReader _br = null;
	private FileInputStream _fis = null;
	private InputStreamReader _isr = null;

	public String _filename = null;
	// 设定FASTA文件读取的格式
	private String format = BIO_CONST.EMBL_FORMAT01;
	private long datetime = Long.parseLong(new SimpleDateFormat("yyyyMMdd")
	.format(new Date()));	
	// Patent number ([A-Z]+?)(\d+?)-?([A-Z]+\d?)/ Patent number EP0139076-A2/10, 02-MAY-1985.
	// Patent.*\s?([A-Z]+?)\s?(\d+?)-?([A-Z]+\d?)/? 兼容 Patent: EP 0135277-A1 14 27-MAR-1985;
	// .*? 非贪婪模式
	private static Pattern _regPno = Pattern.compile("Patent.*?\\s?([A-Z]+?)\\s?(\\d+?)-?([A-Z]+\\d?)/?");
	private static Pattern _regPnoNoKind = Pattern.compile("Patent.*?\\s?([A-Z]+?)\\s?(\\d+)");
	// 专利号对应日期
	private static Pattern _regPnoDT = Pattern.compile("(\\d\\d)-(\\w\\w\\w)-(\\d\\d\\d\\d)");
	/***
	 * 正则表达式，拆分EMBL行的字段名与字段值
	 */
	private Pattern _reg = null;
	
	/***
	 * 上一次读取的下一条记录第一行 以ID开头
	 */
	private String _lastLine = null;
    CRC32 crc = new CRC32();
	
	public EMBLFileReader()
	{
	}
	public EMBLFileReader(String format)
	{
		this.format = format;
		
	}
	public static Hashtable<String, String> month = new Hashtable<String, String>();
	static {
		month.put("JAN", "01");
		month.put("FEB", "02");
		month.put("MAR", "03");
    	month.put("APR", "04");
    	month.put("MAY", "05");
    	month.put("JUN", "06");
    	month.put("JUL", "07");
    	month.put("AUG", "08");
    	month.put("SEP", "09");
    	month.put("OCT", "10");
    	month.put("NOV", "11");
    	month.put("DEC", "12");
	}
	
	/***
	 * 功能：关闭文件EMBL对象
	 */
	public void close()
	{
		try {
			if(_br != null)
			{
				_br.close();
				_br = null;
			}
			if(_isr != null)
			{
				_isr.close();
				_isr = null;
			}
			if(_fis != null)
			{
				_fis.close();
				_fis = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		_lastLine = null;
		_reg = null;
	}
	
	/***
	 * 打开EMBL文件对象
	 * @param sFilePath 文件路径
	 * @param sEncoding 文本编码
	 * @return 成功返回 true
	 */
	public boolean open(String sFilePath, String sEncoding)
	{
		try {
			File f = new File(sFilePath);
			if(!f.exists() && !f.isDirectory())
			{
				return false;
			}
			this._filename = f.getName();

			_fis = new FileInputStream(sFilePath);
			
			_isr = new InputStreamReader(_fis, sEncoding);
			
			_br = new BufferedReader(_isr);
			// 以两位字符开头，间隔3个空格，以分号或者逗点截止
			_reg = Pattern.compile("^([A-Z]{2})\\s{3}(.*?)[;.\\s]");
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	/***
	 * 读取一条记录的字符串，格式保持原样
	 * @return 记录字符串
	 */
	public String readOneStr()
	{
		try {
			String line = null;
			if( _lastLine==null)
			{
				line = _br.readLine();
				if (line == null)
				{
					//文件结尾
					return null;
				}
			}
			else 
			{	
				//上次读取了ID开头的串，表示一条记录开始
				line = _lastLine;
				_lastLine = null;
			}
			
			int count = 0;
			StringBuilder sb = new StringBuilder(64*1024);
			do{
				sb.append(line + BIO_CONST.CHAR_ENTER);
				Matcher mr = _reg.matcher(line);
				if(mr.find())
				{									
					if(mr.group(1).compareTo("ID")==0 && count > 0)
					{

					}else if(mr.group(1).compareTo("XX")==0){
						//记录不同部分分隔符XX
						_lastLine = line;
					}
					
					count++;
				}else{
					if(line.compareTo("//")==0){
						//标识另外一条记录开始
						return sb.toString();
					}
				}
				line = _br.readLine();
			} while (line != null);
			return sb.toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public RecordEmbl readOneObj() throws Exception {
		RecordEmbl record = null;
		if (BIO_CONST.EMBL_FORMAT01.equals(this.format)) {
			record = this.readOneObjFormat01();
		} else if (BIO_CONST.EMBL_FORMAT02.equals(this.format)) {
			record = this.readOneObjFormat01();
		} else {
			;
		}
		if (record != null) {
			if(record.RecID == null){
				log.error("没有解析出record.RecID：" + record.FilePath + record.Content);
			}
			// 解析专利号和专利日期
			this.regPno(record);
			crc.reset();
			crc.update(record.Content.getBytes());
			// 计算内容的CRC32
			record.CRC = crc.getValue();
			// TODO 无此字段
			//record.Source = "Source";
			record.FilePath = this._filename;
			record.TimeCreate = this.datetime;

			record._id = record.RecID;

		}
		return record;

	}

	// RL PNO
	private static Pattern _regRLPno = Pattern.compile("RL   .*Patent.*?\\s?([A-Z][A-Z]+?)\\s?\\(?(\\d\\d\\d+)\\)?-?([A-Z]?)(\\d?)/?.*",Pattern.CASE_INSENSITIVE);
	private static Pattern _regCCPno = Pattern.compile("CC   PN\\s+([A-Z][A-Z]+?)\\s?(\\d\\d\\d+)-?([A-Z]?)(\\d?)/?");
	private static Pattern _regCCPnoDT = Pattern.compile("CC   PD\\s+(\\d\\d)-(\\w\\w\\w)-(\\d\\d\\d\\d)");
	private static Pattern _regCCPnoDTSTD = Pattern.compile("CC   PD\\s+(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d)");
	private static Pattern _regDEPno = Pattern.compile("DE   .*Patent.*?\\s?([A-Z][A-Z]+?)\\s?\\(?(\\d\\d\\d+)\\)?-?([A-Z]?)(\\d?)/?.*",Pattern.CASE_INSENSITIVE);
	//private static Pattern _regDEPno = Pattern.compile("DE   .*Patent.*?\\s?([A-Z][A-Z]+?)\\s?\\(?(\\d\\d\\d+)\\)?-?([A-Z]?)(\\d?)/?.*");
	
	// 专利号对应日期
	private static Pattern _regPnoDTSTD = Pattern.compile("(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d)");


	private RecordEmbl regPno(RecordEmbl record) throws IOException{
		Matcher mrPno = _regRLPno.matcher(record.Content);
		if(mrPno.find()){
			record.PNO = Strings.isNullOrEmpty(mrPno.group(3)) ? mrPno.group(1) + mrPno.group(2)  : mrPno.group(1) + mrPno.group(2) + mrPno.group(3) + mrPno.group(4);
			// 专利号抽取正则
			Matcher mrPnoDt = _regPnoDT.matcher(mrPno.group(0));

			if(mrPnoDt.find()){
				String monthnum = EMBLFileReader.month.get(mrPnoDt.group(2));
				if(monthnum != null){
					record.PNODT = mrPnoDt.group(3) + monthnum + mrPnoDt.group(1);
				}
			}else{
				// yyyy-MM-dd
				mrPnoDt = _regPnoDTSTD.matcher(mrPno.group(0));
				if(mrPnoDt.find()){
					record.PNODT = mrPnoDt.group(1) + mrPnoDt.group(2) + mrPnoDt.group(3);

				}
			}
		}
		else{
			mrPno = _regCCPno.matcher(record.Content);
			if(mrPno.find()){
				record.PNO = mrPno.group(3) == null ? mrPno.group(1) + mrPno.group(2)  : mrPno.group(1) + mrPno.group(2) + mrPno.group(3) + mrPno.group(4);
				Matcher mrPnoDt = _regCCPnoDT.matcher(record.Content);
				if(mrPnoDt.find()){
					String monthnum = EMBLFileReader.month.get(mrPnoDt.group(2));
					if(monthnum != null){
						record.PNODT = mrPnoDt.group(3) + monthnum + mrPnoDt.group(1);
					}
				}else{
					mrPnoDt = _regCCPnoDTSTD.matcher(record.Content);
					if(mrPnoDt.find()){
						record.PNODT = mrPnoDt.group(1) + mrPnoDt.group(2) + mrPnoDt.group(3);
					}
				}
			}else{
				 mrPno = _regDEPno.matcher(record.Content);
				 if(mrPno.find()){
						record.PNO = mrPno.group(3) == null ? mrPno.group(1) + mrPno.group(2)  : mrPno.group(1) + mrPno.group(2) + mrPno.group(3) + mrPno.group(4);
				}
			}
		}
		//log.info(record.PNO);
		if(record.PNO == null){
			Files.append(record._id + "\r\n", new File("D:\\pno.txt"), StandardCharsets.UTF_8);
		}
		return record;
	}
	/***
	 * 读取一条记录的字符串，格式保持原样
	 * @return 记录字符串
	 * @throws Exception 
	 */
	public RecordEmbl readOneObjFormat01() throws Exception
	{
		RecordEmbl record = null;
		// ID；AC；RL正则
		Matcher mr  = null;
		// 专利号抽取正则
		Matcher mrPno = null;
		try {
			String line = null;
			line = _br.readLine();
			if (line == null)
			{
				//文件结尾
				return null;
			}
			record = new RecordEmbl();
			record.Format = BIO_CONST.EMBL_FORMAT01;
			StringBuilder sb = new StringBuilder(64*1024);
			do{
				sb.append(line + BIO_CONST.CHAR_ENTER);
				mr = _reg.matcher(line);
				if(mr.find())
				{									
					if(mr.group(1).compareTo("ID")==0)
					{
						// 先将ID赋值给RecID
						record.RecID = mr.group(2);
					}else if(mr.group(1).compareTo("AC")==0){
						//如果AC有值，将AC赋值给RecID
						record.AC = mr.group(2);
					}
					/***
					else if(mr.group(1).compareTo("RL")==0){
						//如果RL有值，将专利号赋值给PNO
						mrPno = _regPno.matcher(line);
						if(mrPno.find()){
							if(record.PNO != null){
								// 按照规则，EMBL格式1不应该出现多个PNO
								log.error(record.RecID + "发现重复PNO："+record.PNO +"--" + mrPno.group(1) + mrPno.group(2) + mrPno.group(3));
							}
							record.PNO = mrPno.group(1) + mrPno.group(2) + mrPno.group(3);
							// 取专利号对应的日期 倒数第十二位到倒数第二位
							String dt = line.substring(line.length()-12, line.length()-1);
							// 专利号抽取正则
							Matcher mrPnoDt = _regPnoDT.matcher(dt);

							if(mrPnoDt.find()){
								String monthnum = month.get(mrPnoDt.group(2));
								if(monthnum != null){
									record.PNODT = mrPnoDt.group(3) + monthnum + mrPnoDt.group(1);
								}
							}
						}
						
					}else if(mr.group(1).compareTo("CC")==0){
						if(record.PNO == null && line.startsWith("CC   PN")){
							mrPno = _regPno.matcher(line);
							if(mrPno.find()){
								record.PNO = mrPno.group(1) + mrPno.group(2) + mrPno.group(3);
							}
						}
						if(record.PNODT == null && line.startsWith("CC   PD")){
							
						}
					}else if(mr.group(1).compareTo("DE")==0){
						if(record.PNO == null){
							mrPno = _regPno.matcher(line);
							if(mrPno.find()){
								if(record.PNO != null){
									// 按照规则，EMBL格式1不应该出现多个PNO
									log.error(record.RecID + "发现重复PNO："+record.PNO +"--" + mrPno.group(1) + mrPno.group(2) + mrPno.group(3));
								}
								record.PNO = mrPno.group(1) + mrPno.group(2) + mrPno.group(3);
							}

						}
					}
					***/
					
				}else{
					if(line.compareTo("//")==0){
						break;
						//标识另外一条记录开始
					}
				}
				line = _br.readLine();
			} while (line != null);
			record.Content = sb.toString();

		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
		return record;
	}
	/***
	 * 读取一条记录的字符串，格式保持原样
	 * @return 记录字符串
	 */
	public RecordEmbl readOneObjFormat02()
	{
		

		return null;
	}

}
