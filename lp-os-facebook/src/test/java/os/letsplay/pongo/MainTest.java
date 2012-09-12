package os.letsplay.pongo;

import java.util.HashMap;
import java.util.Map;

import os.letsplay.facebook.GraphAPI;
import os.letsplay.facebook.GraphApiException;
import os.letsplay.json.JSON;
import os.letsplay.json.JsonParseError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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
     */
    public void testApp(){
        assertTrue( true );
    }
    
    @SuppressWarnings("unchecked")
	public void testFacebookUserAccesToken() throws GraphApiException {
    	try {
    		GraphAPI api = new GraphAPI("AAADZBQBOEfYgBAHp4OJVoBf2sAj42wAiumZBN1IL6405i0t6jgnK5SjZCZAZAJXqeg5GyvIdIa0M3uwya7x6motp3V8oCWfhdXq93YZBMgjgZDZD");
	        Map<String, Object> me = api.get("/me").call(HashMap.class);
	        JSON.print(me);
	        assertTrue(true);	
		} catch (GraphApiException e) {
			assertTrue(e.getCode()==190);
			assertEquals(e.getType(), "OAuthException");
		}
       
    }
    
    @SuppressWarnings("unchecked")
	public void testFacebookAppAccesToken() throws GraphApiException {
        GraphAPI api = new GraphAPI("156416151148883","b21346db92004fefa02eab87556be189");
        Map<String, Object> me = api.get("/mamyan").call(HashMap.class);
        assertEquals(me.get("first_name"),"Sergey");
    }
}
