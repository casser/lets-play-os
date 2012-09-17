package os.letsplay.bson.binary;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import os.letsplay.utils.BytesUtil;
import os.letsplay.utils.reflection.Simple;


public class MD5 extends Binary implements Simple {
	
	public MD5(byte[] bytes) {
		super(Binary.Type.MD5.getValue(),bytes);
	}
	
	public MD5(String str) {
		this(bytes(str));
	}
	
	public MD5(byte[] buffer, int sPos, int ePos) {
		this(bytes(buffer,sPos,ePos));
	}
	
	@Override
	public String toString() {
		return BytesUtil.toHex(getData());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj!=null){
			if(obj.getClass().equals(MD5.class)){
				MD5 target = (MD5)obj;
				return Arrays.equals(this.getData(),target.getData());
			}else if(obj.getClass().equals(byte[].class)){
				return Arrays.equals(this.getData(),(byte[])obj);
			}
		}
		return false;
	}
	
	
	public static byte[] bytes(String input){
		return bytes(input,false);
	}
	
	private static byte[] bytes(String input, Boolean strict){
		if(!strict && input.matches("^[a-f0-9]{32}$")){
			byte[] bytes =new byte[16];
			if(input.length()==32){
				int i=0;
				while(i<32){
					bytes[i/2]=(byte)Integer.parseInt(""+input.charAt(i)+input.charAt(i+1),16);
					i+=2;
				}
			}
			return bytes;
		}
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
           	return  md.digest(input.getBytes());
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
	}
	
	private static byte[] bytes(byte[] buffer, int offset, int length) {
		try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(buffer,offset,length);
           	return md.digest();
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
	}
	
	public Object toSimple() {
		return toString();
	}
}
