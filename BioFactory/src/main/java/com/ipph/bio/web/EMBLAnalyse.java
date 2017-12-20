package com.ipph.bio.web;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tempuri.StdInfo;

import com.google.common.base.Strings;
import com.hp.util.EMBLUtil;
import com.hp.util.MD5FileUtil;
import com.ipph.bio.model.BioFeature;
import com.ipph.bio.model.BioInfo;
import com.ipph.bio.model.BioPatent;
import com.ipph.bio.model.BioSequence;
import com.ipph.bio.model.BioSequenceAC;
import com.ipph.bio.model.ExchangePno;
import com.ipph.bio.model.MapSeqPat;
import com.ipph.bio.model.RecordEmbl;
import com.ipph.bio.model.RecordFasta;
import com.ipph.bio.service.EMBLReader;
import com.ipph.bio.service.FASTAReader;
import com.ipph.bio.util.BIO_CONST;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

@RestController
@Slf4j
@RequestMapping("/embltest")
public class EMBLAnalyse {
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
	 * @author jiahh 尝试使用专利为单位进行处理
	 * @param country
	 *            WO,EP,US,JP,KR,DE,GB,FR
	 * @param pnos
	 * @param oper
	 *            nin
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/embl3")
	public String embl3(String country, String pnos, String oper) throws Exception {
		// test start
		// mongoTemplate.updateMulti(null, new Update().set("state",
		// 0).set("TimeUpdate", datetime), BIO_CONST.EXCHANGE_PNO);
		// end
		if (Strings.isNullOrEmpty(oper))
			oper = "in";
		// 组装检索式 statt
		// {country:{$nin:["WO","EP","US","JP","KR","DE","GB","FR"]}}
		Criteria criteria = Criteria.where("StdInfo").ne(null);
		if (!Strings.isNullOrEmpty(country)) {
			String[] countryarr = country.split(",");
			if ("in".equals(oper)) {
				criteria.and("country").in(Arrays.asList(countryarr));//.and("state").is(0);
			} else {
				criteria.and("country").nin(Arrays.asList(countryarr));//.and("state").is(0);
			}
		}
		else if (!Strings.isNullOrEmpty(pnos)) {
			String[] pnoryarr = pnos.split(",");
			criteria.and("_id").in(Arrays.asList(pnoryarr));
		}else{
			criteria.and("state").is(0);
		}

		Query querypno = Query.query(criteria);
		// 组装检索式 end

		// 取所有需要处理的专利号
		List<ExchangePno> pnolist = mongoTemplate.find(querypno, ExchangePno.class, BIO_CONST.EXCHANGE_PNO);
		long start = System.currentTimeMillis();
		// for(ExchangePno pno : pnolist){
		ExchangePno pno = null;
		log.info(country + "start:");
		for (int index = 0; index < pnolist.size(); index++) {
			pno = pnolist.get(index);
			//log.info( "index-" + index);			
			if(index == 63){
				log.info( "index-" + index);			
			}
			try {
				this.excute(pno);
			} catch (Exception ex) {
				// save state ExchangePNO 导出到BioPatent失败
				mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(pno._id)),
						new Update().set("state", BIO_CONST.PNO_STATE_5), ExchangePno.class, BIO_CONST.EXCHANGE_PNO);
				log.error("导出到BioPatent失败" + pno._id + "--" + ex.getMessage());
			}
			if (index % 1000 == 0 || index == pnolist.size() - 1) {
				log.info(country + "index:" + index + "timecost:" + (System.currentTimeMillis() - start));
				start = System.currentTimeMillis();
			}

		}

		log.info(country + "end");
		return country + "-" + pnolist.size();
	}

	private void excute(ExchangePno pno) throws NoSuchAlgorithmException {
		WriteResult writeResult = null;
		Hashtable<String, RecordFasta> fastatable = null;
		Hashtable<String, RecordEmbl> embltable = null;
		//Set<String> fastaset = null;
		//Set<String> emblset = null;
		List<RecordEmbl> emblList = null;
		List<RecordFasta> fastas = null;
		BioPatent patent = null;
		List<BioSequenceAC> listSequenceac = null;
		List<BioSequenceAC> listac = null;
		Hashtable<String, BioSequenceAC> tableac = null;
		Hashtable<String, RecordEmbl> embltableClone = null;
		//Hashtable<String, BioSequence> tableseq = null;
		//BioSequence seq = null;
		BioSequenceAC seqac = null;

		// 取出相关embl记录，并去重
		Query query = Query.query(Criteria.where("PNO").is(pno._id));
		query.fields().include("_id").include("RecID");
		long emblcount = mongoTemplate.count(query, RecordEmbl.class, BIO_CONST.SOURCE_EMBL);
		if (emblcount == 0) {
			// 此情况不存在
			log.info("此专利不存在embl记录--" + pno._id);
			// save state ExchangePNO 此专利不存在embl记录，处理下一个专利 2
			writeResult = mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(pno._id).and("state").ne(BIO_CONST.PNO_STATE_2)),
					new Update().set("state", BIO_CONST.PNO_STATE_2), ExchangePno.class, BIO_CONST.EXCHANGE_PNO);
			return;
		}
		if (emblcount > 280000) {
			log.info("embl index over 280000--" + emblcount + "--" + pno._id);
			// save state ExchangePNO embl记录超过10000，处理下一个专利 3
			writeResult = mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(pno._id).and("state").ne(BIO_CONST.PNO_STATE_3)),
					new Update().set("state", BIO_CONST.PNO_STATE_3), ExchangePno.class, BIO_CONST.EXCHANGE_PNO);
			//  save state SOURCE_EMBL embl记录超过300000的所有embl记录
			writeResult = mongoTemplate.updateMulti(new Query(Criteria.where("PNO").is(pno._id).and("state").ne(BIO_CONST.STATE_3)),
					new Update().set("state", BIO_CONST.STATE_3).set("LastModified", datetime), RecordEmbl.class,
					BIO_CONST.SOURCE_EMBL);
			log.info("writeResult.getN():" + writeResult.getN());
			return;
		}
		fastatable = new Hashtable<String, RecordFasta>();
		embltable = new Hashtable<String, RecordEmbl>();
		// fastaset = new HashSet<String>();
		// emblset = new HashSet<String>();
		emblList = mongoTemplate.find(query, RecordEmbl.class, BIO_CONST.SOURCE_EMBL);
		for (RecordEmbl embl : emblList) {
			if (!embltable.containsKey(embl._id)) {
				embltable.put(embl._id, embl);
			}
		}
		emblList = null;
		// 取出和embl相关fasta记录并去重
		Query queryfasta = Query.query(Criteria.where("RecID").in(embltable.keySet()));
		queryfasta.fields().include("RecID").include("MD5").include("Seq");
		fastas = mongoTemplate.find(Query.query(Criteria.where("RecID").in(embltable.keySet())), RecordFasta.class,
				BIO_CONST.SOURCE_FASTA);
		if (fastas.size() == 0) {
			// TODO 不存在fastas记录 可以继续,可以从embl里面切
		}
		RecordFasta fasta = null;
		for (int i = 0; i < fastas.size(); i++) {
			fasta = fastas.get(i);
			if (!fastatable.containsKey(fasta.RecID)) {
				fastatable.put(fasta.RecID, fasta);
			}
		}
		queryfasta = null;
		embltableClone = (Hashtable<String, RecordEmbl>) embltable.clone();
		List<String> emblhasnofasta = new ArrayList<String>();
		List<String> emblcutseq = new ArrayList<String>();
		// 清理无效embl【fasta中不存在相关ac号的embl】
		for (RecordEmbl embl : embltableClone.values()) {
			// 如果fasta中没有embl指定的ac，则查找ＩＤ２
			if (!fastatable.containsKey(embl._id)) {
				RecordFasta id2 = mongoTemplate.findOne(Query.query(Criteria.where("ID2").in(embl._id)), RecordFasta.class,
						BIO_CONST.SOURCE_FASTA);
				if(id2!=null){
					fastatable.put(embl._id, id2);
				}else{
					// 如果还没有，拆分embl里面的。
					seqac = new BioSequenceAC();
					
					String seq = EMBLReader.readEMBLjiahhWithSeq(mongoTemplate.findById(embl._id, RecordEmbl.class,BIO_CONST.SOURCE_EMBL));
					if(!Strings.isNullOrEmpty(seq)){
						RecordFasta rec2 = new RecordFasta();
						rec2.Seq = seq;
						rec2.MD5 = MD5FileUtil.getMD5String(seq.toUpperCase().replace("\r", "").replace("\n", "").replace(" ", ""));
						rec2.RecID = embl._id;
						fastatable.put(embl._id, rec2);
						
						emblcutseq.add(embl._id);
					}else{		
						//还找不到移除embl
						embltable.remove(embl._id);
						emblhasnofasta.add(embl._id);
					}
				}
			}
		}
		if(emblcutseq.size() > 0){
			// save state SourceEMBL没有fasta记录  但是切出了序列内容12
			writeResult = mongoTemplate.updateFirst(new Query(Criteria.where("_id").in(emblcutseq).and("state").ne(BIO_CONST.STATE_12)),
					new Update().set("state", BIO_CONST.STATE_12).set("LastModified", datetime), RecordEmbl.class,
					BIO_CONST.SOURCE_EMBL);
		}
		// save state SourceEMBL没有fasta记录  但是切出了序列内容12
		if(emblhasnofasta.size() > 0){
			// save state SourceEMBL没有fasta记录 4
			writeResult = mongoTemplate.updateFirst(new Query(Criteria.where("_id").in(emblhasnofasta).and("state").ne(BIO_CONST.STATE_4)),
					new Update().set("state", BIO_CONST.STATE_4).set("LastModified", datetime), RecordEmbl.class,
					BIO_CONST.SOURCE_EMBL);
		}
		if(embltable.size() > 0){
			// save state SourceEMBL 导出成功
			writeResult = mongoTemplate.updateMulti(new Query(Criteria.where("_id").in(embltable.keySet()).and("state").ne(BIO_CONST.STATE_1)),
					new Update().set("state", BIO_CONST.STATE_1).set("LastModified", datetime), RecordEmbl.class,
					BIO_CONST.SOURCE_EMBL);
		}

		embltableClone = null;
		// 此专利的所有embl记录都没有匹配上fasta记录，可以操作下一patent了
		if (embltable.size() == 0) {
			// save state ExchangePNO 此专利的所有embl记录都没有匹配上fasta记录 处理下一个专利 4
			writeResult = mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(pno._id).and("state").ne(BIO_CONST.PNO_STATE_4)),
					new Update().set("state", BIO_CONST.PNO_STATE_4), ExchangePno.class, BIO_CONST.EXCHANGE_PNO);
			return;
		}
		// 根据RecID重新检索embl
		query = Query.query(Criteria.where("_id").in(embltable.keySet()));
		emblList = mongoTemplate.find(query, RecordEmbl.class, BIO_CONST.SOURCE_EMBL);
		embltable = new Hashtable<String, RecordEmbl>();
		for (RecordEmbl embl : emblList) {
			if (!embltable.containsKey(embl._id)) {
				embltable.put(embl._id, embl);
			}
		}
		emblList = null;
		// // 组织patent.SeqList 和bioSequence

		patent = new BioPatent();
		patent.country = pno.StdInfo.getSTDPUBCOUNTRY();
		patent.docNumber = pno.StdInfo.getSTDPUBNUM();
		patent.kind = pno.StdInfo.getSTDPUBKIND();

		patent._id = pno.PNS;
		patent.PNS = pno.PNS;
		patent.DocID = pno.PNS;
		patent.PNO = pno._id;
		patent.datePublication = pno.StdInfo.getSTDPUBDATE();
		patent.Format = "FASTA";
		patent.TimeCreate = datetime;
		listSequenceac = new ArrayList<BioSequenceAC>();
		//tableseq = new Hashtable<String, BioSequence>();
		

		// 判断是否存在
		BioPatent patentdb = mongoTemplate.findById(patent._id, BioPatent.class, BIO_CONST.BIO_PATENT);
		//
		patent.state = BIO_CONST.PATENT_STATE_0;

		if (patentdb != null) {
			patent.TimeCreate = patentdb.TimeCreate;
			patent.TimeUpdate = datetime;
			// 为了解决多个原始专利号对应一个标准专利号，如：US7083949A、US7083949--》US7083949B2 补充下列操作
			// 如果patent存在，则将seqlist取出来，将对应PNO的序列去掉，下面循环进行补充
			for(Map.Entry<String,String> entry :patentdb.SeqList.entrySet()){
				if(!patent.PNO.equals(entry.getValue())){
					patent.SeqList.put(entry.getKey(), entry.getValue());
				}
			}
		}
		// 将embl表中的记录装入patent.SeqList
		RecordFasta recordFasta = new RecordFasta();
		for (RecordEmbl embl : embltable.values()) {
			recordFasta = fastatable.get(embl._id);
			String key = recordFasta.MD5 + "_" + embl._id;
			if (!patent.SeqList.containsKey(key)) {
				/**** 暂时取消此表
				seq = new BioSequence();
				seq._id = recordFasta.MD5;
				seq.MD5 = recordFasta.MD5;
				seq.Seq = recordFasta.Seq;
				seq.length = recordFasta.Seq.replaceAll("\r", "").replaceAll("\n", "").length();
				seq.TimeCreate = datetime;
				tableseq.put(seq._id, seq);
				 **/
				seqac = new BioSequenceAC();
				seqac._id = key;
				seqac.AC = embl._id;
				seqac.PNO = patent.PNO;
				seqac.PNS = patent.PNS;
				seqac.MD5 = recordFasta.MD5;
				seqac.Seq = recordFasta.Seq;
				if (BIO_CONST.FASTA_FORMAT02.equals(recordFasta.Format)){
					String header = recordFasta.Header;
					String gn = header.substring(header.indexOf(" ", header.indexOf(" ") + 1));
					seqac.gn = gn;
				}
				seqac.TimeCreate = datetime;
				EMBLReader.readEMBLjiahhWithNoSeq(embl, seqac);
				if(seqac.length == 0 && recordFasta.Seq != null) {
					seqac.length = recordFasta.Seq.replaceAll("\r", "").replaceAll("\n", "").length();;
				}
				// 如果seq为空，说明没找到对应的fasta，则取embl的，同时计算MD5
				if(Strings.isNullOrEmpty(seqac.Seq)){
					seqac.Seq = EMBLReader.readEMBLjiahhWithSeq(embl);
					seqac.MD5 = MD5FileUtil.getMD5String(seqac.Seq.toUpperCase().replace("\r", "").replace("\n", "").replace(" ", ""));
				}
				// 如果type为空，更新sourceEMBL STATE_11
				if(Strings.isNullOrEmpty(seqac.type)){
					// sourceEmbl embl无法解析出type
					writeResult = mongoTemplate.updateFirst(new Query(Criteria.where("RecID").is(seqac.AC).and("state").ne(BIO_CONST.STATE_11)),
							new Update().set("state", BIO_CONST.STATE_11), RecordEmbl.class,
							BIO_CONST.SOURCE_EMBL);
				}else{
					listSequenceac.add(seqac);
					patent.SeqList.put(key, embl.PNO);
					patent.count = patent.SeqList.size();
				}
			}
		}

		listac = mongoTemplate.find(Query.query(Criteria.where("_id").in(patent.SeqList.keySet())),
				BioSequenceAC.class, BIO_CONST.BIO_SEQUENCE_AC);
		tableac = new Hashtable<String, BioSequenceAC>();
		for (BioSequenceAC ac : listac) {
			if (!tableac.containsKey(ac._id)) {
				tableac.put(ac._id, ac);
			}else{
				// TODO 测试程序 理论上首次入库不应该出现此分支
			}
		}


		int state = 0;
		// 判断patent是否发生变化 patent对象没有变化并且组装的bioSequenceList也没有变化才是没有变化
		for (BioSequenceAC ac : listSequenceac) {
			if (tableac.get(ac._id) == null) {
				mongoTemplate.save(ac, BIO_CONST.BIO_SEQUENCE_AC);
			} else if(!tableac.get(ac._id).equals(ac)){
				state = 1;
				ac.TimeUpdate = datetime;
				mongoTemplate.save(ac, BIO_CONST.BIO_SEQUENCE_AC);
			}
		}
		if (patentdb != null && state == 1) {
			patent.TimeUpdate = datetime;
			patent.state = BIO_CONST.PATENT_STATE_1; // 状态：更新
		}
		if (patentdb == null || state == 1 || !patentdb.equals(patent)) {
			mongoTemplate.save(patent, BIO_CONST.BIO_PATENT);
		}
		// save ExchangePNO state 成功处理 1 PNO导出成功 
		writeResult = mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(pno._id).and("state").ne(BIO_CONST.PNO_STATE_1)),
				new Update().set("state", BIO_CONST.PNO_STATE_1), ExchangePno.class, BIO_CONST.EXCHANGE_PNO);
		/****
		 * 暂时取消此表 // 更新一下seq Query querySeq =
		 * Query.query(Criteria.where("_id").in(tableseq.keySet()));
		 * querySeq.fields().include("_id"); List<BioSequence> existSeqs =
		 * mongoTemplate.find(querySeq, BioSequence.class,
		 * BIO_CONST.BIO_SEQUENCE);
		 * 
		 * for (BioSequence s : existSeqs) { tableseq.remove(s._id); } for
		 * (BioSequence s : tableseq.values()) { mongoTemplate.save(s,
		 * BIO_CONST.BIO_SEQUENCE); }
		 ****/

	}


	@RequestMapping("/updateState")
	public void updateAll() {
		try {
			mongoTemplate.updateMulti(new Query(Criteria.where("state").ne(0)),
					new Update().set("state", BIO_CONST.STATE_0), RecordFasta.class, BIO_CONST.SOURCE_FASTA);
			mongoTemplate.updateMulti(new Query(Criteria.where("state").ne(0)),
					new Update().set("state", BIO_CONST.STATE_0), RecordEmbl.class, BIO_CONST.SOURCE_EMBL);
			System.out.println("UPDATE STATE 0 END");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@RequestMapping("/updatePatent")
	public void updatePatent() {
		try {
			List<BioPatent> patents = mongoTemplate.findAll(BioPatent.class, "BioPatent");
			for (BioPatent p : patents) {
				p._id = p.DocID;
				mongoTemplate.save(p, "BioPatentlinl");
			}
			System.out.println("UPDATE PATENTS END");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@RequestMapping("/checkembl")
	public void checkEmbl(String id) {
		if(Strings.isNullOrEmpty(id)){
			return;
		}
		//id = "AAA00478";
		int size = 10000;
		Query query = Query.query(Criteria.where("_id").is(id));
		Pageable pageable = new PageRequest(0, size);
		query.with(pageable);
		long count = mongoTemplate.count(query, BIO_CONST.SOURCE_EMBL);
		long totalpage = (count + size - 1) / size;
		for (int i = 0; i < totalpage; i++) {
			List<RecordEmbl> embls = mongoTemplate.find(query, RecordEmbl.class, BIO_CONST.SOURCE_EMBL);
			for (RecordEmbl embl : embls) {
				BioSequenceAC bioSeqAC = new BioSequenceAC();
				try {
					EMBLReader.readEMBLjiahhWithNoSeq(embl, bioSeqAC);
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			}
			// 翻页处理下一批
			pageable = pageable.next();
			query.with(pageable);
			if (i % 100 == 0){
				System.out.println(i);
			}
		}
		System.out.println("END");
	}

	@RequestMapping("/checkPatent")
	public void checkPatent() {
		int size = 10000;
		Query query = Query.query(Criteria.where("state").is(1));
//		Pageable pageable = new PageRequest(0, size);
//		query.with(pageable);
//		long count = mongoTemplate.count(query, BIO_CONST.EXCHANGE_PNO);
//		long totalpage = (count + size - 1) / size;
//		for (int i = 0; i < totalpage; i++) {
			List<ExchangePno> embls = mongoTemplate.find(query, ExchangePno.class, BIO_CONST.EXCHANGE_PNO);
			List<BioPatent> patents = mongoTemplate.find(query, BioPatent.class, BIO_CONST.BIO_PATENT);
			// 翻页处理下一批
//			pageable = pageable.next();
//			query.with(pageable);
//			if (i % 100 == 0){
//				System.out.println(i);
//			}
//		}
		System.out.println("END");
	}



}
