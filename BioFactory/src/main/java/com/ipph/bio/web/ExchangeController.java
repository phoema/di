package com.ipph.bio.web;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.ipph.bio.ExchangeConfig;
import com.ipph.bio.file.FileUtil;
import com.ipph.bio.model.BioPatent;
import com.ipph.bio.model.BioSequenceAC;
import com.ipph.bio.model.ExchangeControl;
import com.ipph.bio.model.ExchangeControlFile;
import com.ipph.bio.model.ExchangeIndex;
import com.ipph.bio.model.ExchangePatentFileAttr;
import com.ipph.bio.model.ExchangeSequenceList;
import com.ipph.bio.service.ZipUtils;
import com.ipph.bio.util.BIO_CONST;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.thoughtworks.xstream.XStream;

/**
 * 本Controller针对原始文件进行初始的入库操作
 * 
 * @author jiahh 2015年5月13日
 *
 */
@RestController
@Slf4j
@RequestMapping("/exchange")
public class ExchangeController {

	@Autowired
	private ExchangeConfig config;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoClient mongo;
	// yyyymmdd
	private long datetime = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date()));

	@RequestMapping("/")
	public String help() {
		String help = "";
		help += "/excute \r\n" + "执行符合条件的记录的文件导出 \r\n</br>";
		return help;
	}

	/**
	 *
	 * 初始化测试数据
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/init")
	public void init(String path) throws Exception {
//		BioPatent patent = new BioPatent();
//		long docnumber = 1054462;
//
//		for (int i = 0; i < 12; i++) {
//			patent.DocID = "WO" + (docnumber++) + "A2";
//			patent.PatID = String.valueOf(i + 1);
//			patent.country = "WO";
//			// patent.SeqList = "100000001";
//			patent.state = 0;
//			patent._id = patent.DocID;
//
//			//mongoTemplate.save(patent, BIO_CONST.BIO_PATENT);
//			mongoTemplate.remove(Query.query(Criteria.where("DocID").is(patent._id)),  BIO_CONST.BIO_PATENT);
//		}
//		mongoTemplate.remove(Query.query(Criteria.where("_id").in("100000001","100000002")),  BIO_CONST.BIO_SEQUENCE);
//
//		BioSequence sequence = new BioSequence();
//		sequence.IDS = "NRP00000001";
//		sequence._id = "100000001";
//		sequence.type = "DNA";
//		sequence.organism = "线粒体";
//		sequence.Seq = "XHSDAVFTDXYXKQXAVKKYLXLX";
//		mongoTemplate.save(sequence, BIO_CONST.BIO_SEQUENCE);
//
//		BioSequence sequence2 = new BioSequence();
//		sequence2.IDS = "NRP00000002";
//		sequence2._id = "100000002";
//		sequence2.type = "PRT";
//		sequence2.organism = "Hepatitis B virus";
//		sequence2.Seq = "AGCKNFFWKTFTSC";
//		mongoTemplate.save(sequence2, BIO_CONST.BIO_SEQUENCE);
//
//		BioFeature feature = new BioFeature();
//		feature.FeatID = 11111111;
//		feature.SeqID = "100000001";
//		feature.location = "(1)..(324)";
//		feature.keywords = "CDS";
//		feature.other = "AMIDATION";
//		mongoTemplate.save(feature, BIO_CONST.BIO_SEQUENCE);
//
//		feature.FeatID = 11111112;
//		feature.SeqID = "100000001";
//		feature.location = "(6)..(732)";
//		feature.keywords = "CDS";
//		feature.other = "蚓激酶F-Ⅱ全基因DNA";
//		mongoTemplate.save(feature, BIO_CONST.BIO_FEATURE);
//
//		feature.FeatID = 11111113;
//		feature.SeqID = "100000002";
//		feature.location = "(1)..(324)";
//		feature.keywords = "CDS";
//		feature.other = "AMIDATION";
//		mongoTemplate.save(feature, BIO_CONST.BIO_FEATURE);
//
//		feature.FeatID = 11111114;
//		feature.SeqID = "100000002";
//		feature.location = "(6)..(732)";
//		feature.keywords = "CDS";
//		feature.other = "蚓激酶F-Ⅱ全基因DNA";
//		mongoTemplate.save(feature, BIO_CONST.BIO_FEATURE);;
	}
	@RequestMapping("/test")
	public String test() throws Exception {

		return config.basedirFasta;
	}
	/**
	 *
	 * 交换数据生成 注意：此函数不应该多线程并发
	 * docids=GB2244272A,KR19990027632A,LU83206A1
	 * GB2244272A,KR19990027632A,LU83206A1
	 * WO2010128465A1,US8299318B2,
	 * @param path
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/excute")
	public String excute(String country, String oper,String docids) throws Exception {
		long start = System.currentTimeMillis();
		Query query = null;
		if (Strings.isNullOrEmpty(oper))
			oper = "in";
		// 按国家导出
		if(!Strings.isNullOrEmpty(country)){
			query = new Query();
			if ("in".equals(oper)) {
				query.addCriteria(Criteria.where("country").in(Arrays.asList(country.split(","))));
			} else {
				query.addCriteria(Criteria.where("country").nin(Arrays.asList(country.split(","))));
			}
		}else if(!Strings.isNullOrEmpty(docids)){
			// 按号码导出
			if ("in".equals(oper)) {
				query = Query.query(Criteria.where("_id").in(Arrays.asList(docids.split(","))));
			} else {
				query = Query.query(Criteria.where("_id").nin(Arrays.asList(docids.split(","))));
			}
		}else{
			// 增量导出
			query = Query.query(Criteria.where("state").in(BIO_CONST.PATENT_STATE_0, BIO_CONST.PATENT_STATE_1));
		}
		
		// TODO  TEMP 要求只提供2010年之后的数据
		//query = new Query();
		//query = Query.query(Criteria.where("datePublication").gt("20091231"));
//		Sort.Order
		//query.with(new Sort(Direction.ASC,"country","datePublication"));
		//query.with(new Sort(Direction.ASC,"datePublication"));
		// TEMP

		// 判断根目录是否存在
		File rootfile = new File(config.basedirFasta);
		if (!rootfile.exists()) {
			Files.createParentDirs(rootfile);
			rootfile.mkdirs();
		}
		// ZipUtil zip=new ZipUtil();
		// 每个索引文件所包含的专利数
		int pagesize = config.indexPatcnt;
//		pagesize = 50;
		Pageable pageable = new PageRequest(0, pagesize);
		query.with(pageable);

		long count = mongoTemplate.count(query, BioPatent.class, BIO_CONST.BIO_PATENT);
		int loopcount = (int) (count % pagesize == 0 ? count / pagesize : count / pagesize + 1);
		int j = 0;

		ExchangeControl control = new ExchangeControl();
		control.filelist = new ArrayList<ExchangeControlFile>();
		for (j = 0; j < loopcount; j++) {
			log.info("ok:" + (j+1)+"--"+pagesize);
			// 取一批专利
			List<BioPatent> patentlist = mongoTemplate.find(query, BioPatent.class, BIO_CONST.BIO_PATENT);
			List<BioPatent> copylist = new ArrayList<BioPatent>();
			// 取消： 组装专利列表中所包含的所有专利号（国别+文献号+文献类型），生成的MD5值为索引文件的名称。
			// 创建临时文件夹
			File dirname = Files.createTempDir();
			int allamount = 0;
			// 处理专利内容
			for (int size = 0; size < patentlist.size();size ++) {
				BioPatent patent = patentlist.get(size);
				copylist.add(patent);
				// 数据主题
				patent.topic = BIO_CONST.TOPIC;
				patent.status = "CU";
				//patent.path = patent.country + File.separator + patent.PNS + ".txt";
				patent.path = dirname.getName() + File.separator + patent.country;
				// 转换fasta
				patent.file = new ExchangePatentFileAttr();
				patent.file.seqlist = this.createFastaFile(patent, dirname);
				patent.file.filename = patent._id + ".txt";
				patent.file.filetype = "TXT";
				patent.file.section = "SEQ";
				// 计算allamount
				if(Integer.parseInt(patent.file.seqlist.amount)  > 0){
					allamount += Integer.parseInt(patent.file.seqlist.amount);
					for(BioSequenceAC ac : patent.file.seqlist.seqlist){
						if(ac.features != null && ac.features.size() > 0){
							allamount += ac.features.size();
						}
					}
				}

				// 如果专利的序列列表和序列特征列表数量不固定，导致生成的文件体积差异较大，这里平均一下
				if(patentlist.size() == size + 1 || allamount > 60000){
					control.filelist.add(this.createIndexFile(null,dirname, copylist));
					// 删除临时文件夹
					FileUtil.deleteFile(dirname);
					dirname = Files.createTempDir();

					allamount = 0;
					copylist = null;
					copylist = new ArrayList<BioPatent>();
					System.gc();
				}
				patent.SeqList = null;
			}
			// 删除临时文件夹
			FileUtil.deleteFile(dirname);

			// 翻页处理下一批
			pageable = pageable.next();
			query.with(pageable);
		}
		// 0：需等待数据文件完成 1：可以启动数据装载工作
		control.dowork = "1";
		// BIOLOGY：生物序列数据
		control.type = BIO_CONST.EXCHANGE_CONTROLTYPE;
		// 生成控制文件
		File file = new File(config.basedirFasta + File.separator + "IPPHDB_CONTROL_" + String.valueOf(datetime) + UUID.randomUUID().toString()
				+ ".xml");

		// Files.write(new
		// StringBuilder().append(BIO_CONST.XML_HEADER).append(BIO_CONST.CHAR_ENTER).append(xstream.toXML(control)).toString().getBytes(StandardCharsets.UTF_8),
		// file);
		XStream xstream = new XStream();
		xstream.autodetectAnnotations(true);

		Files.write(xstream.toXML(control).getBytes(StandardCharsets.UTF_8), file);
		long end = System.currentTimeMillis();
		// 更新状态和时间
		Update update = new Update();
		update.set("state", BIO_CONST.PATENT_STATE_9);
		update.set("TimeUpdate", datetime);
		update.set("TimeExchange", datetime);
		WriteResult result = mongoTemplate.updateMulti(query, update, BIO_CONST.BIO_PATENT);

		log.info("END:OK-timecost:" + (end - start) + "ms");
		return "OK-timecost:" + (end - start) + "ms";
	}

	/**
	 * 
	 * @param patent
	 * @param dirname
	 * @return
	 * @throws IOException
	 */
	private ExchangeSequenceList createFastaFile(BioPatent patent, File dirname) throws IOException{
		patent.file.seqlist = new ExchangeSequenceList();
		// 获取seqlist大小
		Query seqquery = Query.query(Criteria.where("_id").in(patent.SeqList.keySet()));
		seqquery.fields().exclude("Seq");
		long seqcount = mongoTemplate.count(seqquery, BioSequenceAC.class, BIO_CONST.BIO_SEQUENCE_AC);
		patent.file.seqlist.amount = String.valueOf(seqcount);
		// 组装专利序列列表
		patent.file.seqlist.seqlist = mongoTemplate.find(seqquery, BioSequenceAC.class, BIO_CONST.BIO_SEQUENCE_AC);
		if (seqcount > 0) {
			String country = Strings.isNullOrEmpty(patent.country) ? "" : File.separator + patent.country;

			File file = new File(dirname.getPath() + country + File.separator + patent.DocID + ".txt");
			if(file.exists()){
				file.delete();
			}
			// 生成专利序列文件
			Files.createParentDirs(file);

			StringBuilder build = new StringBuilder();
			int seqpagesize = 1000;
			Pageable pageableseq = new PageRequest(0, seqpagesize);
			seqquery.fields().include("Seq");
			seqquery.with(pageableseq);

			int loopseqcount = (int) (seqcount % seqpagesize == 0 ? seqcount / seqpagesize : seqcount / seqpagesize + 1);
			for (int seqnum = 0; seqnum < loopseqcount; seqnum++) {
				List<BioSequenceAC> seqlist = mongoTemplate.find(seqquery, BioSequenceAC.class, BIO_CONST.BIO_SEQUENCE_AC);
				// 组装专利序列内容
				for (int seqsize = 0;seqsize<seqlist.size(); seqsize++) {
					BioSequenceAC sequence = seqlist.get(seqsize);
					// TODO 替换指定专利的AC号
					// sequence.AC = patent.SeqList.get(sequence._id);
					build.append(">").append(sequence._id).append(BIO_CONST.CHAR_ENTER).append(sequence.Seq);
					if (!sequence.Seq.endsWith(BIO_CONST.CHAR_ENTER)) {
						build.append(BIO_CONST.CHAR_ENTER);
					}
					// 处理特征:去除不属于当前AC的值
//					if (sequence.features != null && sequence.features.size() > 0) {
//						//allamount += sequence.features.size();
//
//					}
					//  个别专利对应的序列过大，为了防止内存溢出，这里特殊处理一下
					if(build.length() > 100000 || seqsize == seqlist.size()-1){
						Files.append(build, file, StandardCharsets.UTF_8);
						build = new StringBuilder();
					}
				}
				// 翻页处理下一批
				pageableseq = pageableseq.next();
				seqquery.with(pageableseq);

			}
			file = null;
		}
		return patent.file.seqlist;
	}
	/**
	 * 创建索引文件，同时删除临时文件夹
	 * @throws Exception 
	 */
	private ExchangeControlFile createIndexFile(XStream xstream, File dirname,List<BioPatent> list) throws Exception{
		// ZipUtil zip=new ZipUtil();
		XStream xstream1 = new XStream();
		xstream1.autodetectAnnotations(true);
		// 压缩 生成专利序列压缩文件
		String indexFilename = dirname.getName();
		File zipfile = new File(config.basedirFasta + File.separator + indexFilename + ".zip");
		ZipUtils.zip(dirname, zipfile);
		
		ExchangeIndex index = new ExchangeIndex();

		index.file = zipfile.getName();
		index.dateExchange = String.valueOf(datetime);
		index.dateProduced = String.valueOf(datetime);
		index.patcnt = String.valueOf(list.size());
		index.filecnt = String.valueOf(list.size());
		// index.size = (zipfile.length()/1024) + "K";
		index.size = String.valueOf(zipfile.length());
		index.md5 = DigestUtils.md5DigestAsHex(Files.toByteArray(zipfile));
		index.status = "CU";

		index.doclist = list;
		
		// 生成索引文件
		File file = new File(config.basedirFasta + File.separator + "" + indexFilename + ".xml");
//		StringBuilder bulider = new StringBuilder().append(BIO_CONST.XML_HEADER).append(BIO_CONST.CHAR_ENTER);
//		Files.write(bulider.append(xstream.toXML(index)).toString().getBytes(StandardCharsets.UTF_8), file);
		//byte[] bytes = xstream.toXML(index).getBytes(StandardCharsets.UTF_8);
		//Files.write(bytes, file);
		FileWriter writer = new FileWriter(file);
		xstream1.toXML(index, writer);
		writer.flush();
		writer.close();
		ExchangeControlFile controlfile = new ExchangeControlFile();
		String filename = file.getName();
		int listsize = list.size();
		controlfile.filename = filename;
		controlfile.patcnt = String.valueOf(listsize);
		controlfile.status = "CU";
		controlfile.section = "TXT";
		controlfile.sequence = "1";
		controlfile.md5 = DigestUtils.md5DigestAsHex(Files.toByteArray(file));

		zipfile = null;
		file = null;
		writer = null;
		return controlfile;
	}
}
