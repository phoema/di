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
import com.thoughtworks.xstream.XStream;

/**
 * Unit test for simple App.
 */
public class XmlControlTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public XmlControlTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( XmlControlTest.class );
    }

    /**
     * Rigourous Test :-)
     * @throws IOException 
     */
    public void testToXml() throws IOException
    {
    	ExchangeControl control = new ExchangeControl();
    	control.dowork = "1";
    	control.type = "BIOLOGY";
    	
    	List<ExchangeControlFile> filelist = new ArrayList<ExchangeControlFile>();
    	ExchangeControlFile file = new ExchangeControlFile();
    	file.filename="IPPHDB_SEQ_20150415_0001_INDEX.XML";
    	file.md5 = "BCEA5D1E3A7E016B881262569FB5EFB2";
    	file.status = "C";
    	file.section = "TXT";
    	file.sequence = "1";
    	filelist.add(file);
    	ExchangeControlFile file2 = new ExchangeControlFile();
    	file2.filename="IPPHDB_SEQ_20150415_0002_INDEX.XML";
    	file2.md5 = "C8C2004D67D18E4423C8FB96E6461DDF";
    	file2.status = "C";
    	file2.section = "TXT";
    	file2.sequence = "1";
    	filelist.add(file2);
    	control.filelist = filelist;
    	XStream xstream = new XStream();  
    	
//    	xstream.processAnnotations(BioControl.class);  
//        xstream.processAnnotations(BioControlFile.class);  
    	// 指定所有class均解析注解
        xstream.autodetectAnnotations(true);
    	System.out.println(xstream.toXML(control));
        File indexfile = new File("D:\\control.xml");
     	Files.write(xstream.toXML(control).getBytes(StandardCharsets.UTF_8), indexfile);
        assertTrue( true );
    }
    /**
     * Rigourous Test :-)
     */
    public void testFromXml()
    {
    	XStream xstream = new XStream();  
    	
    	xstream.processAnnotations(ExchangeControl.class);  
        xstream.processAnnotations(ExchangeControlFile.class);  
    	// 指定所有class均解析注解
        xstream.autodetectAnnotations(true);
        File file = new File ("D:\\MyWork\\didoc\\生物序列\\生物序列数据和中国化学结构数据成品及索引文件说明文档\\生物序列成品样例\\IPPHDB_CONTROL_SQE_20150415.XML");
        ExchangeControl control =  (ExchangeControl)xstream.fromXML(file);
        ExchangeControl person=(ExchangeControl)xstream.fromXML(this.getClass().getClassLoader().getResourceAsStream("IPPHDB_CONTROL_SQE_20150415.XML"));
    	//System.out.println(xstream.toXML(control));
        assertTrue( true );
    }
}
