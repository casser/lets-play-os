package os.letsplay.facebook;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import os.letsplay.facebook.models.User;
import os.letsplay.json.JSON;

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
    
	public void testFacebookUserAccesToken() throws GraphApiException {
    	try {
    		GraphAPI api = new GraphAPI("AAADZBQBOEfYgBAC0twexdwsMzQkXQNf37xpBUt4SZBSAH1ma6vIjUdiqDZAptCpAv0T3lthq4miOIBFbkgyp64Ln31q6elqjH9NSIINuQZDZD");
    		User me = api.get("/me").call(User.class);
	        JSON.print(me);
	        assertTrue(true);	
		} catch (GraphApiException e) {
			assertTrue(e.getCode()==190);
			assertEquals(e.getType(), "OAuthException");
		}
    }
    
	public void testFacebookAppAccesToken() throws GraphApiException {
        GraphAPI api = new GraphAPI("156416151148883","b21346db92004fefa02eab87556be189");
        User me = api.get("/mamyan").call(User.class);
        assertEquals(me.getName(),"Sergey Mamyan");
    }
}
