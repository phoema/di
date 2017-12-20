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
 * 本Controller针对原始文件加工成成品数据的全流程处理
 * 
 * @author jiahh 2015年5月13日
 *
 */
@RestController
@Slf4j
@RequestMapping("/all")
public class AllDoController {

	@Autowired
	private RecordController recordController;
	@Autowired
	private EMBLAnalyse emblAnalyse;
	@Autowired
	private ExchangeController exchangeController;
	// yyyymmdd
	private long datetime = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date()));

	// /all/excute?fasta2folder=/soft/java/sourcedata/fasta2&emblfolder=/soft/java/sourcedata/embl
	/**
	 * 
	 * 外国生物序列数据统一处理
	 * @param fasta2folder fasta2原始数据文件夹，多个文件夹以分号隔开
	 * @param emblfolder embl原始数据文件夹，多个文件夹以分号隔开
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/excute")
	public String excute(String fasta2folder,String emblfolder) throws Exception {
		String ret = "";
		ret += "Start:" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "\r\n<br>";
		ret += "\r\n<br>";
		log.info("excuteFasta2Folder Start");
		// 装载fasta2原始数据 folder 如是数组以分号分开
		ret += recordController.excuteFasta2Folder(fasta2folder);
		log.info("AllDoController_fasta2folder:" + ret);
		log.info("excuteEmblFolder Start");
		// 装载EMBL原始数据folder 如是数组以分号分开
		ret += recordController.excuteEmblFolder(emblfolder);
		log.info("AllDoController_emblfolder:" + ret);
		log.info("pno Start");
		// 处理ExchangePno，包括追加、更新、标准化
		recordController.pno();
		
		log.info("embl3 Start WO,EP,US,JP,KR nin");
		// 尝试使用以国为单位，专利为单位进行处理，解析成出版社格式数据
		emblAnalyse.embl3("WO,EP,US,JP,KR", null, "nin");
		log.info("embl3 Start WO");
		emblAnalyse.embl3("WO", null, "in");
		log.info("embl3 Start EP");
		emblAnalyse.embl3("EP", null, "in");
		log.info("embl3 Start KR");
		emblAnalyse.embl3("KR", null, "in");
		log.info("embl3 Start US");
		emblAnalyse.embl3("US", null, "in");
		log.info("embl3 Start JP");
		emblAnalyse.embl3("JP", null, "in");
		
		// 交换数据生成 
		log.info("exchangeController Start WO,EP,US,JP,KR nin");
		exchangeController.excute("WO,EP,US,JP,KR","nin", null);
		log.info("exchangeController Start WO in");
		exchangeController.excute("WO", "in", null);
		log.info("exchangeController Start EP in");
		exchangeController.excute("EP","in", null);
		log.info("exchangeController Start US in");
		exchangeController.excute("US","in", null);
		log.info("exchangeController Start KR in");
		exchangeController.excute("KR","in", null);
		log.info("exchangeController Start JP in");
		exchangeController.excute("JP","in", null);
		
		ret += "\r\n<br>";
		ret += "End:" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		log.info("ALL End ");

		log.info("AllDoController:" + ret);
		log.info(ret);
		return ret;
	}
	
//	@RequestMapping("/test")
//	public String test() throws Exception {
//		exchangeController.excute("WO,EP,US,JP,KR","nin", null);
//		return null;
//
//	}
}
