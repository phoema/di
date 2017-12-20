package jar;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
    
    /**
     * Rigourous Test :-)
     * @throws IOException 
     */
    public void testFileCompare() throws IOException
    {
    	String file1 = "D:\\MyWork\\didoc\\生物序列\\data\\生物序列\\patentdata\\epo_prt.dat";
    	String file2 = "D:\\MyWork\\didoc\\生物序列\\data\\生物序列\\蛋白生物专利序列注解EMBL\\epo_prt.dat";
    	String file3 = "D:\\MyWork\\didoc\\生物序列\\data\\生物序列\\核苷酸专利序列（冗余，EMBL格式）\\epo_prt.dat";
   //Files.readLines(file, charset, new LineProcessor<int> callback{});
    	File file = new File(file1);
    	// 回调函数示例
    	ArrayList<String> list = Files.readLines(file,Charsets.UTF_8, new LineProcessor<ArrayList<String>>() {

    		ArrayList<String> result = new ArrayList<String>();

			public ArrayList<String> getResult() {

				return result;
			}

			public boolean processLine(String line) throws IOException {
				result.add(line.trim());
				if (line.contains("haha"))
					return false;
				else
					return true;
			}

				});
        assertTrue( true );
    }

    public  void test() throws ParseException{
    	String line = " RL Patent number EP2096177-A2/34777, 02-SEP-2009.";
		// 取专利号对应的日期 倒数第十二位到倒数第二位
		String dt = line.substring(line.length()-12, line.length()-1);

    	HashMap<String,String> map = new HashMap();
    	map.put("JAN", "01");
    	map.put("FEB", "02");
    	map.put("MAR", "03");
    	map.put("APR", "04");
    	map.put("MAY", "05");
    	map.put("JUN", "06");
    	map.put("JUL", "07");
    	map.put("AUG", "08");
    	map.put("SEP", "09");
    	map.put("OCT", "10");
    	map.put("NOV", "11");
    	map.put("DEC", "12");
    	Locale l = new Locale("en");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		for(int i=1;i<13;i++){
			String monthstr = String.format("%02d", i);
			Date date = sdf.parse("2011"+monthstr+"02 11:50:32");
			String day = String.format("%td", date);
			String month = String.format(l,"%tb", date);
			String year = String.format("%tY", date);
			System.out.println(day+" "+month+" "+year);
		}
    }
}


