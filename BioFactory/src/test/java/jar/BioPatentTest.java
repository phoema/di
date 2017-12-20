package jar;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BioPatentTest  extends TestCase{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public BioPatentTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( BioPatentTest.class );
    }

    

}
