package jar;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import lombok.extern.slf4j.Slf4j;

import com.hp.util.MD5FileUtil;
import com.ipph.bio.file.FASTAFileReader;
import com.ipph.bio.model.RecordEmbl;
import com.ipph.bio.model.RecordFasta;
import com.ipph.bio.util.BIO_CONST;

/**
 * 
 * @author jiahh 2015年5月11日
 *
 */
@Slf4j
public class FASTAFileReaderTest extends TestCase{
	@Autowired
	private MongoTemplate mongoTemplate;
   /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public FASTAFileReaderTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( FASTAFileReaderTest.class );
    }


    /**
     * Rigourous Test :-)
     * @throws Exception 
     */
    public void testEMBLConvert() throws Exception
    {
    	FASTAFileReader reader = new FASTAFileReader();
    	
    	reader.open(this.getClass().getClassLoader().getResource("nrn11_fasta.txt").getFile(),BIO_CONST.ROOT_FASTA_FORMAT01, "UTF8",false);
    	RecordFasta record = reader.readBioSequenceObj();
    	int i = 0;
    	List<RecordFasta> list = new ArrayList<RecordFasta>();
    	while(record != null){
    		i++;
    		list.add(record);
    		record = reader.readBioSequenceObj();
    	}
        assertTrue( true );
        for(RecordFasta fasta : list){
            System.out.println(fasta.Header);
            System.out.println(fasta.Seq);
       	
        }
        System.out.println(list.size());
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
		record._id = MD5(record.RecID + "__" + record.FilePath);
    	return record;
    }
	private String MD5(String string) {
		// TODO Auto-generated method stub
		return UUID.randomUUID().toString();
	}
 
	public void testFiles() throws Exception{
		String separator = System.getProperty("file.separator");
    	FASTAFileReader reader = new FASTAFileReader();
    	

		String folder = BIO_CONST.ROOT_FASTA_FORMAT01;
		traverseFolder(folder,reader);
		
	}
	//递归方法-》foldername：要遍历的文件(夹)名（完整路径）  
    public void traverseFolder(String foldername,FASTAFileReader reader) throws Exception{  
        List<RecordFasta>  list = new ArrayList<RecordFasta>(); 
        File file = new File(foldername);  
        //判断是否为文件  
        if(file.isDirectory()){  
	        //若是文件夹，则执行以下操作  
	          
	        //将文件夹下所有文件转换成一个File数组  
	        File[] filearray = file.listFiles();  
	          
	        //遍历数组，若是文件夹则递归，是文件则判断是否为exe文件  
	        for(File currfile:filearray){  
	            if(currfile.isDirectory()){  
	                this.traverseFolder(currfile.getAbsolutePath(),reader);  
	            }else{  
	                if(currfile.getName().lastIndexOf(".txt")!=-1){  
	                	log.debug(currfile.getPath());
	                	reader.open(currfile.getPath(),BIO_CONST.ROOT_FASTA_FORMAT01, "GB2312",true);
	                	RecordFasta record = reader.readBioSequenceObj();
	                	while(record != null){
	                		list.add(record);
	                		log.debug("record.Header:" + record.Header);
	                		log.debug("record.Seq:" + record.Seq);
	                		
	                		record = reader.readBioSequenceObj();
	        				mongoTemplate.save(record, BIO_CONST.SOURCE_FASTA);
                		
	                	}
	                	log.debug("list.size" + list.size());
	                	
	                }  
	            }  
	        }  
        }         
    }  
}
