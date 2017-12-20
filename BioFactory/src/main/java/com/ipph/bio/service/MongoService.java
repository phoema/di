package com.ipph.bio.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryOperators;
import com.mongodb.WriteResult;



@Component("mongoService")
@Slf4j
public class MongoService {
	public int _state = 3;
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private MongoClient mongo;


	public int queryMongoData(String collectionName,String key, String value) {
		DBObject dbObject = new BasicDBObject(key, value);
		DBCursor curs = mongoTemplate.getCollection(collectionName).find(dbObject);
		try {
			while (curs.hasNext()) {
				// sb.append("query result-------" + curs.next()+"\n");
				curs.next();
			}
			if (curs.count() > 0) {
				return 2;
			} else if (curs.count() == 0) {
				return 0;
			}
			// return sb.toString();
		} catch (Exception e) {
			System.err.println("err_rid" + key);
			e.printStackTrace();
			return 1;
		} finally {
			curs.close();
		}
		return -1;
	}

	public void queryAll(String collectionName) {
		// db游标
		DBCursor cur =  mongoTemplate.getCollection(collectionName).find();
		while (cur.hasNext()) {
			System.out.println(cur.next());
		}
	}

	public DBObject querySomeOne(String collectionName) {
		// db游标
		DBCursor cur = mongoTemplate.getCollection(collectionName).find();
		while (cur.hasNext()) {
			DBObject obj = cur.next();
			return obj;
		}
		return null;
	}

	public int removeMongoData(String collectionName,String key, String rid) {
		int result = mongoTemplate.getCollection(collectionName).remove(
				new BasicDBObject(key, new ObjectId(rid))).getN();
		return result;
	}

	public void addRecord(String collectionName,String fileName) {
		FileInputStream fis = null;
		BufferedReader reader = null;
		try {
			fis = new FileInputStream(new File(fileName));
			reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"),
					5 * 1024 * 1024);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String tempString = "";
		BasicDBObject bd = new BasicDBObject();
		List<DBObject> listdatas = new ArrayList<DBObject>();
		try {
			while ((tempString = reader.readLine()) != null) {
				if (!tempString.equals("<patent_article>")
						&& !tempString.equals("</patent_article>")) {
					String field = tempString.substring(
							tempString.indexOf("<") + 1,
							tempString.indexOf(">"));
					String value = tempString.substring(
							tempString.indexOf(">") + 1,
							tempString.lastIndexOf("</"));
					bd.append(field, value);
				} else if (tempString.equals("</patent_article>")) {
					listdatas.add(bd);
					bd = new BasicDBObject();
				}
				// if (listdatas.size() >= 1000) {
				// collection.insert(listdatas);
				// listdatas.clear();
				// }
			}
			System.out.println(listdatas.size() + "   " + listdatas.get(0));
			if (listdatas.size() > 0) {
				mongoTemplate.getCollection(collectionName).insert(listdatas);
			}
			// collection.insert(listdatas, WriteConcern.REPLICAS_SAFE);
			reader.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 新增数据库及集合
	 * 
	 * @param dbName
	 *            新增数据库名称
	 * @param collectionName
	 *            新增数据库集合名称
	 * @return
	 */
	public boolean createDB(String dbName, String collectionName) {
		try {
			
			
			DB db = mongo.getDB(dbName);
			DBCollection collection = db.getCollection(collectionName);
			collection.find();
			return true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 新建MongoDB，调用getDb，如果没有则自动创建
	 * 
	 * @param dbName
	 */
	public void createDB(String dbName) {

		DB db = mongo.getDB(dbName);

	}

	/**
	 * 提供数据库名称、集合名称，如果没有则自动创建
	 * 
	 * @param dbName
	 * @param collectionName
	 */
	public void createCollection(String dbName, String collectionName, Map map) {
		DB db = mongo.getDB(dbName);
		DBObject options = new BasicDBObject();
		if (map != null)
			options.putAll(map);
		// TODO test data start
		// options.put("capped", false);
		// options.put("autoIndexId", true);
		// options.put("dropDups", true);
		// options.put("unique", true);
		// test data end
		// BasicDBObject oper = new BasicDBObject("rid", 1);
		DBCollection conn = db.createCollection(collectionName, options);

		List<DBObject> list = conn.getIndexInfo();
	}

	/**
	 * 提供数据库名称、集合名称，进行删除
	 * 
	 * @param dbName
	 * @param collectionName
	 */
	public void dropCollection(String dbName, String collectionName) {
		DB db = mongo.getDB(dbName);
		db.getCollection(collectionName).drop();
	}

	/**
	 * 删除索引
	 * 
	 * @param dbName
	 * @param collectionName
	 * @param indexName
	 */
	public void dropIndex(String dbName, String collectionName, String indexName) {
		DB db = mongo.getDB(dbName);
		DBObject options = new BasicDBObject();
		DBCollection conn = db.getCollection(collectionName);
		conn.dropIndex(indexName);
	}

	/**
	 * 获取索引信息
	 * 
	 * @param dbName
	 * @param collectionName
	 * @return
	 */
	public List<DBObject> getIndex(String dbName, String collectionName) {
		DB db = mongo.getDB(dbName);
		DBObject options = new BasicDBObject();
		DBCollection conn = db.getCollection(collectionName);
		return conn.getIndexInfo();
	}

	/**
	 * 插入数据，如果提供了_id,且数据库存在重复的，则替换，否则插入 如果没有提供_id，默认插入
	 * 
	 * @param dbName
	 * @param collectionName
	 */
	public void insert(String dbName, String collectionName,
			List<DBObject> documents) {
		DB db = mongo.getDB(dbName);
		DBCollection conn = db.getCollection(collectionName);
		if (documents != null && documents.size() > 0) {
			WriteResult result = conn.insert(documents);
		}
	}

	/**
	 * 提供数据库名称、集合名称，进行记录更新操作 要求必须有_id
	 * 
	 * @param dbName
	 * @param collectionName
	 */
	public void save(String dbName, String collectionName,
			List<BasicDBObject> documents) {
		DB db = mongo.getDB(dbName);
		DBCollection conn = db.getCollection(collectionName);
		for (BasicDBObject document : documents) {
			conn.save(document);
			// conn.update(query, update, upsert, multi)

		}
	}

	/**
	 * TODO 风险、执行删除数据库操作
	 * 
	 * @param dbName
	 */
	public void dropDatabase(String dbName) {
		mongo.dropDatabase(dbName);
	}

	/**
	 * 提供数据库名称、集合名称，进行记录删除操作 要求必须有_id
	 * 
	 * @param dbName
	 * @param collectionName
	 */
	public void delete(String dbName, String collectionName, String[] rids) {
		DB db = mongo.getDB(dbName);
		DBCollection conn = db.getCollection(collectionName);
		for (String rid : rids) {
			BasicDBObject bd = new BasicDBObject();
			bd.append("_id", rid);
			conn.remove(bd);
		}
	}

	/**
	 * 根据_id查询数据
	 * @param dbName
	 * @param collectionName
	 * @param key
	 * @param values
	 * @return
	 */
	public List<Map<String, Object>> queryRecords(String dbName,
			String collectionName, String key, Object[] values) {
		DB db = mongo.getDB(dbName);
		DBCollection conn = db.getCollection(collectionName);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		DBObject dbObject = new BasicDBObject();
		BasicDBObject query = new BasicDBObject(QueryOperators.IN, values);
		dbObject.put(key, query); // rid
		DBCursor curs = conn.find(dbObject);
		try {
			while (curs.hasNext()) {
				DBObject obj = curs.next();
				Map<String, Object> map = new HashMap<String, Object>();
				for (String column : obj.keySet()) {
					map.put(column, obj.get(column));
				}
				list.add(map);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			curs.close();
		}
		return null;
	}


	/**
	 * 根据_id查询数据
	 * @param dbName
	 * @param collectionName
	 * @param key
	 * @param values
	 * @return
	 */
	public <T> List<T> queryRecordsPage(String collectionName ,int start, int end, Query query,Class<T> entityClass) {

		//int start = (pageNo - 1) * pageSize;;  
		query.skip(start);
		query.limit(end - start);
		return mongoTemplate.find(query, entityClass, collectionName);

	}

}
