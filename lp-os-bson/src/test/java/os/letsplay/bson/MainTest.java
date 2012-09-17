package os.letsplay.bson;

import java.util.Arrays;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import os.letsplay.bson.models.Modern;
import os.letsplay.bson.models.Modern.Mappings;
import os.letsplay.bson.models.Modern.Mappings.Key;
import os.letsplay.bson.models.Modern.Mappings.Value;
import os.letsplay.bson.models.Modern.Neighbors;
import os.letsplay.bson.models.User;
import os.letsplay.utils.BytesUtil;
import os.letsplay.utils.Lister;
import os.letsplay.utils.MD5;
import os.letsplay.utils.Mapper;

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
	/*
	private File basicFile;
	private File complexFile;
	private File modelUserFile;
	*/
	
	private static final byte[] TEST_1 = {
		(byte)0x16, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x68, (byte)0x65, (byte)0x6c, (byte)0x6c, (byte)0x6f, (byte)0x00, (byte)0x06, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x77, 
		(byte)0x6f, (byte)0x72, (byte)0x6c, (byte)0x64, (byte)0x00, (byte)0x00
	};
	
	private static final byte[] TEST_2 = {
		(byte)0x31, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x42, (byte)0x53, (byte)0x4f, (byte)0x4e, (byte)0x00, (byte)0x26, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x30, 
		(byte)0x00, (byte)0x08, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x61, (byte)0x77, (byte)0x65, (byte)0x73, (byte)0x6f, (byte)0x6d, (byte)0x65, (byte)0x00, (byte)0x01, (byte)0x31, (byte)0x00, 
		(byte)0x33, (byte)0x33, (byte)0x33, (byte)0x33, (byte)0x33, (byte)0x33, (byte)0x14, (byte)0x40, (byte)0x10, (byte)0x32, (byte)0x00, (byte)0xc2, (byte)0x07, (byte)0x00, (byte)0x00, (byte)0x00, 
		(byte)0x00
	};
	
	private static final byte[] TEST_3 = {
		(byte)0xf9, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x73, (byte)0x74, (byte)0x72, (byte)0x69, (byte)0x6e, (byte)0x67, (byte)0x00, (byte)0x0d, (byte)0x00, (byte)0x00, (byte)0x00, 
		(byte)0x73, (byte)0x74, (byte)0x72, (byte)0x69, (byte)0x6e, (byte)0x67, (byte)0x20, (byte)0x76, (byte)0x61, (byte)0x6c, (byte)0x75, (byte)0x65, (byte)0x00, (byte)0x10, (byte)0x69, (byte)0x6e, 
		(byte)0x74, (byte)0x00, (byte)0x40, (byte)0xe2, (byte)0x01, (byte)0x00, (byte)0x01, (byte)0x64, (byte)0x6f, (byte)0x75, (byte)0x62, (byte)0x6c, (byte)0x65, (byte)0x00, (byte)0x77, (byte)0xbe, 
		(byte)0x9f, (byte)0x1a, (byte)0x2f, (byte)0xdd, (byte)0x5e, (byte)0x40, (byte)0x12, (byte)0x6c, (byte)0x6f, (byte)0x6e, (byte)0x67, (byte)0x00, (byte)0x40, (byte)0xe2, (byte)0x01, (byte)0x00, 
		(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x08, (byte)0x74, (byte)0x72, (byte)0x75, (byte)0x65, (byte)0x00, (byte)0x01, (byte)0x08, (byte)0x66, (byte)0x61, (byte)0x6c, (byte)0x73, 
		(byte)0x65, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x61, (byte)0x72, (byte)0x72, (byte)0x61, (byte)0x79, (byte)0x00, (byte)0x9e, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x30, 
		(byte)0x00, (byte)0x07, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x73, (byte)0x74, (byte)0x72, (byte)0x69, (byte)0x6e, (byte)0x67, (byte)0x00, (byte)0x10, (byte)0x31, (byte)0x00, (byte)0x40, 
		(byte)0xe2, (byte)0x01, (byte)0x00, (byte)0x01, (byte)0x32, (byte)0x00, (byte)0x77, (byte)0xbe, (byte)0x9f, (byte)0x1a, (byte)0x2f, (byte)0xdd, (byte)0x5e, (byte)0x40, (byte)0x12, (byte)0x33, 
		(byte)0x00, (byte)0x40, (byte)0xe2, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x08, (byte)0x34, (byte)0x00, (byte)0x01, (byte)0x08, (byte)0x35, (byte)0x00, 
		(byte)0x00, (byte)0x03, (byte)0x37, (byte)0x00, (byte)0x1f, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x61, (byte)0x31, (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x00, 
		(byte)0x62, (byte)0x00, (byte)0x10, (byte)0x62, (byte)0x31, (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x63, (byte)0x31, (byte)0x00, (byte)0x03, (byte)0x00, 
		(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x38, (byte)0x00, (byte)0x1f, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x61, (byte)0x32, (byte)0x00, (byte)0x02, (byte)0x00, 
		(byte)0x00, (byte)0x00, (byte)0x62, (byte)0x00, (byte)0x10, (byte)0x62, (byte)0x32, (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x63, (byte)0x32, (byte)0x00, 
		(byte)0x03, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x39, (byte)0x00, (byte)0x1f, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x61, (byte)0x33, (byte)0x00, 
		(byte)0x02, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x62, (byte)0x00, (byte)0x10, (byte)0x62, (byte)0x33, (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x63, 
		(byte)0x33, (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00
	};
	
	public MainTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(MainTest.class);
	}
	
	public void testEncode1()  {
		Mapper map 	= Mapper.create("hello", "world");
		byte[] bytes = BSON.encode(map);
		print(BytesUtil.getJavaCode(bytes));
		Arrays.equals(TEST_1, bytes);
	}
	
	public void testEncode2()  {
		Mapper map 	= Mapper
			.create("BSON",Lister
				.create("awesome")
				.insert(5.05)
				.insert(1986)
			)
		;
		byte[] bytes = BSON.encode(map);
		print(BytesUtil.getJavaCode(bytes));
		Arrays.equals(TEST_2, bytes);
	}
	
	public void testEncode3() throws BsonParseError{
		Mapper map 	= Mapper
			.create("string", 	"string value"	)
			.insert("int", 		123456			)
			.insert("double", 	123.456			)
			.insert("long", 	123456L			)
			.insert("true",		true			)
			.insert("false",	false			)
			.insert("null",		null			)
			.insert("array",	Lister
				.create("string")
				.insert(123456)
				.insert(123.456)
				.insert(123456L)
				.insert(true)
				.insert(false)
				.insert(null)
				.insert(Mapper.create("a1","b").insert("b1",2).insert("c1",3))
				.insert(Mapper.create("a2","b").insert("b2",2).insert("c2",3))
				.insert(Mapper.create("a3","b").insert("b3",2).insert("c3",3))
			)
		;
		byte[] bytes = BSON.encode(map);
		print(BytesUtil.getJavaCode(bytes));
		Arrays.equals(TEST_3, bytes);
	}
	
	public void testDecode1() throws BsonParseError  {
		Map<String, Object> map = BSON.decode(TEST_1);
		print(map);
	}
	
	public void testDecode2() throws BsonParseError  {
		Map<String, Object> map = BSON.decode(TEST_2);
		print(map);
	}
	
	public void testDecode3() throws BsonParseError  {
		Map<String, Object> map = BSON.decode(TEST_3);
		print(map);
	}
	
	public void testModel() throws BsonParseError {
		Mapper map 	= Mapper
			.create("id", 					BsonId.get()				)
			.insert("email", 				"sergey.mamyan@gmail.com"	)
			.insert("any", 					Mapper
				.create("hello","world")		
			)
			.insert("mappings", 			Mapper								
				.create("DC",				Mapper
					.create("id",			"U1_DC_PLATFORM_ID"			)
					.insert("token",		"U1_DC_PLATFORM_ID"			)
				)
				.insert("FB",				Mapper
					.create("id",			"U1_DC_PLATFORM_ID"			)
					.insert("token",		"U1_DC_PLATFORM_ID"			)
				)
			)
			.insert("neighbors", 			Mapper								
				.create("U2",				"NONE"						)
				.insert("U3",				"PENDING"					)
				.insert("U4",				"ACCEPTED"					)
			)
			.insert("outbox",				Lister
				.create("R1")								
				.insert("R2")								
				.insert("R3")								
			)
			.insert("inbox", 				Mapper								
				.create("FB@R2",			Mapper
					.create("sender",		"U1_DC_PLATFORM_ID"			)
					.insert("createdAt",	1328925960086L				)
				)
				.insert("DC@R2",			Mapper
					.create("sender",		"U1_DC_PLATFORM_ID"			)
					.insert("createdAt",	1328925960086L				)
				)
				.insert("OD@R2",			Mapper
					.create("sender",		"U1_DC_PLATFORM_ID"			)
					.insert("createdAt",	1328925960086L				)
				)
			)
		;
		
		byte[] mpBytes 				= BSON.encode(map);
		User user 	 				= BSON.decode(mpBytes,User.class);
		print(user);
		byte[] mlBytes 				= BSON.encode(user);
		Map<Object,Object> rMap 	= BSON.decode(mlBytes);
		print(rMap);
		
	}
	
	public void testModel2() throws BsonParseError {
		Modern m = Modern.create()
		.id("1")
		.email("sergey.mamyan@gmail.com")
		.any(152)
		/*.inbox(Modern.Inbox.create().insert(
				"Hello", Inbox.Value.create().sender("SSSS")))*/
		.neighbors(Neighbors.create().insert("hello", os.letsplay.bson.models.Modern.Neighbors.Value.ACCEPTED))
		.mappings(
			Mappings.create()
			.insert( Key.DC, Value.create()
				.id("HEELO")
				.token("TOKEN")
			)
			.insert( Key.FB, Value.create()
				.id("HEELO")
				.token("TOKEN")
			)
		);
		
		byte[] mpBytes 				= BSON.encode(m);
		Modern modern 	 			= BSON.decode(mpBytes,Modern.class);
		print(modern);
		byte[] mlBytes 				= BSON.encode(modern);
		Map<Object,Object> rMap 	= BSON.decode(mlBytes);
		print(rMap);
		
	}
	
	private static void print(Object o){
		String hash = MD5.hex(o==null?"":o.toString());
		if(DEBUG) {
			System.out.println((line++)+". "+hash);
			System.out.println(o);
		}else{
			assertEquals(hash, HASHES[line++]);
		}
	}
	
}
