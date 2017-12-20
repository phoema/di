package jar;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.google.common.io.Files;
import com.ipph.bio.model.ExchangeControl;
import com.ipph.bio.model.ExchangeControlFile;
import com.ipph.bio.model.BioFeature;
import com.ipph.bio.model.ExchangeIndex;
import com.ipph.bio.model.BioPatent;
import com.ipph.bio.model.ExchangePatentFileAttr;
import com.ipph.bio.model.BioSequenceAC;
import com.ipph.bio.model.ExchangeSequenceList;
import com.thoughtworks.xstream.XStream;

/**
 * Unit test for simple App.
 */
public class XmlIndexTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public XmlIndexTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( XmlIndexTest.class );
    }

    /**
     * Rigourous Test :-)
     * @throws IOException 
     */
    public void testToXml() throws IOException
    {
    	ExchangeIndex index = new ExchangeIndex();
    	index.file = "IPPHDB_SEQ_20150415_0001.ZIP";
    	index.dateExchange = "20150415";
    	index.dateProduced = "20150415";
    	index.patcnt = "1";
    	index.filecnt = "1";
    	index.size = "716.5M";
    	index.md5 = "993DD7F7211CF278B43FC4B0B624FBD0";
    	index.status = "C";
    	List<BioPatent> Patentlist = new ArrayList<BioPatent>();
    	index.doclist = Patentlist;
    	BioPatent doclist = new BioPatent();
    	Patentlist.add(doclist);
    	doclist.topic="CN_SEQ";
    	doclist.country = "CN";
    	doclist.docNumber = "301967775";
    	doclist.kind = "A";
    	doclist.PNO = "CN301967775A";
    	doclist.PNS = "CN301967775S";
    	doclist.datePublication = "20120627";
    	doclist.Format = "FASTA";
    	doclist.path = "IPPHDB_SEQ_20150415_0001/CN";
    	doclist.status = "C";
    	ExchangePatentFileAttr fileattr = new ExchangePatentFileAttr();
    	fileattr.filename = "CN301967775A.txt";
    	fileattr.filetype = "TXT";
    	fileattr.section = "SEQ";
    	doclist.file = fileattr;
    	ExchangeSequenceList seqlist = new ExchangeSequenceList();
    	seqlist.amount = "2";
    	List<BioSequenceAC> seqlist1 = new ArrayList<BioSequenceAC>();
    	BioSequenceAC seq = new BioSequenceAC();
    	BioSequenceAC seq2 = new BioSequenceAC();
    	seqlist1.add(seq);
    	seqlist1.add(seq2);
    	seq.SeqID = "CN301967775A_000001";
    	seq.length = 729;
    	seq.type = "DNA";
    	seq.gn = "蚓激酶F-Ⅱ";
    	seq.og = "线粒体";
    	seq.organism = "Eisenia fetida";
    	
    	List<BioFeature> features = new ArrayList<BioFeature>();
    	BioFeature feature1 = new BioFeature();
    	BioFeature feature2 = new BioFeature();
    	feature1.sequence = "1";
    	feature1.location = "(1)..(324)";
    	feature1.keywords = "CDS";
    	feature1.other = "AMIDATION";
    	feature2.sequence = "2";
    	feature2.location = "(6)..(732)";
    	feature2.keywords = "CDS";
    	feature2.other = "蚓激酶F-Ⅱ全基因DNA";
    	features.add(feature1);
    	features.add(feature2);
     	seq.features = features;
    	
    	seq2.SeqID = "CN301967775A_000002";
    	seq2.length = 9;
    	seq2.type = "PRT";
    	seq2.organism = "Hepatitis B virus";
    	
    	List<BioFeature> features2 = new ArrayList<BioFeature>();
    	BioFeature feature3 = new BioFeature();
    	BioFeature feature4 = new BioFeature();
    	feature3.sequence = "1";
    	feature3.location = "(1)..(6)";
    	feature3.keywords = "DISULFID";
    	feature3.other = "缩宫素（催产素；Oxytocin）氨基酸序列";
    	feature4.sequence = "2";
    	feature4.location = "(9)..(9)";
    	feature4.keywords = "MOD_RES";
    	feature4.other = "AMIDATION";
    	features2.add(feature3);
    	features2.add(feature4);
     	seq2.features = features2;



    	
    	seqlist.seqlist = seqlist1;
    	doclist.file = new ExchangePatentFileAttr();
    	doclist.file.seqlist = seqlist;
    	
    	
    	XStream xstream = new XStream();  
    	
//    	xstream.processAnnotations(BioControl.class);  
//        xstream.processAnnotations(BioControlFile.class);  
    	// 指定所有class均解析注解 Bean->XML有效
        xstream.autodetectAnnotations(true);
       	System.out.println(xstream.toXML(index));
        assertTrue( true );
        
        File file = new File("D:\\index.xml");
     	Files.write(xstream.toXML(index).getBytes(StandardCharsets.UTF_8), file);

    }
    /**
     * Rigourous Test :-)
     */
    public void testFromXml()
    {
    	XStream xstream = new XStream();  
    	
    	xstream.processAnnotations(ExchangeIndex.class);  

    	// 指定所有class均解析注解 XML->无效
        //xstream.autodetectAnnotations(true);
        File file = new File ("D:\\MyWork\\didoc\\生物序列\\生物序列数据和中国化学结构数据成品及索引文件说明文档\\生物序列成品样例\\IPPHDB_SEQ_20150415_0001_INDEX.xml");
        ExchangeIndex control =  (ExchangeIndex)xstream.fromXML(file);
        ExchangeIndex person=(ExchangeIndex)xstream.fromXML(this.getClass().getClassLoader().getResourceAsStream("IPPHDB_SEQ_20150415_0001_INDEX.xml"));
    	//System.out.println(xstream.toXML(control));
        assertTrue( true );
    }
 }
