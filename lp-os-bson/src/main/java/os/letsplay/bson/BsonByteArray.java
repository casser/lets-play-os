package os.letsplay.bson;

import java.nio.charset.Charset;
import java.security.InvalidParameterException;
import java.util.Date;
import java.util.UUID;

import os.letsplay.bson.binary.Binary;
import os.letsplay.utils.ByteArray;
import os.letsplay.utils.BytesUtil;


public class BsonByteArray extends ByteArray{
	
	private static final byte 		TERMINATOR 	= 0x00;
	private static final Charset 	UTF8 		= Charset.forName("UTF-8");
	
	public BsonByteArray(){
		super();
	}
	public BsonByteArray(int capasity){
		super(capasity);
	}
	
	public void writeString(Object object) {
		byte[] bytes = object.toString().getBytes(UTF8);
		writeInt(bytes.length+1);
		writeBytes(bytes);
		writeByte(TERMINATOR);
	}
	
	public String readString() {
		int length = readInt();
		byte[] bytes = readBytes(length-1);
		readByte();
		return new String(bytes,UTF8);
	}
	
	public byte[] readBytes() {
		int size = readInt();
		backward(4);
		return readBytes(size);
	}
	
	public byte[] readBinary() {
		int  length 	= readInt();
		byte[] bytes 	= new byte[length+1];
		readBytes(bytes);
		return bytes;
	}
	
	public void writeBinary(Object object) {
		byte[] bytes;
		if(object instanceof UUID){
			bytes = new byte[16];
			BytesUtil.writeLong(bytes, 0, ((UUID)object).getMostSignificantBits());
			BytesUtil.writeLong(bytes, 8, ((UUID)object).getLeastSignificantBits());
			object = new Binary(Binary.Type.UUID.getValue(), bytes);
		}
		if(object instanceof BsonBinary){
			BsonBinary bin = (BsonBinary)object;
			bytes = bin.getData();
		}else{
			throw new InvalidParameterException("Invalid BsonBinary data");
		}
		writeInt(bytes.length-1);
		writeBytes(bytes);
	}
	
	public String readCString() {
		int sPos = position();
		int ePos = sPos;
		while(readByte()!=TERMINATOR){
			ePos++;
		}
		byte[] bytes = new byte[ePos-sPos];
		position(sPos);
		readBytes(bytes);
		readByte();
		return new String(bytes,UTF8);
	}
	
	public void writeCString(String string) {
		byte[] bytes = string.getBytes(UTF8); 
		writeBytes(bytes);
		writeByte((byte)0x00);
	}
	public Date readDate() {
		long time = readLong();
		return new Date(time);
	}
	public void writeDate(Date object) {
		long time = object.getTime();
		writeLong(time);
	}
	
		
}
