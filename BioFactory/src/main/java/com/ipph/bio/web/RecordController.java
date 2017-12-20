package com.ipph.bio.web;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

import org.bson.BsonSerializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.hp.util.EMBLUtil;
import com.ipph.bio.file.EMBLFileReader;
import com.ipph.bio.file.FASTAFileReader;
import com.ipph.bio.model.ExchangePno;
import com.ipph.bio.model.RecordEmbl;
import com.ipph.bio.model.RecordFasta;
import com.ipph.bio.util.BIO_CONST;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

/**
 * 本Controller针对原始文件进行初始的入库操作
 * 
 * @author jiahh 2015年5月13日
 *
 */
@RestController
@Slf4j
@RequestMapping("/record")
public class RecordController {

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoClient mongo;
	// yyyymmdd
	private long datetime = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date()));

	@RequestMapping("/")
	public String help() {
		String help = "";
		help += "/embl \r\n" + "将指定位置的embl文件入库 \r\n</br>" + "/fasta \r\n" + "将指定位置的fasta文件入库 \r\n";
		return help;
	}
	/**
	 * 
	 * @param filepath
	 * @param type 默认为格式1
	 * @return
	 * @throws Exception
	 */
	//http://localhost:8082/record/fasta2/excute?filepath=D:\MyWork\didoc\生物序列\data\生物序列\蛋白生物专利序列FASTA\kpop
	//http://localhost:8082/record/fasta2/excute?filepath=D:\MyWork\didoc\生物序列\data\生物序列\蛋白生物专利序列FASTA\epop
	//http://localhost:8082/record/fasta2/excute?filepath=D:\MyWork\didoc\生物序列\data\生物序列\蛋白生物专利序列FASTA\jpop
	//http://localhost:8082/record/fasta2/excute?filepath=D:\MyWork\didoc\生物序列\data\生物序列\蛋白生物专利序列FASTA\\uspop
	@RequestMapping("/fasta2/excute")
	public String excuteFasta2(String filepath) throws Exception {
		return this.excuteFasta(filepath, "2");
	}	
	// http://localhost:8082/record/fasta/excute?filepath=D:\MyWork\didoc\生物序列\data\生物序列\蛋白生物专利序列FASTA\epopnr
	// http://localhost:8082/record/fasta/excute?filepath=D:\MyWork\didoc\生物序列\data\生物序列\蛋白生物专利序列FASTA\jpopnr
	// http://localhost:8082/record/fasta/excute?filepath=D:\MyWork\didoc\生物序列\data\生物序列\蛋白生物专利序列FASTA\kpopnr
	// http://localhost:8082/record/fasta/excute?filepath=D:\MyWork\didoc\生物序列\data\生物序列\蛋白生物专利序列FASTA\nrpl1
	// http://localhost:8082/record/fasta/excute?filepath=D:\MyWork\didoc\生物序列\data\生物序列\蛋白生物专利序列FASTA\nrpl2
	// http://localhost:8082/record/fasta/excute?filepath=D:\MyWork\didoc\生物序列\data\生物序列\蛋白生物专利序列FASTA\\uspopnr
	// http://localhost:8082/record/fasta/excute?filepath=D:\MyWork\didoc\生物序列\data\生物序列\核苷酸专利序列（非冗余，FASTA格式）\nrnl1
	// http://localhost:8082/record/fasta/excute?filepath=D:\MyWork\didoc\生物序列\data\生物序列\核苷酸专利序列（非冗余，FASTA格式）\nrnl2---Fasta，格式1文件没有以>开头
	/**
	 * 
	 * @param filepath
	 * @param type 默认为格式1
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/fasta/excute")
	public String excuteFasta(String filepath, String type) throws Exception {
		FASTAFileReader reader = null;
		if(!Strings.isNullOrEmpty(type) && "2".equals(type)){
			reader = new FASTAFileReader(BIO_CONST.FASTA_FORMAT02);
		}else{
			reader = new FASTAFileReader();
		}
		
		// File file = new File(filepath);
		// String dbconn = file.getName();
		reader.open(filepath, BIO_CONST.ROOT_FASTA_FORMAT01, "UTF-8", true);
		log.info("start-" + reader._filename);
		RecordFasta record = reader.readBioSequenceObj();
		List<RecordFasta> list = new ArrayList<RecordFasta>();
		
		//
		// 临时增加不能入库的错误数据的输出路径
		String errorpath = "/soft/java/error/";
		String filename = "/MaxBSONsize-" + record.FilePath +".txt" ;
		File errorfile = new File(errorpath+filename);
		if(errorfile.exists()) errorfile.delete();
		//
		

		//
		int i = 0;
		// 构造检索条件
		Query seqquery = new Query();

		seqquery.fields().include("_id").include("CRC").include("TimeCreate");

		int insertnum = 0;
		int updatenum = 0;
		int ignorenum = 0;

		long start = System.currentTimeMillis();

		Hashtable<String, RecordFasta> dict = new Hashtable<String, RecordFasta>();

		while (record != null) {
			i++;
			if (!dict.containsKey(record._id)) {
				dict.put(record._id, record);
			}

			record = reader.readBioSequenceObj();
			if (i % 1000 == 0 || record == null) {
				Map<String, Integer> map = this.excuteFasta(dict);
				dict = new Hashtable<String, RecordFasta>();
				insertnum += map.get("insertnum");
				updatenum += map.get("updatenum");
				ignorenum += map.get("ignorenum");
				log.info(reader._filename +"-i:" + i);
			}

		}

		reader.close();

		Files.append(filepath.subSequence(0, filepath.length()), new File("D:\\over.txt"), StandardCharsets.UTF_8);
		log.info(filepath + ":处理数量:" + i + "插入数量:" + insertnum + "更新数量:" + updatenum + "跳过数量:" + ignorenum);
		log.info("总耗时:" + (System.currentTimeMillis() - start) + "ms");
		log.info("end-" + reader._filename);
		return filepath + "总耗时:" + (System.currentTimeMillis() - start) + "ms " + "处理数量:" + i + "插入数量:" + insertnum
				+ "更新数量:" + updatenum + "跳过数量:" + ignorenum;

	}
	
	/**
	 * 装载fasta2原始数据
	 * @param folder 如是数组以分号分开
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/fasta2/folder")
	public String excuteFasta2Folder(String folder) throws Exception {
		String ret = "";
		String[] folderArr = folder.split(";"); 
		for(String folderStr : folderArr){
			File file = new File(folderStr);
			log.info("folder-" + folderStr);

			// 判断是否为文件
			if (file.isDirectory()) {
				// 若是文件夹，则执行以下操作
				// 将文件夹下所有文件转换成一个File数组
				File[] filearray = file.listFiles();
				for(File fileembl : filearray){
					String filepath = fileembl.getPath();
					log.info("filepath-" + filepath);
					ret += this.excuteFasta(filepath, "2") + "\r\n<br>";
				}
			}
		}
		return ret;
	}

	/**
	 * 
	 * @param filepath
	 * @param isfind
	 * @return
	 * @throws Exception
	 */
	// http://localhost:8082/record/embl/excute?isfind=true&filepath=D:\MyWork\didoc\生物序列\data\生物序列\patentdata\jpo_prt.dat
	// http://localhost:8082/record/embl/excute?isfind=true&filepath=D:\MyWork\didoc\生物序列\data\生物序列\patentdata\kipo_prt.dat
	// 去除双斜杠
	// http://localhost:8082/record/embl/excute?isfind=true&filepath=D:\MyWork\didoc\生物序列\data\生物序列\patentdata\\uspto_prt.dat
	// http://localhost:8082/record/embl/excute?isfind=true&filepath=D:\MyWork\didoc\生物序列\data\生物序列\patentdata\epo_prt.dat
	// http://localhost:8082/record/embl/excute?isfind=true&filepath=D:\MyWork\didoc\生物序列\data\生物序列\蛋白生物专利序列注解EMBL\epo_prt.dat
	// http://localhost:8082/record/embl/excute?isfind=true&filepath=D:\MyWork\didoc\生物序列\data\生物序列\核苷酸专利序列（冗余，EMBL格式）\epo_prt.dat
	// http://localhost:8082/record/embl/excute?isfind=true&filepath=D:\MyWork\didoc\生物序列\data\生物序列\核苷酸专利序列（冗余，EMBL格式）\rel_pat_env_01_r122.dat
	// http://localhost:8082/record/embl/excute?isfind=true&filepath=D:\MyWork\didoc\生物序列\data\生物序列\核苷酸专利序列（冗余，EMBL格式）\rel_pat_phg_01_r122.dat
	// http://localhost:8082/record/embl/excute?isfind=true&filepath=D:\MyWork\didoc\生物序列\data\生物序列\核苷酸专利序列（冗余，EMBL格式）\rel_pat_pln_02_r122.dat
	// http://localhost:8082/record/embl/excute?isfind=true&filepath=D:\MyWork\didoc\生物序列\data\生物序列\核苷酸专利序列（冗余，EMBL格式）\rel_pat_vrt_01_r122.dat
	// http://localhost:8082/record/embl/excute?isfind=true&filepath=D:\MyWork\didoc\生物序列\data\生物序列\核苷酸专利序列（冗余，EMBL格式）\rel_pat_fun_02_r122.dat
	// http://localhost:8082/record/embl/excute?isfind=true&filepath=D:\MyWork\didoc\生物序列\data\生物序列\核苷酸专利序列（冗余，EMBL格式）\
	// http://localhost:8082/record/embl/excute?isfind=true&filepath=D:\MyWork\didoc\生物序列\data\生物序列\核苷酸专利序列（冗余，EMBL格式）\
	@RequestMapping("/embl/excute")
	public String excuteEmbl(String filepath, boolean isfind) throws Exception {

		// @PathVariable String rootpath
		EMBLFileReader reader = new EMBLFileReader();
		reader.open(filepath, "UTF-8");
		log.info("start-" + reader._filename);
		RecordEmbl record = null;
		int i = 0;
		int insertnum = 0;
		int updatenum = 0;
		int ignorenum = 0;
		long start = System.currentTimeMillis();
		// 读取转换后的记录
		record = reader.readOneObj();
		Hashtable<String, RecordEmbl> dict = new Hashtable<String, RecordEmbl>();

		while (record != null) {
			i++;
			if (!dict.containsKey(record._id)) {
				dict.put(record._id, record);
			}

			record = reader.readOneObj();
			if (i % 100000 == 0 || record == null) {
				Map<String, Integer> map = this.excuteEmbl(dict);
				dict = new Hashtable<String, RecordEmbl>();
				insertnum += map.get("insertnum");
				updatenum += map.get("updatenum");
				ignorenum += map.get("ignorenum");
				log.info(reader._filename +"-i:" + i);
			}

		}

		reader.close();

		Files.append(filepath.subSequence(0, filepath.length()), new File("D:\\over.txt"), StandardCharsets.UTF_8);
		log.info(filepath + ":处理数量:" + i + "插入数量:" + insertnum + "更新数量:" + updatenum + "跳过数量:" + ignorenum);
		log.info("总耗时:" + (System.currentTimeMillis() - start) + "ms");
		log.info("end-" + reader._filename);
		return filepath + "总耗时:" + (System.currentTimeMillis() - start) + "ms " + "处理数量:" + i + "插入数量:" + insertnum
				+ "更新数量:" + updatenum + "跳过数量:" + ignorenum;

	}
	
	/**
	 * 装载EMBL原始数据
	 * @param folder 如是数组以分号分开
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/embl/folder")
	public String excuteEmblFolder(String folder) throws Exception {
		String ret = "";
		String[] folderArr = folder.split(";"); 
		for(String folderStr : folderArr){
			File file = new File(folderStr);
			log.info("folder-" + folderStr);

			// 判断是否为文件
			if (file.isDirectory()) {
				// 若是文件夹，则执行以下操作
				// 将文件夹下所有文件转换成一个File数组
				File[] filearray = file.listFiles();
				for(File fileembl : filearray){
					String filepath = fileembl.getPath();
					log.info("filepath-" + filepath);
					ret += this.excuteEmbl(filepath, true) + "\r\n<br>";
				}
			}
		}		
		return ret;
	}

		
	private Map<String, Integer> excuteFasta(Hashtable<String, RecordFasta> dict) throws Exception {

		int insertnum = 0;
		int updatenum = 0;
		int ignorenum = 0;

		Query seqquery = Query.query(Criteria.where("_id").in(dict.keySet()));
		seqquery.fields().include("_id").include("CRC").include("TimeCreate");
		// 根据主键查找数据库集合中是否存在此数据
		List<RecordFasta> dbresultList = mongoTemplate.find(seqquery, RecordFasta.class, BIO_CONST.SOURCE_FASTA);
		for (RecordFasta dbresult : dbresultList) {
			RecordFasta record = dict.get(dbresult._id);
			// 如果存在，则比较crc
			if (record.CRC == dbresult.CRC) {
				ignorenum++;
				// 一致，不处理
			} else {
				// 如果存在 但是CRC不一致，则更新
				record.TimeCreate = dbresult.TimeCreate;
				record.TimeUpdate = datetime;
				record.status = BIO_CONST.STATUS_1;
				record.state = BIO_CONST.STATE_0;// 不写也是0，为了明确
				record.OldRecord = dbresult.OldRecord;
				if (record.OldRecord == null) {
					record.OldRecord = new Hashtable<Long, RecordFasta>();
				}
				// 防止无限递归，将本属性置null
				dbresult.OldRecord = null;
				if (!record.OldRecord.containsKey(dbresult.CRC)) {
					// 这个检索只是为了保存旧值，进行数据校验使用
					RecordFasta ret = mongoTemplate.findById(dbresult._id, RecordFasta.class, BIO_CONST.SOURCE_FASTA);
					ret.OldRecord = null;
					record.OldRecord.put(dbresult.CRC, ret);
				}
				Files.append(record._id, new File("D:\\doubleid-fasta.txt"), StandardCharsets.UTF_8);
				Files.append("\r\n", new File("D:\\doubleid-fasta.txt"), StandardCharsets.UTF_8);
				mongoTemplate.save(record, BIO_CONST.SOURCE_FASTA);
				updatenum++;
			}
			dict.remove(dbresult._id);

		}
		for (RecordFasta record : dict.values()) {
			try{
				mongoTemplate.save(record, BIO_CONST.SOURCE_FASTA);
				insertnum++;
			}catch(BsonSerializationException ex){
				// 当出现异常时，输出数据到错误记录文件，继续
				String filepath = "/soft/java/error/";
				String filename = "/MaxBSONsize-" + record.FilePath +".txt" ;
				File errorfile = new File(filepath);
				if (!errorfile.exists()) {
					Files.createParentDirs(errorfile);
					errorfile.mkdirs();
				}
				File file = new File(filepath+filename);
				if(!file.exists())
					file.createNewFile();
				Files.append(record.Header, file, StandardCharsets.UTF_8);
				Files.append("\r\n", file, StandardCharsets.UTF_8);
				Files.append(record.Seq, file, StandardCharsets.UTF_8);
				Files.append("\r\n", file, StandardCharsets.UTF_8);
				ignorenum++;
				record.Seq = ex.getMessage();
				mongoTemplate.save(record, "FastaError");
				log.error("record._id:" + record._id);
				//throw ex;
			}catch(Exception ex){
				// 当出现异常时，输出数据到错误记录文件，继续
				String filepath = "/soft/java/error/";
				String filename = "/Exception-" + record.FilePath +".txt" ;
				File errorfile = new File(filepath);
				if (!errorfile.exists()) {
					Files.createParentDirs(errorfile);
					errorfile.mkdirs();
				}
				File file = new File(filepath+filename);
				if(!file.exists())
					file.createNewFile();
				Files.append(record.Header, file, StandardCharsets.UTF_8);
				Files.append("\r\n", file, StandardCharsets.UTF_8);
				Files.append(record.Seq, file, StandardCharsets.UTF_8);
				Files.append("\r\n", file, StandardCharsets.UTF_8);
				ignorenum++;
				log.error("record._id:" + record._id);
				record.Seq = ex.getMessage();
				mongoTemplate.save(record, "FastaError");

				//throw ex;
			}
		}

		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("total", dict.size());
		map.put("insertnum", insertnum);
		map.put("updatenum", updatenum);
		map.put("ignorenum", ignorenum);
		return map;
	}

	private Map<String, Integer> excuteEmbl(Hashtable<String, RecordEmbl> dict) throws IOException {

		int insertnum = 0;
		int updatenum = 0;
		int ignorenum = 0;

		Query seqquery = Query.query(Criteria.where("_id").in(dict.keySet()));
		seqquery.fields().include("_id").include("CRC").include("TimeCreate");
		// 根据主键查找数据库集合中是否存在此数据
		List<RecordEmbl> dbresultList = mongoTemplate.find(seqquery, RecordEmbl.class, BIO_CONST.SOURCE_EMBL);
		for (RecordEmbl dbresult : dbresultList) {
			RecordEmbl record = dict.get(dbresult._id);
			// 如果存在，则比较crc
			if (record.CRC == dbresult.CRC) {
				ignorenum++;
				// 一致，不处理
			} else {
				// 如果存在 但是CRC不一致，则更新
				record.TimeCreate = dbresult.TimeCreate;
				record.TimeUpdate = datetime;
				record.status = BIO_CONST.STATUS_1;
				record.state = BIO_CONST.STATE_0;// 不写也是0，为了明确
				record.OldRecord = dbresult.OldRecord;
				if (record.OldRecord == null) {
					record.OldRecord = new Hashtable<Long, RecordEmbl>();
				}
				// 防止无限递归，将本属性置null
				dbresult.OldRecord = null;
				if (!record.OldRecord.containsKey(dbresult.CRC)) {
					// 这个检索只是为了保存旧值，进行数据校验使用
					RecordEmbl ret = mongoTemplate.findById(dbresult._id, RecordEmbl.class, BIO_CONST.SOURCE_EMBL);
					ret.OldRecord = null;
					record.OldRecord.put(dbresult.CRC, ret);
				}
				Files.append(record._id, new File("D:\\doubleid-embl.txt"), StandardCharsets.UTF_8);
				Files.append("\r\n", new File("D:\\doubleid-embl.txt"), StandardCharsets.UTF_8);
				mongoTemplate.save(record, BIO_CONST.SOURCE_EMBL);
				updatenum++;
			}
			dict.remove(dbresult._id);

		}
		for (RecordEmbl record : dict.values()) {
			insertnum++;
			mongoTemplate.save(record, BIO_CONST.SOURCE_EMBL);
		}

		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("total", dict.size());
		map.put("insertnum", insertnum);
		map.put("updatenum", updatenum);
		map.put("ignorenum", ignorenum);
		return map;
	}


	/**
	 * 
	 * @param country WO,EP,US
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/reset/stdinfo")
	public WriteResult resetstdinfo(String country, String oper) throws Exception {
		if (Strings.isNullOrEmpty(oper))
			oper = "in";

		Query query = null;
		query = new Query();
		if(!Strings.isNullOrEmpty(country)){
			if ("in".equals(oper)) {
				query.addCriteria(Criteria.where("country").in(Arrays.asList(country.split(","))));
			} else {
				query.addCriteria(Criteria.where("country").nin(Arrays.asList(country.split(","))));
			}
			
		}else{
			query.addCriteria(Criteria.where("StdInfo").is(null));
		}
		//query.addCriteria(Criteria.where("exception").is("KR:CutkindNoDate->US,PN"));
		//query.addCriteria(Criteria.where("StdInfo").is(null));
		//String[] array = {"KR:KR10->KR,AN","KR:KR10->KR,PN","KR:Cut10->KR,PN"};
		//query.addCriteria(Criteria.where("exception").nin(array));


		WriteResult result = mongoTemplate.updateMulti(query, new Update().set("StdInfo", null).set("exception", null).set("PNS", null), BIO_CONST.EXCHANGE_PNO);
		return result;
	}
	/**
	 * 
	 * @param country WO,EP,US
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/reset/stdinfoexception")
	public WriteResult resetstdinfoexception(String country, String oper,String exception) throws Exception {
		if (Strings.isNullOrEmpty(oper))
			oper = "in";

		Query query = null;
		query = new Query();
		Criteria criteria = new Criteria();
		if(!Strings.isNullOrEmpty(country)){
			if ("in".equals(oper)) {
				query.addCriteria(Criteria.where("country").in(Arrays.asList(country.split(","))).and("StdInfo").is(null));
			} else {
				query.addCriteria(Criteria.where("country").nin(Arrays.asList(country.split(","))).and("StdInfo").is(null));
			}
			
		}
		if(!Strings.isNullOrEmpty(exception)){
			if ("in".equals(oper)) {
				query.addCriteria(Criteria.where("exception").in(Arrays.asList(exception.split("@"))));
			} else {
				query.addCriteria(Criteria.where("exception").nin(Arrays.asList(exception.split("@"))));
			}
			
		}
		//{country:"KR",exception:{$nin:["KR:KR10->KR,AN","KR:KR10->KR,PN","KR:Cut10->KR,PN"]}}
		//query.addCriteria(Criteria.where("StdInfo").is(null));

		WriteResult result = mongoTemplate.updateMulti(query, new Update().set("exception", null).set("PNS", null), BIO_CONST.EXCHANGE_PNO);
		return result;
	}	/**
	 * 将ExchangePNO 重置state，为了重新生成BioPatent数据
	 * @param country WO,EP,US
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/reset/exchangepno")
	public WriteResult resetexchangepno(String country, String oper,String pnos) throws Exception {
		if (Strings.isNullOrEmpty(oper))
			oper = "in";

		Query query = null;
		query = new Query();
		query.addCriteria(Criteria.where("state").ne(0));
		if(!Strings.isNullOrEmpty(country)){
			if ("in".equals(oper)) {
				query.addCriteria(Criteria.where("country").in(Arrays.asList(country.split(","))));
			} else {
				query.addCriteria(Criteria.where("country").nin(Arrays.asList(country.split(","))));
			}
			
		}else if(!Strings.isNullOrEmpty(pnos)){
			query.addCriteria(Criteria.where("_id").in(Arrays.asList(pnos.split(","))));
		}
		WriteResult result = mongoTemplate.updateMulti(query, new Update().set("state", 0), BIO_CONST.EXCHANGE_PNO);
		return result;
	}
	@RequestMapping("/reset/biopatent")
	public WriteResult resetbiopatent(String country, String oper) throws Exception {
		if (Strings.isNullOrEmpty(oper))
			oper = "in";

		Query query = null;
		if(!Strings.isNullOrEmpty(country)){
			query = new Query();
			if ("in".equals(oper)) {
				query.addCriteria(Criteria.where("country").in(Arrays.asList(country.split(","))));
			} else {
				query.addCriteria(Criteria.where("country").nin(Arrays.asList(country.split(","))));
			}
		}
		WriteResult result = mongoTemplate.updateMulti(query, new Update().set("state", 0), BIO_CONST.BIO_PATENT);
		return result;
	}
	@RequestMapping("/reset/fasta")
	public WriteResult resetfasta(String country, String oper) throws Exception {
		Query query = Query.query(Criteria.where("status").ne(0));
		WriteResult result = mongoTemplate.updateMulti(query, new Update().set("status", 0), BIO_CONST.SOURCE_FASTA);
		return result;
	}
	@RequestMapping("/reset/embl")
	public WriteResult resetembl(String country, String oper) throws Exception {
		Query query = Query.query(Criteria.where("status").ne(2));
		WriteResult result = mongoTemplate.updateMulti(query, new Update().set("status", 2), BIO_CONST.SOURCE_EMBL);
	
		return result;
	}

	/**
	 * 处理ExchangePno，包括追加、更新、标准化
	 * @throws Exception
	 */
	@RequestMapping("/pno")
	public void pno() throws Exception{
		// 将由于fasta变化导致的embl变化的状态更新
		//pnoExcuteFasta();
		// 将pno插入ExchangePNO
		//在embl数据入库之后，处理标准专利号之前的插件，用来补充处理一些没有解析出来的embl记录中的专利号
		//pnoGetEmbl();
		//获取EMBL原始专利号,并插入ExchangePNO表
		pnoExcuteEmbl();
		// TODO 将embl或者fasta有变化的记录对应的PNO进行状态更新
		//pnoemblupdate();
		// 将ExchangePNO表的专利号进行标准化
		pnoExcuteStd(null,null,null);
	}
	
	/**
	 * 将fasta记录对应embl的专利号拿出来，将ExchangePNO记录重置
	 */
	private void pnoExcuteFasta(){
		log.info("处理pnoExcuteFasta：START");

		// 构造检索条件
		Query query = new Query();
		query.addCriteria(Criteria.where("status").in(BIO_CONST.STATUS_1,BIO_CONST.STATUS_0));
		// TODO 以后会不会换成RecID
		query.fields().include("SeqID");
		int pagesize = 500000;
		int startpage = 0;
		Pageable pageable = new PageRequest(startpage, pagesize);
		query.with(pageable);
		long count = mongoTemplate.count(query, BIO_CONST.SOURCE_FASTA);
		int loopcount = (int) (count % pagesize == 0 ? count / pagesize : count / pagesize + 1);
		List<RecordFasta> list = null;
		HashSet<String> seqidset = null;
		HashSet<String> pnoset = null;
		// 开始循环fasta
		for (int i = 0; i < loopcount; i++) {
			if(i < startpage) continue;
			seqidset = new HashSet<String>();
			log.info("处理页码：" + (i + 1));
			list = mongoTemplate.find(query, RecordFasta.class, BIO_CONST.SOURCE_FASTA);
			log.info("读取记录完毕：" + (i + 1));
			//pnoStrList = new ArrayList<String>();
			List<String> listfastaid = new ArrayList<String>();
			// 将seqid取出
			for (RecordFasta record : list) {
				listfastaid.add(record._id);
				if(!seqidset.contains(record.SeqID)){
					seqidset.add(record.SeqID);
				}
			}
			// 找到对应的embl 并且embl已经处理成2 的
			Query emblquery = Query.query(Criteria.where("RecID").in(seqidset).and("status").is(BIO_CONST.STATUS_2));	
			long emblcount = mongoTemplate.count(emblquery, BIO_CONST.SOURCE_EMBL);
			// 更新sourceEMBL记录status=1
			mongoTemplate.updateMulti(
					emblquery,
					new Update().set("status", BIO_CONST.STATUS_1).set("state", BIO_CONST.STATE_0),
					BIO_CONST.SOURCE_EMBL);
			// 更新sourcefasta status=2 已处理
			long fastacount = mongoTemplate.count(Query.query(Criteria.where("_id").in(listfastaid)), BIO_CONST.SOURCE_FASTA);
			mongoTemplate.updateMulti(
					Query.query(Criteria.where("_id").in(listfastaid)),
					new Update().set("status", BIO_CONST.STATUS_2),
					BIO_CONST.SOURCE_FASTA);
			
			//跳页
//			pageable = pageable.next();
//			query = query.with(pageable);

		}
		log.info("处理pnoExcuteFasta：END");

	}
	/**
	 * 补充专利号标准格式信息
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/pno/std")
	public String pnoExcuteStd(String country,String pnos, String oper) throws Exception {
		// 构造检索条件
		Query query = new Query();
		long timecost = 0;
		Criteria creteria = null;
		if(!Strings.isNullOrEmpty(country)){
			if ("nin".equals(oper)) {
				creteria = Criteria.where("StdInfo").is(null).and("country").nin(Arrays.asList(country.split(","))).and("exception").is(null);
			} else {
				creteria = Criteria.where("StdInfo").is(null).and("country").in(Arrays.asList(country.split(","))).and("exception").is(null);
			}
			
		}else if(!Strings.isNullOrEmpty(pnos)){
			creteria = Criteria.where("_id").in(Arrays.asList(pnos.split(",")));
		}else{
			creteria = Criteria.where("StdInfo").is(null).and("exception").is(null);
		}

//		if(!Strings.isNullOrEmpty(country)){
//			creteria = Criteria.where("StdInfo").is(null).and("country").is(country).and("exception").is(null);
//		}else if(!Strings.isNullOrEmpty(pnos)){
//			creteria = Criteria.where("_id").in(Arrays.asList(pnos.split(",")));
//		}else{
//			creteria = Criteria.where("StdInfo").is(null).and("exception").is(null);
//		}
		// TEST
		//query.addCriteria(Criteria.where("exception").is("KR:NoDate->US,PN"));
		// TEST
		query.addCriteria(creteria);
		//query.addCriteria(Criteria.where("StdInfo").is(null).and("country").nin("WO","EP","JP","US"));

		// query.fields().include("_id").include("Header");
		int pagesize = 1000;
		// 由于检索条件在被更新，分页不跳页，一直取第一页
		Pageable pageable = new PageRequest(0, pagesize);
		query.with(pageable);
		log.info("/pno/std-Start:");
		long count = mongoTemplate.count(query, BIO_CONST.EXCHANGE_PNO);
		int loopcount = (int) (count % pagesize == 0 ? count / pagesize : count / pagesize + 1);
		for (int i = 0; i < loopcount; i++) {
			long start = System.currentTimeMillis();
			long costweb = 0;
			log.info("page:" + (i + 1));
			List<ExchangePno> list = mongoTemplate.find(query, ExchangePno.class, BIO_CONST.EXCHANGE_PNO);
			for (ExchangePno pno : list) {
				long startweb = System.currentTimeMillis();
				pno = EMBLUtil.getDocId(pno);
				if(pno != null && pno.StdInfo != null){
					pno.TimeUpdate = datetime;
				}else{
					System.out.println(pno._id + "   " +  " getSTDPUBNUM is null ");
				}

				costweb += System.currentTimeMillis()-startweb;
				mongoTemplate.save(pno, BIO_CONST.EXCHANGE_PNO);
			}
			log.info("this.costweb" + costweb);
			log.info("this.timecost" + (System.currentTimeMillis() - start));
			timecost += System.currentTimeMillis() - start;
			log.info("PNO2.timecost" + timecost);

			// pageable = pageable.next();
			// query = query.with(pageable);

			log.info("StdInfo.is(null):" + mongoTemplate.count(query, BIO_CONST.EXCHANGE_PNO));
		}

		log.info("End:");
		return String.valueOf(count);

	}
	/**
	 * 在embl数据入库之后，处理标准专利号之前的插件，用来补充处理一些没有解析出来的embl记录中的专利号
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/pno/test")
	public String pnoGetEmbl() throws Exception {
		log.info("处理pnoExcuteEmbl：START");
		// 构造检索条件
		Query query = new Query();
		query.addCriteria(Criteria.where("status").is(BIO_CONST.STATUS_0));
		//query.addCriteria(Criteria.where("_id").is("DZ000001"));
		query.fields().include("Content");
		int pagesize = 1000;
		int startpage = 0;
		Pageable pageable = new PageRequest(startpage, pagesize);
		query.with(pageable);
		log.info("Start:");
		long count = mongoTemplate.count(query, BIO_CONST.SOURCE_EMBL);
		int loopcount = (int) (count % pagesize == 0 ? count / pagesize : count / pagesize + 1);

		Matcher matcherSplit = null;
		ExchangePno Pno = null;
		List<RecordEmbl> list = null;
		//List<String> pnoStrList = null;
		Hashtable<String, ExchangePno> dict = new Hashtable<String, ExchangePno>();
		for (int i = 0; i < loopcount; i++) {
			log.info("处理页码：" + (i + 1));
			list = mongoTemplate.find(query, RecordEmbl.class, BIO_CONST.SOURCE_EMBL);
			log.info("读取记录完毕：" + (i + 1));
			//pnoStrList = new ArrayList<String>();
			List<String> listfastaid = new ArrayList<String>();
			
			for (RecordEmbl record : list) {
				regPno(record);
				mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(record._id)),
						new Update().set("status", BIO_CONST.STATUS_1)
						.set("PNO", record.PNO).set("PNODT", record.PNODT)
						.set("TimeUpdate", datetime), BIO_CONST.SOURCE_EMBL)
						;

				
			}
			// 由于更新了检索条件，不需要跳页，循环取第一页即可
//			pageable = pageable.next();
//			query = query.with(pageable);

		}
		log.info("END");
		return null;
	}
	// RL PNO
	private static Pattern _regRLPno = Pattern.compile("RL   .*Patent.*?\\s?([A-Z][A-Z]+?)\\s?\\(?(\\d\\d\\d+)\\)?-?([A-Z]?)(\\d?)/?.*",Pattern.CASE_INSENSITIVE);
	private static Pattern _regCCPno = Pattern.compile("CC   PN\\s+([A-Z][A-Z]+?)\\s?(\\d\\d\\d+)-?([A-Z]?)(\\d?)/?");
	private static Pattern _regCCPnoDT = Pattern.compile("CC   PD\\s+(\\d\\d)-(\\w\\w\\w)-(\\d\\d\\d\\d)");
	private static Pattern _regCCPnoDTSTD = Pattern.compile("CC   PD\\s+(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d)");
	private static Pattern _regDEPno = Pattern.compile("DE   .*Patent.*?\\s?([A-Z][A-Z]+?)\\s?\\(?(\\d\\d\\d+)\\)?-?([A-Z]?)(\\d?)/?.*",Pattern.CASE_INSENSITIVE);
	//private static Pattern _regDEPno = Pattern.compile("DE   .*Patent.*?\\s?([A-Z][A-Z]+?)\\s?\\(?(\\d\\d\\d+)\\)?-?([A-Z]?)(\\d?)/?.*");
	
	// 专利号对应日期
	private static Pattern _regPnoDT = Pattern.compile("(\\d\\d)-(\\w\\w\\w)-(\\d\\d\\d\\d)");
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
		return null;
	}
	/**
	 * 获取EMBL原始专利号
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/pno/embl")
	public String pnoExcuteEmbl() throws Exception {
		log.info("pnoExcuteEmbl:START");
		Pattern reg_splitno = Pattern.compile("^([A-Za-z]{2,5})(\\d+)(\\w*)");
		// 构造检索条件
		Query query = new Query();
		query.addCriteria(Criteria.where("status").ne(BIO_CONST.STATUS_2));
		query.fields().include("PNO").include("PNODT");
		int pagesize = 500000;
		int startpage = 0;
		Pageable pageable = new PageRequest(startpage, pagesize);
		query.with(pageable);
		log.info("Start:");
		long count = mongoTemplate.count(query, BIO_CONST.SOURCE_EMBL);
		int loopcount = (int) (count % pagesize == 0 ? count / pagesize : count / pagesize + 1);

		Matcher matcherSplit = null;
		ExchangePno Pno = null;
		List<RecordEmbl> list = null;
		//List<String> pnoStrList = null;
		Hashtable<String, ExchangePno> dict = new Hashtable<String, ExchangePno>();
		for (int i = 0; i < loopcount; i++) {
			if(i < startpage) continue;
			dict = new Hashtable<String, ExchangePno>();
			log.info("处理页码：" + (i + 1));
			list = mongoTemplate.find(query, RecordEmbl.class, BIO_CONST.SOURCE_EMBL);
			log.info("读取记录完毕：" + (i + 1));
			//pnoStrList = new ArrayList<String>();
			List<String> listfastaid = new ArrayList<String>();
			
			for (RecordEmbl record : list) {
				listfastaid.add(record._id);
				if(record.PNO == null) continue;
				Pno = new ExchangePno();
				Pno._id = record.PNO;
				Pno.date = record.PNODT;
				Pno.source = "E";// 来源与EMBL补充
				Pno.TimeCreate = datetime;
				matcherSplit = reg_splitno.matcher(Pno._id);
				if (matcherSplit.find()) {
					Pno.country = matcherSplit.group(1);
					Pno.docnum = matcherSplit.group(2);
					Pno.kind = matcherSplit.group(3);
				} else {
					log.error("无法拆分文献号：" + Pno._id);
				}
				//pnoStrList.add(Pno._id);
				if (!dict.containsKey(Pno._id)) {
					// pnoObjList.add(Pno);
					dict.put(Pno._id, Pno);
				}
				//else if(dict.get(Pno._id).status == BIO_CONST.STATUS_2 && Pno.status != BIO_CONST.STATUS_2){
//					dict.remove(Pno._id);
//					dict.put(Pno._id, Pno);
//				}

			}
			this.excutePno(dict);
			// 由于更新了检索条件，不需要跳页，循环取第一页即可
//			pageable = pageable.next();
//			query = query.with(pageable);
			// 更新embl记录状态为已经抽取PNO
			mongoTemplate.updateMulti(Query.query(Criteria.where("_id").in(listfastaid)),
					new Update().set("status", BIO_CONST.STATUS_2), BIO_CONST.SOURCE_EMBL);

		}

		log.info("处理pnoExcuteEmbl：END");

		return null;

	}
	private void excutePno(Hashtable<String, ExchangePno> dict){
		
		//log.info("获取文献号：" + pnoStrList.size());
		log.info("去重dict.size()：" + dict.size());
		Query queryPno = Query.query(Criteria.where("_id").in(dict.keySet()));
		queryPno.fields().include("_id");
		List<ExchangePno> listret = mongoTemplate.find(queryPno,ExchangePno.class, BIO_CONST.EXCHANGE_PNO);
		// 更新存在的PNO状态为有变化
		WriteResult pnoresult = mongoTemplate.updateMulti(
				Query.query(Criteria.where("_id").in(dict.keySet()).and("status").ne(BIO_CONST.STATUS_1)),
				new Update().set("status", BIO_CONST.STATUS_1).set("state", BIO_CONST.STATE_0),
				BIO_CONST.EXCHANGE_PNO);
		for (ExchangePno pno : listret) {
			if (dict.containsKey(pno._id)) {
				dict.remove(pno._id);
			}
		}

		log.info("插入dict.size()：" + dict.size());
		try {
			mongoTemplate.insert(dict.values(), BIO_CONST.EXCHANGE_PNO);
		} catch (Exception ex) {
			log.info(ex.getMessage());
		}
		log.info("EXCHANGE_PNO记录：" + mongoTemplate.count(null, BIO_CONST.EXCHANGE_PNO));

	}
//	/**
//	 * 将embl记录有变化的专利号拿出来，将ExchangePNO记录重置
//	 */
//	private void pnoemblupdate(){
//		// 构造检索条件
//		Query query = new Query();
//		query.addCriteria(Criteria.where("status").is(BIO_CONST.STATUS_1));
//		query.fields().include("PNO");
//		int pagesize = 500000;
//		int startpage = 0;
//		Pageable pageable = new PageRequest(startpage, pagesize);
//		query.with(pageable);
//		log.info("Start:");
//		long count = mongoTemplate.count(query, BIO_CONST.SOURCE_EMBL);
//		int loopcount = (int) (count % pagesize == 0 ? count / pagesize : count / pagesize + 1);
//		List<RecordEmbl> listembl = null;
//		HashSet<String> pnoset = null;
//		for (int i = 0; i < loopcount; i++) {
//			if(i < startpage) continue;
//			pnoset = new HashSet<String>();
//			log.info("处理页码：" + (i + 1));
//			listembl = mongoTemplate.find(query, RecordEmbl.class, BIO_CONST.SOURCE_EMBL);
//			log.info("读取记录完毕：" + (i + 1));
//			//pnoStrList = new ArrayList<String>();
//			List<String> listemblid = new ArrayList<String>();
//			
//			for (RecordEmbl record : listembl) {
//				listemblid.add(record._id);
//				if(!pnoset.contains(record.PNO)){
//					pnoset.add(record.PNO);
//				}
//			}
//			
//			// 更新存在的PNO状态为有变化
//			WriteResult pnoresult = mongoTemplate.updateMulti(
//					Query.query(Criteria.where("_id").in(pnoset).and("state").ne(BIO_CONST.STATE_0)),
//					new Update().set("status", BIO_CONST.STATUS_1).set("state", BIO_CONST.STATE_0),
//					BIO_CONST.EXCHANGE_PNO);
//			// 更新sourceEMBL记录status=2
//			mongoTemplate.updateMulti(
//					Query.query(Criteria.where("_id").in(listemblid)),
//					new Update().set("status", BIO_CONST.STATUS_2).set("state", BIO_CONST.STATE_0),
//					BIO_CONST.SOURCE_EMBL);
//			
//			//跳页
//			pageable = pageable.next();
//			query = query.with(pageable);
//
//		}
//	}
}
