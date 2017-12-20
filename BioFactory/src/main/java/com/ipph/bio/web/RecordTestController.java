package com.ipph.bio.web;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tempuri.DocResult;
import org.tempuri.DocdbnumService;
import org.tempuri.DocdbnumServiceSoap;
import org.tempuri.StdInfo;

import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.TreeTraverser;
import com.google.common.io.Files;
import com.hp.util.EMBLUtil;
import com.ipph.bio.file.EMBLFileReader;
import com.ipph.bio.file.FASTAFileReader;
import com.ipph.bio.model.BioPatent;
import com.ipph.bio.model.BioSequenceAC;
import com.ipph.bio.model.ExchangePno;
import com.ipph.bio.model.RecordEmbl;
import com.ipph.bio.model.RecordFasta;
import com.ipph.bio.util.BIO_CONST;
import com.ipph.bio.util.BioUtil;
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
@RequestMapping("/test")
public class RecordTestController {

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
	@RequestMapping("/doc")
	public BioPatent patent(String doc) {
		if(Strings.isNullOrEmpty(doc)){
			doc = "WO9013312A1";
		}
		long start = System.currentTimeMillis();
		BioPatent patent = mongoTemplate.findById(doc, BioPatent.class,"BioPatent");
		log.info((System.currentTimeMillis() - start)+"");
		return patent;
	}

	@RequestMapping("/up")
	public void aaa(){
		
		// 更新状态和时间
		Update update = new Update();
		update.set("Format", BIO_CONST.FASTA_FORMAT01);
		WriteResult result = mongoTemplate.updateMulti(null, update, BIO_CONST.SOURCE_FASTA);
		
		 update = new Update();
		 update.set("Format", BIO_CONST.EMBL_FORMAT01);
		 result = mongoTemplate.updateMulti(null, update, BIO_CONST.SOURCE_EMBL);

	}
	/**
	 *
	 * 向EMBL的源库中插入从文件中读取的记录
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/embl")
	public List<RecordEmbl> embl(String path) throws Exception {

		EMBLFileReader reader = new EMBLFileReader();
		// 打开指定文件
		reader.open("D:\\MyWork\\didoc\\生物序列\\nrnl1.annot.xml.txt", "UTF8");
		RecordEmbl record = null;
		List<RecordEmbl> list = new ArrayList<RecordEmbl>();
		int i = 0;
		// 读取转换后的记录
		record = reader.readOneObj();
		while (record != null) {
			i++;
			list.add(record);
			record = reader.readOneObj();
		}
		// TODO delete
		long count = mongoTemplate.count(null, BIO_CONST.SOURCE_EMBL);
		// 插入Mongo
		if (list.size() > 0) {
			for (RecordEmbl embl : list) {
				mongoTemplate.save(embl, BIO_CONST.SOURCE_EMBL);
			}
		}
		// TODO delete
		count = mongoTemplate.count(null, BIO_CONST.SOURCE_EMBL);
		return list;
	}

	/**
	 * 想FASTA的源库中插入从文件中读取的记录
	 * 
	 * @param rids
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/fasta")
	public List<RecordFasta> fasta(String rootpath) throws Exception {

		FASTAFileReader reader = new FASTAFileReader();

		reader.open("D:\\MyWork\\didoc\\生物序列\\nrn11_fasta.txt", "D:\\MyWork\\didoc\\生物序列\\", "UTF8", false);
		RecordFasta record = reader.readBioSequenceObj();
		int i = 0;
		List<RecordFasta> list = new ArrayList<RecordFasta>();
		while (record != null) {
			i = i + 1;
			list.add(record);
			record = reader.readBioSequenceObj();
		}

		mongoTemplate.getCollection(BIO_CONST.SOURCE_FASTA);
		// TODO delete
		long count = mongoTemplate.count(null, BIO_CONST.SOURCE_FASTA);
		// 插入Mongo
		if (list.size() > 0) {
			for (RecordFasta embl : list) {
				mongoTemplate.save(embl, BIO_CONST.SOURCE_FASTA);
			}
		}
		// TODO delete
		count = mongoTemplate.count(null, BIO_CONST.SOURCE_FASTA);
		List<RecordFasta> list1 = mongoTemplate.find(null, RecordFasta.class, BIO_CONST.SOURCE_FASTA);
		return list1;
	}

	/**
	 * 测试用途 TODO delete
	 * 
	 * @param rids
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/fasta/select")
	public List<RecordFasta> find(String[] rids) throws Exception {

		mongoTemplate.getCollection(BIO_CONST.SOURCE_FASTA);
		long count = mongoTemplate.count(null, BIO_CONST.SOURCE_FASTA);

		count = mongoTemplate.count(null, BIO_CONST.SOURCE_FASTA);
		List<RecordFasta> list1 = mongoTemplate.find(null, RecordFasta.class, BIO_CONST.SOURCE_FASTA);
		return list1;
	}

	@RequestMapping("/fasta/excute/all")
	public void excuteFolder() throws Exception {
		// @PathVariable String rootpath
		FASTAFileReader reader = new FASTAFileReader();
		// @RequestBody(required = false)
		// TODO　folder　＝　rootpath；
		String folder = BIO_CONST.ROOT_FASTA_FORMAT01;
		log.info("start");
		traverseFolder_FASTA(folder, reader);
		log.info("end");

	}

	/**
	 * 
	 * 
	 * @param filepath
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
	@RequestMapping("/embl/excute2")
	public String excuteEmbl2(String filepath, boolean isfind) throws Exception {

		mongoTemplate.updateMulti(Query.query(Criteria.where("state").is(9)),
				new Update().set("OldRecord", null).set("state", 0), BIO_CONST.SOURCE_EMBL);

		// @PathVariable String rootpath
		EMBLFileReader reader = new EMBLFileReader();
		reader.open(filepath, "UTF-8");
		log.info("start");
		RecordEmbl record = null;
		int i = 0;
		int insertnum = 0;
		int updatenum = 0;
		int ignorenum = 0;
		long start = System.currentTimeMillis();
		// 读取转换后的记录
		record = reader.readOneObj();
		// 构造检索条件
		Query seqquery = new Query();

		while (record != null) {
			i++;
			if (isfind) {
				seqquery = Query.query(Criteria.where("_id").in(record._id));
				seqquery.fields().include("_id").include("CRC").include("TimeCreate");
				// 根据主键查找数据库集合中是否存在此数据
				RecordEmbl dbresult = mongoTemplate.findById(record._id, RecordEmbl.class, BIO_CONST.SOURCE_EMBL);
				if (dbresult == null) {
					insertnum++;
					// 如果不存在则插入
					mongoTemplate.save(record, BIO_CONST.SOURCE_EMBL);
				} else if (dbresult.CRC != record.CRC) {
					// 如果存在 但是CRC不一致，则更新
					record.TimeCreate = dbresult.TimeCreate;
					record.LastModified = datetime;
					record.status = BIO_CONST.STATUS_1;
					record.state = BIO_CONST.STATE_0;// 不写也是0，为了明确
					record.OldRecord = dbresult.OldRecord;
					if (record.OldRecord == null) {
						record.OldRecord = new Hashtable<Long, RecordEmbl>();
					}
					// 防止无限递归，将本属性置null
					dbresult.OldRecord = null;
					if (!record.OldRecord.containsKey(dbresult.CRC)) {
						record.OldRecord.put(dbresult.CRC, dbresult);
					}
					Files.append(filepath.subSequence(0, filepath.length()), new File("D:\\doubleid.txt"),
							StandardCharsets.UTF_8);
					//
					mongoTemplate.save(record, BIO_CONST.SOURCE_EMBL);
					updatenum++;
				} else {
					ignorenum++;
				}
			} else {
				mongoTemplate.save(record, BIO_CONST.SOURCE_EMBL);
			}
			record = reader.readOneObj();
			if (i % 10000 == 0) {
				log.info("i:" + i);
			}

		}

		reader.close();

		Files.append(filepath.subSequence(0, filepath.length()), new File("D:\\over.txt"), StandardCharsets.UTF_8);
		log.info(filepath + ":处理数量:" + i + "插入数量:" + insertnum + "更新数量:" + updatenum + "跳过数量:" + ignorenum);

		log.info("end");
		log.info("总耗时:" + (System.currentTimeMillis() - start) + "ms");
		return filepath + "总耗时:" + (System.currentTimeMillis() - start) + "ms " + "处理数量:" + i + "插入数量:" + insertnum
				+ "更新数量:" + updatenum + "跳过数量:" + ignorenum;

	}

	@RequestMapping("/embl/excute")
	public String excuteEmbl(String filepath, boolean isfind) throws Exception {

		// @PathVariable String rootpath
		EMBLFileReader reader = new EMBLFileReader();
		reader.open(filepath, "UTF-8");
		log.info("start");
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
			if (i % 10000 == 0 || record == null) {
				Map<String, Integer> map = this.excuteEmbl(dict);
				dict = new Hashtable<String, RecordEmbl>();
				insertnum += map.get("insertnum");
				updatenum += map.get("updatenum");
				ignorenum += map.get("ignorenum");
				log.info("i:" + i);
			}

		}

		reader.close();

		Files.append(filepath.subSequence(0, filepath.length()), new File("D:\\over.txt"), StandardCharsets.UTF_8);
		log.info(filepath + ":处理数量:" + i + "插入数量:" + insertnum + "更新数量:" + updatenum + "跳过数量:" + ignorenum);
		log.info("总耗时:" + (System.currentTimeMillis() - start) + "ms");
		log.info("end");
		return filepath + "总耗时:" + (System.currentTimeMillis() - start) + "ms " + "处理数量:" + i + "插入数量:" + insertnum
				+ "更新数量:" + updatenum + "跳过数量:" + ignorenum;

	}

	private Map<String, Integer> excuteFasta(Hashtable<String, RecordFasta> dict) throws IOException {

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
			insertnum++;
			mongoTemplate.save(record, BIO_CONST.SOURCE_FASTA);
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
	// 递归方法-》foldername：要遍历的文件(夹)名（完整路径）
	private void traverseFolder_FASTA(String foldername, FASTAFileReader reader) throws Exception {
		List<RecordFasta> list = new ArrayList<RecordFasta>();
		File file = new File(foldername);
		// 判断是否为文件
		if (file.isDirectory()) {
			// 若是文件夹，则执行以下操作

			// 将文件夹下所有文件转换成一个File数组
			File[] filearray = file.listFiles();

			// 遍历数组，若是文件夹则递归，是文件则判断是否为exe文件
			for (File currfile : filearray) {
				if (currfile.isDirectory()) {
					this.traverseFolder_FASTA(currfile.getAbsolutePath(), reader);
				} else {
					if (currfile.getName().lastIndexOf(".txt") != -1) {
						excuteFasta(currfile.getPath(), "2");
					}
				}
			}
		}
	}

	@RequestMapping("/embl/findfasta")
	public int test() throws Exception {
		Pageable page = new PageRequest(0, 50000);

		List<RecordEmbl> list = new ArrayList<RecordEmbl>();
		List<RecordFasta> listfasta = new ArrayList<RecordFasta>();
		Query query = new Query();
		query.with(page);
		int i = 1;
		while (listfasta.size() == 0) {
			list = mongoTemplate.find(query, RecordEmbl.class, BIO_CONST.SOURCE_EMBL);

			List<String> emblid = new ArrayList<String>();
			for (RecordEmbl embl : list) {
				emblid.add(embl.RecID);

			}
			Query queryfasta = Query.query(Criteria.where("SeqID").in(emblid));
			listfasta = mongoTemplate.find(queryfasta, RecordFasta.class, BIO_CONST.SOURCE_FASTA);
			page = page.next();
			query = query.with(page);
			log.info("" + i);
			i++;
		}

		List<String> listresult = new ArrayList<String>();

		for (RecordFasta fasta : listfasta) {
			listresult.add(fasta._id);
			System.out.println(fasta._id);
		}
		log.info("" + listfasta.size());

		return listfasta.size();
	}

	@RequestMapping("/treefile")
	public List<String> tree(String rootpath) throws IOException {
		if (rootpath == null)
			rootpath = "D:\\MyWork\\didoc\\生物序列\\data\\生物序列\\核苷酸专利序列（冗余，EMBL格式）";
		TreeTraverser<File> treeTraverser = Files.fileTreeTraverser();
		FluentIterable<File> files = treeTraverser.breadthFirstTraversal(new File(rootpath));
		List<String> list = new ArrayList<String>();
		for (File file : files) {
			//if (file.getName().endsWith("dat") && !"epo_prt.dat".equals(file.getName())) {
				String path = "\"C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe\" \"http://localhost:8082/record/fasta2/excute?size="
						+ file.length() + "&filepath=" + file.getPath() + "\"";
				System.out.println(path);
				Files.append(path, new File("D:\\over.txt"), StandardCharsets.UTF_8);
				Files.append(BIO_CONST.CHAR_ENTER, new File("D:\\over.txt"), StandardCharsets.UTF_8);
				list.add(path);
			//}
		}

		return list;
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String args[]) throws IOException {
		RecordTestController controller = new RecordTestController();
		controller.tree("D:\\MyWork\\didoc\\生物序列\\data\\生物序列\\fastafiles_emblrelease");
	}
	/**
	 * 获取原始专利号
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/pno")
	public ExchangePno Pno(String country, String docnum, String kind) throws Exception {
		ExchangePno pno = new ExchangePno();
		pno.country = country;
		pno.docnum = docnum;
		pno.kind = kind;
		
		return EMBLUtil.getDocId(pno);
	}


	/**
	 * 获取FASTA原始专利号
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/fasta/pno1")
	public String exchangePnoFasta() throws Exception {
//		Pattern reg_splitno = Pattern.compile("^([A-Za-z]+?)(\\d+)(\\w+)");
//		// 构造检索条件
//		Query query = new Query();
//		query.addCriteria(Criteria.where("status").ne(BIO_CONST.STATUS_2));
//		query.fields().include("PNO");
//		int pagesize = 500000;
//		int startpage = 0;
//		Pageable pageable = new PageRequest(startpage, pagesize);
//		query.with(pageable);
//		log.info("Start:");
//		long count = mongoTemplate.count(query, BIO_CONST.SOURCE_FASTA);
//		int loopcount = (int) (count % pagesize == 0 ? count / pagesize : count / pagesize + 1);
//
//		Matcher matcherSplit = null;
//		Hashtable<String, ExchangePno> dict = new Hashtable<String, ExchangePno>();
//		for (int i = 0; i < loopcount; i++) {
//			dict = new Hashtable<String, ExchangePno>();
//			log.info("处理页码：" + (i + 1));
//			List<RecordFasta> list = mongoTemplate.find(query, RecordFasta.class, BIO_CONST.SOURCE_FASTA);
//			log.info("读取记录完毕：" + (i + 1));
//			List<String> pnoStrList = new ArrayList<String>();
//			List<String> listfastaid = new ArrayList<String>();
//
//			ExchangePno Pno = null;
//			for (RecordFasta record : list) {
//				listfastaid.add(record._id);
//				if (record.PNO != null && !"Unknown".equals(record.PNO)) {
//					Pno = new ExchangePno();
//					Pno._id = record.PNO;
//					matcherSplit = reg_splitno.matcher(Pno._id);
//					if (matcherSplit.find()) {
//						Pno.source = "F";// 来源与FASTA
//						Pno.country = matcherSplit.group(1);
//						Pno.docnum = matcherSplit.group(2);
//						Pno.kind = matcherSplit.group(3);
//					} else {
//						log.error("无法拆分文献号：" + Pno._id);
//					}
//					pnoStrList.add(Pno._id);
//					if (!dict.containsKey(Pno._id)) {
//						dict.put(Pno._id, Pno);
//					}
//				}
//
//			}
//			log.info("获取文献号：" + pnoStrList.size());
//			this.excutePno(dict);
//			// 由于更新了检索条件，不需要跳页，循环取第一页即可
////			pageable = pageable.next();
////			query = query.with(pageable);
//
//			// 更新fasta记录状态为已经抽取PNO
//			WriteResult fastaresult = mongoTemplate.updateMulti(Query.query(Criteria.where("_id").in(listfastaid)),
//					new Update().set("status", BIO_CONST.STATUS_2), BIO_CONST.SOURCE_FASTA);
//		
//		}
//
//		log.info("End:");
//
		return null;

	}
	/**
	 * 补充专利号标准格式信息
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/fasta/pno2")
	public String exchangePno2() throws Exception {
		// 构造检索条件
		Query query = new Query();
		long timecost = 0;

		//query.addCriteria(Criteria.where("StdInfo").is(null).and("exception").is(null));
		query.addCriteria(Criteria.where("StdInfo").is(null));

		// query.fields().include("_id").include("Header");
		int pagesize = 10;
		// 由于检索条件在被更新，分页不跳页，一直取第一页
		Pageable pageable = new PageRequest(0, pagesize);
		query.with(pageable);
		log.info("Start:");
		long count = mongoTemplate.count(query, BIO_CONST.EXCHANGE_PNO);
		int loopcount = (int) (count % pagesize == 0 ? count / pagesize : count / pagesize + 1);
		for (int i = 0; i < loopcount; i++) {
			long start = System.currentTimeMillis();
			long costweb = 0;
			log.info("处理页码：" + (i + 1));
			List<ExchangePno> list = mongoTemplate.find(query, ExchangePno.class, BIO_CONST.EXCHANGE_PNO);
			for (ExchangePno pno : list) {
				long startweb = System.currentTimeMillis();
				pno = EMBLUtil.getDocId(pno);
				costweb += System.currentTimeMillis()-startweb;
				mongoTemplate.save(pno, BIO_CONST.EXCHANGE_PNO);
			}
			log.info("本次costweb" + costweb);
			log.info("本次timecost" + (System.currentTimeMillis() - start));
			timecost += System.currentTimeMillis() - start;
			log.info("PNO2.timecost" + timecost);

			// pageable = pageable.next();
			// query = query.with(pageable);

			log.info("StdInfo.is(null):" + mongoTemplate.count(query, BIO_CONST.EXCHANGE_PNO));
		}

		log.info("End:");
		return null;

	}
	
	/**
	 * 获取EMBL原始专利号
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/embl/pno1")
	public String exchangePnoEmbl() throws Exception {
		Pattern reg_splitno = Pattern.compile("^([A-Za-z]{2,5})(\\d+)(\\w*)");
		// 构造检索条件
		Query query = new Query();
		query.addCriteria(Criteria.where("status").ne(BIO_CONST.STATUS_2));
		query.fields().include("PNO");
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
				Pno.source = "E";// 来源与EMBL补充
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

			}
			this.excutePno(dict);
			// 由于更新了检索条件，不需要跳页，循环取第一页即可
//			pageable = pageable.next();
//			query = query.with(pageable);
			// 更新fasta记录状态为已经抽取PNO
			WriteResult fastaresult = mongoTemplate.updateMulti(Query.query(Criteria.where("_id").in(listfastaid)),
					new Update().set("status", BIO_CONST.STATUS_2), BIO_CONST.SOURCE_EMBL);

		}

		log.info("End:");

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

	@RequestMapping("/id2")
	public void updateid2(){
		//Query query = Query.query(Criteria.where("ID2").is(null).and("Format").is(BIO_CONST.FASTA_FORMAT02));
		Query query = Query.query(Criteria.where("Format").is(BIO_CONST.FASTA_FORMAT02));
		query.fields().include("_id").include("ID2").include("Header").include("Format");
		int pagesize = 100000;
		int startpage = 0;
		Pageable pageable = new PageRequest(startpage, pagesize);
		query.with(pageable);
		log.info("Start:");
		long count = mongoTemplate.count(query, BIO_CONST.SOURCE_FASTA);
		log.info("total：" + count);
		//int loopcount = (int) (count % pagesize == 0 ? count / pagesize : count / pagesize + 1);
		//int loopcount = 1000;
		int i = 0;
		while(true){
			// 取一批专利
			List<RecordFasta> list = mongoTemplate.find(query, RecordFasta.class, BIO_CONST.SOURCE_FASTA);
			if(list == null || list.size() == 0){
				break;
			}
			for(RecordFasta record: list){
				if(!Strings.isNullOrEmpty(record.ID2))
					continue;
				// 更新状态和时间
				Update update = new Update();
				String id2 = getid2(record);
				if(!Strings.isNullOrEmpty(id2)){
					update.set("ID2", id2);
					mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(record._id)), update, BIO_CONST.SOURCE_FASTA);					
				}
			}
			// 翻页处理下一批
				pageable = pageable.next();
				query.with(pageable);
			log.info("i:" + (i+1)*pagesize);
			i++;
		}


		log.info("End:");

	}
	
	/***
	 * 功能：读取FASTA格式记录的生物序列
	 * 
	 * @return 生物序列串
	 * @throws Exception 
	 */
	Pattern reg_Format02 = Pattern.compile("^>((.+?):(.+?))\\s+?(.+?)\\s+");

	public String getid2(RecordFasta record) {
		if(record != null){
			// 格式2多解析一个ID2
			Matcher matcher = reg_Format02.matcher(record.Header);
			if (matcher.find()) {
				record.ID2 = matcher.group(4);
			}

			record.Format = BIO_CONST.FASTA_FORMAT02;
		}
		return record.ID2;
	}

	/**
	 * 临时函数，补充ExchangePNO的PNS字段
	 */
	@RequestMapping("/pns")
	public void updateExchangePNOPNS(){
		Query query = Query.query(Criteria.where("StdInfo").ne(null));
		query.fields().include("StdInfo");
		List<ExchangePno> list = mongoTemplate.find(query, ExchangePno.class, BIO_CONST.EXCHANGE_PNO);
		log.info("Start");
		for(int i = 0 ;i<list.size();i++){
			ExchangePno pno = list.get(i);
			pno.PNS = pno.StdInfo.getSTDPUBCOUNTRY()+ pno.StdInfo.getSTDPUBNUM() + pno.StdInfo.getSTDPUBKIND();
			mongoTemplate.updateFirst(Query.query(Criteria.where("_id").in(pno._id)),
					new Update().set("PNS", pno.PNS), BIO_CONST.EXCHANGE_PNO);

			if(i%10000==0 || i==list.size()-1){
				log.info("i:" + i);
			}
		}
		log.info("END");
		
	}
	/**
	 * 临时函数，补充ExchangePNO的PNS字段
	 */
	@RequestMapping("/std2old")
	public void updateExchangePNOstd2old(){
		Query query = Query.query(Criteria.where("StdInfo2").ne(null));
		WriteResult result = mongoTemplate.updateMulti(query, new Update().set("StdInfo2", null), BIO_CONST.EXCHANGE_PNO);
		
		query = Query.query(Criteria.where("StdInfo").ne(null));
		query.fields().include("StdInfo");
		List<ExchangePno> list = mongoTemplate.find(query, ExchangePno.class, BIO_CONST.EXCHANGE_PNO);
		log.info("Start");
		for(int i = 0 ;i<list.size();i++){
			ExchangePno pno = list.get(i);
			pno.PNS = pno.StdInfo.getSTDPUBCOUNTRY()+ pno.StdInfo.getSTDPUBNUM() + pno.StdInfo.getSTDPUBKIND();
			result = mongoTemplate.updateFirst(Query.query(Criteria.where("_id").in(pno._id)),
					new Update().set("StdInfo2", pno.StdInfo).set("StdInfo", null), BIO_CONST.EXCHANGE_PNO);

			if(i%10000==0 || i==list.size()-1){
				log.info("i:" + i);
			}
		}
		log.info("END");
		
	}
	/**
	 * 临时函数，比较和上一版本的PNS的差异
	 */
	@RequestMapping("/diffpns")
	public void updateExchangePNOstddiffpns(){
		Query query = Query.query(Criteria.where("StdInfo2").ne(null));
		query.fields().include("StdInfo").include("StdInfo2");
		List<ExchangePno> list = mongoTemplate.find(query, ExchangePno.class, BIO_CONST.EXCHANGE_PNO);
		WriteResult result;
		log.info("Start");
		for(int i = 0 ;i<list.size();i++){
			ExchangePno pno = list.get(i);
			//pno.PNS = pno.StdInfo.getSTDPUBCOUNTRY()+ pno.StdInfo.getSTDPUBNUM() + pno.StdInfo.getSTDPUBKIND();
			String pns = pno.StdInfo2.getSTDPUBCOUNTRY()+ pno.StdInfo2.getSTDPUBNUM() + pno.StdInfo2.getSTDPUBKIND();
			if(!pns.equals(pno.PNS)){
				log.info("pno._id:" + pno._id + " pno.PNS:"+pno.PNS + "  pns:"+pns);
			}

			if(i%10000==0 || i==list.size()-1){
				log.info("i:" + i);
			}
		}
		log.info("END");
		
	}

	/**
	 * 将state更新为-1
	 */
	@RequestMapping("/updatesourceid2")
	public WriteResult updatesourceid2(){
		Query query = Query.query(Criteria.where("PNO").is(null));
		log.info("Start");
		// 更新状态和时间
		Update update = new Update();
		update.set("status", BIO_CONST.STATUS_0);
		//mongoTemplate.updateMulti(null, update, BIO_CONST.SOURCE_EMBL);
		WriteResult result =mongoTemplate.updateMulti(query, update, BIO_CONST.SOURCE_EMBL);
		log.info("END");
		return result;
	}

	/**
	 * 将state更新为-1
	 */
	@RequestMapping("/resetid")
	public WriteResult resetid(){
		Query query = Query.query(Criteria.where("ID2").ne(null));
		log.info("Start");
		// 更新状态和时间
		Update update = new Update();
		update.set("state", -1);
		WriteResult result =mongoTemplate.updateMulti(query, update, BIO_CONST.SOURCE_FASTA);
		log.info("END");
		return result;
	}
	
	/**
	 * PNS重复
	 * {PNS:{$in:["US6133024A","US5468610A","US5449720A","US6136585A","US4801456A","US6153420A"]}}
	 * 序列PNO：US7435798A  欧专局US7435798 (B2) 接口返回 US6133024A
	 * 所有重复记录：PNS	PNO
	 * US4801456A	US4801456A
US4801456A	US7135187A
US5449720A	US5449720A
US5449720A	US6489293A
US5468610A	US5468610A
US5468610A	US7427593A
US6133024A	US6133024A
US6133024A	US7435798A
US6136585A	US6136585A
US6136585A	US7160698A
US6153420A	US6153420A
US6153420A	US7238498A
	 */
	@RequestMapping("/pnsdistinct")
	public void distinctpno(String country){
		
		if(mongoTemplate.collectionExists("pnorepeat")){
			mongoTemplate.dropCollection("pnorepeat");
		}
		if(mongoTemplate.collectionExists("pnorepeatall")){
			mongoTemplate.dropCollection("pnorepeatall");
		}
		Query query = Query.query(Criteria.where("StdInfo").ne(null));
		if(!Strings.isNullOrEmpty(country)){
			query = Query.query(Criteria.where("StdInfo").ne(null).and("country").is(country));
		}
		//query.fields().include("PNS");
		List<ExchangePno> list = mongoTemplate.find(query, ExchangePno.class, BIO_CONST.EXCHANGE_PNO);
		Hashtable<String,ExchangePno> set = new Hashtable<String,ExchangePno>();
		log.info("Start");
		for(int i = 0 ;i<list.size();i++){
			ExchangePno pno = list.get(i);
			if(set.containsKey(pno.PNS)){
				//log.info(pno.PNS);
				mongoTemplate.insert(pno, "pnorepeat");
				mongoTemplate.insert(pno, "pnorepeatall");
				mongoTemplate.insert(set.get(pno.PNS), "pnorepeatall");
			}else{
				set.put(pno.PNS,pno);
			}
		}

		log.info(""+set.size());
		log.info("END");
		
	}
	@RequestMapping("/distinctsequence")
	public void distinctsequence(){
		Query query = new Query();
		query.fields().include("_id");
		int pagesize = 500000;
		int startpage = 0;
		Pageable pageable = new PageRequest(startpage, pagesize);
		query.with(pageable);
		log.info("Start:");
		long count = mongoTemplate.count(query, BIO_CONST.BIO_SEQUENCE_AC);
		int loopcount = (int) (count % pagesize == 0 ? count / pagesize : count / pagesize + 1);
		Hashtable<String,ExchangePno> set = new Hashtable<String,ExchangePno>();
		log.info("Start");
		for(int i = 0 ;i<loopcount;i++){
			List<BioSequenceAC> list = mongoTemplate.find(query, BioSequenceAC.class, BIO_CONST.BIO_SEQUENCE_AC);
			for(BioSequenceAC ac :list){
				ac._id = ac._id.substring(0,ac._id.indexOf("_"));
				mongoTemplate.save(ac,"BIO_SEQUENCE");
			}
			// 翻页处理下一批
			pageable = pageable.next();
			query.with(pageable);
		}
		log.info("END");
		
	}
	@RequestMapping("/patentdistinct")
	public void patentdistinct(String country){
		Query query = new Query();
		if(!Strings.isNullOrEmpty(country)){
			query = Query.query(Criteria.where("country").is(country));
		}
		query.fields().include("_id").include("country");
		List<BioPatent> list = mongoTemplate.find(query, BioPatent.class, BIO_CONST.BIO_PATENT);
		Hashtable<String,BioPatent> set = new Hashtable<String,BioPatent>();
		log.info("Start");
		for(int i = 0 ;i<list.size();i++){
			BioPatent pno = list.get(i);
			long count = mongoTemplate.count(Query.query(Criteria.where("PNS").is(pno._id)), BIO_CONST.EXCHANGE_PNO);
			if( count > 1){
				pno.count = (int)count;
				mongoTemplate.insert(pno, "patentrepeat");
			}
		}

		log.info(""+set.size());
		log.info("END");
		
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
	public String excuteFasta(String filepath) throws Exception {
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
		log.info("start" + reader._filename);
		//RecordFasta record = reader.readBioSequenceObj();
		RecordFasta record = reader.readBioSequenceObjForamt02();
		List<RecordFasta> list = new ArrayList<RecordFasta>();
		//Integer.MAX_VALUE
		byte[] bytes = record.Seq.getBytes();
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
			System.out.println(i);
			if (!dict.containsKey(record._id)) {
				dict.put(record._id, record);
			}
			record = reader.readBioSequenceObjForamt02();
			if(i==1693){
				Files.append(record.Header, new File("D:\\em_rel_std_pln-1693.txt"), StandardCharsets.UTF_8);
				Files.append("\r\n", new File("D:\\em_rel_std_pln-1693.txt"), StandardCharsets.UTF_8);
				Files.append(record.Seq, new File("D:\\em_rel_std_pln-1693.txt"), StandardCharsets.UTF_8);
				
			}
			if(i>=1660000 && i <1670000){
				Files.append(record.Header, new File("D:\\em_rel_std_pln-1660000.txt"), StandardCharsets.UTF_8);
				Files.append("\r\n", new File("D:\\em_rel_std_pln-1660000.txt"), StandardCharsets.UTF_8);
				Files.append(record.Seq, new File("D:\\em_rel_std_pln-1660000.txt"), StandardCharsets.UTF_8);
				
			}
			if (i % 10000 == 0 || record == null) {
				if(i == 1670000){
					System.out.print("dd");
				}
				dict = new Hashtable<String, RecordFasta>();
				log.info("i:" + i);
			}

		}

		reader.close();

		Files.append(filepath.subSequence(0, filepath.length()), new File("D:\\over.txt"), StandardCharsets.UTF_8);
		log.info(filepath + ":处理数量:" + i + "插入数量:" + insertnum + "更新数量:" + updatenum + "跳过数量:" + ignorenum);
		log.info("总耗时:" + (System.currentTimeMillis() - start) + "ms");
		log.info("end");
		return filepath + "总耗时:" + (System.currentTimeMillis() - start) + "ms " + "处理数量:" + i + "插入数量:" + insertnum
				+ "更新数量:" + updatenum + "跳过数量:" + ignorenum;

	}
	/**
	 * 查找embl不存在fasta的数据
	 */
	@RequestMapping("/selectidfromfasta")
	public void selectidfromfasta(){
		Query query = new Query();
		query.fields().include("_id");
		int pagesize = 500000;
		int startpage = 0;
		Pageable pageable = new PageRequest(startpage, pagesize);
		query.with(pageable);
		log.info("Start:");
		long count = mongoTemplate.count(query, BIO_CONST.SOURCE_EMBL);
		int loopcount = (int) (count % pagesize == 0 ? count / pagesize : count / pagesize + 1);
		for (int i = 0; i < loopcount; i++) {
			// 取一批专利
			List<RecordEmbl> list = mongoTemplate.find(query, RecordEmbl.class, BIO_CONST.SOURCE_EMBL);
			HashSet<String> recordset = new HashSet<String>();
			for(RecordEmbl record: list){
				recordset.add(record._id);
			}
			Query queryfasta = Query.query(Criteria.where("RecID").in(recordset));
			queryfasta.fields().include("_id").include("RecID");
			List<RecordFasta> listFasta = mongoTemplate.find(queryfasta, RecordFasta.class, BIO_CONST.SOURCE_FASTA);
			//NRP_A09080 recid null
			for(RecordFasta fasta : listFasta){
				if(recordset.contains(fasta.RecID)){
					recordset.remove(fasta.RecID);
				}
			}
			log.info("i:" + (i+1)*pagesize);
			log.info("recordset.size():" + recordset.size());
			// 如果还有剩余，查找ID2
			if(recordset.size()>0){
				queryfasta = Query.query(Criteria.where("ID2").in(recordset));
				queryfasta.fields().include("_id").include("ID2");
				listFasta = mongoTemplate.find(queryfasta, RecordFasta.class, BIO_CONST.SOURCE_FASTA);
				for(RecordFasta fasta : listFasta){
					if(recordset.contains(fasta.ID2)){
						recordset.remove(fasta.ID2);
					}
				}

			}
			log.info("recordset.size():" + recordset.size());
			// 翻页处理下一批
			pageable = pageable.next();
			query.with(pageable);
			for(String recid :recordset){
				//System.out.println(recid);
				RecordEmbl rec = new RecordEmbl();
				rec._id=recid;
				mongoTemplate.save(rec, "emblhasnofasta");
				
			}

		}
		log.info("End:");

		
	}
	
	/**
	 * 查找biopatent中不在seqac中的号。
	 */
	@RequestMapping("/selectacfrombiopatent")
	public void selectacfrombiopatent(){
		Query query = new Query();
		query.fields().include("_id").include("SeqList").include("count");
		int pagesize = 1000;
		int startpage = 0;
		Pageable pageable = new PageRequest(startpage, pagesize);
		query.with(pageable);
		log.info("Start:");
		long count = mongoTemplate.count(query, BIO_CONST.BIO_PATENT);
		int loopcount = (int) (count % pagesize == 0 ? count / pagesize : count / pagesize + 1);
		for (int i = 0; i < loopcount; i++) {
			// 取一批专利
			List<BioPatent> list = mongoTemplate.find(query, BioPatent.class, BIO_CONST.BIO_PATENT);
			HashSet<String> recordset = new HashSet<String>();
			for(BioPatent record: list){
				recordset.add(record._id);
				Query queryfasta = Query.query(Criteria.where("_id").in(record.SeqList.keySet()));
				List<BioSequenceAC> listFasta = mongoTemplate.find(queryfasta, BioSequenceAC.class, BIO_CONST.BIO_SEQUENCE_AC);
				if(listFasta.size() == record.SeqList.size())
					continue;
				

			}
			Query queryfasta = Query.query(Criteria.where("RecID").in(recordset));
			queryfasta.fields().include("_id").include("RecID");
			List<RecordFasta> listFasta = mongoTemplate.find(queryfasta, RecordFasta.class, BIO_CONST.SOURCE_FASTA);
			//NRP_A09080 recid null
			for(RecordFasta fasta : listFasta){
				if(recordset.contains(fasta.RecID)){
					recordset.remove(fasta.RecID);
				}
			}
			log.info("i:" + (i+1)*pagesize);
			log.info("recordset.size():" + recordset.size());
			// 如果还有剩余，查找ID2
			if(recordset.size()>0){
				queryfasta = Query.query(Criteria.where("ID2").in(recordset));
				queryfasta.fields().include("_id").include("ID2");
				listFasta = mongoTemplate.find(queryfasta, RecordFasta.class, BIO_CONST.SOURCE_FASTA);
				for(RecordFasta fasta : listFasta){
					if(recordset.contains(fasta.ID2)){
						recordset.remove(fasta.ID2);
					}
				}

			}
			log.info("recordset.size():" + recordset.size());
			// 翻页处理下一批
			pageable = pageable.next();
			query.with(pageable);
			for(String recid :recordset){
				//System.out.println(recid);
				RecordEmbl rec = new RecordEmbl();
				rec._id=recid;
				mongoTemplate.save(rec, "emblhasnofasta");
				
			}

		}
		log.info("End:");

		
	}
	
	/**
	 *重新处理emblhasnofasta表中的记录对应的专利
	 */
	@RequestMapping("/reexcuteemblhasnofasta")
	public WriteResult reexcuteemblhasnofasta(){
		Query query = new Query();
		query.fields().include("_id");
		log.info("Start:");
		// 取一批专利
		List<RecordEmbl> list = mongoTemplate.find(query, RecordEmbl.class, "emblhasnofasta");
		HashSet<String> recordset = new HashSet<String>();
		for(RecordEmbl record: list){
			recordset.add(record._id);
		}
		Query queryembl = Query.query(Criteria.where("_id").in(recordset));
		queryembl.fields().include("_id").include("PNO");
		List<RecordEmbl> listembl = mongoTemplate.find(queryembl, RecordEmbl.class, BIO_CONST.SOURCE_EMBL);
		//NRP_A09080 recid null
		HashSet<String> pnodset = new HashSet<String>();
		for(RecordEmbl fasta : listembl){
			if(!pnodset.contains(fasta.PNO)){
				pnodset.add(fasta.PNO);
			}
		}

		
		// 更新状态和时间
		Update update = new Update();
		update.set("state", 0);
		WriteResult result =mongoTemplate.updateMulti(Query.query(Criteria.where("_id").in(pnodset)), update, BIO_CONST.EXCHANGE_PNO);
		log.info("END");

		return result;
	}
	/**
	 *查找fasta记录，embl中不包含的记录。
	 */
	@RequestMapping("/reexcutefastahasnoembl")
	public void reexcutefastahasnoembl(){
		Query query = new Query();
		query.fields().include("RecID").include("ID2");
		int pagesize = 500000;
		int startpage = 80;
		Pageable pageable = new PageRequest(startpage, pagesize);
		query.with(pageable);
		log.info("Start:");
		long count = mongoTemplate.count(query, BIO_CONST.SOURCE_FASTA);
		int loopcount = (int) (count % pagesize == 0 ? count / pagesize : count / pagesize + 1);
		for (int i = 80; i < loopcount; i++) {
			// 取一批专利
			List<RecordFasta> list = mongoTemplate.find(query, RecordFasta.class, BIO_CONST.SOURCE_FASTA);
			HashSet<String> recordset = new HashSet<String>();
			Hashtable<String,String> table = new Hashtable<String,String>();
			for(RecordFasta record: list){
				
				if(!record.RecID.startsWith("NRP") && !record.RecID.startsWith("NRN") && !table.containsKey(record.RecID)){
					table.put(record.RecID, record.ID2);
				}
				if(record.ID2 != null && !table.containsKey(record.ID2)){
					table.put(record.ID2, record.RecID);
				}
			}
			
			Query queryfasta = Query.query(Criteria.where("_id").in(table.keySet()));
			queryfasta.fields().include("_id").include("RecID");
			List<RecordEmbl> listFasta = mongoTemplate.find(queryfasta, RecordEmbl.class, BIO_CONST.SOURCE_EMBL);
			//NRP_A09080 recid null
			for(RecordEmbl fasta : listFasta){
				if(table.containsKey(fasta.RecID)){
					String id2 = table.get(fasta.RecID);
					table.remove(fasta.RecID);
					if(table.containsKey(id2)){
						table.remove(id2);
					}
				}
			}
			log.info("recordset.size():" + recordset.size());
			// 翻页处理下一批
			pageable = pageable.next();
			query.with(pageable);
			for(String recid :table.keySet()){
				System.out.println(recid);
				RecordFasta rec = new RecordFasta();
				rec._id=recid;
				mongoTemplate.save(rec, "fastahasnoembl");
				
			}

		}
		log.info("End:");
	}
	/**
	 *重新处理emblhasnofasta表中的记录对应的专利
	 */
	@RequestMapping("/reexcuteemblstate4")
	public WriteResult reexcuteemblstate4(){
	
		log.info("Start:");
		Query queryembl = Query.query(Criteria.where("state").is(4));
		queryembl.fields().include("_id").include("PNO");
		List<RecordEmbl> listembl = mongoTemplate.find(queryembl, RecordEmbl.class, BIO_CONST.SOURCE_EMBL);
		//NRP_A09080 recid null
		HashSet<String> pnodset = new HashSet<String>();
		HashSet<String> idset = new HashSet<String>();
		for(RecordEmbl fasta : listembl){
			idset.add(fasta._id);
			if(!pnodset.contains(fasta.PNO)){
				pnodset.add(fasta.PNO);
			}
		}

		
		// 更新状态和时间
		Update update = new Update();
		update.set("state", 0);
		WriteResult result =mongoTemplate.updateMulti(Query.query(Criteria.where("_id").in(idset)), update, BIO_CONST.SOURCE_EMBL);
		mongoTemplate.updateMulti(Query.query(Criteria.where("_id").in(pnodset)), update, BIO_CONST.EXCHANGE_PNO);
		log.info("END");

		return result;
	}
	/**
	 *查找没有匹配标准专利的记录
	 */
	@RequestMapping("/testlast")
	public WriteResult testlast(String country){
		log.info("Start:");
		Query query = Query.query(Criteria.where("StdInfo").is(null));
		if(!Strings.isNullOrEmpty(country)){
			query = Query.query((Criteria.where("StdInfo").is(null).and("country").is(country)));
		}
		List<ExchangePno> listpno = mongoTemplate.find(query, ExchangePno.class, BIO_CONST.EXCHANGE_PNO);
		//NRP_A09080 recid null
		HashSet<String> pnodset = new HashSet<String>();
		HashSet<String> idset = new HashSet<String>();
		int i = 0;
		for(ExchangePno pno : listpno){
			
			pno = EMBLUtil.getDocId(pno);
			if(pno.StdInfo == null){
				System.out.println(pno._id);
				i++;
			}
		}

		
		System.out.println(i);
		log.info("END");

		return null;
	}
	/**
	 *查找没有匹配标准专利的记录
	 * @throws IOException 
	 */
	@RequestMapping("/testpnsdiff")
	public WriteResult testpnsdiff(String country) throws IOException{
		log.info("Start:");
		Query query = Query.query(Criteria.where("StdInfo").is(null));
		//String[] arrays = {"WO1999024568A","WO1999027117A","WO1999026978A","WO1999028452A","WO1999028459A","WO1999028458A","WO1999029715A","WO1999031239A","WO1999031238A","WO1999031237A","WO1999031271A","WO1999032638A","WO1999032511A","WO1999033876A","WO1999033873A","WO1999033978A","WO1999033977A","WO1999033961A","WO1999035287A","WO1999034670A","WO1999036532A","WO1999037677A","WO1999038991A","WO1999038984A","WO1999039002A","WO1999040205A","WO1999040190A","WO1999040118A","WO1999040191A","WO1999040935A","WO1999042572A","WO1999043818A","WO1999043792A","WO1999043703A","WO1999043803A","WO1999045131A","WO1999045107A","WO1999046369A","WO1999046232A","WO1999046388A","WO1999046378A","WO1999046393A","WO1999048920A","WO1999048491A","WO1999048528A","WO1999048926A","WO1999050294A","WO1999050395A","WO1999050412A","WO1999050407A","WO1999050401A","WO1999050663A","WO1999050453A","WO1999051743A","WO1999051633A","WO1999051752A","WO1999051627A","WO1999053057A","WO1999053056A","WO1999053313A","WO1999053023A","WO1999054483A","WO1999054478A","WO1999054360A","WO1999054357A","WO1999054455A","WO1999055864A","WO1999055863A","WO1999055853A","WO1999055361A","WO1999057249A","WO1999057269A","WO1999057147A","WO1999057143A","WO1999058716A","WO1999058668A","WO1999058662A","WO1999060113A","WO1999060025A","WO1999059636A","WO1999060158A","WO1999060112A","WO1999062347A","WO1999063085A","WO1999062556A","WO1999063084A","WO1999064591A","WO1999066073A","WO1999066061A","WO1999067290A","WO1999067369A","WO1999067288A","WO2010007059A1","WO2010007118A1","WO2010007176A1","WO2010007464A1","WO2010006973A2","WO2010007031A2","WO2010008454A1","WO2010009353A1","WO2010009255A1","WO2010009337A2","WO2010008023A","WO2010006814A1","WO2010007093A1","WO2010009377A2","WO2010007063A1","WO2010007797A","WO2010011961A1","WO2010010551A1","WO2010011952A1","WO2010009856A1","WO2010010096A1","WO2010011845A1","WO2010014225A1","WO2010012948A1","WO2010012828A1","WO2010013012A1","WO2010013138A1","WO2010014248A1","WO2010013231A1","WO2010013071A1","WO2010016760A1","WO2010015938A1","WO2010015627A1","WO2010015709A1","WO2010015592A1","WO2010015929A1","WO2010016087A1","WO2010016071A1","WO2010016064A1","WO2010018731A1","WO2010020552A1","WO2010020638A1","WO2010020694A1","WO2010020645A1","WO2010020289A1","WO2010020767A1","WO2010020669A1","WO2010020647A1","WO2010020695A1","WO2010020777A2","WO2010020766A2","WO2010020657A1","WO2010020290A1","WO2010020618A1","WO2010020677A2","WO2010020868A1","WO2010020681A1","WO2010020676A1","WO2010020593A1","WO2010020494A1","WO2010020787A1","WO2010020619A1","WO2010030739A1","WO2010032762A"	,"WO2010032697A"	,"WO2010032696A"	,"WO2010032786A"	,"WO2010033925A1","WO2010031968A1","WO2010033227A1","WO2010034028A1","WO2010033220A1","WO2010034032A1","WO2010033215A1","WO2010033204A1","WO2010032059A1","WO2010031720A1","WO2010033862A1","WO2010033223A1","WO2010032061A1","WO2010031772A1","WO2010033222A1","WO2010031767A1","WO2010034029A1","WO2010033958A1","WO2010033248A1","WO2010032458A","WO2010032408A","WO2010032448A","WO2010035889A1","WO2010035686A","WO2010035107A2","WO2010035725A","WO2010035618A","WO2010035465A","WO2010035888A","WO2010035837A","WO2010035784A","WO2010035757A","WO2010035756A"};
		//String[] arrays = {"WO1997020059A1","WO1997038096A","WO1997038099A","WO1997039026A","WO1997040150A","WO1997041217A","WO1997042319A","WO1997043408A","WO1997043411A","WO1997045451A","WO1997046677A","WO1997046586A","WO1997046583A","WO1997046676A","WO1997047755A","WO1997047648A","WO1997049819A","WO1997049427A","WO1997049724A","WO1998001565A"};
		//query = Query.query(Criteria.where("_id").in(arrays));
		//String[] countryarrays = {"WO","EP","JP","US"};// 
		//String[] countryarrays = {"KR"};// DE GBok
		if(Strings.isNullOrEmpty(country)) country = "GB";
		//countryarrays = {"WO","EP","JP","US"};//DE
		query = Query.query(Criteria.where("country").in(country));
		//List<ExchangePno> listpno = mongoTemplate.find(query, ExchangePno.class, BIO_CONST.EXCHANGE_PNO);
		List<ExchangePno> listpno = mongoTemplate.find(query, ExchangePno.class, BIO_CONST.EXCHANGE_PNO);
		//NRP_A09080 recid null
		HashSet<String> pnodset = new HashSet<String>();
		HashSet<String> idset = new HashSet<String>();
		System.out.println("_id \t date \t newPns \t oldpns\t");
		String info = null;
		for(int i = 0;i<listpno.size();i++){
			ExchangePno pno = listpno.get(i);
			String oldpns = pno.PNS;
			pno = EMBLUtil.getDocId(pno);
			if(pno.StdInfo== null || !pno.PNS.equals(oldpns)){
				info = pno._id + "\t" + pno.date + "\t"+ pno.PNS + "\t" + oldpns;
				System.out.println(info);
				Files.append(info, new File("D:\\pnsdiff-KR.txt"), StandardCharsets.UTF_8);
				Files.append("\r\n", new File("D:\\pnsdiff-KR.txt"), StandardCharsets.UTF_8);

			}
			

		}

		log.info("END");

		return null;
	}
	
	/**
	 *查找没有匹配标准专利的记录
	 * @throws IOException 
	 */
	@RequestMapping("/testpnskrdouble")
	public WriteResult testpnsdouble(String country) throws IOException{
		log.info("Start:");
		Query query = Query.query(Criteria.where("StdInfo").is(null));
		//String[] arrays = {"WO1999024568A","WO1999027117A","WO1999026978A","WO1999028452A","WO1999028459A","WO1999028458A","WO1999029715A","WO1999031239A","WO1999031238A","WO1999031237A","WO1999031271A","WO1999032638A","WO1999032511A","WO1999033876A","WO1999033873A","WO1999033978A","WO1999033977A","WO1999033961A","WO1999035287A","WO1999034670A","WO1999036532A","WO1999037677A","WO1999038991A","WO1999038984A","WO1999039002A","WO1999040205A","WO1999040190A","WO1999040118A","WO1999040191A","WO1999040935A","WO1999042572A","WO1999043818A","WO1999043792A","WO1999043703A","WO1999043803A","WO1999045131A","WO1999045107A","WO1999046369A","WO1999046232A","WO1999046388A","WO1999046378A","WO1999046393A","WO1999048920A","WO1999048491A","WO1999048528A","WO1999048926A","WO1999050294A","WO1999050395A","WO1999050412A","WO1999050407A","WO1999050401A","WO1999050663A","WO1999050453A","WO1999051743A","WO1999051633A","WO1999051752A","WO1999051627A","WO1999053057A","WO1999053056A","WO1999053313A","WO1999053023A","WO1999054483A","WO1999054478A","WO1999054360A","WO1999054357A","WO1999054455A","WO1999055864A","WO1999055863A","WO1999055853A","WO1999055361A","WO1999057249A","WO1999057269A","WO1999057147A","WO1999057143A","WO1999058716A","WO1999058668A","WO1999058662A","WO1999060113A","WO1999060025A","WO1999059636A","WO1999060158A","WO1999060112A","WO1999062347A","WO1999063085A","WO1999062556A","WO1999063084A","WO1999064591A","WO1999066073A","WO1999066061A","WO1999067290A","WO1999067369A","WO1999067288A","WO2010007059A1","WO2010007118A1","WO2010007176A1","WO2010007464A1","WO2010006973A2","WO2010007031A2","WO2010008454A1","WO2010009353A1","WO2010009255A1","WO2010009337A2","WO2010008023A","WO2010006814A1","WO2010007093A1","WO2010009377A2","WO2010007063A1","WO2010007797A","WO2010011961A1","WO2010010551A1","WO2010011952A1","WO2010009856A1","WO2010010096A1","WO2010011845A1","WO2010014225A1","WO2010012948A1","WO2010012828A1","WO2010013012A1","WO2010013138A1","WO2010014248A1","WO2010013231A1","WO2010013071A1","WO2010016760A1","WO2010015938A1","WO2010015627A1","WO2010015709A1","WO2010015592A1","WO2010015929A1","WO2010016087A1","WO2010016071A1","WO2010016064A1","WO2010018731A1","WO2010020552A1","WO2010020638A1","WO2010020694A1","WO2010020645A1","WO2010020289A1","WO2010020767A1","WO2010020669A1","WO2010020647A1","WO2010020695A1","WO2010020777A2","WO2010020766A2","WO2010020657A1","WO2010020290A1","WO2010020618A1","WO2010020677A2","WO2010020868A1","WO2010020681A1","WO2010020676A1","WO2010020593A1","WO2010020494A1","WO2010020787A1","WO2010020619A1","WO2010030739A1","WO2010032762A"	,"WO2010032697A"	,"WO2010032696A"	,"WO2010032786A"	,"WO2010033925A1","WO2010031968A1","WO2010033227A1","WO2010034028A1","WO2010033220A1","WO2010034032A1","WO2010033215A1","WO2010033204A1","WO2010032059A1","WO2010031720A1","WO2010033862A1","WO2010033223A1","WO2010032061A1","WO2010031772A1","WO2010033222A1","WO2010031767A1","WO2010034029A1","WO2010033958A1","WO2010033248A1","WO2010032458A","WO2010032408A","WO2010032448A","WO2010035889A1","WO2010035686A","WO2010035107A2","WO2010035725A","WO2010035618A","WO2010035465A","WO2010035888A","WO2010035837A","WO2010035784A","WO2010035757A","WO2010035756A"};
		//String[] arrays = {"WO1997020059A1","WO1997038096A","WO1997038099A","WO1997039026A","WO1997040150A","WO1997041217A","WO1997042319A","WO1997043408A","WO1997043411A","WO1997045451A","WO1997046677A","WO1997046586A","WO1997046583A","WO1997046676A","WO1997047755A","WO1997047648A","WO1997049819A","WO1997049427A","WO1997049724A","WO1998001565A"};
		//query = Query.query(Criteria.where("_id").in(arrays));
		//String[] countryarrays = {"WO","EP","JP","US"};// 
		//String[] countryarrays = {"KR"};// DE GBok
		if(Strings.isNullOrEmpty(country)) country = "GB";
		//countryarrays = {"WO","EP","JP","US"};//DE
		query = Query.query(Criteria.where("country").in(country));
		//List<ExchangePno> listpno = mongoTemplate.find(query, ExchangePno.class, BIO_CONST.EXCHANGE_PNO);
		List<ExchangePno> listpno = mongoTemplate.find(query, ExchangePno.class, BIO_CONST.EXCHANGE_PNO);
		//NRP_A09080 recid null
		HashSet<String> pnodset = new HashSet<String>();
		HashSet<String> idset = new HashSet<String>();
		System.out.println("_id \t date \t newPns \t oldpns\t");
		String info = null;
		DocdbnumService service = new DocdbnumService();
		// wsimport -p org.tempuri -keep http://192.168.6.28/ipphdataservice/docdbnum_service.asmx?wsdl -extension
		DocdbnumServiceSoap soap = service.getDocdbnumServiceSoap();
		DocResult result = null;

		for(int i = 0;i<listpno.size();i++){
			ExchangePno pno = listpno.get(i);
			String oldpns = pno.PNS;
			result = EMBLUtil.getStd(pno.country, pno.docnum,"",  pno.date, 0, true, soap);
			if(result !=null)
			if(pno.StdInfo== null || !pno.PNS.equals(oldpns)){
				info = pno._id + "\t" + pno.date + "\t"+ pno.PNS + "\t" + oldpns;
				System.out.println(info);
				Files.append(info, new File("D:\\pnsdiff-KR.txt"), StandardCharsets.UTF_8);
				Files.append("\r\n", new File("D:\\pnsdiff-KR.txt"), StandardCharsets.UTF_8);

			}
			

		}

		log.info("END");

		return null;
	}
	/**
	 *查找没有匹配标准专利的记录
	 */
	@RequestMapping("/getpno")
	public WriteResult getpno(){
		log.info("Start:");
		Query query = Query.query(Criteria.where("StdInfo").is(null));
		//String[] arrays = {"WO1999024568A","WO1999027117A","WO1999026978A","WO1999028452A","WO1999028459A","WO1999028458A","WO1999029715A","WO1999031239A","WO1999031238A","WO1999031237A","WO1999031271A","WO1999032638A","WO1999032511A","WO1999033876A","WO1999033873A","WO1999033978A","WO1999033977A","WO1999033961A","WO1999035287A","WO1999034670A","WO1999036532A","WO1999037677A","WO1999038991A","WO1999038984A","WO1999039002A","WO1999040205A","WO1999040190A","WO1999040118A","WO1999040191A","WO1999040935A","WO1999042572A","WO1999043818A","WO1999043792A","WO1999043703A","WO1999043803A","WO1999045131A","WO1999045107A","WO1999046369A","WO1999046232A","WO1999046388A","WO1999046378A","WO1999046393A","WO1999048920A","WO1999048491A","WO1999048528A","WO1999048926A","WO1999050294A","WO1999050395A","WO1999050412A","WO1999050407A","WO1999050401A","WO1999050663A","WO1999050453A","WO1999051743A","WO1999051633A","WO1999051752A","WO1999051627A","WO1999053057A","WO1999053056A","WO1999053313A","WO1999053023A","WO1999054483A","WO1999054478A","WO1999054360A","WO1999054357A","WO1999054455A","WO1999055864A","WO1999055863A","WO1999055853A","WO1999055361A","WO1999057249A","WO1999057269A","WO1999057147A","WO1999057143A","WO1999058716A","WO1999058668A","WO1999058662A","WO1999060113A","WO1999060025A","WO1999059636A","WO1999060158A","WO1999060112A","WO1999062347A","WO1999063085A","WO1999062556A","WO1999063084A","WO1999064591A","WO1999066073A","WO1999066061A","WO1999067290A","WO1999067369A","WO1999067288A","WO2010007059A1","WO2010007118A1","WO2010007176A1","WO2010007464A1","WO2010006973A2","WO2010007031A2","WO2010008454A1","WO2010009353A1","WO2010009255A1","WO2010009337A2","WO2010008023A","WO2010006814A1","WO2010007093A1","WO2010009377A2","WO2010007063A1","WO2010007797A","WO2010011961A1","WO2010010551A1","WO2010011952A1","WO2010009856A1","WO2010010096A1","WO2010011845A1","WO2010014225A1","WO2010012948A1","WO2010012828A1","WO2010013012A1","WO2010013138A1","WO2010014248A1","WO2010013231A1","WO2010013071A1","WO2010016760A1","WO2010015938A1","WO2010015627A1","WO2010015709A1","WO2010015592A1","WO2010015929A1","WO2010016087A1","WO2010016071A1","WO2010016064A1","WO2010018731A1","WO2010020552A1","WO2010020638A1","WO2010020694A1","WO2010020645A1","WO2010020289A1","WO2010020767A1","WO2010020669A1","WO2010020647A1","WO2010020695A1","WO2010020777A2","WO2010020766A2","WO2010020657A1","WO2010020290A1","WO2010020618A1","WO2010020677A2","WO2010020868A1","WO2010020681A1","WO2010020676A1","WO2010020593A1","WO2010020494A1","WO2010020787A1","WO2010020619A1","WO2010030739A1","WO2010032762A"	,"WO2010032697A"	,"WO2010032696A"	,"WO2010032786A"	,"WO2010033925A1","WO2010031968A1","WO2010033227A1","WO2010034028A1","WO2010033220A1","WO2010034032A1","WO2010033215A1","WO2010033204A1","WO2010032059A1","WO2010031720A1","WO2010033862A1","WO2010033223A1","WO2010032061A1","WO2010031772A1","WO2010033222A1","WO2010031767A1","WO2010034029A1","WO2010033958A1","WO2010033248A1","WO2010032458A","WO2010032408A","WO2010032448A","WO2010035889A1","WO2010035686A","WO2010035107A2","WO2010035725A","WO2010035618A","WO2010035465A","WO2010035888A","WO2010035837A","WO2010035784A","WO2010035757A","WO2010035756A"};
		//query = Query.query(Criteria.where("_id").in(arrays));

		query = Query.query(Criteria.where("country").is("WO").and("StdInfo").is(null));
		List<ExchangePno> listpno = mongoTemplate.find(query, ExchangePno.class, BIO_CONST.EXCHANGE_PNO);
		//NRP_A09080 recid null
		HashSet<String> pnodset = new HashSet<String>();
		HashSet<String> idset = new HashSet<String>();
		int i = 0;
		System.out.println(listpno.size());
		for(ExchangePno pno : listpno){
			System.out.println(pno._id + "\t" + pno.date);
		}
		log.info("END");

		return null;
	}

	/**
	 * 针对各集合数据进行统计，便于生成报表需要的数据
	 * @throws Exception
	 */
	@RequestMapping("/report")
	public void report() throws Exception{
		// 做一个新表
		this.distinctpno(null);
		// Source
		long emblcount = mongoTemplate.count(null, BIO_CONST.SOURCE_EMBL);
		long emblnopnocount = mongoTemplate.count(Query.query(Criteria.where("PNO").is(null)), BIO_CONST.SOURCE_EMBL);
		long fastacount = mongoTemplate.count(null, BIO_CONST.SOURCE_FASTA);
		long pnocount = mongoTemplate.count(null, BIO_CONST.EXCHANGE_PNO);
		
		//Aggregation agg = new Aggregation();
		Aggregation agg = newAggregation(
			      match(Criteria.where("PNO").is(null)),
			      group("country").count().as("total"),
			      project("total").and("country").previousOperation(),
			      sort(Sort.Direction.DESC, "country")		 
			    );

	 
			    //Convert the aggregation result into a List
			    AggregationResults<HashMap> groupResults 
			      = mongoTemplate.aggregate(agg, BIO_CONST.EXCHANGE_PNO, HashMap.class);
			    List<HashMap> result = groupResults.getMappedResults();
			    for(HashMap map : result){
			    	String keyvalue = "";
			    	for(Object key : map.keySet()){
			    		keyvalue += "--" + key +"--"+ map.get(key);
			    	}
			    	System.out.println(keyvalue);
			    }
			    log.info("Aggregation End");
			    

				// 在F盘创建测试.xls文档，并在该文档中的第一个位置创建名称为第一页的工作表。
				WritableWorkbook book = Workbook.createWorkbook(new File("/soft/java/reprot-"+ datetime +".xls"));
				// 第0页作为统计页
				WritableSheet sheet0 = book.createSheet("概览", 0);
				// 第0行
				BioUtil.createSheetColumn(sheet0, 1, 0, "原始FASTA记录");
				BioUtil.createSheetColumn(sheet0, 2, 0, String.valueOf(fastacount));
				// 第1行
				BioUtil.createSheetColumn(sheet0, 1, 1, "原始-EMBL记录");
				BioUtil.createSheetColumn(sheet0, 2, 1, String.valueOf(emblcount));
				// 第2行
				BioUtil.createSheetColumn(sheet0, 1, 2, "原始专利号记录");
				BioUtil.createSheetColumn(sheet0, 2, 2, String.valueOf(pnocount));

				// 第3行
				BioUtil.createSheetColumn(sheet0, 1, 3, "导出专利记录");
				BioUtil.createSheetColumn(sheet0, 2, 3, String.valueOf(mongoTemplate.count(null, BIO_CONST.BIO_PATENT)));

				// 第4行
				BioUtil.createSheetColumn(sheet0, 1, 4, "导出序列记录");
				BioUtil.createSheetColumn(sheet0, 2, 4, String.valueOf(mongoTemplate.count(null, BIO_CONST.BIO_SEQUENCE_AC)));

				// 第10行开始  原始专利号记录-细览
				BioUtil.createSheetColumn(sheet0, 0, 10, "原始-EMBL记录-细览");

				BioUtil.createSheetColumn(sheet0, 1, 10, "超过30w");
				//long t = mongoTemplate.count(Query.query(Criteria.where("status").is(3)), BIO_CONST.SOURCE_EMBL);
				BioUtil.createSheetColumn(sheet0, 2, 10, String.valueOf(mongoTemplate.count(Query.query(Criteria.where("state").is(3)), BIO_CONST.SOURCE_EMBL)));
				
				BioUtil.createSheetColumn(sheet0, 1, 11, "未解析专利号");
				BioUtil.createSheetColumn(sheet0, 2, 11, String.valueOf(mongoTemplate.count(Query.query(Criteria.where("PNO").is(null)), BIO_CONST.SOURCE_EMBL)));


				BioUtil.createSheetColumn(sheet0, 1, 12, "未找到标准号");
				List<ExchangePno> list = mongoTemplate.find(Query.query(Criteria.where("StdInfo").is(null)), ExchangePno.class, BIO_CONST.EXCHANGE_PNO);
				List<String> listpnonopns = new ArrayList<String>();
				for(ExchangePno pno : list){
					listpnonopns.add(pno._id);
				}
				BioUtil.createSheetColumn(sheet0, 2, 12, String.valueOf(mongoTemplate.count(Query.query(Criteria.where("PNO").in(listpnonopns)), BIO_CONST.SOURCE_EMBL)));

				BioUtil.createSheetColumn(sheet0, 1, 13, "正常导出");
				BioUtil.createSheetColumn(sheet0, 2, 13, String.valueOf(mongoTemplate.count(Query.query(Criteria.where("state").is(1)), BIO_CONST.SOURCE_EMBL)));

				// 第20行开始  原始专利号记录-细览
				BioUtil.createSheetColumn(sheet0, 0, 20, "原始专利号记录-细览");
				BioUtil.createSheetColumn(sheet0, 1, 20, "超过30w");
				BioUtil.createSheetColumn(sheet0, 2, 20, String.valueOf(mongoTemplate.count(Query.query(Criteria.where("state").is(3)), BIO_CONST.EXCHANGE_PNO)));
				
				BioUtil.createSheetColumn(sheet0, 1, 21, "未找到标准号");
				BioUtil.createSheetColumn(sheet0, 2, 21, String.valueOf(mongoTemplate.count(Query.query(Criteria.where("StdInfo").is(null)), ExchangePno.class, BIO_CONST.EXCHANGE_PNO)));

				BioUtil.createSheetColumn(sheet0, 1, 22, "重复专利数");
				BioUtil.createSheetColumn(sheet0, 2, 22, String.valueOf(mongoTemplate.count(null, "pnorepeat")));

				BioUtil.createSheetColumn(sheet0, 1, 23, "正常导出专利数");
				BioUtil.createSheetColumn(sheet0, 2, 23, String.valueOf(mongoTemplate.count(null, BIO_CONST.BIO_PATENT)));
				//BioUtil.createSheetColumn(sheet0, 3, 23, String.valueOf(mongoTemplate.count(Query.query(Criteria.where("state").is(1)), BIO_CONST.EXCHANGE_PNO)));

				
				BioUtil.createSheetColumn(sheet0, 0, 26, "正常导出专利国别统计");
				BioUtil.createSheetColumn(sheet0, 1, 26, "country");
				BioUtil.createSheetColumn(sheet0, 2, 26, "total");
				Aggregation aggpatent = newAggregation(
					      //match(Criteria.where("PNO").is(null)),
					      group("country").count().as("total"),
					      project("total").and("country").previousOperation(),
					      sort(Sort.Direction.ASC, "country")		 
					    );

			    //Convert the aggregation result into a List
			    AggregationResults<HashMap> groupResultsPatent 
			      = mongoTemplate.aggregate(aggpatent, BIO_CONST.BIO_PATENT, HashMap.class);
			    //List<HashMap> result = groupResults.getMappedResults();
			    int i = 1;
			    for(HashMap map : groupResults.getMappedResults()){
					BioUtil.createSheetColumn(sheet0, 1, 26 + i, String.valueOf(map.get("country")));
					BioUtil.createSheetColumn(sheet0, 2, 26 + i, String.valueOf(map.get("total")));
					i++;
			    }

			    
				BioUtil.createSheetColumn(sheet0, 0, 26 + i + 2 , "正常导出序列数量");
				Aggregation aggsequence = newAggregation(
					      //match(Criteria.where("PNO").is(null)),
					      group("country").sum("count").as("total"),
					      project("total").and("country").previousOperation(),
					      sort(Sort.Direction.ASC, "country")		 
					    );

			    //Convert the aggregation result into a List
			    AggregationResults<HashMap> groupResultsSequence 
			      = mongoTemplate.aggregate(aggsequence, BIO_CONST.BIO_PATENT, HashMap.class);
			    //List<HashMap> result = groupResults.getMappedResults();
			    int j = 1;
			    for(HashMap map : groupResultsSequence.getMappedResults()){
					BioUtil.createSheetColumn(sheet0, 1, 26 + i + 2 + j, String.valueOf(map.get("country")));
					BioUtil.createSheetColumn(sheet0, 2, 26 + i + 2 + j, String.valueOf(map.get("total")));
					i++;
			    }

				book.write();
				book.close();

		//mongoTemplate.aggregate(aggregation, collectionName, outputType)
		// 将由于fasta变化导致的embl变化的状态更新
		//pnoExcuteFasta();
		// 将pno插入ExchangePNO
		//在embl数据入库之后，处理标准专利号之前的插件，用来补充处理一些没有解析出来的embl记录中的专利号
		//pnoGetEmbl();
		//获取EMBL原始专利号,并插入ExchangePNO表
	    log.info("report end");
	}
	

	
	/**
	 * 获取保证号码和日期都能匹配的申请信息或公开信息
	 * @param pno
	 * @throws IOException 
	 */
	private void testkr(ExchangePno pno){
		DocdbnumService service = new DocdbnumService();
		// wsimport -p org.tempuri -keep http://192.168.6.28/ipphdataservice/docdbnum_service.asmx?wsdl -extension
		DocdbnumServiceSoap soap = service.getDocdbnumServiceSoap();
		DocResult result = null;
		DocResult resultAN = null;
		DocResult resultPN = null;
		if(pno.country.equals("KR")){
			// KR 单独处理
			int num = Integer.parseInt(pno.docnum.substring(2,6));
			String docnum = pno.docnum.substring(2);
			//pno.exception = "KR:KR10->KR,AN";
			resultAN = EMBLUtil.getStd(pno.country, pno.docnum, null, pno.date ,2,true,soap);
			if(resultAN.getStdInfos() == null){
				resultAN = EMBLUtil.getStd(pno.country, docnum, null, pno.date ,2,true,soap);
			}
			
			resultPN = EMBLUtil.getStd(pno.country, pno.docnum, null, pno.date ,1,true,soap);
			if(resultPN.getStdInfos() == null){
				resultPN = EMBLUtil.getStd(pno.country, docnum, null, pno.date ,1,true,soap);
			}

			if(resultAN.getStdInfos() != null && resultPN.getStdInfos() != null ){
				StdInfo stdinfoAN = (resultAN.getStdInfos()).getStdInfo().get(0);
				StdInfo stdinfoPN = (resultPN.getStdInfos()).getStdInfo().get(0);
				pno.StdInfo = stdinfoAN;
				pno.StdInfo2 = stdinfoPN;
				mongoTemplate.save(pno,"pnsdoubleKR");
			}
		}
	}
	@RequestMapping("/testdouble")
	public void testdouble(String country) {
		Query query = Query.query(Criteria.where("StdInfo").ne(null));
		if(!Strings.isNullOrEmpty(country)){
			query = Query.query(Criteria.where("StdInfo").ne(null).and("country").is(country));
		}
		List<ExchangePno> list = mongoTemplate.find(query, ExchangePno.class, BIO_CONST.EXCHANGE_PNO);
		log.info("Start");
		for(int i = 0 ;i<list.size();i++){
			ExchangePno pno = list.get(i);
			testkr(pno);
		}

		log.info(""+list.size());
		log.info("END");
		
	}
}
