package os.letsplay.mongo;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import os.letsplay.mongo.models.User;
import os.letsplay.utils.MD5;

/**
 * Unit test for simple App.
 */
public class MainTest extends TestCase {
	
	private static final Boolean DEBUG = true; 
	private static Integer line = 0;
	private static final String[] HASHES= {
		"98bfff304808f6452825a009caee373c",
		"d765e88c09da47455424c386fede9752",
		"84929c76d6df451de0107abbc8d56f33",
		"1f35d27416c6dc49b6a5a96fc0065890",
		"8e6df8fcc91730f1bea3fc2f4e8fc147",
		"84e67ff66d267abc80ca25bddde54fa4",
		"e0e357d066c2591045deea7976775328",
		"a817c57638fb2e252be50c4d828f2cf7",
		"0280c15be88ab05fda0a3ffa3b551895",
		"896ba47752fb47815f4b43221b85db61"
	};
		
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MainTest( String testName ){
        super( testName );
        Mongo.init(
        	"mongodb://admin:admin@flame.mongohq.com:27074/test-sm?poolsize=20",
        	"mongodb://127.0.0.1/test"
        );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite(){
        return new TestSuite( MainTest.class );
    }

    /**
     * Rigourous Test :-)
     * @throws Exception 
     */
    public void testApp() throws Exception{
        Collection<User> col = Mongo.db("test-sm").getCollection(User.class);
        User u = col.get("5054641790dbcf0005000003");
        u.name("Sergey Modified");
        col.save(u);
    }
    
    
    @SuppressWarnings("unused")
	private static void print(Object o){
		String hash = MD5.hex(o.toString());
		if(DEBUG && o!=null) {
			System.out.println((line++)+". "+hash);
			System.out.println(o);
		}else{
			assertEquals(hash, HASHES[line++]);
		}
	}
}
