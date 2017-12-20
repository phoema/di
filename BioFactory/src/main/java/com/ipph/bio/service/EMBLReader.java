package com.ipph.bio.service;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Strings;
import com.hp.util.EMBLUtil;
import com.hp.util.MD5FileUtil;
import com.ipph.bio.model.BioFeature;
import com.ipph.bio.model.BioSequenceAC;
import com.ipph.bio.model.RecordEmbl;

@Slf4j
public class EMBLReader {
	private String basePath = "D:\\林林\\工作文件";
	//group[1]通过上述sequence拆分方式，在一个特征值序列中，标识符“FT”中，提取第4个字符开始取值，取到空格为结束。
	//group[3]通过上述sequence拆分方式，在一个特征值序列中，标识符“FT”中，当第4个字符有值时，则提取这个值+空格之后的内容。
	static Pattern reg_keywords_location = Pattern.compile("^(.*?)(\\s+)(.*?)$");
	// jiahh 20160801 增加单引号的规则 取group[1]
	static Pattern reg_mol_type = Pattern.compile("mol_type=[\"'](.+)[\"']");

	/**
	 * 
	 * @param path
	 * @throws NoSuchAlgorithmException 
	 */
//	public static void readEMBLjiahh(RecordEmbl embl, BioSequenceAC bioSeqAC) throws NoSuchAlgorithmException {
//		String document = embl.Content;
//		String[] tempStrings = document.split("\r\n");
//		Map<String, String> map = new HashMap<String, String>();
//		BioFeature bioFeature = null;
//		for (String tempString : tempStrings) {
//			if (tempString.startsWith("ID")) {
//				String[] arr = tempString.split("   ");
//				if (arr.length > 0) {
//					// System.out.println(arr[0]);
//				}
//				// 七条数据
//				String[] arr1 = arr[1].split(";");
//				if (arr1.length == 7) {
//					//ID   A00022; SV 1; linear; protein; PRT; SYN; 14 AA.
//					bioSeqAC.length = Integer.parseInt((arr1[6].split(" ")[1]));
//					bioSeqAC.srcType = arr1[3].trim();
//					// TODO:....
//				} else {
//					// System.out.println("ID数据项错误:" + tempString);
//					// bioSeqAC.state = BIO_CONST.STATE_3;
//					// 取得各分号之间的内容  ID   AAA00478       STANDARD;       PRT;    87 AA.
//					String[] fenhao = tempString.split(";");
//					bioSeqAC.length = Integer.parseInt((fenhao[2].trim().split(" ")[0]));
//					bioSeqAC.srcType = fenhao[1].trim();
////					if ("PRT".equals(bioSeqAC.srcType)){
////						bioSeqAC.srcType = "protein";
////					}
//				}
//			} else {
//				if (!tempString.startsWith("XX") && !tempString.startsWith("//")) {
//					String key = tempString.substring(0, 2);
//					String value = tempString.length() > 5 ? tempString.substring(5) : "";
//					// System.out.println(key + "=" + value);
//					setKeyValue(map, key, value, tempString);
//				}
//			}
//		}
//		/**
//		  jiahh:20150812 新版规则对长度的解析取消此部分
//		// length RP
//		String rp = map.get("RP");
//		if (rp != null) {
//			Pattern pattern = Pattern.compile("(\\d+)-(\\d+)");
//			Matcher matcher = pattern.matcher(rp);
//			if (matcher.find()) {
//				String match = matcher.group();
//				String[] arr = match.split("-");
//				bioSeqAC.length = Integer.parseInt((arr[1]));
//			}
//		}
//		**/
//
//		// TODO:type FT FH
//		String count = map.get("count");
//		int num = count != null ? Integer.parseInt(count) : 0;
//		for (int i = 1; i <= num; i++) {
//			bioFeature = new BioFeature();
//			String location = map.get("location" + i);
//			String ft = map.get("FT" + i);
//			if (location != null) {
//				bioFeature.sequence = i + "";
//				location = location.trim().replace("\r\n", "");
//				Pattern pattern = Pattern.compile("(\\d+)..(\\d+)");
//				Matcher matcher = pattern.matcher(location);
//				if (matcher.find()) {
//					String match = matcher.group();
//					int pos = match.indexOf("..");
//					if (pos != -1) {
//						bioFeature.beg_pos = Integer.parseInt(match.substring(0, pos));
//						bioFeature.end_pos = Integer.parseInt(match.substring(pos + 2));
//					}
//				}
//				bioFeature.location = location;
//				bioFeature.keywords = map.get("keywords" + i);
//				bioFeature.content = map.get("FTTotal" + i);
//				bioFeature.crc = EMBLUtil.getCRC32(bioFeature.content);
//			}
//			if (ft != null) {
//				bioFeature.other = ft.replace("\r\n", "\n");
//			}
//			// mol_type
//			if (ft != null && ft.contains("mol_type=")) {
//				int start = ft.indexOf("mol_type=") + "mol_type=".length();
//				String type = ft.substring(start + 1, ft.indexOf("\"", start + 1));
//				bioSeqAC.srcType = type;
//				bioFeature.mol_type = type;
//			}
//			Hashtable<String, String> types = EMBLUtil.types;
//			bioSeqAC.type = types.get(bioSeqAC.srcType);
//			//
//			if (ft != null && ft.contains("db_xref=")) {
//				int start = ft.indexOf("db_xref=") + "db_xref=".length();
//				String type = ft.substring(start + 1, ft.indexOf("\"", start + 1));
//				bioFeature.db_xref = type;
//			}
//			/**
//			 organism 从OS中取，取消此方式
//			if (ft != null && ft.contains("organism=")) {
//				int start = ft.indexOf("organism=") + "organism=".length();
//				String organism = ft.substring(start + 1, ft.indexOf("\"", start + 1));
//				bioSeqAC.organism = organism;
//			}
//			**/
//			bioFeature.TimeCreate = bioSeqAC.TimeCreate;
//			bioFeature.AC = bioSeqAC.AC;
//			bioFeature.SeqID = bioSeqAC.MD5;
//			String id = MD5FileUtil.getMD5String(bioFeature.SeqID + "_" + bioFeature.location + "_" + bioFeature.AC
//					+ "_" + bioFeature.crc);
//			bioFeature._id = id;
//			bioSeqAC.features.add(bioFeature);
//		}
//
//		// gn
//		String de = map.get("DE");
//		if (de != null){
//			if (de.indexOf(":") != -1){
//				bioSeqAC.gn = de.substring(de.indexOf(":") + 1).trim();
//			} else if (bioSeqAC.gn != null && de.indexOf("from patent") != -1){
//				
//			} else {
//				bioSeqAC.gn = map.get("DE").trim();
//			}
//		}
//		//organism 来源生物体
//		String organism = map.get("OS");
//		if(!Strings.isNullOrEmpty(organism)){
//			bioSeqAC.organism = organism;
//		}
//
//		// SQ
//		if (bioSeqAC.Seq == null || "".equals(bioSeqAC.Seq)){
//			bioSeqAC.Seq = map.get("  ");
//			bioSeqAC.MD5 = MD5FileUtil.getMD5String(bioSeqAC.Seq.toUpperCase().replace("\r", "").replace("\n", "").replace(" ", ""));
//		}
//		// og 标识符“OG”中的所有内容，去掉首尾格式。
//		bioSeqAC.og = map.get("OG");
//		document = null;
//		tempStrings = null;
//		map = null;
//	}
	/**
	 * 
	 * @param path
	 * @throws NoSuchAlgorithmException 
	 */
	public static void readEMBLjiahhWithNoSeq(RecordEmbl embl, BioSequenceAC bioSeqAC) throws NoSuchAlgorithmException {
		String document = embl.Content;
		String[] tempStrings = document.split("\r\n");
		Map<String, String> map = new HashMap<String, String>();
		BioFeature bioFeature = null;
		for (String tempString : tempStrings) {
			if (tempString.startsWith("ID")) {
				String[] arr = tempString.split("   ");
				if (arr.length > 0) {
					// System.out.println(arr[0]);
				}
				// 七条数据
				String[] arr1 = arr[1].split(";");
				if (arr1.length == 7) {
					//ID   A00022; SV 1; linear; protein; PRT; SYN; 14 AA.
					bioSeqAC.length = Integer.parseInt((arr1[6].split(" ")[1]));
					bioSeqAC.srcType = arr1[3].trim();
					// TODO:....
				} else {
					// System.out.println("ID数据项错误:" + tempString);
					// bioSeqAC.state = BIO_CONST.STATE_3;
					// 取得各分号之间的内容  ID   AAA00478       STANDARD;       PRT;    87 AA.
					String[] fenhao = tempString.split(";");
					bioSeqAC.length = Integer.parseInt((fenhao[2].trim().split(" ")[0]));
					bioSeqAC.srcType = fenhao[1].trim();
//					if ("PRT".equals(bioSeqAC.srcType)){
//						bioSeqAC.srcType = "protein";
//					}
				}
			} else {
				if (!tempString.startsWith("XX") && !tempString.startsWith("//") && !tempString.startsWith("  ")) {
					String key = tempString.substring(0, 2);
					String value = tempString.length() > 5 ? tempString.substring(5) : "";
					// System.out.println(key + "=" + value);
					setKeyValue(map, key, value, tempString);
				}
			}
		}
		/**
		  jiahh:20150812 新版规则对长度的解析取消此部分
		// length RP
		String rp = map.get("RP");
		if (rp != null) {
			Pattern pattern = Pattern.compile("(\\d+)-(\\d+)");
			Matcher matcher = pattern.matcher(rp);
			if (matcher.find()) {
				String match = matcher.group();
				String[] arr = match.split("-");
				bioSeqAC.length = Integer.parseInt((arr[1]));
			}
		}
		**/

		// TODO:type FT FH
		String count = map.get("count");
		int num = count != null ? Integer.parseInt(count) : 0;
		for (int i = 1; i <= num; i++) {
			bioFeature = new BioFeature();
			String location = map.get("location" + i);
			String ft = map.get("FT" + i);
			if (location != null) {
				bioFeature.sequence = i + "";
				location = location.trim().replace("\r\n", "");
				Pattern pattern = Pattern.compile("(\\d+)..(\\d+)");
				Matcher matcher = pattern.matcher(location);
				if (matcher.find()) {
					String match = matcher.group();
					int pos = match.indexOf("..");
					if (pos != -1) {
						bioFeature.beg_pos = Integer.parseInt(match.substring(0, pos));
						bioFeature.end_pos = Integer.parseInt(match.substring(pos + 2));
					}
				}else{
					//log.debug(embl._id);
				}
				bioFeature.location = location;
				bioFeature.keywords = map.get("keywords" + i);
				bioFeature.content = map.get("FTTotal" + i);
				bioFeature.crc = EMBLUtil.getCRC32(bioFeature.content);
			}
			if (ft != null) {
				bioFeature.other = ft.replace("\r\n", "\n");
			}
			// mol_type 
			///mol_type="protein"
			///mol_type='DNA' 发现单引号规则 ID   HW643034 PNO JP2014520784A
//			if (ft != null && ft.contains("mol_type=")) {
//				int start = ft.indexOf("mol_type=") + "mol_type=".length();
//				String type = ft.substring(start + 1, ft.indexOf("\"", start + 1));
//				bioSeqAC.srcType = type;
//				bioFeature.mol_type = type;
//			}
			if (ft != null && ft.contains("mol_type=")) {
				// 使用正在 mol_type=".+"|mol_type='.+'
				Matcher matcher = reg_mol_type.matcher(ft);
				if (matcher.find()) {
					String type = matcher.group(1);
					bioSeqAC.srcType = type;
					bioFeature.mol_type = type;
				}
			}
			Hashtable<String, String> types = EMBLUtil.types;
			bioSeqAC.type = types.get(bioSeqAC.srcType);
			//
			if (ft != null && ft.contains("db_xref=")) {
				int start = ft.indexOf("db_xref=") + "db_xref=".length();
				String type = ft.substring(start + 1, ft.indexOf("\"", start + 1));
				bioFeature.db_xref = type;
			}
			/**
			 organism 从OS中取，取消此方式
			if (ft != null && ft.contains("organism=")) {
				int start = ft.indexOf("organism=") + "organism=".length();
				String organism = ft.substring(start + 1, ft.indexOf("\"", start + 1));
				bioSeqAC.organism = organism;
			}
			**/
			bioFeature.TimeCreate = bioSeqAC.TimeCreate;
			bioFeature.AC = bioSeqAC.AC;
			bioFeature.SeqID = bioSeqAC.MD5;
			String id = MD5FileUtil.getMD5String(bioFeature.SeqID + "_" + bioFeature.location + "_" + bioFeature.AC
					+ "_" + bioFeature.crc);
			bioFeature._id = id;
			bioSeqAC.features.add(bioFeature);
		}
		// comments 备注
		{
			String comments = null;
			String comments_CC = map.get("comments_CC");
			String comments_FH = map.get("comments_FH");
			String comments_FT = map.get("comments_FT");
			if(comments_FH != null && comments_FT != null){
				if(comments_CC != null){
					comments = comments_CC + "\n" + comments_FH+ "\n" + comments_FT;
				}else{
					comments = comments_FH+ "\n" + comments_FT;
				}
			}else{
				comments = comments_CC;
			}
			bioSeqAC.comments = comments;
		}
		// gn
		String de = map.get("DE");
		if (de != null){
			//20161202 抽检后修改规则 此处找王昕确认，取DE之后的所有内容 AC:A27484 A27482 A27483
			//from patent 若有该序列ID的FASTA2文件，取FASTA2中描述信息，没有该序列ID的FASTA2文件，取标示符“DE”
			if (bioSeqAC.gn != null && de.indexOf("from patent") != -1){
				
			} else {
				bioSeqAC.gn = map.get("DE").trim();
			}

		}
		//organism 来源生物体
		String organism = map.get("OS");
		if(!Strings.isNullOrEmpty(organism)){
			bioSeqAC.organism = organism;
		}
//		20160407 jiahh 去掉Seq的获取
//		// SQ
//		if (bioSeqAC.Seq == null || "".equals(bioSeqAC.Seq)){
//			bioSeqAC.Seq = map.get("  ");
//			bioSeqAC.MD5 = MD5FileUtil.getMD5String(bioSeqAC.Seq.toUpperCase().replace("\r", "").replace("\n", "").replace(" ", ""));
//		}
		// og 标识符“OG”中的所有内容，去掉首尾格式。
		bioSeqAC.og = map.get("OG");
		document = null;
		tempStrings = null;
		map = null;
	}
	
	
	/**
	 * 有的序列内容过长，为提高效率，必须解析的才解析
	 * @param embl
	 * @return
	 */
	public static String readEMBLjiahhWithSeq(RecordEmbl embl){
		String document = embl.Content;
		String[] tempStrings = document.split("\r\n");
		Map<String, String> map = new HashMap<String, String>();
		for (String tempString : tempStrings) {
			if (tempString.startsWith("  ")) {
				String key = tempString.substring(0, 2);
				String value = tempString.length() > 5 ? tempString.substring(5) : "";
				// System.out.println(key + "=" + value);
				setKeyValue(map, key, value, tempString);
			}
		}

		document = null;
		tempStrings = null;
		return map.get("  ");
	}
	/**
	 * 
	 * @param path
	 *            ID A00022; SV 1; linear; protein; PRT; SYN; 14 AA.
	 */
//	public void readEMBL2(String path) {
//		RecordEmbl embl = new RecordEmbl();
//		RecordFasta sequence = new RecordFasta();
//		BioSequenceAC bs = new BioSequenceAC();
//		BioFeature bf = new BioFeature();
//		FileInputStream fis = null;
//		BufferedReader reader = null;
//		try {
//			fis = new FileInputStream(new File(path));
//			reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"), 5 * 1024 * 1024);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		String tempString = "";
//		StringBuffer seq = new StringBuffer();
//		try {
//			StringBuffer sb = new StringBuffer();
//			Map<String, String> map = new HashMap<String, String>();
//			while ((tempString = reader.readLine()) != null) {
//				sb.append(tempString + "\r\n");
//				if (tempString.startsWith("ID")) {
//					if (!"".equals(sequence.RecID)) {
//						// System.out.println("path=" + path.replace(basePath,
//						// ""));
//						// System.out.println("ID=" + bioSeq.ID + "  seq=" +
//						// bioSeq.Seq);
//						// System.out.println("MD5=" + md5);
//						// TODO: 数据入库
//						System.out.println(sb.toString());
//						sb = new StringBuffer();
//						System.out.println(map);
//						// map = new HashMap<String, String>();
//						break;
//					}
//					sequence = new RecordFasta();
//					seq = new StringBuffer();
//					String[] arr = tempString.split("   ");
//					if (arr.length > 0) {
//						System.out.println(arr[0]);
//					}
//					// 七条数据
//					String[] arr1 = arr[1].split(";");
//					if (arr1.length == 7) {
//						System.out.println(arr1[0]);
//						embl._id = arr1[0];
//						embl.RecID = arr1[0];
//						sequence.RecID = arr1[0];
//						// TODO:....
//					} else {
//						System.out.println("ID数据项错误:" + tempString);
//					}
//				} else {
//					seq.append(tempString);
//					if (!tempString.startsWith("XX") && !tempString.startsWith("//")) {
//						String key = tempString.substring(0, 2);
//						String value = tempString.length() > 5 ? tempString.substring(5) : "";
//						// System.out.println(key + "=" + value);
//						setKeyValue(map, key, value, tempString);
//					}
//				}
//			}
//			/**
//			 organism 从OS中取，取消此方式
//			// TODO:type FT FH
//			String ft = map.get("FT");
//			// organism
//			if (ft != null && ft.contains("organism=")) {
//				int start = ft.indexOf("organism=") + "organism=".length();
//				String organism = ft.substring(start + 1, ft.indexOf("\"", start + 1));
//				System.out.println(organism);
//			}
//			 **/
//			reader.close();
//			fis.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}

	private static void setKeyValue(Map<String, String> map, String key, String value, String tempString) {
		String val = map.get(key);
		if (value != null) {
			value = value.replace("\r", "");
		}
		// || "CC".equals(key) && value != null && value.startsWith("FT   ")
			// E03975
			// CC   CC   Synthetic
			// CC   FH   Key             Location/Qualifiers
			// CC   FT   misc_feature    1..755
			// key = CC
			// value=FT   misc_feature    1..755
			 if("CC".equals(key)){
				 if(value != null && value.startsWith("CC   ") && value.length()>5){
					 	// CC   Synthetic
				 	if(map.get("comments_CC") != null){
					 	map.put("comments_CC", map.get("comments_CC") +"\n" + value);
				 	}else{
					 	map.put("comments_CC", value);
				 	}
				 }
				 //判断有没有“CC   FH”标签，以及“CC   FH”标签后是否有值
				 if(value != null && value.startsWith("FH")){
				 	// FH   Key             Location/Qualifiers
				 	if(map.get("comments_FH") != null){
					 	map.put("comments_FH", map.get("comments_FH") +"\n" + value);
				 	}else{
					 	map.put("comments_FH", value);
				 	}
				 }
				 //判断有没有“CC   FT”标签，以及“CC   FT”标签后是否有值
				 if(value != null && value.startsWith("FT   ") && value.length()>5){
				 	if(map.get("comments_FT") != null){
					 	map.put("comments_FT", map.get("comments_FT") +"\n" + value);
				 	}else{
					 	map.put("comments_FT", value);
				 	}
				 }
			 }
		// FH FT
		if ("FT".equals(key) && value != null) {
			// jiahh:根据需求，下面的符号截取是没有必要的，删除符号会出问题
			//value = value.replace("<", "").replace(">", "").replace("complement(", "").replace(")", "");
			// 修改截位规则，使用正则表达式替换
//			String keywords = value.substring(0, 15).trim();
//			String location = value.substring(16);
			String keywords = null;
			String location = null;
			// keywords:通过上述sequence拆分方式，在一个特征值序列中，标识符“FT”中，提取第4个字符开始取值，取到空格为结束。
			// location:通过上述sequence拆分方式，在一个特征值序列中，标识符“FT”中，当第4个字符有值时，则提取这个值+空格之后的内容。
			Matcher matcher = reg_keywords_location.matcher(value);
			if (matcher.find()) {
				//record.RecID = matcher.group(1);
				keywords = matcher.group(1);
				location = matcher.group(3);
			}
			
			String count = map.get("count");
			int num = 0;
			if (count != null) {
				num = Integer.parseInt(count);
			}
			if (!"".equals(keywords)) {
				num++;
				map.put("keywords" + num, keywords);
				map.put("location" + num, location);
			} else {
				val = map.get("FT" + num);
				map.put("FT" + num, (val != null ? val + "\r\n" : "") + location);
			}
			String total = map.get("FTTotal" + num);
			map.put("FTTotal" + num, total == null ? "" : total + "\r\n" + tempString);
			map.put("count", num + "");
		} 
		// FHkey暂未要求解析
//		else if ("FH".equals(key)) {
//			if (value.startsWith("Key") && !"".equals(value)) {
//				String content = value.substring(16);
//				map.put("FHkey", content);
//			}
//		}
		else if ("  ".equals(key)) {
			if (value == null){
				return;
			}
			int index = value.indexOf("  ");
			if (index != -1){
				value = value.substring(0, index).trim().replace(" ", "");
			}
			if (val != null && value != null && !"".equals(value)) {
				map.put(key, val + "\r\n" + value);
			} else {
				map.put(key, value);
			}
		} else {
			// 普通情况
			if (val != null && value != null && !"".equals(value)) {
				map.put(key, val + " " + value);
			} else {
				map.put(key, value);
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "D:\\林林\\工作文件\\DI\\国外生物序列\\embl.txt";
		path = "D:\\林林\\工作文件\\DI\\国外生物序列\\源数据ZIP\\蛋白生物专利序列注解EMBL\\epo_prt.dat\\epo_prt.dat";
		path = "D:\\林林\\工作文件\\DI\\国外生物序列\\源数据ZIP\\patentdata\\kipo_prt.dat\\kipo_prt.dat";
		EMBLReader reader = new EMBLReader();
		//reader.readEMBL2(path);
		System.out.println("END");
	}

}
