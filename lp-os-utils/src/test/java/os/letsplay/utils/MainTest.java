package os.letsplay.utils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import os.letsplay.utils.models.User;
import os.letsplay.utils.reflection.Definitions;
import os.letsplay.utils.reflection.exceptions.ReflectionException;

/**
 * Unit test for simple App.
 */
public class MainTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MainTest( String testName ){
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite(){
        return new TestSuite( MainTest.class );
    }
    
    /**
     * Rigourous Test :-)
     * @throws ReflectionException 
     */
    public void testApp() throws ReflectionException{
        assertTrue( true );
        Definitions.get(User.class);
        print(Definitions.list());
    }
    
    public static void print(Object o){
    	System.out.println(o);
    }
}
