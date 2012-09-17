
package os.letsplay.bson;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

import os.letsplay.bson.annotations.BsonDocument;
import os.letsplay.utils.ByteArray;
import os.letsplay.utils.MD5;
import os.letsplay.utils.reflection.Simple;


public class BsonId implements Simple, Comparable<BsonId> {
	
	public static class BsonIdComparator implements Comparator<BsonId>{
		public int compare(BsonId ba, BsonId bb) {
			byte[] a = ba.toByteArray();
			byte[] b = bb.toByteArray();
			if(a.length != b.length){
				return a.length - b.length;
			}
			int n = Math.min(a.length, b.length);
		    for (int i = 0; i < n; ++i) {
		    	byte delta =(byte)((a[i] & 0xff)-(b[i] & 0xff));
		    	if (delta != 0) { 
		    		return delta; 
		    	}
		    }
		    return 0;
		 }
	}
	
	public int compareTo(BsonId o) {
		return new BsonIdComparator().compare(this, o);
	}

    public static BsonId get(){
        return new BsonId();
    }
    
    public static boolean isValid( String s ){
        if ( s == null )
            return false;

        final int len = s.length();
        if ( len != 24 )
            return false;

        for ( int i=0; i<len; i++ ){
            char c = s.charAt( i );
            if ( c >= '0' && c <= '9' )
                continue;
            if ( c >= 'a' && c <= 'f' )
                continue;
            if ( c >= 'A' && c <= 'F' )
                continue;

            return false;
        }

        return true;
    }
    
    public static BsonId toObjectId( Object o ){
        if ( o == null )
            return null;
        if ( o instanceof BsonId )
            return (BsonId)o;
        if ( o instanceof String ){
            String s = o.toString();
            if ( isValid( s ) )
                return new BsonId( s );
        }
        return null;
    }


	protected byte[] bytes;

    public BsonId(byte[] bytes){
        if (bytes.length != 12){
            throw new IllegalArgumentException( "need 12 bytes" );
        }
        this.bytes = bytes;
    }

    public BsonId( String s ){
        this(toBytesArray(s));
    }
    
    public BsonId( int time , int machine , int inc ){
        this(toBytesArray(time,machine,inc));
    }

    /** Create a new object id.
     */
    public BsonId(){
    	this(getMachineTime(),getMachineId(),getNextInc());
    }

    public BsonId(Class<?> type, String id) {
    	this.bytes = getTypedBytes(type,id);
	}
    
    protected static byte[] getTypedBytes(Class<?> type){
    	return getTypedBytes(type,null);
    }
    
    protected static byte[] getTypedBytes(Class<?> type, String id){
    	byte typeByte = (byte)0x00;
    	if(id==null){
    		id = MD5.hash();
    	}
    	if(type.isAnnotationPresent(BsonDocument.class)){
    		typeByte= type.getAnnotation(BsonDocument.class).model();
    	}
    	byte[] bytes = new byte[12];
    	System.arraycopy(MD5.bytes(id), 0, bytes, 0, bytes.length);
    	bytes[11] = typeByte;
    	return bytes;
    }
    
	public int hashCode(){
        return bytes.hashCode();
    }

    public boolean equals( Object o ){
        if ( this == o ) return true;
        BsonId other = toObjectId( o );
        if(other==null)  return false;
        return Arrays.equals(this.toByteArray(),other.toByteArray());
    }
    
    public String toString(){
        byte b[] = toByteArray();
        StringBuilder buf = new StringBuilder(24);
        for ( int i=0; i<b.length; i++ ){
            int x = b[i] & 0xFF;
            String s = Integer.toHexString( x );
            if ( s.length() == 1 )
                buf.append( "0" );
            buf.append( s );
        }
        return buf.toString();
    }

    public byte[] toByteArray(){
        return bytes;
    }

    public int getMachine(){
        return 0;
    }
    
    public static int getMachineTime() {
    	return (int)(System.currentTimeMillis() / 1000);
    }
    
    public static int getMachineId() {
    	try{
    		return Integer.parseInt(System.getProperty("machine.id"),16);
    	}catch(Exception ex){
    		return 0;
    	}
    }
    
    public static int getNextInc() {
        return _nextInc.getAndIncrement();
    }
    
    public static byte[] toBytesArray(int time, int machine, int inc) {
    	ByteArray ba = new ByteArray(24);
    	ba.writeInt(time);
    	ba.writeInt(machine);
    	ba.writeInt(inc);
    	return ba.array();
    }
    
    public static byte[] toBytesArray(String s) {
    	if ( ! isValid( s ) )
            throw new IllegalArgumentException( "invalid ObjectId [" + s + "]" );
        byte b[] = new byte[12];
        for ( int i=0; i<b.length; i++ ){
            b[i] = (byte)Integer.parseInt( s.substring( i*2 , i*2 + 2) , 16 );
        } 
        return b;
    }
    
    private static AtomicInteger _nextInc = new AtomicInteger( (new java.util.Random()).nextInt() );

	public Object toSimple() {
		return toString();
	}
	
	public static BsonId valueOf(String string) {
		return new BsonId();
	}
	
}

