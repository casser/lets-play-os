package os.letsplay.json;

import java.io.File;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import os.letsplay.json.models.Animal;
import os.letsplay.json.models.Master;
import os.letsplay.json.models.User;
import os.letsplay.utils.MD5;
import os.letsplay.utils.reflection.exceptions.ReflectionException;

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
	private File basicFile;
	private File complexFile;
	private File modelUserFile;
	private File modelDogFile;
	private File modelCatFile;
	private File modelParrotFile;
	private File modelAnimailFile;
	
	public MainTest(String testName) {
		super(testName);
		basicFile 			= new File("target/test-classes/test-basic.json");
		complexFile 		= new File("target/test-classes/test-complex.json");
		modelUserFile 		= new File("target/test-classes/test-model-user.json");
		modelDogFile 		= new File("target/test-classes/test-dog.json");
		modelCatFile 		= new File("target/test-classes/test-cat.json");
		modelParrotFile 	= new File("target/test-classes/test-parrot.json");
		modelAnimailFile 	= new File("target/test-classes/test-animal.json");
	}

	public static Test suite() {
		return new TestSuite(MainTest.class);
	}
	
	public void testBasics() throws JsonParseError, ReflectionException, IOException {
		print(JSON.encode(JSON.decode("'string'"),true,true));
		print(JSON.encode(JSON.decode("true"),true,true));
		print(JSON.encode(JSON.decode("false"),true,true));
		print(JSON.encode(JSON.decode("[1,2,3]"),true,true));
		print(JSON.encode(JSON.decode("{a:1,b:2,c:3}"),true,true));
		print(JSON.encode(JSON.decode("125.24"),true,true));
		print(JSON.encode(JSON.decode("hello world jan axper jan = hhhh"),true,true));
	}
	
	public void testComplex() throws JsonParseError, ReflectionException, IOException {
		print(JSON.encode(JSON.decode(basicFile),  true,true));
		print(JSON.encode(JSON.decode(complexFile),true,true));
	}
	
	public void testModel() throws JsonParseError, ReflectionException, IOException {
		print(JSON.encode(JSON.decode(modelUserFile,User.class), true,true));
	}
	
	public void testAbstractParam() throws JsonParseError, ReflectionException, IOException {
		print(JSON.encode(JSON.decode(modelDogFile,Master.class), true,true));
		print(JSON.encode(JSON.decode(modelDogFile,Master.class), true,true));
		print(JSON.encode(JSON.decode(modelCatFile,Master.class), true,true));
		print(JSON.encode(JSON.decode(modelParrotFile,Master.class), true,true));
		print(JSON.encode(JSON.decode("{'type':'DOG','name':'Dog'}",Animal.class), true,true));
		print(JSON.encode(JSON.decode("{'type':'CAT','name':'Cat'}",Animal.class), true,true));
		print(JSON.encode(JSON.decode("{'type':'PARROT','name':'Parrot'}",Animal.class), true,true));
	}
	
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
