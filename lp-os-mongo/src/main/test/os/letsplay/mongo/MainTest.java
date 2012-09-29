package os.letsplay.mongo;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import os.letsplay.bson.BSON;
import os.letsplay.json.JSON;
import os.letsplay.mongo.models.Acceptance;
import os.letsplay.mongo.models.Animal;
import os.letsplay.mongo.models.Cat;
import os.letsplay.mongo.models.Dog;
import os.letsplay.mongo.models.Master;
import os.letsplay.mongo.models.Message;
import os.letsplay.mongo.models.Parrot;
import os.letsplay.mongo.models.Profile;
import os.letsplay.mongo.models.User;
import os.letsplay.utils.BytesUtil;
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

    public void testApp() throws Exception{
    	Collection<User> col = Mongo.db().getCollection(User.class);
    	col.drop();
    	User u1 = new User().name("Sergey");
    	col.save(u1);
        User u2 = col.get(u1.id());
        u2.name("Sergey Modified");
        col.save(u2);
    }
    public void testAnimals() throws Exception{
    	Cat cat = (Cat) JSON.decode("{'type':'CAT','name':'Dog'}",Animal.class);
    	print(JSON.encode(BSON.decode(BSON.encode(cat),Animal.class), true,true));
    	
    	Dog dog = (Dog) JSON.decode("{'type':'DOG','name':'Dog'}",Animal.class);
    	print(JSON.encode(BSON.decode(BSON.encode(dog),Animal.class), true,true));
    	
    	Parrot parrot = (Parrot) JSON.decode("{'type':'PARROT','name':'Dog'}",Animal.class);
    	print(JSON.encode(BSON.decode(BSON.encode(parrot),Animal.class), true,true));
    	
    	Master master = JSON.decode("{'type':'DOG','animal':{'name':'Cat'}}",Master.class);
    	print(JSON.encode(BSON.decode(BSON.encode(master),Master.class), true,true));
    }
    
    public void testMongoAnimals() throws Exception{
    	Collection<Animal> col = Mongo.db().getCollection(Animal.class);
    	col.drop();
    	
    	Dog 	d1 = (Dog) 		new Dog().name("Boby");
    	Cat 	c1 = (Cat) 		new Cat().name("Pussy");
    	Parrot 	p1 = (Parrot) 	new Parrot().name("Jako");
    	col.save(d1);
    	col.save(c1);
    	col.save(p1);
    	print(JSON.encode(col.get(d1.id()),true,true));
    	print(JSON.encode(col.get(c1.id()),true,true));
    	print(JSON.encode(col.get(p1.id()),true,true));
    }
    
    public void testMongoMessage() throws Exception{
    	Collection<Message> col = Mongo.db().getCollection(Message.class);
    	col.drop();
    	Message message = new Message();
    	message.content("Hello All");
    	message.recipients()
    		.add("0000001@FB")
    		.add("0000001@GP")
    		.add("hakob@gmail.com")
    	;
    	message.acceptances()
    		.add("0000001@FB",Acceptance.ACCEPTED)
    		.add("0000001@GP",Acceptance.DECLINED)
    		.add("hakob@gmail.com",Acceptance.WAITING)
    	;
    	col.save(message);
    	print(JSON.encode(JSON.decode(JSON.encode(message),Message.class),true,true));
    	print(JSON.encode(col.get(message.id()),true,true));
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
