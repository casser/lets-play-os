
package os.letsplay.bson;

public class BSON {
	public static final byte[] EMPTY_DOC = {
		(byte)0x05,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00
	};
	// the BSON types
	public static final byte TERMINATOR  	= (byte)0x00;
	public static final byte DOUBLE  		= (byte)0x01;
	public static final byte STRING  		= (byte)0x02;
	public static final byte DOCUMENT  		= (byte)0x03;
	public static final byte ARRAY  		= (byte)0x04;
	public static final byte BINARY  		= (byte)0x05;
	// 0x06 is deprecated
	public static final byte OBJECTID  		= (byte)0x07;
	public static final byte BOOLEAN  		= (byte)0x08;
	public static final byte UTC  			= (byte)0x09;
	public static final byte NULL  			= (byte)0x0a;
	public static final byte REGEXP 		= (byte)0x0b;
	// 0x0c is deprecated
	public static final byte JS  			= (byte)0x0d;
	public static final byte SYMBOL  		= (byte)0x0e;
	public static final byte SCOPEDJS  		= (byte)0x0f;
	public static final byte INT32 			= (byte)0x10;
	public static final byte TIMESTAMP 		= (byte)0x11;
	public static final byte INT64  		= (byte)0x12;
	public static final byte MAX_KEY  		= (byte)0x7f;
	public static final byte MIN_KEY  		= (byte)0xff;
	
	public static <T> T decode(byte[] document) throws BsonParseError{
		return decode(document,null);
	}
	public static <T> T decode(byte[] document, Class<T> type) throws BsonParseError{
		return (T)(new BsonDecoder().decode(document,type));
	}
	
	public static byte[] encode(Object document){
		return new BsonEncoder().encode(document);
	}
}
