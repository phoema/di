package com.ipph.bio;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;

import com.ipph.bio.model.*;

/***
 * IPPH生物序列文件索引文件XML
 * @author lhp
 * 功能描述：将BioPatent保存为索引控制文件和专利生物序列FASTA文件
 */
public class IPPHIndexFileWriter {
	private Document _doc = null;
	private Element _root = null;
	
	public IPPHIndexFileWriter()
	{
	}
	
	public boolean save(String sFilePath, String sEncoding)
	{
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			DOMSource src = new DOMSource(_doc);
			
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			if(sEncoding==null || sEncoding.isEmpty())
			{
				trans.setOutputProperty(OutputKeys.ENCODING, "utf-8");
				sEncoding = "utf-8";
			}
			else 
			{
				trans.setOutputProperty(OutputKeys.ENCODING, sEncoding);
			}
			
			FileOutputStream fos = new FileOutputStream(sFilePath);
			PrintWriter pwr = new PrintWriter(fos);
			
			StreamResult res = new StreamResult(pwr);
			trans.transform(src, res);
			pwr.close();
			fos.close();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	public boolean open(String sVersion, String sDocURI)
	{
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbr = dbf.newDocumentBuilder();
			
			_doc = dbr.newDocument();
			if(sVersion==null || sVersion.isEmpty())
			{
				sVersion = "1.0";
			}
			_doc.setXmlVersion(sVersion);
			
			if(sDocURI!=null && !sDocURI.isEmpty())
			{
				_doc.setDocumentURI(sDocURI);
			}
			_root = _doc.createElement("content");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	public boolean write(BioPatent obj, List<BioFeature> lstFeat)
	{
		try {
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	protected boolean saveFeatures(List<BioFeature> lstFeat)
	{
		return false;
	}
}
