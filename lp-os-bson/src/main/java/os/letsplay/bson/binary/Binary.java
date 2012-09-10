package os.letsplay.bson.binary;

import java.util.Comparator;

import os.letsplay.bson.BsonBinary;
import os.letsplay.utils.BytesUtil;


public class Binary implements BsonBinary, Comparable<Binary> {
	
	public static class BinaryComparator implements Comparator<Binary>{
		public int compare(Binary ba, Binary bb) {
			byte[] a = ba.getData();
			byte[] b = bb.getData();
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
	
	public static enum Type {
		
		BINARY_GENERIC			((byte)0x00),
		FUNCTION				((byte)0x01),
		BINARY_OLD				((byte)0x02),
		UUID					((byte)0x03),
		MD5						((byte)0x05),
		USER_DEFINED			((byte)0x80);
		
		private byte value;
		public byte getValue() {
			return value;
		}
		
		private Type(byte value) {
			this.value = value;
		}
		public static Type valueOf(byte b){
			switch(b){
				case (byte)0x01	:return FUNCTION;
				case (byte)0x02	:return BINARY_OLD;
				case (byte)0x03	:return UUID;
				case (byte)0x05	:return MD5;
				case (byte)0x80	:return USER_DEFINED;
				default			:return BINARY_GENERIC;
			}
		}
	}
	
	
	private byte[] data ;
	
	
	public byte getType() {
		return this.data[0];
	}
	
	public void setType(byte value) {
		this.data[0] = value;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public Binary(byte type, int length) {
		setData(new byte[length]);
		setType(type);
	}
	
	public Binary(byte[] data) {
		setData(data);
	}
	
	public Binary( byte type, byte[] data) {
		this.data = new byte[data.length+1];
		this.data[0] = type;
		BytesUtil.write(this.data, 1, data);
	}
	
	@Override
	public String toString() {
		return "Binary<"+BytesUtil.toHex(data)+">";
	}
	
	public int compareTo(Binary o) {
		return new BinaryComparator().compare(this, o);
	}
}

