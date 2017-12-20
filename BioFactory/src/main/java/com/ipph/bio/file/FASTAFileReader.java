package com.ipph.bio.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

import lombok.extern.slf4j.Slf4j;

import com.google.common.io.Files;
import com.hp.util.MD5FileUtil;
import com.ipph.bio.model.RecordFasta;
import com.ipph.bio.util.BIO_CONST;

/***
 * FASTA格式文件读取对象
 * 
 * @author lhp FASTA文件格式 每一条记录由‘〉’字符开始 一条记录由信息+生物序列两个部分
 *         生物信息：保存在一行，不同字段由分隔串隔开，一般用|隔开 生物序列：一行或多行，多行按80个字符换行
 */
@Slf4j
public class FASTAFileReader {
	private String _rootpath = null;
	private String _filepath = null;
	public String _filename = null;

	private BufferedReader _br = null;
	private FileInputStream _fis = null;
	private InputStreamReader _isr = null;
	// yyyymmdd
	private long datetime = Long.parseLong(new SimpleDateFormat("yyyyMMdd")
	.format(new Date()));

	// 设定FASTA文件读取的格式
	private String format = BIO_CONST.FASTA_FORMAT01;
	/***
	 * 是否保留生物序列的回车符号
	 */
	private boolean _keepReturn = false;

	/***
	 * 上一次读取的下一条记录第一行 以ID开头
	 */
	private String _lastLine = null;
	private String _firstLine = null;

	public FASTAFileReader() {
		this.datetime = Long.parseLong(new SimpleDateFormat("yyyyMMdd")
				.format(new Date()));
	}

	public FASTAFileReader(String format) {
		this.datetime = Long.parseLong(new SimpleDateFormat("yyyyMMdd")
		.format(new Date()));
		this.format = format;
	}

	/***
	 * 功能：关闭文件EMBL对象
	 */
	public void close() {
		try {
			if (_br != null) {
				_br.close();
				_br = null;
			}
			if (_isr != null) {
				_isr.close();
				_isr = null;
			}
			if (_fis != null) {
				_fis.close();
				_fis = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		_lastLine = null;
	}

	/***
	 * 功能：打开EMBL文件对象
	 * 
	 * @param sFilePath
	 *            文件路径
	 * @param sEncoding
	 *            文本编码
	 * @param keepReturn
	 *            是否保持生物序列的回车
	 * @return 成功返回 true
	 */
	public boolean open(String sFilePath, String sRootPath, String sEncoding,
			boolean keepReturn) {
		try {
			_filepath = sFilePath;
			this._rootpath = sRootPath;
			
			File f = new File(sFilePath);
			if (!f.exists() && !f.isDirectory()) {
				return false;
			}
			this._filename = f.getName();
			_keepReturn = keepReturn;

			_fis = new FileInputStream(sFilePath);
			if (sEncoding == null) {
				_isr = new InputStreamReader(_fis);
			} else {
				_isr = new InputStreamReader(_fis, sEncoding);
			}

			_br = new BufferedReader(_isr);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}

	/***
	 * 功能：读取FASTA格式记录第一行
	 * 
	 * @return 记录第一行文本
	 */
	public String readFirstLine() {
		try {
			String line = null;
			if (_lastLine != null) {
				line = _lastLine;
				_lastLine = null;
			} else {
				do {
					line = _br.readLine();
					if (line == null) {
						return null;
					}

					// 剔除空行
					if (line.length() > 0) {
						// 判断记录第一行开始标志
						if (line.charAt(0) == '>') {
							return line.substring(0);
						}
					}
				} while (true);
			}
			return line;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/***
	 * 功能：读取FASTA格式记录的生物序列
	 * 
	 * @return 生物序列串
	 */
	public String readBioSequence() {
		try {
			StringBuffer str = null;
			String line = null, result = null;
			do {
				line = _br.readLine();
				if (line == null) {
					break;
				}

				// 滤除空行
				if (line.length() > 0) {
					// 判断是否为新记录开始行
					if (line.charAt(0) == '>') {
						_lastLine = line.substring(1);
						break;
					}

					if (result == null) {
						// 解决生物序列只有一行的性能问题
						result = line;
					} else {
						if (str == null) {
							// 生物序列可能存在很多行，字符串直接相加存在性能问题
							str = new StringBuffer(8 * 1024);
						}

						// 生物序列是否保持回车换行
						if (_keepReturn) {
							if (result != null) {
								str.append(result + BIO_CONST.CHAR_ENTER);
								result = null;
							}
							str.append(line + BIO_CONST.CHAR_ENTER);
						} else {
							if (result != null) {
								str.append(result);
								result = null;
							}
							str.append(line);
						}
					}
				}
			} while (true);

			if (str != null) {
				return str.toString();
			}
			return result;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/***
	 * 功能：读取FASTA格式记录的生物序列
	 * 
	 * @return 生物序列串
	 */
	CRC32 crc = new CRC32();

	public RecordFasta readBioSequenceObj() throws Exception {
		RecordFasta record = null;
		if (BIO_CONST.FASTA_FORMAT01.equals(this.format)) {
			record = this.readBioSequenceObjForamt01();
		} else if (BIO_CONST.FASTA_FORMAT02.equals(this.format)) {
			record = this.readBioSequenceObjForamt02();
		} else {
			record = this.readBioSequenceObjForamt02();
			;
		}
		if (record != null && record.RecID != null) {
			// RecID__Source__FilePath
			record.FilePath = this._filename;
			// CRC 为总体内容的crc
			try{
				// 如果长度超过99999999，就不计算CRC了。容易发生内存溢出，导致程序崩溃。
				if(record.Seq.length() > 99999999){
					record.CRC = Integer.MAX_VALUE;
					// MD5也不计算了
					record.MD5 = "cccccccccccccccccccccccccccccccc";
					log.error("record.Seq.length() > 99999999--"+ record.Header);

				}else{
					crc.reset();
					crc.update((record.Header + record.Seq).getBytes());
					record.CRC = crc.getValue();
					//record.MD5 = MD5FileUtil.getMD5String(record.Seq.replace("\r", "").replace("\n", ""));
					record.MD5 = MD5FileUtil.getMD5String(record.Seq.toUpperCase().replace("\r", "").replace("\n", "").replace(" ", ""));
				}
			}catch(Exception ex){
				//
				// 临时增加不能入库的错误数据的输出路径
				String errorpath = "/soft/java/error/";
				String filename = "/VMlimit-"+record.FilePath+".txt" ;
				File errorfile = new File(errorpath+filename);
				if(!errorfile.exists())
					errorfile.createNewFile();
//

				Files.append(record.Header, new File(filename), StandardCharsets.UTF_8);
				Files.append("\r\n", new File(filename), StandardCharsets.UTF_8);
				Files.append(record.Seq, new File(filename), StandardCharsets.UTF_8);
				record.CRC = Long.MAX_VALUE;
				//其实这个异常catch不住，程序崩溃了。
				log.error("Requested array size exceeds VM limit"+ record.Header);
			}
			record._id = record.Source +":"+record.RecID;
			record.TimeCreate = this.datetime;
		}
		return record;

	}

	/***
	 * 功能：读取FASTA格式记录的生物序列
	 * 
	 * @return 生物序列串
	 */
	//Pattern reg_Format01 = Pattern.compile("^>(.+?):(.+?)\\s");
	//Pattern reg_Format01 = Pattern.compile("^>((.+?):(.+?))\\sPN:(.+)");
	// PN非必须 reg_Format01 reg_Format02通用 01有PN 02 无PN group 1 2 3是通用的
	Pattern reg_Format01 = Pattern.compile("^>((.+?):(.+?))\\s(PN:(.+))?");
	// >EPOPNR:NRP_A34224 PN:Unknown
	// 第一行

	public RecordFasta readBioSequenceObjForamt01() throws Exception {
		RecordFasta record = null;

		StringBuffer str = null;
		String line = null, result = null;
		do {
			line = _br.readLine();
			if (line == null) {
				break;
			}
			if (_firstLine == null) {

				_firstLine = line;
				// 第一行如果不是>开头，则跳过
				if (_firstLine.length() == 0) {
					continue;
				}
				if (_firstLine.length() != 0 && _firstLine.charAt(0) != '>') {
					throw new Exception(this._filename + "Fasta，格式1文件没有以>开头");
				}
			}
			record = new RecordFasta();
			record.Format = BIO_CONST.FASTA_FORMAT01;
			record.Header = _lastLine;
			// 滤除空行
			if (line.length() > 0) {
				// 判断是否为新记录开始行
				if (line.charAt(0) == '>') {
					if (_lastLine != null) {
						_lastLine = line;
						break;
					}
					_lastLine = line;
				} else if (result == null) {
					// 解决生物序列只有一行的性能问题
					result = line;
				} else {
					if (str == null) {
						// 生物序列可能存在很多行，字符串直接相加存在性能问题
						str = new StringBuffer(8 * 1024);
					}

					// 生物序列是否保持回车换行
					if (_keepReturn) {
						str.append(line + BIO_CONST.CHAR_ENTER);
					} else {
						str.append(line);
					}
				}
			}
		} while (true);

		if (result != null) {
			if (str != null) {
				// 多行序列情况
				str.insert(0, result + BIO_CONST.CHAR_ENTER);
				record.Seq = str.toString().toUpperCase();
				
			} else {
				// 一行序列情况
				record.Seq = result.toUpperCase();
			}
			
			Matcher matcher = reg_Format01.matcher(record.Header);
			if (matcher.find()) {
				//record.RecID = matcher.group(1);
				record.Source = matcher.group(2);
				record.RecID = matcher.group(3);
				if(record.RecID.indexOf("_") >0){
					record.SeqID = record.RecID.substring(record.RecID.indexOf("_")+1);
				}else{
					record.SeqID = record.RecID;
				}
				if(matcher.group(4) != null){
					record.PNO = matcher.group(5).replace(" ", "");
				}
			}else{
				log.error("record.Header 不符合规则reg_Format01：" + record.Header);
				record = null;
				
			}
		}
		return record;

	}

	/***
	 * 功能：读取FASTA格式记录的生物序列
	 * 
	 * @return 生物序列串
	 * @throws Exception 
	 */
	Pattern reg_Format02 = Pattern.compile("^>((.+?):(.+?))\\s+?(.+?)\\s+");

	public RecordFasta readBioSequenceObjForamt02() throws Exception {
		RecordFasta record = null;
		record = this.readBioSequenceObjForamt01();
		if(record != null){
			// 格式2多解析一个ID2
			Matcher matcher = reg_Format02.matcher(record.Header);
			if (matcher.find()) {
				record.ID2 = matcher.group(4);
			}

			record.Format = BIO_CONST.FASTA_FORMAT02;
		}
		return record;
	}
	public RecordFasta readBioSequenceObjnotCRC() throws Exception {
		RecordFasta record = null;
		if (BIO_CONST.FASTA_FORMAT01.equals(this.format)) {
			record = this.readBioSequenceObjForamt01();
		} else if (BIO_CONST.FASTA_FORMAT02.equals(this.format)) {
			record = this.readBioSequenceObjForamt02();
		} else {
			record = this.readBioSequenceObjForamt02();
			;
		}
		if (record != null && record.RecID != null) {
			// RecID__Source__FilePath
			record.FilePath = this._filename;
		}
		return record;

	}

}
