package com.di.web;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * 本Controller针对原始文件进行初始的入库操作
 * 
 * @author jiahh 2015年5月13日
 *
 */
@RestController
@Slf4j
@RequestMapping("/mongo")
public class MongoController {

	@Autowired
	private MongoTemplate mongoTemplate;
//	@Autowired
//	private MongoClient mongo;
	// yyyymmdd
	private long datetime = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date()));

	@RequestMapping("/")
	public String help() {
		String help = "";
		help += "/embl \r\n" + "将指定位置的embl文件入库 \r\n</br>" + "/fasta \r\n" + "将指定位置的fasta文件入库 \r\n";
		return help;
	}

	/**
	 * 针对各集合数据进行统计，便于生成报表需要的数据
	 * groupkey:docInfo.pdb 或 docInfo.py
	 * searchkey: docInfo.pdb
	 * searchvalue:CNA0
	 * @throws Exception
	 */
	@RequestMapping("/group")
	public List<HashMap> report(String groupkey, String searchkey, String searchvalue) throws Exception {
		log.info("start");

		if (Strings.isNullOrEmpty(groupkey)) {
			groupkey = "docInfo.pdb";
		}

		// Source
		// long emblcount = mongoTemplate.count(null, "appealInfo");
		// System.out.println(emblcount);
		// long emblnopnocount =
		// mongoTemplate.count(Query.query(Criteria.where("PNO").is(null)),
		// BIO_CONST.SOURCE_EMBL);
		// long fastacount = mongoTemplate.count(null, BIO_CONST.SOURCE_FASTA);
		// long pnocount = mongoTemplate.count(null, BIO_CONST.EXCHANGE_PNO);

		// Aggregation agg = new Aggregation();
		Criteria criteria = new Criteria();
		if (!Strings.isNullOrEmpty(searchkey)) {
			String[] keys = searchkey.split(";");
			String[] values = searchvalue.split(";");
			criteria = Criteria.where(keys[0]).is(values[0]);
			if(keys.length > 1){
				for(int i = 1;i<keys.length;i++){
					criteria.and(keys[i]).is(values[i]);
				}
			}
			
			//agg.match(criteria);
		}
		Aggregation agg = newAggregation(
				match(criteria),
				group(groupkey).count().as("total"), project("total").and(groupkey).previousOperation(),
				sort(Sort.Direction.DESC, groupkey));

		// Convert the aggregation result into a List
		AggregationResults<HashMap> groupResults = mongoTemplate.aggregate(agg, "patent", HashMap.class);
		List<HashMap> result = groupResults.getMappedResults();
		for (HashMap map : result) {
			String keyvalue = "";
			for (Object key : map.keySet()) {
				keyvalue += "--" + key + "--" + map.get(key);
			}
			System.out.println(keyvalue);
		}
		log.info("end");
		return result;

		// mongoTemplate.aggregate(aggregation, collectionName, outputType)
		// 将由于fasta变化导致的embl变化的状态更新
		// pnoExcuteFasta();
		// 将pno插入ExchangePNO
		// 在embl数据入库之后，处理标准专利号之前的插件，用来补充处理一些没有解析出来的embl记录中的专利号
		// pnoGetEmbl();
		// 获取EMBL原始专利号,并插入ExchangePNO表
	}

	/**
	 * 针对各集合数据进行统计，便于生成报表需要的数据
	 * groupkey:docInfo.pdb 或 docInfo.py
	 * searchkey: docInfo.pdb
	 * searchvalue:CNA0
	 * http://localhost:8083/mongo/group_trade?groupkey=FD&searchkey=tmdb&searchvalue=US
	 * @throws Exception
	 */
	@RequestMapping("/group_trade")
	public List<HashMap> report_trade(String groupkey, String searchkey, String searchvalue) throws Exception {
		log.info("start");

		if (Strings.isNullOrEmpty(groupkey)) {
			groupkey = "tradeMark.tmdb";
		}
		Criteria criteria = new Criteria();
		if (!Strings.isNullOrEmpty(searchkey)) {
			String[] keys = searchkey.split(";");
			String[] values = searchvalue.split(";");
			criteria = Criteria.where(keys[0]).is(values[0]);
			if(keys.length > 1){
				for(int i = 1;i<keys.length;i++){
					criteria.and(keys[i]).is(values[i]);
				}
			}
		}
		Aggregation agg = newAggregation(
				match(criteria),
				group(groupkey).count().as("total"), project("total").and(groupkey).previousOperation(),
				sort(Sort.Direction.DESC, groupkey));

		// Convert the aggregation result into a List
		AggregationResults<HashMap> groupResults = mongoTemplate.aggregate(agg, "trademark", HashMap.class);
		List<HashMap> result = groupResults.getMappedResults();
		for (HashMap map : result) {
			String keyvalue = "";
			for (Object key : map.keySet()) {
				keyvalue += "--" + key + "--" + map.get(key);
			}
			System.out.println(keyvalue);
		}
		log.info("end");
		return result;
	}

	/**
	 * 针对各集合数据进行统计，便于生成报表需要的数据 searchkey=docInfo.pdb;docInfo.pd&searchvalue=CNB0;20001101
	 * groupkey:docInfo.pdb 或 docInfo.py
	 * searchkey: docInfo.pdb
	 * searchvalue:CNA0
	 * @throws Exception
	 */
	@RequestMapping("/search")
	public List<DBObject> search(String searchkey, String searchvalue) throws Exception {
		log.info("start");
		Criteria criteria = new Criteria();
		if (!Strings.isNullOrEmpty(searchkey)) {
			String[] keys = searchkey.split(";");
			String[] values = searchvalue.split(";");
			criteria = Criteria.where(keys[0]).is(values[0]);
			if(keys.length > 1){
				for(int i = 1;i<keys.length;i++){
					criteria.and(keys[i]).is(values[i]);
				}
			}
			
			//agg.match(criteria);
		}
		
		Query query = Query.query(criteria);
		query.fields().include("_id");
		long count = mongoTemplate.count(query, "patent");
		List<DBObject> result = mongoTemplate.find(query, DBObject.class,"patent");
		/*******Test Start****/
//		for (DBObject map : result) {
//			String keyvalue = "";
//			for (String key : map.keySet()) {
//				keyvalue += "--" + key + "--" + map.get(key);
//			}
//			System.out.println(keyvalue);
//		}
		/*******Test End****/

		log.info("end");
		return result;

		// mongoTemplate.aggregate(aggregation, collectionName, outputType)
		// 将由于fasta变化导致的embl变化的状态更新
		// pnoExcuteFasta();
		// 将pno插入ExchangePNO
		// 在embl数据入库之后，处理标准专利号之前的插件，用来补充处理一些没有解析出来的embl记录中的专利号
		// pnoGetEmbl();
		// 获取EMBL原始专利号,并插入ExchangePNO表
	}
	@RequestMapping("/count")
	public long count(String searchkey, String searchvalue) throws Exception {
		Criteria criteria = new Criteria();
		if (!Strings.isNullOrEmpty(searchkey)) {
			String[] keys = searchkey.split(";");
			String[] values = searchvalue.split(";");
			criteria = Criteria.where(keys[0]).is(values[0]);
			if(keys.length > 1){
				for(int i = 1;i<keys.length;i++){
					criteria.and(keys[i]).is(values[i]);
				}
			}
			
			//agg.match(criteria);
		}
		
		Query query = Query.query(criteria);
		query.fields().include("_id");
		long count = mongoTemplate.count(query, "patent");
		return count;
	}

}
