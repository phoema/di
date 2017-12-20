package jar;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.google.common.base.Strings;
import com.ipph.bio.file.EMBLFileReader;
import com.ipph.bio.model.RecordEmbl;

/**
 * 
 * @author jiahh 2015年5月11日
 *
 */
public class EMBLFileReaderTest extends TestCase{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public EMBLFileReaderTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( EMBLFileReaderTest.class );
    }


    /**
     * Rigourous Test :-)
     * @throws Exception 
     */
    public void testemblObj() throws Exception
    {
    	EMBLFileReader reader = new EMBLFileReader();
    	
    	reader.open(this.getClass().getClassLoader().getResource("nrnl1.annot.xml.txt").getFile(), "UTF8");
    	RecordEmbl record = reader.readOneObj();
    	
    	int i = 0;
    	List<RecordEmbl> list = new ArrayList<RecordEmbl>();
    	while(record != null){
    		i++;
    		list.add(record);
        	System.out.println("i:" + i );
        	record = reader.readOneObj();	

    	}
        assertTrue( true );
    }
    /**
     * Rigourous Test :-)
     */
    public void testemblConvert()
    {
    	EMBLFileReader reader = new EMBLFileReader();
    	
    	reader.open(this.getClass().getClassLoader().getResource("nrnl1.annot.xml.txt").getFile(), "UTF8");
    	String onsstr = reader.readOneStr();
    	int i = 0;
    	List<RecordEmbl> list = new ArrayList<RecordEmbl>();
    	while(!Strings.isNullOrEmpty(onsstr)){
    		i++;
    		RecordEmbl record = new RecordEmbl();
    		record = Str2RecordEmbl(onsstr);
    		list.add(record);
        	System.out.println("i:" + i );
        	System.out.println(onsstr);
    		onsstr = reader.readOneStr();    		

    	}
        assertTrue( true );
    }
    CRC32 crc = new CRC32();
    Pattern pattern = Pattern.compile("^(ID)\\s{3}(.*)\r\n");
    private RecordEmbl Str2RecordEmbl(String str){
		RecordEmbl record = new RecordEmbl();
		Matcher mr = pattern.matcher(str);
		if(mr != null && mr.find()) {
			record.RecID = mr.group(2);
			System.out.println(record.RecID);
		}else{
			System.out.println("has no id:" + str);
			return null;
		}
		// 获取内容
		record.Content = str;
		crc.reset();
		crc.update(str.getBytes());
		// 计算内容的CRC32
		record.CRC = crc.getValue();
		record.FilePath = "FilePath";//TODO
		record.Format = "Format1";//TODO
		//记录ID，RecID__Source__FilePath 全大写，MD5小写
		record._id = MD5(record.RecID+"__" + record.FilePath);
    	return record;
    }
	private String MD5(String string) {
		// TODO Auto-generated method stub
		return UUID.randomUUID().toString();
	}
 
	
}
